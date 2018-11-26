/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Manage clients' connections in a thread per connection way.
 **/
package Server;
import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class UserThreads extends Thread {
	private Game game;
	private Socket client;
	BufferedReader reader;
	BufferedWriter writer;
	private boolean status = false;
	private boolean identifier = true;

	public UserThreads(Socket client, Game game) {
		try{
			this.client = client;
			this.game = game;
		} catch (Exception e){
			System.out.println("client socket");
			e.printStackTrace();
		}
	}

	public void run() {
		//create a socket for a user connection
		try {
			String msg = null;
			reader = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
			while ((msg = reader.readLine()) != null) {
				//print out every msg from user in the Console
				System.out.println("User msg:"+msg);
				game.commander(msg);
			}
		} catch (SocketException ex) {
			try {
				reader.close();
				writer.close();
				client.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (Exception er) {
			System.out.println(er.getMessage());
		}
		//KeyPoint: When a abrupt exit happens, the game will be over
		game.peaceOut((UserThreads) currentThread());
	}

	public void write(String msg) {
		//send a JSON message to user
		try {
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
			writer.write(msg + "\n");
			writer.flush();
		} catch (IOException e) {
			UserThreads ut = (UserThreads) currentThread();
			ClientManager.getInstance().clientDisconnected(ut);
		}
	}
}
