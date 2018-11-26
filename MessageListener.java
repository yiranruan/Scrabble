/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Initialize the message listener thread; it is used to receive and process messages from the server
 **/
package Client;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
//import java.awt.TextArea;
//import java.awt.TextField;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
//import java.lang.invoke.SwitchPoint;
//import java.net.SocketException;
import java.util.ArrayList;
//import java.util.Iterator;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MessageListener extends Thread {

	private BufferedReader reader;
	private BufferedWriter writer;
	private JTextArea user;
	private String msg;
	private JSONObject listener;
	private String[][] letter;
	private Grids grids;
	private String clientName;
	private JFrame frame2;
	private JFrame invitationWindow;
	private JTextArea invite_list;
	public ArrayList<String> userList;
	private JSONObject score;
	private ArrayList<String> obGame;
	private boolean status;




	public MessageListener(BufferedReader reader,BufferedWriter writer, JTextArea user, String[][] letter, Grids grids, JFrame frame2, JFrame invitationWindow,JTextArea invite_list) {
		this.reader = reader;
		this.writer = writer;
		this.user = user;
		this.letter = new String[20][20];
		this.letter = letter;
		this.grids = grids;
		this.frame2 = frame2;
		this.invitationWindow = invitationWindow;
		this.invite_list = invite_list;
		userList = new ArrayList<>();
		obGame = new ArrayList<>();

	}
