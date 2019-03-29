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
	
	Remote(DefaultListModel<String> _buffer, int port) {
		//initialize as server
		buffer = _buffer;
		try {
			ServerSocket remoteSocket = new ServerSocket(port); 
			Socket clientSocket = remoteSocket.accept();
			PrintWriter remote_writer = new PrintWriter(clientSocket.getOutputStream());
			BufferedReader remote_reader = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			
			String inputLine;
			while ((inputLine = remote_reader.readLine()) != null) {
				buffer.addElement(inputLine); //just add the thing to the thing... let's see what happens
				//something something have an end condition here or something
			}
		}
        catch (IOException e) { 
			System.out.println("oof. Something went wrong.");
		}
	}
	
	Remote(DefaultListModel<String> _buffer, int port, String hostname) {
		//initialize as client
		buffer = _buffer;
		try {
			Socket remoteSocket = new Socket(hostname, port); 
			remote_writer = new PrintWriter(remoteSocket.getOutputStream());
			remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));
			
			String inputLine; 
			while ((inputLine = remote_reader.readLine()) != null) {
				buffer.addElement(inputLine);
			}
		}
		catch (IOException e) { 

		}
	}
	
	//write to whatever the remote is connecting to right now
	public void write(String write_string) {
		remote_writer.write(write_string);
	}
	
	//remove this probably
	public String read() {
		//read until end of line
		return "honk";
	}
}
