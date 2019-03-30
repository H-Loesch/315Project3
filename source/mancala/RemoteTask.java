package mancala;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.DefaultListModel;

//TODO look up java concurrency thread pool
//TODO split server to operate on threads so that this doesn't freeze everything else 
public class RemoteTask implements Runnable {
	private PrintWriter remote_writer;
	private BufferedReader remote_reader;
	public DefaultListModel<String> buffer;
	private Socket remoteSocket;
	private ServerSocket listenSocket;
	public String client_or_server; //client or server
	String task = "initialize"; //
	
	RemoteTask(DefaultListModel<String> _buffer, String config) {
		//initialize as client
		buffer = _buffer;
		if (config == "write" || config == "read") {
			// read/write tasks
			task = config;
		} else {
			//general purpose tasks 
			task = null;
		}
	}

	@Override
	public void run() {
		//if read task, read. else, error
		if (task == "read") {
		
			try {
				buffer.addElement( remote_reader.readLine());
			} catch (IOException e) {
				//will an empty line cause this? who knows!
				System.out.println("Reading from remote server failed.");
			}
		} else {
			//that's not a read task. stop that. 
		}
	}
	
	public void run(int port) {
		//initialize as server
		client_or_server = "server";
		try {
			ServerSocket listenSocket = new ServerSocket(port); 
			Socket clientSocket = listenSocket.accept();
			remote_writer = new PrintWriter(clientSocket.getOutputStream());
			remote_reader = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			
		}
        catch (IOException e) { 
			System.out.println("Something went wrong initializing server.");
		}
	}
	
	public void run(int port, String hostname) {
		//initialize as client
		client_or_server = "client";
		try {
			Socket remoteSocket = new Socket(hostname, port); 
			remote_writer = new PrintWriter(remoteSocket.getOutputStream());
			remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));

		}
		catch (IOException e) { 
			System.out.println("Something wentwrong connecting to server.");
		}
	}
	
	public void run(String _in)  {
		if (task == "write") {
			remote_writer.write(_in);
		} else if (_in == "close") {
			try {
				remoteSocket.close(); //works whether client or server
				listenSocket.close(); //if not a server, catches. easy-peasy.
			} catch (IOException e) {
				//Don't eeeeven worry about it !
				//probably
			}
		}
	}
}
