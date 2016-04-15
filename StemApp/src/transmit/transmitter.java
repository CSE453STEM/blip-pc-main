package transmit;

import java.util.*;
import jssc.*;
import java.io.*;

public class transmitter {
    InputStream instream;
    OutputStream outstream;
    static SerialPort sp;
    Thread readthread;
    private Queue<String> queueA = new LinkedList<String>();
	private Boolean busy;
	static UI.ui myUI;
	
	public transmitter(UI.ui passedUI){
		myUI = passedUI;
		
		try{
			sp = new SerialPort("COM4");
		} catch(Exception e){
			System.out.println("Port doesn't exist");
			return;
		}
	
		try{
			sp.openPort();
			sp.setParams(9600,  8,  1,  0);
			sp.setEventsMask(SerialPort.MASK_RXCHAR);
			sp.addEventListener(new portReader());
		} catch(SerialPortException e){
			System.out.println(e);
		}
		
	}
	
	private static class portReader implements SerialPortEventListener {
		String aggstr = "";
		
		public void serialEvent(SerialPortEvent event) {
	        if(event.isRXCHAR()){
	        	int bufsize = event.getEventValue();
	        	try {
	        		byte buffer[] = sp.readBytes(bufsize);
	        		//System.out.println(buffer);
	        		String str = new String(buffer);
	        		aggstr += str;
	        		String finalchar = str.substring(bufsize-1);
	        		if(finalchar.equals("\n")){
	        			String mstr = aggstr.replaceAll("[\u0000-\u001f]", "");
	        			myUI.printReceived(mstr);
	        			aggstr = "";
	        		}
	        	} catch (SerialPortException e) {
	        		System.out.println(e);
	        	}
	        }
		}
	}
	public void sendData(String message){
		try{
			sp.writeString(message);
			sp.writeString("\n");
		}catch(SerialPortException e){
			System.out.println(e);
		}
		
	}
}

