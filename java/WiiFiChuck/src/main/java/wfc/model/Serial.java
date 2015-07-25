package wfc.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jssc.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ljoha on 7/12/2015.
 */
@Component
public class Serial {
    private SerialPort serialPort;
    private StringProperty portName = new SimpleStringProperty();
    private ObservableList<String> portNames = FXCollections.observableArrayList();
    private StringProperty status = new SimpleStringProperty();
    private ScheduledExecutorService executor;
    private SerialPortEventListener handleSerialPortEvents = new SerialPortEventListener() {
        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
//                    System.out.println(String.format("EventType: [%s]", serialPortEvent.getEventType()));
            byte[] bytes;
            try {
                while ((bytes = serialPort.readBytes()) != null) {
//                            System.out.println(new String(bytes));
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    };
    private Runnable isHardwareDeviceDisconnected = new Runnable() {
        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (status.get() == STATUS.CONNECTED.toString() && executor.isShutdown() == false) {
                            SerialPort isDeviceConnected = new SerialPort(Serial.this.serialPort.getPortName());
                            isDeviceConnected.openPort(); //should throw here
                            isDeviceConnected.closePort();
                        }
                    } catch (SerialPortException e) {
                        if (e.getExceptionType() == "Port not found") {
                            status.set(STATUS.DEVICE_DISCONNECTED.toString());
                            executor.shutdown();
                            try {
                                serialPort.closePort(); //or we won't be able to connect to this port again after hardware is reconnected
                            } catch (SerialPortException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    };

    public Serial() {
        this.updatePortNames();
    }

    public void connect(String portName) {
        this.status.set(STATUS.CONNECTING.toString());
        this.portName.set(portName);
        this.serialPort = new SerialPort(portName);
        try {
            this.serialPort.openPort();
            this.serialPort.setParams(9600, 8, 1, 0);
            this.serialPort.addEventListener(handleSerialPortEvents);
            this.status.set(STATUS.CONNECTED.toString());
        } catch (SerialPortException e) {
            if (e.getExceptionType() == "Port not found") {
                this.status.set(STATUS.PORT_NOT_FOUND.toString());
            } else if (e.getExceptionType() == "Port busy") {
                this.status.set(STATUS.PORT_BUSY.toString());
            } else {
                this.status.set(STATUS.ERROR.toString());
                e.printStackTrace();
            }
            return;
        }
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleAtFixedRate(isHardwareDeviceDisconnected, 1, 1, TimeUnit.SECONDS);
    }

    public void disconnect() {
        try {
            this.serialPort.closePort();
            this.status.set(STATUS.DISCONNECTED.toString());
        } catch (SerialPortException e) {
            this.status.set(STATUS.ERROR.toString());
            e.printStackTrace();
        }
        this.executor.shutdown();
    }

    public void updatePortNames() {
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
        this.portNames.setAll(portNames);

    }

    public enum STATUS {
        CONNECTED,
        CONNECTING,
        DISCONNECTED,
        DEVICE_DISCONNECTED,
        PORT_BUSY,
        PORT_NOT_FOUND,
        ERROR
    }

    public String getPortName() {
        return portName.get();
    }

    public StringProperty portNameProperty() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName.set(portName);
    }

    public ObservableList<String> getPortNames() {
        return portNames;
    }

    public void setPortNames(ObservableList<String> portNames) {
        this.portNames = portNames;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
