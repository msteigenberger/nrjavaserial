/*-------------------------------------------------------------------------
|   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
|   RXTX is a native interface to serial ports in java.
|   Copyright 1997-2008 by Trent Jarvi tjarvi@qbang.org and others who
|   actually wrote it.  See individual source files for more information.
|
|   A copy of the LGPL v 2.1 may be found at
|   http://www.gnu.org/licenses/lgpl.txt on March 4th 2007.  A copy is
|   here for your convenience.
|
|   This library is free software; you can redistribute it and/or
|   modify it under the terms of the GNU Lesser General Public
|   License as published by the Free Software Foundation; either
|   version 2.1 of the License, or (at your option) any later version.
|
|   This library is distributed in the hope that it will be useful,
|   but WITHOUT ANY WARRANTY; without even the implied warranty of
|   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
|   Lesser General Public License for more details.
|
|   An executable that contains no derivative of any portion of RXTX, but
|   is designed to work with RXTX by being dynamically linked with it,
|   is considered a "work that uses the Library" subject to the terms and
|   conditions of the GNU Lesser General Public License.
|
|   The following has been added to the RXTX License to remove
|   any confusion about linking to RXTX.   We want to allow in part what
|   section 5, paragraph 2 of the LGPL does not permit in the special
|   case of linking over a controlled interface.  The intent is to add a
|   Java Specification Request or standards body defined interface in the
|   future as another exception but one is not currently available.
|
|   http://www.fsf.org/licenses/gpl-faq.html#LinkingOverControlledInterface
|
|   As a special exception, the copyright holders of RXTX give you
|   permission to link RXTX with independent modules that communicate with
|   RXTX solely through the Sun Microsytems CommAPI interface version 2,
|   regardless of the license terms of these independent modules, and to copy
|   and distribute the resulting combined work under terms of your choice,
|   provided that every copy of the combined work is accompanied by a complete
|   copy of the source code of RXTX (the version of RXTX used to produce the
|   combined work), being distributed under the terms of the GNU Lesser General
|   Public License plus this exception.  An independent module is a
|   module which is not derived from or based on RXTX.
|
|   Note that people who make modified versions of RXTX are not obligated
|   to grant this special exception for their modified versions; it is
|   their choice whether to do so.  The GNU Lesser General Public License
|   gives permission to release a modified version without this exception; this
|   exception also makes it possible to release a modified version which
|   carries forward this exception.
|
|   You should have received a copy of the GNU Lesser General Public
|   License along with this library; if not, write to the Free
|   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
|   All trademarks belong to their respective owners.
--------------------------------------------------------------------------*/
package gnu.io.factory;

import java.util.Collection;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.TreeSet;

import gnu.io.SerialPort;

/**
 *
 * @author MatthiasS
 *
 */
public class SerialPortRegistry {

    private Collection<SerialPortCreator<? extends SerialPort>> portCreators;

    public SerialPortRegistry() {
        // register the LOCAL PortCreator as last argument, so that is always taken into account when no other creator
        // is applicable.
        this.portCreators = new TreeSet<SerialPortCreator<? extends SerialPort>>(
                new Comparator<SerialPortCreator<? extends SerialPort>>() {

                    @Override
                    public int compare(SerialPortCreator<? extends SerialPort> o1,
                            SerialPortCreator<? extends SerialPort> o2) {
                        if (o1.getProtocol().equals(SerialPortCreator.LOCAL)) {
                            return 1;
                        }
                        if (o2.getProtocol().equals(SerialPortCreator.LOCAL)) {
                            return -1;
                        }
                        return o1.getProtocol().compareTo(o2.getProtocol());
                    }
                });

        registerDefaultSerialPortCreators();
    }

    /**
     * Registers the {@link RxTxPortCreator} and the {@link RFC2217PortCreator}.
     */
    protected void registerDefaultSerialPortCreators() {
        ServiceLoader<SerialPortCreator> serviceLoader = ServiceLoader.load(SerialPortCreator.class);
        for (SerialPortCreator<?> portCreator : serviceLoader) {

            registerSerialPortCreator(portCreator);
        }
    }

    /**
     * Registers a {@link SerialPortCreator}.
     *
     * @param creator
     */
    public void registerSerialPortCreator(SerialPortCreator<? extends SerialPort> creator) {
        this.portCreators.add(creator);
    }

    /**
     * Gets the best applicable {@link SerialPortCreator} for the given <code>portName</code>
     *
     * @param portName The port's name.
     * @return A found {@link SerialPortCreator} or null if none could be found.
     */
    @SuppressWarnings("unchecked")
    public <T extends SerialPort> SerialPortCreator<T> getPortCreatorForPortName(String portName,
            Class<T> expectedClass) {
        for (@SuppressWarnings("rawtypes")
        SerialPortCreator creator : this.portCreators) {
            try {
                if (creator.isApplicable(portName, expectedClass)) {
                    return creator;
                }
            } catch (Exception e) {
                System.err.println("Error for SerialPortCreator#isApplicable: " + creator.getClass() + "; "
                        + creator.getProtocol() + " -> " + e.getMessage());
            }
        }
        return null;
    }
}
