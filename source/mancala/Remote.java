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
public class Remote {
	PrintWriter remote_writer;
	BufferedReader remote_reader;
	DefaultListModel<String> buffer;
	Socket remoteSocket;
	ServerSocket listenSocket;
	String config;
	
	Remote(DefaultListModel<String> _buffer, int port) {
		//initialize as server
		buffer = _buffer;
		config = "server";
		try {
			ServerSocket listenSocket = new ServerSocket(port); 
			Socket clientSocket = listenSocket.accept();
			remote_writer = new PrintWriter(clientSocket.getOutputStream());
			remote_reader = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			
		}
        catch (IOException e) { 
			System.out.println("oof. Something went wrong.");
		}
	}
	
	Remote(DefaultListModel<String> _buffer, int port, String hostname) {
		//initialize as client
		buffer = _buffer;
		config = "client";
		try {
			Socket remoteSocket = new Socket(hostname, port); 
			remote_writer = new PrintWriter(remoteSocket.getOutputStream());
			remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));

		}
		catch (IOException e) { 

		}
	}
	
	//write to whatever the remote is connecting to right now
	public void write(String write_string	) {
		remote_writer.write(write_string);
	}
	
	//Reads a line from remote, and writes to the local buffer.
	public void read() {
		//read until end of line
		try {
			buffer.addElement( remote_reader.readLine());
		} catch (IOException e) {
			//will an empty line cause this? who knows!
			System.out.println("Reading from remote server failed.");
		}
	}
	
	//could probably just remove the if/else, and let try/catch handle listenSocket failure if we're a client
	public void close() {
		if (config == "server") {
			try {
				if (config == "server") {
					remoteSocket.close();
					listenSocket.close();
				} else if (config == "client") {
					remoteSocket.close();
				} else {
					//uh...okay
				}
			} catch (IOException e) {
				//uh... failed to close. okay.
			}
		}
	}
}
