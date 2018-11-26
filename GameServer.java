/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Manage server's socket
 **/
package Server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.JSONObject;

public class GameServer implements Runnable {
	private Game game = new Game();
	private int port;
	private ServerSocket server = null;
	private int clientNum = 0;
	private boolean power;
	private ArrayList<String> nameList = new ArrayList<String>();

	public GameServer (int port) {
		this.port = port;
		System.out.println("------\nServer Console\n------");
		game.gameReset();
	}

	public void run() throws NullPointerException{
		//build a server socket to listening on port
		try {
			server = new ServerSocket(port);
			ServerGUI.showLog("Server listening on port "+port+" for a connection");
			power = true;
			while(power){
				BufferedReader reader;
				BufferedWriter writer;
				String username;
				ClientManager.getInstance();
				Socket client = server.accept();
				clientNum++;
				reader = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
				username = reader.readLine();
				writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8"));
				JSONObject msgName = new JSONObject();
				//create a new user thread
				UserThreads user = new UserThreads(client, game);
				String usernameMsg="";
				if (username.equals("\n") || username.isEmpty()|| username==null) {
					user.setName("a" + clientNum);
				} else {
					if(isExist(username)) {
						user.setName("a" + clientNum);
						//duplicated username is renamed here
						usernameMsg="Sorry, your desired name is occupied. We assign you a name for the game, a" + clientNum+".Cheers.";
					} else {
						user.setName(username);
					}
				}
				nameList.add(user.getName());
				msgName.put("command", "NAME");
				msgName.put("name",user.getName());
				msgName.put("msg",usernameMsg);
				msgName.put("gameStatus",game.getStatus());
				//KeyPoint:send this user's player name and confirm connection
				writer.write(msgName.toString()+"\n");
				writer.flush();
				user.start();
				ClientManager.getInstance().clientConnected(user);
				ServerGUI.showLog("New client \""+user.getName()+"\" is connected.");
			}
		} catch (Exception ex2){
			//ex2.printStackTrace();
			ServerGUI.showLog("Socket:"+ex2.getMessage());
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				ServerGUI.showLog(e.getMessage());
			} catch (NullPointerException ex) {
				throw new NullPointerException(ex.getMessage());
			}
		}
	}

	public void stopServer() {
		//close server
		try {
			ServerGUI.showLog("Server is closed. See you next time");
			server.close();
			power = false;
			clientNum = 0;
		} catch (IOException e) {
			ServerGUI.showLog(e.getMessage());
		}
	}

	private boolean isExist(String name) {
		//judge a user-generated name legal or not
		for(int i = 0; i<nameList.size();i++) {
			if (nameList.get(i).equals(name)) {
				return true;
			}
		}
		nameList.add(name);
		return false;
	}

	//getter
	public int getClientNum() {
		return clientNum;
	}
}