/**
 * update characters and their positions which are written by other players
 */
	private void updateMatirx(String msg){
		try {
			JSONObject lis = new JSONObject(msg);
			int x =lis.getInt("x");
			int y =lis.getInt("y");
			String character = (String)lis.get("letter");
			String direction = (String)lis.get("direction");
			letter[x][y] = character;
			if(direction.equals("N")){
				grids.setHightlight(x, y,1);
			}else{
				String thecell="";
				int position	=(direction.equals("V"))?y:x;
				for(int q=0;q<20;q++){
					thecell=(direction.equals("V"))?letter[x][q]:letter[q][y];
					if(thecell!= null && !thecell.isEmpty()){
						if((direction.equals("V"))){
							grids.setHightlight(x,q,1);
						}else{
							grids.setHightlight(q,y,1);
						}
					}else if(q<position){
						for(int m=0;m<q;m++){
							if((direction.equals("V"))){
								grids.setHightlight(x,q,0);
							}else{
								grids.setHightlight(q,y,0);
							}
						}
					}else if(q>position){
						break;
					}
				}
				grids.repaint();
			}
		}catch(JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private void getUserName(String msg) {//RYR 2018929
		try {
			JSONObject lis = new JSONObject(msg);
			JSONArray users = lis.getJSONArray("userlist");
			userList.clear();
			for(int i=0; i<users.length(); i++) {
				invite_list.append(users.getString(i)+"\n");
				userList.add(users.getString(i));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void displayScore(String msg) {

			try {
				JSONObject lis = new JSONObject(msg);
				score = lis.getJSONObject("scores");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			user.append("---Score---"+"\n");
			getScore(score, user);
			System.out.println("---Score---");

	}

	private void getScore(JSONObject score, JTextArea display) {
		try {
			
			for (int i=0; i<userList.size();i++) {
					String player = userList.get(i).toString();
					if(score.has(player)) {
						display.append(player+"  "+score.getInt(player)+"\n");
					}
					else {
						obGame.add(player);
						System.out.println(player + "he is observing ");
					}
				}
		} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}

	private String findWinner() {
		int max = 0;
		String winner = "";
		for (int i=0; i<userList.size();i++) {
			String player = userList.get(i).toString();
			try {
				if(score.has(player)) {
					int temp = score.getInt(player);
					if (temp > max) {
						max = temp;
						winner = player;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String result = "the winner is "+ winner +", gets "+ max;
		return result;
	}

	/**
	 * 
	 * obtain user names which is defined by server automatically
	 */
	
	private void setClientName(String msg) {
		try {
			JSONObject lis = new JSONObject(msg);
			clientName = lis.getString("name");
			String message = lis.getString("msg");
			status = lis.getBoolean("gameStatus");
			if(message.length()>0) {
				warningWindow("Warning", message);
			}
			if(status) {
				ClientLogin.setGameStart();
			}
			if(!status) {
				
				ClientLogin.setGameOver();
			}
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String getClientName() {
		return clientName;
	}
	
	private void warningWindow(String title, String info) {
		JFrame warning = new JFrame(title);
		warning.setVisible(true);
		warning.setBounds(250, 325, 200, 100);
		warning.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		warning.getContentPane().setLayout(null);

		JLabel lblPleaseConnectTo = new JLabel(info);
		lblPleaseConnectTo.setBounds(25, 6, 180, 50);
		warning.getContentPane().add(lblPleaseConnectTo);

		JButton btnQuit = new JButton("Quit");
		btnQuit.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				warning.dispose();
			}
		});
		btnQuit.setBounds(35, 43, 117, 29);
		warning.getContentPane().add(btnQuit);
	}


	private void vote(String msg) {
		try {

			JSONObject lis = new JSONObject(msg);
			String word = lis.getString("word");
			String userName = lis.getString("username");
			if(userName.equals(getClientName()))return;
			for(int i =0; i < obGame.size(); i++) {
				if(getClientName().equals(obGame.get(i))) {
					return;
				}
			}
			JFrame voting = new JFrame("Vote");
			voting.setVisible(true);
			voting.setBounds(100, 100, 300, 200);
			voting.getContentPane().setLayout(null);
			JLabel information = new JLabel("The player: "+userName+" input a word "+ word);
			information.setBounds(37, 37, 250, 16);
			voting.getContentPane().add(information);
			JButton agree = new JButton("Agree");
			agree.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					JSONObject vote = new JSONObject();
					try {
						vote.put("command", "VOTE");
						vote.put("vote","1");
						String reply = vote.toString();
						writer.write(reply+"\n");
						writer.flush();
						voting.dispose();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			agree.setBounds(26, 120, 117, 29);
			voting.getContentPane().add(agree);


			JButton disagree = new JButton("Disagree");
			disagree.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					JSONObject vote = new JSONObject();
					try {
						vote.put("command", "VOTE");
						vote.put("vote","0");//备注
						String reply = vote.toString();
						writer.write(reply+"\n");
						writer.flush();
						voting.dispose();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			});
			disagree.setBounds(155, 120, 117, 29);
			voting.getContentPane().add(disagree);


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





	private void gameOver() {
		JFrame gameover = new JFrame("Game OVer");
		gameover.setVisible(true);
		gameover.setBounds(250, 325, 400, 300);
		gameover.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameover.getContentPane().setLayout(null);
		JTextArea finalDisplay = new JTextArea();
		finalDisplay.setBounds(0, 0, 400, 230);
		gameover.getContentPane().add(finalDisplay);
		String win = findWinner();
		finalDisplay.append(win+"\n");
		finalDisplay.append("\n");
		finalDisplay.append("---score---\n");
		getScore(score, finalDisplay);
		JButton exit = new JButton("EXIT");
		exit.setBounds(160, 250, 80, 23);
		gameover.getContentPane().add(exit);
		exit.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				gameover.dispose();
				MatrixManage manage = new MatrixManage(letter);
				manage.resetMatix();
				invitationWindow.setVisible(true);
				frame2.setTitle("Scrabble - "+ getClientName());
				frame2.getContentPane().setBackground(null);
				frame2.dispose();
			}
		});
	}





	public void run() {
		try {

			System.out.println("listenning...");
			while((msg = reader.readLine()) != null) {
				System.out.println("server msg:"+msg);//ddd
				listener = new JSONObject(msg);
				String command = listener.getString("command");
				switch (command) {
				case "USERLIST":
					invite_list.setText("");
					getUserName(msg);
					break;
				case "FILLCELL":
					updateMatirx(msg);
					break;
				case "VOTE":
					vote(msg);
					break;
				case "SCORES":
					System.out.println("Scores arrived!!!"+msg);
					user.setText("");
					invitationWindow.setVisible(false);
					frame2.setVisible(true);
					displayScore(msg);
					break;
				case "GAMEOVER":
					ClientLogin.blockUsers();
					frame2.setTitle("Game Over - "+getClientName());
					frame2.getContentPane().setBackground(new Color(0,0,0));
					gameOver();


					break;
				case "RELEASEBLOCK":
					grids.resetHighlight();
					grids.repaint();
					if(listener.getString("playerName").equals(getClientName())){
						ClientLogin.setReleaseBreak();
						frame2.getContentPane().setBackground(new Color(0, 168, 255));
					}
					break;

				case "NAME":
					setClientName(msg);
					frame2.setTitle("Scrabble - "+ getClientName());
					invitationWindow.setTitle("Invitation-"+ getClientName());
					System.out.println("-------\nUser Name: "+ clientName+"\n------");
					break;



				default:
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
