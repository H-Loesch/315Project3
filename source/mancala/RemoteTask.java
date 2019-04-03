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
	String in; //these two variables will be used to bastardize giving thread-pool input
	String in2;  //believe me, I'm extremely ashamed.
	int in_int;
	String task = "initialize"; //
	
	RemoteTask(DefaultListModel<String> _buffer, String config, String _client_or_server) {
		//initialize as client
		buffer = _buffer;
		if (_client_or_server == "client" || _client_or_server == "server") {
			client_or_server = _client_or_server;
		} else {
			System.out.println("client/server labelling failed.");
		}
		
		if (config == "write" || config == "read") {
			// read/write tasks
			task = config;
		} else {
			//general purpose tasks 
			task = null;
		} 
	}

	//run(): now we run our threads in various manners
	//based on qualities of these threads, this function does different things! 
	//For now, we have a read, write, and general thread. 
	@Override
	public void run() {
		if (task == "write") {
			//write
			remote_writer.write(in);
		}
		
		if (task == "read") {
			//if we're a read task, then read.
			try {
				buffer.addElement( remote_reader.readLine());
			} catch (IOException e) {
				//will an empty line cause this? who knows!
				System.out.println("Reading from remote server failed.");
			}
			
		} else {
			if (in == "close") {
				//close the connection 
				try {
					remoteSocket.close(); //works whether client or server
					listenSocket.close(); //if not a server, catches. easy-peasy.
				} catch (IOException e) {
					//Don't eeeeven worry about it !
					//probably
				}
				
			} else if (in == "mancalaServer18242") {
				//initialize as server
				try {
					listenSocket = new ServerSocket(in_int); 
					remoteSocket = listenSocket.accept();
					remote_writer = new PrintWriter(remoteSocket.getOutputStream());
					remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));
					System.out.println("Server Connected.");
				}
		        catch (IOException e) { 
					System.out.println("Something went wrong initializing server.");
				}
				
			} else {
				//client
				try {
					remoteSocket = new Socket(in2, in_int); 
					remote_writer = new PrintWriter(remoteSocket.getOutputStream());
					remote_reader = new BufferedReader( new InputStreamReader(remoteSocket.getInputStream()));

				}
				catch (IOException e) { 
					System.out.println("Something went wrong connecting to server.");
				}
			}
		}
		
	}
}
