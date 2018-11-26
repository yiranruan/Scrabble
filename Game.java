/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Handle the game interaction,process all clients'requests.
 **/
package Server;
import org.json.*;
import java.util.ArrayList;
import java.util.Iterator;

class Game {
	private JSONArray msg;
	private static String[][] cell;
	private boolean gameStatus;//RYR: identify the game start or not
	private JSONObject jmail;//for json communication
	private JSONObject scores;//for recording all players' points
	private String whoGetPoints;// who will get points after a poll
	private int countVotes;//for counting the vote
	private int countVoters;//for counting how many players voted
	private int howManyPoints;//the length of the word
	private int countPass;
	private int countCells;
	private int howManyPlayers;
	private int curPlayerIndex;
	ArrayList<String> players = new ArrayList<String>();

	public synchronized void gameReset(){
		this.cell = new String[20][20];
		this.gameStatus = false;
		this.jmail = new JSONObject();
		this.scores = new JSONObject();
		this.whoGetPoints="";
		this.countVotes =0;
		this.countVoters=0;
		this.howManyPoints=0;
		this.countPass=0;
		this.countCells=400;
		this.howManyPlayers=0;
		this.curPlayerIndex=0;
		players.clear();
	}

	public synchronized void commander(String msg){
		//get msg from listener,and react accordingly like its name
		try{
			JSONObject msgJson = new JSONObject(msg);
			switch (msgJson.getString("command")) {
				case "START":
					if(!gameStatus) {//RYR 2018929
						invite(msgJson);
						gameStatus = true;
					}
					break;

				case "FILLCELL":
					fillcell(msgJson);
					break;

				case "VOTE":
					vote(msgJson.getInt("vote"));
					break;

				case "PASS":
					pass();
					break;

				default:
					break;
			}
		}catch (Exception er) {
				System.out.println(er.getMessage());
		}
	}

	public synchronized void pushScores(){
		//push current scores to all online users
		try{
			jmail.put("command","SCORES");
			jmail.put("scores",this.scores);
			ClientManager.getInstance().send(jmail.toString());
			jmail = new JSONObject();
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	public synchronized void invite(JSONObject msgJson) {
		//decide who will play the game, and tell the user to
		//begin the first move
		try {
			JSONArray j = msgJson.getJSONArray("user");
			for (int i=0;i<j.length();i++){
				this.howManyPlayers++;
				players.add(j.getString(i));
				this.scores.put(j.getString(i),0);
			}
			pushScores();
			nextPlay();
		} catch (Exception e) {
			ServerGUI.showLog("GameInvite: "+e.getMessage());
		}
	}

	public synchronized void fillcell(JSONObject msgJson){
		//after a player fills a cell, update the game status
		//it may or may not initial a poll
			this.countPass=0;
			this.countCells--;//When count reaches 0, it will terminate the game
			try{
				int x = msgJson.getInt("x");
				int y = msgJson.getInt("y");
				cell[x][y] = msgJson.getString("letter");
				String direction = msgJson.getString("direction");
				// push to clients: the update of matrix
				jmail.put("command","FILLCELL");
				jmail.put("x",x);
				jmail.put("y",y);
				jmail.put("direction",direction);
				jmail.put("letter",cell[x][y]);
				ClientManager.getInstance().send(jmail.toString());
				jmail = new JSONObject();
				if(direction.equals("N")){//no vote when a player chooses "No"
					nextPlay();
				}else{// initial a poll
					String userIndex=Thread.currentThread().getName();
					initialPoll(userIndex,x,y,direction);
				}
			}catch(JSONException e){
				 e.printStackTrace();
			}catch(Exception ex){
			 	ServerGUI.showLog("GameFillCell: "+ex.getMessage());
			}
	}

	public synchronized void initialPoll(String userIndex,int x,int y,String direction){
		//push all players to vote
		String word		="";
		String thecell	="";
		int position	=(direction.equals("V"))?y:x;
		//calculating a word for vote
		for(int i=0;i<20;i++){
			thecell=(direction.equals("V"))?cell[x][i]:cell[i][y];
			if(thecell!= null && !thecell.isEmpty()){
				word+=thecell;
			}else if(i<position){
				word="";
			}else if(i>position){
				break;
			}
		}
		//push to clients: the word to vote
		try{
			jmail.put("command","VOTE");
			jmail.put("word",word);
			jmail.put("username",userIndex);//who gets points for this word
			ClientManager.getInstance().send(jmail.toString());
			jmail = new JSONObject();
		}catch(JSONException e){
			e.printStackTrace();
		}
		//reset all statistics for the poll
		this.countVotes=0;
		this.countVoters=0;
		this.whoGetPoints=userIndex;
		this.howManyPoints=word.length();
	}

	public synchronized void vote(int agree){
		//count the vote, assign points, continue the game
		this.countVotes+=agree;
		this.countVoters++;
		if(this.countVoters==this.howManyPlayers-1){
			if(this.countVotes==this.countVoters){
				//Update user's points, otherwise, do nothing
				try{
					int p=this.scores.getInt(this.whoGetPoints)+this.howManyPoints;
					this.scores.put(this.whoGetPoints,p);
					pushScores();
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
			//Finish voting, move to next round or game over
			nextPlay();
		}
	}

	public synchronized void pass(){
		//count how many user clicked "pass", when it reaches the current players number,
		//the game will be over.countPass will be reset to 0 at every FILLCELL command
		this.countPass++;
		if(this.countPass==this.howManyPlayers){
			this.gameOver();// game over method 1
		}else{
			nextPlay();
		}
	}

	public synchronized void nextPlay(){
		//tell the next player to move
		System.out.println(countCells+"cells left.");
		if(countCells==0){
			this.gameOver();// game over method 2
		}else{
			try{
				jmail.put("command","RELEASEBLOCK");
				jmail.put("playerName",this.players.get(curPlayerIndex));
				ClientManager.getInstance().send(jmail.toString());
				jmail = new JSONObject();
				curPlayerIndex=(curPlayerIndex==howManyPlayers-1)?0:curPlayerIndex+1;
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
	}

	private synchronized void gameOver(){
		//tell all user, the game is over
		try{
			//push to clients: game result
			jmail.put("command","GAMEOVER");
			ClientManager.getInstance().send(jmail.toString());
			jmail = new JSONObject();
			gameStatus = false;//RYR 2018929
		}catch(JSONException e){
			e.printStackTrace();
		}
		gameReset();
	}

	public synchronized void peaceOut(UserThreads ut){
		//finish the game elegantly when a user is disconnected
		ClientManager.getInstance().clientDisconnected(ut);
		if(gameStatus){
			gameOver();
		}
	}

	//getter
	public synchronized boolean getStatus() {
		return gameStatus;
	}//RYR 2018929
}
