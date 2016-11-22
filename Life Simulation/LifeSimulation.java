import java.awt.*;
import javax.imageio.*; // allows image loading
import java.io.*; // allows file access
import javax.swing.*;
import java.awt.event.*;  // Needed for ActionListener

class LifeSimulation extends JFrame
{
    //declaration of constants and variables
    static Environment grid;
    static Movement moveColony; // KeyListener for timer
    static Timer t;
    int butPress=0;//increments when start button is pressed
    JEditorPane score= new JEditorPane();
    JComboBox levelbox;//declare JComboBox
    int chance=0;//increments when start button is pressed
    //======================================================== constructor
    public LifeSimulation ()
    {
        BtnListener btnListener = new BtnListener (); // listener for all buttons
        KeyboardListener keyListener = new KeyboardListener ();

        JButton startBtn = new JButton ("Start");
        startBtn.addActionListener (btnListener);
        startBtn.addKeyListener (keyListener);

        JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new BorderLayout ()); // Use BorderLayout for panel
        content.setBackground(Color.BLACK);
        JPanel north = new JPanel (); // where the buttons, etc. will be
        north.setLayout (new FlowLayout ()); // Use FlowLayout for input area
        north.setBackground(Color.BLACK);
        JPanel south = new JPanel (); // where the buttons, etc. will be
        south.setLayout (new FlowLayout ()); // Use FlowLayout for input area
        south.setBackground(Color.BLACK);

        //JComboBox initialization
        String levellist[]={"Easy","Medium","Hard"};
        levelbox = new JComboBox(levellist);
        levelbox.addActionListener(btnListener);//Creates a response

        DrawArea board = new DrawArea (700, 700); // Area for board to be displayed

        north.add (startBtn);
        north.add(levelbox);
        north.add(score);
        grid = new Environment ();//new object

        //content.add(south,"South");
        content.add (north, "North"); // Input area
        content.add (board, "South"); // board display area
        setContentPane (content);
        pack ();
        setTitle ("SURVIVAL");
        setSize (970, 800);
        setBackground(Color.BLACK); 
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);           // Center window.
    }

    class BtnListener implements ActionListener 
    {
        public void actionPerformed (ActionEvent e)
        {

            moveColony = new Movement (); // ActionListener for timer

            if (!e.getActionCommand ().equals ("Start"))
            {
                grid = new Environment (levelbox.getSelectedIndex());//new object
                chance++;//increment 
            }    

            if (e.getActionCommand ().equals ("Start"))
            {
                if(chance==0)
                    grid = new Environment (levelbox.getSelectedIndex());
                butPress++;
                t = new Timer (500, moveColony); // set up Movement to run every 500 milliseconds
                t.start (); // start simulation
                chance=0;//set to 0
            }

            repaint ();            // refresh display of colony
        }
    }

    class KeyboardListener implements KeyListener
    {
        public void keyPressed (KeyEvent e)
        {
            if(butPress>0 && grid.getPlayerRow()!=-1 && grid.getRows("AI").length!=0)
            {
                if ((e.getKeyCode () == KeyEvent.VK_UP) || (e.getKeyCode () == KeyEvent.VK_W)) // player moves up if up arrow key or w is pressed
                {
                    grid.playerMoveUp ();
                }
                else if ((e.getKeyCode () == KeyEvent.VK_DOWN) || (e.getKeyCode () == KeyEvent.VK_S)) // player moves down if down arrow key or s is pressed
                {
                    grid.playerMoveDown ();
                }
                else if ((e.getKeyCode () == KeyEvent.VK_RIGHT) || (e.getKeyCode () == KeyEvent.VK_D)) // player moves right if right arrow key or d is pressed
                {
                    grid.playerMoveRight ();
                }
                else if ((e.getKeyCode () == KeyEvent.VK_LEFT) || (e.getKeyCode () == KeyEvent.VK_A)) // player moves left if left arrow key or a is pressed
                {
                    grid.playerMoveLeft ();
                }
                else if (e.getKeyCode () == KeyEvent.VK_ENTER) // player drops tnt if enter is pressed
                {
                    grid.dropTNT ();
                }
                else if (e.getKeyCode () == KeyEvent.VK_T) // player drops tnt if enter is pressed
                {
                    grid.tnt ();
                }

                grid.checker (); // check the entire map to see if fighting/reproduction happens
                repaint (); // redraw everything
            }

        }

        public void keyReleased (KeyEvent e)
        {
        }

        public void keyTyped (KeyEvent e)
        {
        }
    }

    class DrawArea extends JPanel
    {
        public DrawArea (int width, int height)
        {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g)
        {
            if(!grid.allEmpty())
            {
                if (grid.getPlayerRow() != -1)//display in the JTextPane
                {
                    if (grid.getRows ("AI").length==0)
                    {
                        t.stop();
                        score.setText ("YOU WIN!");
                    }
                    else    
                        score.setText ("PLAYER\nHealth: " + grid.getPlayerHealth () + "\nSkill: " + grid.getPlayerSkill ());
                }
                else if (grid.getPlayerRow() == -1)
                {  
                    t.stop ();
                    score.setText ("Player Dead");
                }
                grid.show (g);//display grid
            }

            else
            {

                grid.show (g);//display grid
                //declaring fonts
                Font head = new Font("Bank Gothic", Font.BOLD, 55);
                Font dis = new Font("Arial", Font.BOLD + Font.ITALIC, 16);
                Color heading = new Color(153,0,76);
                Color sub = new Color(0,0,102);

                g.setColor(heading);
                g.setFont(head);

                //display welcome msg
                g.drawString("SELECT YOUR",140,150); 
                g.drawString("DIFFICULTY LEVEL",85,250); 
                g.drawString("AND",275,350); 
                g.drawString("PRESS START",150,450); 

                g.setFont(dis);
                g.setColor(sub);

                g.drawString("FIGHTING TIP",100,550); 
                g.drawString("THE GREATER YOUR HEALTH, THE GREATER THE CHANCE ",100,580); 
                g.drawString("OF WINNING IN A FIGHT",100,610); 
            }   
        }
    }

    class Movement implements ActionListener
    {
        public void actionPerformed (ActionEvent event)
        {
            grid.allAIMove (); // advance to the next time step
            repaint (); // refresh 
        }
    }
    //======================================================== method main
    public static void main (String[] args)
    {
        LifeSimulation window = new LifeSimulation ();
        window.setVisible (true);
    }
}