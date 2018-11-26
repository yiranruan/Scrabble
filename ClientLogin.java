/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Initialize the client user interface including login window, invitation window, and game window.
 **/
package Client;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.awt.Button;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.io.*;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;


public class ClientLogin {

	private JFrame frame1;
	private JFrame frame2;
	private JFrame InvitationWindow;
	private JTextField txtIp;
	private JTextField txtPort;
	private JTextField txtUsername;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Socket socket;
	private JSONObject send;
	private JSONObject user;
	private Grids grids;
	static private JButton btnPass;
	private JButton btnQuit_1;
	private String direction=null;
	
	private MessageListener ml;
	private JTextArea textArea;
	private JTextArea u_list;
	private JSONArray userList;
	private String[][] letter;
	private int point_x;
	private int point_y;
	private String character=null;
	private boolean connectionStatus = false;
	private MatrixManage matrix_manage;
	private static boolean game_status = false;
	private static boolean startGame = false;
	private String clientName;
	


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientLogin window = new ClientLogin();
					window.frame1.setVisible(true);
					//window.frame2.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientLogin() {
		initialize();
		letter = new String[20][20];
		gameUI();
		inventation_window();
		user = new JSONObject();
		send = new JSONObject();
		userList = new JSONArray();
		matrix_manage = new MatrixManage(letter);

	}

	/**
	 * Initialize the contents of the frame.
	 */

	public void addUser(String userName) {
		userList.put(userName);
	}


	private void addChar(int x, int y, String letter) {

			this.letter[x][y] = letter;

	}

	/**
	 * create a socket to establish connection with server
	 */
	public void connection(String host, int port)throws ConnectException {

		try {
			socket = new Socket(host, port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			System.out.println("Connection with server established");
			connectionStatus = true;
			ml = new MessageListener(reader, writer, textArea, letter, grids, frame2, InvitationWindow,u_list);
			ml.start();
			clientName = ml.getClientName();
		}catch (Exception e) {

		}
	}
	/**
	 * GUI of the warning window
	 */

	public void warningWindow(String title, String info) {
		JFrame warning = new JFrame(title);
		warning.setVisible(true);
		warning.setBounds(250, 325, 200, 100);
		warning.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		warning.getContentPane().setLayout(null);

		JLabel lblPleaseConnectTo = new JLabel(info);
		lblPleaseConnectTo.setBounds(25, 6, 169, 34);
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


	public static void setReleaseBreak() {
		game_status = true;
		btnPass.setVisible(true);
	}
	
	public static void blockUsers() {
		game_status = false;
	}
	
	public static void setGameStart() {
		System.out.println("Start game");
		startGame = true;
	}
	
	public static void setGameOver() {
		startGame = false;
	}
	
	private void clearUserList() {
		for (int i=0; i < userList.length(); i++ ) {
			userList.remove(i);
			userList.remove(0);
		}
		for (int i=0; i < userList.length(); i++ ) {
			try {
				System.out.println("remain in the user list "+ userList.get(i).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * the GUI of the login window
	 */
	private void initialize() {
		frame1 = new JFrame("Log in");
		frame1.setBounds(100, 100, 450, 300);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.getContentPane().setLayout(null);

		txtIp = new JTextField();
		txtIp.setText("localhost");
		txtIp.setBounds(44, 86, 130, 26);
		frame1.getContentPane().add(txtIp);
		txtIp.setColumns(10);

		txtPort = new JTextField();
		txtPort.setText("4321");
		txtPort.setBounds(205, 86, 130, 26);
		frame1.getContentPane().add(txtPort);
		txtPort.setColumns(10);

		txtUsername = new JTextField();
		txtUsername.setBounds(44, 148, 130, 26);
		frame1.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);

		JButton btnLogIn = new JButton("Log in");
		btnLogIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					String host = txtIp.getText();
					int port = Integer.valueOf(txtPort.getText());
					System.out.println("extablish connection");
					connection(host, port);
					String userName = txtUsername.getText();
					writer.write(userName+"\n");
					writer.flush();
					System.out.println(userName);
				}catch (IOException e1) {
					// TODO: handle exception
					System.out.println("input error");
				}catch(Exception e2){
					System.out.println("connection error");
					warningWindow("Error", "connection error");
					connectionStatus = false;
				}
				
				if(connectionStatus) {
					InvitationWindow.setVisible(true);
					frame1.dispose();
				}
			}
		});
		btnLogIn.setBounds(205, 148, 117, 29);
		frame1.getContentPane().add(btnLogIn);

	}
	
	/**
	 * GUI of the inventation window
	 */
	
	private void inventation_window() {
		InvitationWindow = new JFrame();
		InvitationWindow.setBounds(100, 100, 500, 350);
		InvitationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		InvitationWindow.getContentPane().setLayout(null);
		
		JTextField Invite_name = new JTextField();
		Invite_name.setBounds(20, 284, 206, 26);
		InvitationWindow.getContentPane().add(Invite_name);
		Invite_name.setColumns(10);
		
		JLabel lblInvite = new JLabel("Invite ");
		lblInvite.setBounds(20, 269, 89, 16);
		InvitationWindow.getContentPane().add(lblInvite);
		
		JTextArea w_list = new JTextArea();
		w_list.setBounds(257, 39, 206, 219);
		InvitationWindow.getContentPane().add(w_list);
		
		JButton invite = new JButton("Invite");
		invite.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!startGame) {
					String userName = Invite_name.getText();
					Invite_name.setText("");
					boolean indicator = true;
					boolean isUserExist = false;
					userList.put(ml.getClientName());
					for(int i = 0; i<userList.length();i++) {
						try {
							System.out.println("hahahah"+ml.getClientName());
							if (ml.getClientName().equals(userName)) {
								indicator = false;
								warningWindow("DuplicateInvite", "Do not invite yourself");
								break;
							} else if (userList.get(i).equals(userName)) {
								indicator = false;
								warningWindow("DuplicateInvite", "Duplicate Invite");
								break;
							}
							
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					for(int i=0; i<ml.userList.size();i++) {
						if(ml.userList.get(i).equals(userName)) {
							isUserExist = true;
						}
					}
					if(isUserExist) {
						if (indicator) {
							addUser(userName);
							w_list.append(userName+"\n");
							System.out.println("INVITE");
						}
						
					}
					else {
						warningWindow("Error", "User does not exist");
					}
				}
				else {
					warningWindow("Cannot invite", "Game is running, Please wait");
				}
			
			}
		});
		invite.setBounds(257, 284, 89, 29);
		InvitationWindow.getContentPane().add(invite);
		JButton start = new JButton("START");
		start.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!startGame) {
					try {
						user.put("command", "START");
						user.put("user",userList);

					} catch (JSONException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					String command = user.toString();
					try {
						writer.write(command + "\n");
						writer.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("START");
					System.out.println(command);
					w_list.setText("");
					frame2.setVisible(true);
					InvitationWindow.dispose();
					clearUserList();
				}
				
				else {
					warningWindow("Cannot start game", "Cannot invite Game is running, Please wait");
				}
				
				
			}
		});
		start.setBounds(374, 284, 89, 29);
		InvitationWindow.getContentPane().add(start);
		
		u_list = new JTextArea();
		u_list.setBounds(20, 39, 206, 219);
		InvitationWindow.getContentPane().add(u_list);
		
		
		JLabel lblUserList = new JLabel("User List:");
		lblUserList.setBounds(20, 6, 61, 16);
		InvitationWindow.getContentPane().add(lblUserList);
		
		JLabel lblInvitingList = new JLabel("Inviting List:");
		lblInvitingList.setBounds(257, 6, 79, 16);
		InvitationWindow.getContentPane().add(lblInvitingList);
		
		
	}
	/**
	 * GUI of the game window
	 */

	private void gameUI() {

		frame2 = new JFrame();
		frame2.getContentPane().setBackground(new Color(238,238,238));
		frame2.setTitle(clientName);
		frame2.setBounds(100, 100, 800, 570);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.getContentPane().setLayout(null);


		grids = new Grids(1,1,letter);
		grids.setBounds(20, 20, 525, 525);
		frame2.getContentPane().add(grids);
		grids.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(game_status == true) {
					point_x=e.getX()/25;
					point_y=e.getY()/25;
					if(point_x == 20) {
						point_x = 19;
					}
					if(point_y == 20) { 
						point_y = 19;
					}
					if(matrix_manage.isValid(point_x, point_y)) {
						JFrame addChar = new JFrame("input a character");
						addChar.getContentPane().setLayout(null);
						addChar.setBounds(100,100,300,200);
						addChar.setVisible(true);
						JLabel info = new JLabel("input a letter at "+point_x+","+point_y);
						info.setBounds(26, 30, 150, 16);
						JTextField enter = new JTextField();
						enter.setBounds(179, 28, 77, 26);
						addChar.getContentPane().add(info);
						addChar.getContentPane().add(enter);
						JButton btnConfirm = new JButton("Submit");
						JRadioButton rdbtnHorizontal = new JRadioButton("Horizontal");
						JCheckBox rdbtnNonvote = new JCheckBox("Do not Vote");
						JRadioButton rdbtnVertical = new JRadioButton("Vertical");
						rdbtnNonvote.setBounds(47,105,141,23);
						addChar.getContentPane().add(rdbtnNonvote);
						ButtonGroup groupVote = new ButtonGroup();
						groupVote.add(rdbtnHorizontal);
						groupVote.add(rdbtnVertical);
						
//						rdbtnHorizontal.addItemListener(new ItemListener() {
//							public void itemStateChanged(ItemEvent e) {
//								if(rdbtnNonvote.isSelected()) {
//									rdbtnHorizontal.setEnabled(false);
//									rdbtnVertical.setEnabled(false);
//									groupVote.clearSelection();
//									direction = "N";
//								}
//								else {
//									rdbtnHorizontal.setEnabled(true);
//									rdbtnVertical.setEnabled(true);
//								}
//								
//							}
//						});
						
						rdbtnVertical.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent e) {
								if(rdbtnNonvote.isSelected()) {
									direction = "N";
									
								}
								
								else {
									if(rdbtnVertical.isSelected()) {
										direction = "V";
									}
								}
									
								System.out.println(direction);
							}
						});
						rdbtnVertical.setBounds(47, 85, 141, 23);
						addChar.getContentPane().add(rdbtnVertical);

						
						rdbtnHorizontal.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent e) {
								if(rdbtnNonvote.isSelected()) {
									direction = "N";
									
								}
								else {
									if(rdbtnHorizontal.isSelected()) {
										direction = "H";
				
									}
								}
									
								System.out.println(direction);
							}
						});
						
						rdbtnHorizontal.setBounds(47, 65, 141, 23);
						addChar.getContentPane().add(rdbtnHorizontal);
						
						
						
						
						//group.add(rdbtnNonvote);

						btnConfirm.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								character = enter.getText();
								if (matrix_manage.isValidInput(character)) {
									character = matrix_manage.modifyInput(character);
									addChar(point_x, point_y, character);
									grids.repaint();
									addChar.dispose();

									try {
										if (direction == null) {
											direction = "N";
										}
										if(rdbtnNonvote.isSelected()) {
											direction = "N";
										}
										System.out.println(direction+" sended as direction");
										send.put("command", "FILLCELL");
										send.put("direction", direction);
										send.put("x", Integer.toString(point_x));
										send.put("y", Integer.toString(point_y));
										send.put("letter", character);
										String command = send.toString();
										writer.write(command+"\n");
										writer.flush();
									} catch (JSONException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									blockUsers();
									frame2.getContentPane().setBackground(new Color(238,238,238));
									
								}
								else {
									warningWindow("Tips", "Invalid char! input again");
									addChar.dispose();
								}
							
								
							}
						});
						btnConfirm.setBounds(26, 140, 117, 29);
						addChar.getContentPane().add(btnConfirm);
						JButton btnCancel = new JButton("Cancel");
						btnCancel.setBounds(155, 140, 117, 29);
						addChar.getContentPane().add(btnCancel);
						btnCancel.addMouseListener(new MouseAdapter() {
							public void mousePressed(MouseEvent e) {
								addChar.dispose();
							}
						});
					}
					else {
						warningWindow("Invalid Enter", "Select another place!");
					}
				}
				else {
					warningWindow("Warning", "You cannot enter now!");
				}




			}
		});

		textArea = new JTextArea();
		textArea.setBounds(563, 53, 186, 400);
		frame2.getContentPane().add(textArea);
		textArea.setEditable(false);


		
		
		btnPass = new JButton("PASS");
		btnPass.setBounds(597, 307, 119, 29);
		frame2.getContentPane().add(btnPass);
		if(game_status) {
			btnPass.setVisible(true);
		}
		else {
			btnPass.setVisible(false);
		}
		btnPass.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(game_status) {
					try {
						send.put("command", "PASS");
						String command = send.toString();
						writer.write(command+"\n");
						writer.flush();
						
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}catch(IOException e1) {
						e1.printStackTrace();
					}
					btnPass.setVisible(false);
					frame2.getContentPane().setBackground(new Color(238,238,238));
				}
				
			}
		});
		

		JLabel lblUserList = new JLabel("Information Board");
		lblUserList.setBounds(563, 25, 136, 16);
		frame2.getContentPane().add(lblUserList);


		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(563, 53, 186, 203);
		frame2.getContentPane().add(scrollPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		

	}
}
