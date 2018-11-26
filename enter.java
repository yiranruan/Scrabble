package Client;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;

public class enter {

	private JFrame InvitationWindow;
	private JTextField Invite_name;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					enter window = new enter();
					window.InvitationWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public enter() {
		inventation_window();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void inventation_window() {
		InvitationWindow = new JFrame();
		InvitationWindow.setBounds(100, 100, 500, 350);
		InvitationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		InvitationWindow.getContentPane().setLayout(null);
		
		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		btnConfirm.setBounds(257, 284, 89, 29);
		InvitationWindow.getContentPane().add(btnConfirm);
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(374, 284, 89, 29);
		InvitationWindow.getContentPane().add(btnCancel);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(20, 39, 206, 219);
		InvitationWindow.getContentPane().add(textArea);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(257, 39, 206, 219);
		InvitationWindow.getContentPane().add(textArea_1);
		
		JLabel lblUserList = new JLabel("User List:");
		lblUserList.setBounds(20, 6, 61, 16);
		InvitationWindow.getContentPane().add(lblUserList);
		
		JLabel lblInvitingList = new JLabel("Inviting List:");
		lblInvitingList.setBounds(257, 6, 79, 16);
		InvitationWindow.getContentPane().add(lblInvitingList);
		
		Invite_name = new JTextField();
		Invite_name.setBounds(20, 284, 206, 26);
		InvitationWindow.getContentPane().add(Invite_name);
		Invite_name.setColumns(10);
		
		JLabel lblInvite = new JLabel("Invite ");
		lblInvite.setBounds(20, 269, 89, 16);
		InvitationWindow.getContentPane().add(lblInvite);
	}
}
