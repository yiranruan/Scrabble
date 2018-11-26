/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Initialize the 2D matrix; it is used to store and process characters which are input by players
 **/
package Client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MatrixManage {

	private String[][] matrix;

	public MatrixManage(String[][] matrix) {
		// TODO Auto-generated constructor stub
		this.matrix = new String[20][20];
		this.matrix = matrix;
	}

	public boolean isValid(int x, int y) {
		if(matrix[x][y]==null) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * test input character whether is valid; player should input English letter from a to z.
	 */
	public boolean isValidInput(String input) {
		input = modifyInput(input);
		String regExp = "[A-Z]";
		Pattern pattern = Pattern.compile(regExp);
		Matcher matcher = pattern.matcher(input);
		boolean re = matcher.matches();
		return re;
	}

	public void addValue(int x, int y,String value) {
		matrix[x][y] = value;

	}
	
	public void warningWindow(String title, String info) {
		JFrame warning = new JFrame(title);
		warning.setVisible(true);
		warning.setBounds(250, 325, 200, 100);
		warning.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		warning.getContentPane().setLayout(null);

		JLabel lblPleaseConnectTo = new JLabel(info);
		lblPleaseConnectTo.setBounds(25, 6, 200, 34);
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

	/**
	 * 
	 * @param if player input more than one letters and in lower case
	 * @return the first letter will be output; and the lower case letter will be transfer to upper case
	 */
	public String modifyInput(String input) {
		String output = input;
		if(output.length()>1) {
			output = output.substring(0,1);
			warningWindow("Warning", "The first letter is submitted");

		}
		output = output.toUpperCase();
		return output;
	}
	
	public void resetMatix() {
		for (int i = 0 ; i < 20; i++) {
			for (int j = 0; j < 20 ; j++) {
				matrix[i][j] = null;
			}
		}
	}

}
