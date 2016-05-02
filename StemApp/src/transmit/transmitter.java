package transmit;

import jssc.*;
import java.io.*;

public class transmitter {
    InputStream instream;
    OutputStream outstream;
    static SerialPort sp;
	static UI.ui myUI;
	
	/* Constructor one - used on start-up, tries to pick COM Port automatically*/
	public transmitter(UI.ui passedUI){
		/* Pass UI in so that transmitter can call UI methods */
		myUI = passedUI;	
		String[] ports = SerialPortList.getPortNames();
		
		/* Assume BLIP Array will be at highest-numbered available port */
		try{
			if(ports.length == 0){
				myUI.printReceived("No ports detected\n", true);
				return;
			}
			/* Try highest-numbered available COM port for communication */
			sp = new SerialPort(ports[ports.length-1]);
		} catch(Exception e){
			System.out.println(e);
			myUI.printReceived("Could not open the automatically-detected port: " 
								+ ports[ports.length-1] + "\n", true);
			return;
		}
	
		commonConstructor(sp, ports[ports.length-1]);
	}
	
	/* Constructor two - uses a specified COM port */
	public transmitter(UI.ui passedUI, String portName){
		myUI = passedUI;
		try{
			sp = new SerialPort(portName);
		} catch(Exception e){
			System.out.println(e);
			myUI.printReceived("Could not open the specified port: " + portName
								+ "\n", true);
			return;
		}
		
		commonConstructor(sp, portName);
	}
	
	/* Common code for both versions of the constructor */
	public void commonConstructor(SerialPort sp, String spname){
		try{
			/* Set up COM Port for communication */
			sp.openPort();
			sp.setParams(19200,  8,  1,  0);
			sp.setEventsMask(SerialPort.MASK_RXCHAR);
			/* Add portReader as event listener */
			sp.addEventListener(new portReader());
		} catch(SerialPortException e){
			System.out.println(e);
			myUI.printReceived("Port " + spname 
					+ " found, but could not be initialized. May be busy\n", true);
			return;
		}
		
		myUI.printReceived("Port " + spname
					+ " opened successfully. Commmunications open\n", true);
	}
	
	/* Close port - wrapper to call from UI class and catch exception */
	public static void closePort(){
		try {
			sp.closePort();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/* Listener for reading transmissions */
	private static class portReader implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			System.out.println("Characters received");
			
			/* When one or more characters are received */
	        if(event.isRXCHAR()){
	        	int bufsize = event.getEventValue();
	        	try {
	        		/* Create buffer large enough for received characters */
	        		byte buffer[] = sp.readBytes(bufsize);
	        		/* Print received characters */
	        		myUI.printReceived(new String(buffer));
	        	} catch (SerialPortException e) {
	        		System.out.println(e);
	        	}
	        }
	        else{
	        	System.out.println(event);
	        }
		}
	}
	
	/* Send data in correctly-formatted package */
	public void sendData(String message, int baud){
		char c = 255;
		int ln = message.length() + 1;
		try{
			sp.writeByte((byte) c); //Message-start control character
			sp.writeByte((byte) baud); //Baud rate
			sp.writeByte((byte) ln); //Message length
			sp.writeString(message); 
			sp.writeString("\n"); //Message end
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
	
	/* Retrieves available COM Ports for UI */
	public Object[] getPorts(){
		Object[] ports = null;
		try{
			ports = SerialPortList.getPortNames();
		}catch(Exception e){
			System.out.println(e);
		}
		return ports;
	}
}

