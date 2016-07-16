import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class LightsOutSolver extends JFrame implements MouseListener
{
	//constants
	static final int WIDTH = 500, HEIGHT = 500, ROW = 3, COL = 3, MAXN = ROW*COL, OFFX = 8, OFFY = 30, SIZE = 50, RADIUS = 5, OFF = (WIDTH - COL*SIZE)/2 - OFFX;
	//stores board
    boolean[][] board = new boolean[ROW][COL], solvedBoard = new boolean[ROW][COL];
    //editing or playing, solved or not
    boolean play = true, solved = false;
    //custom jpanel
	class GPanel extends JPanel //implements ActionListener
	{
		@Override
		public void paintComponent(Graphics g)
	    {
	        paintBoard(g);
	    }
	}
	//mouse stuff
	public void mouseClicked(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
    //user clicks
    public void mousePressed(MouseEvent e)
    {
    	//calculates which grid user clicked
    	int x = e.getX() - OFFX, y = e.getY() - OFFY;
    	if(x >= OFF && x <= COL * SIZE + OFF && y >= OFF && y <= COL * SIZE + OFF)
    	{
    		int r = (y - OFF) / SIZE, c = (x - OFF) / SIZE;
    		//play mode
    		if(play)
    		{
    			//update board to point
    			updateBoard(board, r, c);
    		}
    		//if the board is solved
    		if(solved)solvedBoard[r][c] ^= true;
    		boolean cleared = true;
    		//board cleared, turn off dots
    		for(int i = 0; i < ROW; i++)
    		for(int j = 0; j < COL; j++)if(board[i][j])cleared = false;
    		if(cleared)solved = false;
    	}
    	repaint();
    }
    //updates the board on click
    void updateBoard(boolean[][] board, int r, int c)
    {
    	board[r][c] ^= true;
    	if(r >= 1)board[r-1][c] ^= true;
		if(r <= ROW - 2)board[r+1][c] ^= true;
		if(c >= 1)board[r][c-1] ^= true;
		if(c <= COL - 2)board[r][c+1] ^= true;
    }
    //sets up panel
	public LightsOutSolver()
    {
        GPanel panel=new GPanel();
        setSize(WIDTH, HEIGHT);
        add(panel);
        //button stuff
        JButton newBtn = new JButton("New");
        JButton editBtn = new JButton("Edit"); 
        JButton playBtn = new JButton("Play");
        JButton solveBtn = new JButton("Solve");
        //more button stuff
        newBtn.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		//random grid
        		Random rand = new Random();
        		for(int i = 0; i < ROW; i++)
        		for(int j = 0; j < COL; j++)board[i][j] = rand.nextInt(2) == 1 ? true : false;
        		solved = false;
        		play = true;
        		repaint();
        	}
        });
        //edit mode
        editBtn.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){play = false; solved = false;}
        });
        //play mode
        playBtn.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){play = true; solved = false;}
        });
        //solves board
        solveBtn.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		solved = true;
        		play = true;
        		solvedBoard = solve(board);
        		//test if the solution is valid, if not is not solvable on size
        		if(verifyAnswer(solvedBoard, board))repaint();
        		else JOptionPane.showMessageDialog(null, "This board is not solvable due to dimensions");
        	}
        });
        //buttons stuff
        newBtn.setVisible(true);
        editBtn.setVisible(true);
        playBtn.setVisible(true);
        solveBtn.setVisible(true);
        panel.add(newBtn);
        panel.add(editBtn);
        panel.add(playBtn);
        panel.add(solveBtn);
        //mouse listener
        addMouseListener(this);
    }
	//verify if the board is solvable or not
	boolean verifyAnswer(boolean[][] answer, boolean[][] test)
	{
		boolean[][] tmpBoard = new boolean[ROW][COL];
		for(int i = 0; i < ROW; i++)
		for(int j = 0; j < COL; j++)tmpBoard[i][j] = test[i][j];
		for(int i = 0; i < ROW; i++)
		for(int j = 0; j < COL; j++)if(answer[i][j])updateBoard(tmpBoard, i, j);
		for(int i = 0; i < ROW; i++)
		for(int j = 0; j < COL; j++)if(tmpBoard[i][j])return false;
		return true;
	}
	//paints the board
	public void paintBoard(Graphics g)
	{
		for(int i = 0; i < ROW; i++)
			for(int j = 0; j < COL; j++)
			{
				//grid is on
				if(board[i][j])
				{
					g.setColor(Color.green);
					g.fillRect(OFF + j * SIZE, OFF + i * SIZE, SIZE, SIZE);
				}
				//grid is off
				else
				{
					g.setColor(Color.white);
					g.fillRect(OFF + j * SIZE, OFF + i * SIZE, SIZE, SIZE);
				}
				//solved mode and grid must be clicked
				if(solved && solvedBoard[i][j])
				{
					int x = OFF + j * SIZE + SIZE/2, y = OFF + i * SIZE + SIZE/2;
					g.setColor(Color.black);
					g.fillOval(x - RADIUS, y - RADIUS, RADIUS*2, RADIUS*2);
				}
			}
	}
	//returns solved board
	public boolean[][] solve(boolean[][] a)
	{
		//equations
		int[][] l = new int[MAXN][MAXN], r = new int[MAXN][MAXN];
		boolean[] e = new boolean[MAXN];
		int[] ptr = new int[MAXN], v = new int[MAXN];
		//set up equations
		for(int i = 0; i < ROW; i++)
		for(int j = 0; j < COL; j++)
		{
			int cur = i * COL + j;
			r[cur][cur] = 1;
			l[cur][cur] = 1;
			if(i >= 1)r[cur][cur - COL] = 1;
			if(i <= ROW-2)r[cur][cur + COL] = 1;
			if(j >= 1)r[cur][cur-1] = 1;
			if(j <= COL-2)r[cur][cur+1] = 1;
		}
		//x_i
		for(int i = 0; i < MAXN; i++)
		{
			//find equation with x_i
			for(int j = 0; j < MAXN; j++)if(!e[j] && r[j][i] == 1)
			{
				//mark equation
				e[j] = true;
				ptr[i] = j;
				//add equation
				for(int k = j + 1; k < MAXN; k++)if(!e[k] && r[k][i] == 1)
				{
					for(int m = 0; m < MAXN; m++)l[k][m] ^= l[j][m];
					for(int m = 0; m < MAXN; m++)r[k][m] ^= r[j][m];
				}
				break;
			}
		}
		boolean ok = true;
		for(int i = MAXN-1; i >= 0; i--)
		{
			//checks if solved or not
			int tot = 0, idx = ptr[i];
			if(idx == -1)
			{
				ok = false;
				break;
			}
			//solve for grid
			for(int j = 0; j < MAXN; j++)if(l[idx][j] == 1)tot ^= a[j/COL][j%COL] ? 1 : 0;
			for(int j = i+1; j < MAXN; j++)if(r[idx][j] == 1)tot ^= v[j];
			v[i] = tot;
			for(int j = 0; j < i; j++)if(r[idx][j] == 1)ok = false;
		}
		//return as 2d boolean array
		boolean [][] ret = new boolean[ROW][COL];
		for(int i = 0; i < ROW; i++)
		for(int j = 0; j < COL; j++)ret[i][j] = v[i*COL + j] == 1 ? true : false;
		return ret;
	}
    public static void main(String []args){
        LightsOutSolver s =new LightsOutSolver();
        s.setVisible(true);
    }
}