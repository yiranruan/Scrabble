/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Initialize the game server process
 **/
package Server;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;

public class ServerGUI {

	private JFrame frame;
	private JTextField textField;
	private GameServer server = null;
	private static JTextArea textArea;
	private static JTextArea textArea_1;

	public static void main(String[] args) {
		//Launch the application
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI(args);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ServerGUI(String[] args) {
		//Create the application
		initialize(args);
	}

	private void initialize(String[] args) {
		//Initialize the contents of the frame
		frame = new JFrame();
		frame.setBounds(200, 200, 775, 631);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblLog = new JLabel("Connection Log");
		lblLog.setBounds(40, 84, 107, 14);
		frame.getContentPane().add(lblLog);

		textArea = new JTextArea();
		textArea.setBounds(40, 109, 320, 453);
		frame.getContentPane().add(textArea);
		textArea.setEditable(false);

		textArea_1 = new JTextArea();
		textArea_1.setBounds(414, 109, 320, 453);
		frame.getContentPane().add(textArea_1);
		textArea_1.setEditable(false);

		JLabel lblUserlist = new JLabel("UserList");
		lblUserlist.setBounds(414, 84, 86, 14);
		frame.getContentPane().add(lblUserlist);

		textField = new JTextField();
		textField.setBounds(320, 24, 86, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		try {
			textField.setText(args[0]);
		} catch (Exception e) {
			textField.setText("4321");
			textArea.append("Default port number used: 4321\n");
		}

		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(283, 27, 27, 14);
		frame.getContentPane().add(lblPort);

		JButton btnEnter = new JButton("Enter");
		btnEnter.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Thread threadServer;
				try {
					if (btnEnter.getText().equals("Enter")) {
						server = new GameServer(Integer.parseInt(textField.getText()));
						textArea.append("Used port number used: "+textField.getText()+"\n");
						threadServer = new Thread(server);
						threadServer.start();
						btnEnter.setText("Exit");
					} else if (btnEnter.getText().equals("Exit")) {
						server.stopServer();
						ClientManager.getInstance().reset(); //reset game history
						textArea_1.setText("");
						//threadServer.kill();
						btnEnter.setText("Enter");
					}
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, nfe.getMessage() +", Invalid", "Empty Port Error",
							JOptionPane.ERROR_MESSAGE);
				} catch (NullPointerException npe) {
					textArea.append(npe.getMessage());
				}

			}
		});
		btnEnter.setBounds(450, 23, 89, 23);
		frame.getContentPane().add(btnEnter);

		JScrollPane scrollPane = new JScrollPane(textArea);/////////wait
		scrollPane.setBounds(40, 109, 320, 453);
		frame.getContentPane().add(scrollPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JScrollPane scrollPane_1 = new JScrollPane(textArea_1);
		scrollPane_1.setBounds(414, 109, 320, 453);
		frame.getContentPane().add(scrollPane_1);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				JLabel lblConnectionNumber = new JLabel();
				lblConnectionNumber.setBounds(600, 86, 133, 14);
				frame.getContentPane().add(lblConnectionNumber);
				while(true) {
					if (Thread.activeCount()-4<0) {
						count = 0;
					}else {
						count = Thread.activeCount()-4;
					}
					lblConnectionNumber.setText("Client Number: "+count);
				}
			}
		}).start();
	}

	public synchronized static void showLog(String t) {
		//show log on server GUI
		textArea.append(t+"\n");
	}

	public synchronized static void showPlayer(String t){
		//show current user list on server GUI
		textArea_1.append(t+"\n");
	}

	public synchronized static void clearPleayer() {
		//reset user list to none
		textArea_1.setText("");
	}
}
