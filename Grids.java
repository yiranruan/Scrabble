/**
 *COMP90015:Distributed Systems
 *Assignment 2 – Team Project: A Distributed Game
 *@author BurningMyCalories
 *
 *Used to draw 20*20 grids on the game window.
 **/
package Client;
import java.awt.Color;
import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Toolkit;
//import java.util.Iterator;
//import java.util.Iterator;

import javax.swing.JPanel;



public class Grids extends JPanel {

	final static int ROW = 20;
	final static int SPAN = 25;

	private int x;
	private int y;
	private Graphics g;
	static private String[][] letter=null;
	private int [][] highlight = null;


	public Grids(int x, int y, String[][] letter) {
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
		this.highlight = new int[20][20];
		resetHighlight();
		this.letter = new String[20][20];
		this.letter = letter;


	}
	/**
	 * use to highlight the selected cell
	 */

	public void setHightlight(int x, int y,int val) {
		highlight[x][y]=val;
	}

	/**
	 * reset highlighted cells
	 */
	public void resetHighlight() {
		for(int i=0; i < 20; i++) {
			for (int j=0; j<20; j++) {
				highlight[i][j]=0;

			}
		}
	}

	public void drawGrid(int x,int y,Graphics g) {

		for(int i=0;i<=ROW;i++)
		{
			g.drawLine(x, y+i*SPAN, SPAN*ROW,y+i*SPAN);
		}

		for(int i=0;i<=ROW;i++)
		{
			g.drawLine(x+i*SPAN, y, x+i*SPAN,SPAN*ROW);
		}

		for (int i=0; i<ROW; i++) {
			for (int j=0; j<ROW; j++) {


				if(highlight[i][j]!=1) {
					if(letter[i][j] != null) {
						drawStr(i, j, letter[i][j].toString(), g);
					}
					continue;
				}
				else {
					color(i, j, g);
					if(letter[i][j] != null) {
						drawStr(i, j, letter[i][j].toString(), g);
					}
				}


			}
		}




	}
	/**
	 * 
	 * @param x coordinate on x-axis
	 * @param y coordinate on y-axis
	 * @param letter: the character which needs to write on grids
	 * @param g
	 */

	public void drawStr(int x, int y, String letter, Graphics g) {
		int point_x = 10 + x*SPAN;
		int point_y = 17 + y*SPAN;
		g.setColor(Color.black);
		g.drawString(letter, point_x, point_y);

	}

	public void color(int x, int y, Graphics g) {
		g.setColor(Color.yellow);
		g.fillRect(1+x*SPAN,1+ y*SPAN, SPAN, SPAN);
	}


	public void paint(Graphics g) {
		super.paint(g);
		this.drawGrid(x, y, g);
	}




}
