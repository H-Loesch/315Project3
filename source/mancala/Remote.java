package mancala;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.DefaultListModel;

//shouldn't be hard to add others later.

public class Remote implements Runnable {
	public PrintWriter remote_writer = null; //this is public so that we can write to the remote connection.
	private BufferedReader remote_reader = null;
	public DefaultListModel<String> buffer;
	private Socket remoteSocket;
	private ServerSocket listenSocket;
	Config config; //client or server
	String in; //these two variables will be used to bastardize giving thread-pool input
	String in2;  //believe me, I'm extremely ashamed.

	Remote(DefaultListModel<String> _buffer, int _port) {
		//initialize as server
		buffer = _buffer;
		try {
			listenSocket = new ServerSocket(_port); 	
			System.out.println("Server created.");
			
		} catch (IOException e) {
			System.out.println("Server creation failed");
		}
	}
	
	Remote(DefaultListModel<String> _buffer, int _port, String hostname) {
		//initialize as client
		buffer = _buffer;
		try {
			remoteSocket = new Socket(hostname, _port);
		} catch (IOException e) {
			System.out.println("Server connection failed");
		}

	}
	
	//run(): This is run in a thread because... uh....
	//well. because I did this wrong but along the way I made the server not hang, so.
	//good enough, okay? The server program doesn't hang while waiting for a client. yay.
	@Override
	public void run() {
		try {
			if (listenSocket != null) {
				remoteSocket = listenSocket.accept();
				remote_writer = new PrintWriter(remoteSocket.getOutputStream(), true);
				remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));
				System.out.println("Client Connected.");
			} else {
				remote_writer = new PrintWriter(remoteSocket.getOutputStream(), true);
				remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));
				System.out.println("Server Connected.");
			}
		} catch (IOException e) {
			System.out.println("remote creation failed.");
		}
		
		String inputLine;
		try {
			while ((inputLine = remote_reader.readLine()) != null) {
				//read repeatedly
				System.out.println("WHOA INPUT RECEIVED FROM REMOTE	");
				if (inputLine.equals("close")) {
					break;
				}
				buffer.addElement(inputLine); //legit I built this to run off the buffer so just. write to that
			
			}
			
			System.out.println("exiting reading while loop");
		
		} catch (IOException e) {
			System.out.println("Server reading has broken.");
			//idk dude. try not breaking things sometimes?
		}
		
	}
}
