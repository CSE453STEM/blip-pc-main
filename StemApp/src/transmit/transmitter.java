package transmit;

import jssc.*;
import java.io.*;

public class transmitter {
    InputStream instream;
    OutputStream outstream;
    static SerialPort sp;
	static UI.ui myUI;
	
	public transmitter(UI.ui passedUI){
		myUI = passedUI;
		
		String[] ports = SerialPortList.getPortNames();
		
		/* Assume BLIP Array will be at highest-numbered available port */
		try{
			sp = new SerialPort(ports[ports.length-1]);
		} catch(Exception e){
			System.out.println("Port doesn't exist");
			return;
		}
	
		try{
			/* Set up COM Port for communication */
			sp.openPort();
			sp.setParams(19200,  8,  1,  0);
			sp.setEventsMask(SerialPort.MASK_RXCHAR);
			sp.addEventListener(new portReader());
		} catch(SerialPortException e){
			System.out.println(e);
		}
		
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
	        if(event.isRXCHAR()){
	        	int bufsize = event.getEventValue();
	        	try {
	        		/* Create buffer large enough for received characters */
	        		byte buffer[] = sp.readBytes(bufsize);
	        		myUI.printReceived(new String(buffer));
	        	} catch (SerialPortException e) {
	        		System.out.println(e);
	        	}
	        }
		}
	}
	
	/* Send data in correctly-formatted package */
	public void sendData(String message){
		char c = 255;
		int baud = 200;
		int ln = message.length();
		try{
			sp.writeByte((byte) c);
			sp.writeByte((byte) baud);
			sp.writeByte((byte) ln);
			sp.writeString(message); 
			sp.writeString("\n");
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
}

