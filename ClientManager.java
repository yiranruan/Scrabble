/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Process JSON messages received from clients and manage the server state
 **/
package Server;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientManager {

	private static ClientManager instance;
	private static List<UserThreads> connectedClients;

	private ClientManager() {
		connectedClients = new ArrayList<UserThreads>();
	}

	public static synchronized ClientManager getInstance() {
		if(instance == null) {
			instance = new ClientManager();
		}
		return instance;
	}

	public synchronized void send(String msg) {
		// Broadcast the client message to all the clients connected
		// to the server
		for(UserThreads UserThreads : connectedClients) {
			UserThreads.write(msg);
		}
	}

	public synchronized void clientConnected(UserThreads ut) {
		//add a client's connection
		try {
			connectedClients.add(ut);
			JSONObject msgJSON = new JSONObject();
			msgJSON.put("command", "USERLIST");
			JSONArray userlist = new JSONArray();
			ServerGUI.clearPleayer();
			for(UserThreads UserThreads : connectedClients) {
				ServerGUI.showPlayer(UserThreads.getName());
				userlist.put(UserThreads.getName());
			}
			msgJSON.put("userlist", userlist);
			for(UserThreads UserThreads : connectedClients) {
				 UserThreads.write(msgJSON.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public synchronized void clientDisconnected(UserThreads ut) {
		//disconnect a client's connection
		try {
			connectedClients.remove(ut);
			JSONObject msgJSON = new JSONObject();
			msgJSON.put("command", "USERLIST");
			JSONArray userlist = new JSONArray();
			ServerGUI.showLog("\""+ut.getName()+"\" is disconnected.");
			ServerGUI.clearPleayer();
			for(UserThreads UserThreads : connectedClients) {
				ServerGUI.showPlayer(UserThreads.getName());
				userlist.put(UserThreads.getName());
			}
			msgJSON.put("userlist", userlist);
			for(UserThreads UserThreads : connectedClients) {
				UserThreads.write(msgJSON.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public synchronized List<UserThreads> getConnectedClients() {
		return connectedClients;
	}

	public synchronized void reset() {
		connectedClients.clear();
	}

}
