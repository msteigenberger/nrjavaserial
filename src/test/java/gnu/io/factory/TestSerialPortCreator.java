package gnu.io.factory;

import static org.junit.Assert.*;

import org.junit.Test;

import gnu.io.RXTXPort;

public class TestSerialPortCreator {

    @Test
    public void testSerialPortUtilCreator() {
        assertEquals("a;b;c;d", SerialPortUtil.initSerialPort("d", "a;b;c"));
    }

    @Test
    public void testSerialPortCreatorSpi() {
        SerialPortRegistry serialPortRegistry = new SerialPortRegistry();
        SerialPortCreator<RXTXPort> portCreator = serialPortRegistry.getPortCreatorForPortName("abc", RXTXPort.class);
        assertNotNull(portCreator);
    }
}
