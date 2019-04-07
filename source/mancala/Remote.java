package mancala;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.DefaultListModel;

//shouldn't be hard to add others later.

public class Remote implements Runnable {
	public PrintWriter remote_writer = null; //this is public so that we can write to the remote connection.
	private BufferedReader remote_reader = null;
	public DefaultListModel<String> buffer;
	private Socket remoteSocket;
	private ServerSocket listenSocket;
	String store;
	Config config; //client or server

	Remote(DefaultListModel<String> _buffer, int _port) {
		//initialize as server
		buffer = _buffer;
		Boolean scanning = true;
		while (scanning) {
			try {
		
				listenSocket = new ServerSocket(_port); 
				remoteSocket = listenSocket.accept();
				System.out.println("Server created.");
				scanning = false;
			} catch (SocketException se) {
				try {
					System.out.println("Client connection failed: trying again in 2 sec");
					Thread.sleep(2000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			} catch (IOException e) {
				System.out.println("Server creation failed");
			}
		}
	}
	
	Remote(DefaultListModel<String> _buffer, int _port, String hostname) {
		//initialize as client
		buffer = _buffer;
		Boolean scanning = true;
		while (scanning) {
			try { 
				remoteSocket = new Socket(hostname, _port);
				scanning = false;
			} catch (SocketException e) {
				try {
					System.out.println("Server connection failed; trying again in 2s");
					Thread.sleep(2000);
				} catch (InterruptedException ie) {
					//well gosh darn! rude.
					ie.printStackTrace();
				}
			} catch (IOException ioe) {
				//I don't even know what an IOException is 
			}
		}
	}
	
	@Override
	public void run() {
		try {
			if (listenSocket != null) {
				remote_writer = new PrintWriter(remoteSocket.getOutputStream(), true);
				remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));
				remote_writer.println("WELCOME");
				remote_writer.println(store);
				System.out.println("Client Connected.");
			} else {
				remote_writer = new PrintWriter(remoteSocket.getOutputStream(), true);
				remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));
			}
		} catch (IOException e) {
			System.out.println("remote creation failed.");
		}
		
		String inputLine;
		try {
			while ((inputLine = remote_reader.readLine()) != null) {
				//read repeatedly, until the remote socket is closed or returns null.
	
				buffer.addElement(inputLine); //legit I built this to run off the buffer so just. write to that
				if ((inputLine.equals("WINNER") || inputLine.equals("LOSER") || inputLine.equals("TIE"))) {
					//we are client, have received final message from server.
					remoteSocket.close();
				}
			}
			listenSocket.close();
			System.out.println("Remote connection closed.");
		} catch (IOException e) {
			System.out.println("Remote connection closed.");
		}
		
	}
}
