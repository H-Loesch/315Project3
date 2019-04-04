package mancala;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.DefaultListModel;

//shouldn't be hard to add others later.
enum Task {
	READ, WRITE, GENERAL
}
//TODO look up java concurrency thread pool
//TODO split server to operate on threads so that this doesn't freeze everything else 
public class RemoteTask implements Runnable {
	private PrintWriter remote_writer = null;
	private BufferedReader remote_reader = null;
	public DefaultListModel<String> buffer;
	private Socket remoteSocket;
	private ServerSocket listenSocket;
	Config config; //client or server
	String in; //these two variables will be used to bastardize giving thread-pool input
	String in2;  //believe me, I'm extremely ashamed.
	Task task = Task.GENERAL; //we default to being a read thread. 
	
	RemoteTask(DefaultListModel<String> _buffer, Task _task, Config _client_or_server) {
		//initialize as client
		config = _client_or_server;
		buffer = _buffer;
		task = _task;
	}

	//run(): now we run our threads in various manners
	//based on qualities of these threads, this function does different things! 
	//For now, we have a read, write, and general thread. 
	@Override
	public void run() {
		if (task == Task.WRITE) {
			//write
			remote_writer.write(in);
		}
		
		else if (task == Task.READ) {
			//if we're a read task, then read.
			try {
				//buffer.addElement( remote_reader.readLine());
				System.out.println( remote_reader.readLine());
			} catch (IOException e) {
				//will an empty line cause this? who knows!
				System.out.println("Reading from remote server failed.");
			}
			
		}  else if (remote_writer == null && remote_reader == null) {
			//if these are null, then let's go ahead and initialize
			if (in2 == null) {
				//initialize as server
				try {
					listenSocket = new ServerSocket(Integer.parseInt(in)); 
					remoteSocket = listenSocket.accept();
					remote_writer = new PrintWriter(remoteSocket.getOutputStream());
					remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));
					System.out.println("Server Connected.");
				} catch (IOException e) {
					System.out.println("Server creation failed");
				}
			} else {
				//initialize as client
				try {
					remoteSocket = new Socket(in2, Integer.parseInt(in));
					remote_writer = new PrintWriter(remoteSocket.getOutputStream());
					remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));
					System.out.println("Server Connected.");
				} catch (IOException e) {
					System.out.println("Server creation failed");
				}
			}
			
		} else if (in.equals("close")) {
			//close the connection 
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
