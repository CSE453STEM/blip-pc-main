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
			sp.setParams(9600,  8,  1,  0);
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
		String aggstr = "";
		
		public void serialEvent(SerialPortEvent event) {
	        if(event.isRXCHAR()){
	        	int bufsize = event.getEventValue();
	        	try {
	        		/* Create buffer large enough for received characters */
	        		byte buffer[] = sp.readBytes(bufsize);
	        		String str = new String(buffer);
	        		
	        		/* Append read string into aggstr */
	        		aggstr += str;
	        		String finalchar = str.substring(bufsize-1);
	        		
	        		/* If received data is end of package, print all received data */
	        		if(finalchar.equals("\n")){
	        			String mstr = aggstr.replaceAll("[\u0000-\u001f]", "");
	        			myUI.printReceived(mstr);
	        			/* Reset aggregate string */
	        			aggstr = "";
	        		}
	        	} catch (SerialPortException e) {
	        		System.out.println(e);
	        	}
	        }
		}
	}
	
	/* Send data in correctly-formatted package */
	public void sendData(String message){
		try{
			char c = 255;
			sp.writeString(Character.toString(c));
			sp.writeInt(200);
			sp.writeInt(message.length());
			sp.writeString(message); //should be limited to 126
			sp.writeString("\n");
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
}

