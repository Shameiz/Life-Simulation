import sun.audio.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

class Environment
{
    private Lifeform grid [] []; //declaration
    String gametype;//level

    public Environment () // default constructor
    {
        grid = new Lifeform [15] [15]; // create 15x15 grid
        for (int row = 0 ; row < grid.length ; row++) // go through each row
        {   
            for (int col = 0 ; col < grid [0].length ; col++) // each item in row
            {
                grid [row] [col] = new Lifeform (); // 75% to generate a blank space
            }
        }

    }

    public Environment (int num) // initialize life forms
    {
        grid = new Lifeform [15] [15]; // create 15x15 grid
        int rand;
        int count=0;
        //level of game
        if(num==0)
            gametype="Easy";

        else if(num==1)
            gametype="Medium";

        else
            gametype="Hard";

        for (int row = 0 ; row < grid.length ; row++) // go through each row
        {   
            for (int col = 0 ; col < grid [0].length ; col++) // each item in row
            {
                rand = (int) (Math.random () * 100 + 1); // generates random number between 1-100
                if (rand >= 1 && rand <= 10)
                    grid [row] [col] = new Lifeform ("Food"); // 10% chance of generating food
                else if (rand >= 11 && rand <= 20)
                    grid [row] [col] = new Lifeform ("Rock"); // 10% chance of generating an obstacle
                else if (rand >= 21 && rand <= 23+num)
                    grid [row] [col] = new Lifeform ("AI"); // max 5% chance of generating an AI
                else
                    grid [row] [col] = new Lifeform (); // 75% to generate a blank space
            }
        }

        for (int row = 0 ; row < grid.length ; row++)//better spawning of player
        {
            for (int col = 0 ; col < grid [0].length ; col++)
            {
                if (checkUp (row, col).equals ("Space") && checkUp (row+1, col).equals ("Space") && checkDown (row, col).equals ("Space") && checkDown (row-1, col).equals ("Space") && checkRight (row, col).equals ("Space") && checkRight (row, col+1).equals ("Space") && checkLeft (row, col).equals ("Space") && checkLeft (row, col-1).equals ("Space")) // goes through the environment to find an empty spot to place the player
                {
                    count++;
                    grid [row] [col] = new Lifeform ("Player");
                    col = 100;
                    row = 100;
                }
            }
        }

        if(count==0)//spawning of player
        {
            for (int row = 0 ; row < grid.length ; row++)
            {
                for (int col = 0 ; col < grid [0].length ; col++)
                {
                    if (checkUp (row, col).equals ("Space") && checkDown (row, col).equals ("Space") && checkRight (row, col).equals ("Space") && checkLeft (row, col).equals ("Space")) // goes through the environment to find an empty spot to place the player
                    {
                        grid [row] [col] = new Lifeform ("Player");
                        col = 100;
                        row = 100;
                    }
                }
            }
        }
    }

    public void show (Graphics g) // called from paintComponent of GUI
    {
        for (int row = 0 ; row < grid.length ; row++)//displays lifeform and health
        {
            for (int col = 0 ; col < grid [0].length ; col++)
            {
                grid [row] [col].show (g, col * 40 + 40, row * 40 + 50);
                grid [row] [col].showHealth (g, col * 40 + 45, row * 40 + 50);
            }
        }

        //displays menu on the left side of the screen
        Font dis = new Font("Arial", Font.BOLD, 16);
        Font head = new Font("Bank Gothic", Font.BOLD, 48);
        Color heading = new Color(255,128,0);

        g.setColor(heading);
        g.setFont(head);

        g.drawString("SURVIVAL ",670,100);
        g.drawLine(640,120,950,120);

        g.setColor(Color.white);
        g.setFont(dis);

        g.drawString("PLAYER MOVEMENT ",650,160);
        g.drawImage(picture("arrowkeys"),900,130,null);

        g.drawString("PRESS ENTER TO DROP ",650,220);
        g.drawImage(picture("TNT"),900,190,null);

        g.drawString("PRESS T: MASS EXTINCTION",650,280);
        g.drawImage(picture("TNT"),900,250,null);

        g.drawString("CONFRONT GHOST TO FIGHT",650,340);
        g.drawImage(picture("playerUP"),900,310,null);

        g.drawString("EAT TO INCREASE HEALTH",650,400);
        g.drawImage(picture("Food"),900,370,null);

        g.drawString("PLAYER",650,460);
        g.drawImage(picture("playerUP"),900,430,null);

        g.drawString("GHOST SKILL 1",650,520);
        g.drawImage(picture("ghost1"),900,490,null);

        g.drawString("GHOST SKILL 2",650,580);
        g.drawImage(picture("ghost2"),900,550,null);

        g.drawString("GHOST SKILL 3",650,640);
        g.drawImage(picture("ghost3"),900,610,null);

    }

    public Image picture (String name)//method that returns image 
    {
        Image image=null;
        try
        {
            image = ImageIO.read (new File ("Images"+ File.separator + name + ".gif")); // load file into Image object

        }
        catch (IOException e)
        {
            System.out.println ("File not found pic");
        }
        return image;
    }

    public void playSound(String name) //plays sound
    {
        AudioPlayer MGP = AudioPlayer.player;//creates audio player
        AudioStream BGM;
        AudioData MD;

        try
        {
            InputStream test = new FileInputStream(name+".wav");
            BGM = new AudioStream(test);
            AudioPlayer.player.start(BGM);//starts playing audio

        }
        catch(FileNotFoundException e){
            System.out.print(e.toString());
        }
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
    }

    public boolean playerMoveUp () // moves player up if there is nothing above except for food
    {
        int row = getPlayerRow (), col = getPlayerCol (); // gets the row and column that the player is in

        if (row > 0)
        {
            playerChecker (); // checks for tnt around the player
            if ((grid [row - 1] [col].getType ().equals ("Space")) || (grid [row - 1] [col].getType ().equals ("Food")))
            {
                if (grid [row - 1] [col].getType ().equals ("Food")) // increases health and changes image if the player is able to move up
                {    
                    grid [row] [col].eat ();
                    playSound("food");//play eat sound
                }
                grid [row] [col].setOrientation ("Up");
                grid [row] [col].changeState (1);
                grid [row - 1] [col] = grid [row] [col];
                grid [row] [col] = new Lifeform ();
            }
            return true; // returns true if moved
        }
        return false; // returns false if not moved
    }

    public boolean playerMoveDown () // moves player down if there is nothing below except for food
    {
        int row = getPlayerRow (), col = getPlayerCol (); // gets the row and column that the player is in

        if (row < grid.length - 1)
        {
            playerChecker (); // checks for tnt around the player
            if ((grid [row + 1] [col].getType ().equals ("Space")) || (grid [row + 1] [col].getType ().equals ("Food")))
            {
                if (grid [row + 1] [col].getType ().equals ("Food")) // increases health and changes image if the player is able to move down
                {    
                    grid [row] [col].eat ();
                    playSound("food");//play eat sound
                }
                grid [row] [col].setOrientation ("Down");
                grid [row] [col].changeState (2);
                grid [row + 1] [col] = grid [row] [col];
                grid [row] [col] = new Lifeform ();
            }
            return true; // returns true if moved
        }
        return false; // returns false if not moved
    }

    public boolean playerMoveRight () // moves player to the right if there is nothing to the right except for food
    {
        int row = getPlayerRow (), col = getPlayerCol (); // gets the row and column that the player is in

        if (col < grid [0].length - 1)
        {
            playerChecker (); // checks for tnt around the player
            if ((grid [row] [col + 1].getType ().equals ("Space")) || (grid [row] [col + 1].getType ().equals ("Food")))
            {
                if (grid [row] [col + 1].getType ().equals ("Food")) // increases health and changes image if the player is able to move right
                {    
                    grid [row] [col].eat ();
                    playSound("food");//play eat sound
                }
                grid [row] [col].setOrientation ("Right");
                grid [row] [col].changeState (3);
                grid [row] [col + 1] = grid [row] [col];
                grid [row] [col] = new Lifeform ();
            }
            return true; // returns true if moved
        }
        return false; // returns false if not moved
    }

    public boolean playerMoveLeft () // moves player to the left if there is nothing to the left except for food
    {
        int row = getPlayerRow (), col = getPlayerCol (); // gets the row and column that the player is in

        if (col > 0)
        {
            playerChecker (); // checks for tnt around the player
            if ((grid [row] [col - 1].getType ().equals ("Space")) || (grid [row] [col - 1].getType ().equals ("Food")))
            {
                if (grid [row] [col - 1].getType ().equals ("Food")) // increases health and changes image if the player is able to move left
                {    
                    grid [row] [col].eat ();
                    playSound("food");//play eat sound
                }
                grid [row] [col].setOrientation ("Left");
                grid [row] [col].changeState (4);
                grid [row] [col - 1] = grid [row] [col];
                grid [row] [col] = new Lifeform ();
            }
            return true; // returns true if moved
        }
        return false; // returns false if not moved
    }

    public int getPlayerRow () // returns the row where the player is
    {
        for (int row = 0 ; row < grid.length ; row++)
        {
            for (int col = 0 ; col < grid [0].length ; col++)
            {
                if (grid [row] [col].getType ().equals ("Player")) // finds the player and returns the row
                    return row;
            }
        }
        return -1; // returns -1 if player is not found
    }

    public int getPlayerCol () // returns the column where the player is
    {
        for (int row = 0 ; row < grid.length ; row++)
        {
            for (int col = 0 ; col < grid [0].length ; col++)
            {
                if (grid [row] [col].getType ().equals ("Player")) // finds the player and returns the column
                    return col;
            }
        }
        return -1; // returns -1 if player is not found
    }

    public void rockMove ()
    {
        int row [] = getRows ("Rock"); // gets the rows where there are obstacles
        int col [] = getCols ("Rock"); // gets the columns where there are obstacles
        int rand;
        String checkRight, checkLeft;

        for (int x = 0 ; x < row.length ; x++)
        {
            checkRight = checkRight (row [x], col [x]); // checks to see what's on the right of the obstacle
            checkLeft = checkLeft (row [x], col [x]); // checks to see what's on the left of the obstacle
            rand = (int) (Math.random () * 100 + 1); // generates random number between 1-100
            if (rand >= 1 && rand <= 50) // 50% chance for the obstacle to move from side to side
            {
                if ((rand >= 1 && rand <= 25) && checkRight.equals ("Space")) // 25% chance for it to move right
                {
                    grid [row [x]] [col [x] + 1] = new Lifeform ("Rock");
                    grid [row [x]] [col [x]] = new Lifeform ();
                }
                else if ((rand >= 26 && rand <= 50) && checkLeft.equals ("Space")) // 25% chance for it to move left
                {
                    grid [row [x]] [col [x] - 1] = new Lifeform ("Rock");
                    grid [row [x]] [col [x]] = new Lifeform ();
                }
            }
        }
    }

    public void allAIMove ()
    {
        int row [] = getRows ("AI"); // gets the rows where there are AI
        int col [] = getCols ("AI"); // gets the columns where there are AI
        int rp = getPlayerRow (); // gets the row where the player is
        int cp = getPlayerCol (); // gets the column where the player is
        int rand, rand2, rowdif, coldif;

        for (int x = 0 ;  x < row.length ; x++)
        {
            while (grid [row [x]] [col [x]].getType ().equals ("AI"))
            {
                rowdif = row [x] - rp;//row difference between player and AI
                coldif = col [x] - cp;//column difference between player and AI
                rand = (int) (Math.random () * 10 + 1); // generates a random from 1-10
                if (rand >= 1 && rand <= 5) // 50% chance for the AI to move randomly
                {
                    if (rand == 1)
                        moveAIUp (row [x], col [x]); // tries to move the AI up
                    else if (rand == 2)
                        moveAIDown (row [x], col [x]); // tries to move the AI down
                    else if (rand == 3)
                        moveAIRight (row [x], col [x]); // tries to move the AI right
                    else if (rand == 4)
                        moveAILeft (row [x], col [x]); // tries to move the AI left
                }
                else // 50% chance for the AI to move towards the player
                {
                    rand2 = (int) (Math.random () * 2 + 1); 
                    if(rand2==1)
                    {
                        if(rowdif>0)
                            moveAIUp (row [x], col [x]);
                        else
                            moveAIDown (row [x], col [x]);

                    }

                    else
                    {
                        if(coldif>0)
                            moveAILeft (row [x], col [x]);
                        else
                            moveAIRight(row [x], col [x]);
                    }   
                }   
            }
        }
        int r = (int) (Math.random () * (grid.length - 1)); // generates a random number from 0-row length
        int c = (int) (Math.random () * (grid [0].length - 1)); // generates a random number from 0-column length
        addFood (r, c); // tries to add food
        r = (int) (Math.random () * (grid.length - 1)); // generates a random number from 0-row length
        c = (int) (Math.random () * (grid [0].length - 1)); // generates a random number from 0-column length
        addTNT (r, c); // tries to add tnt
        rockMove (); // calls the method to move obstacles randomly
        checker (); // check the entire map to see if fighting/reproduction happens
    }

    public int [] getRows (String str) // finds and returns the row of a certain type of lifeform
    {
        int counter = 0;

        for (int row = 0 ; row < grid.length ; row++)
        {
            for (int col = 0 ; col < grid [0].length ; col++)
            {
                if (grid [row] [col].getType ().equals (str))
                    counter++; // finds how many occurences there are of such lifeform
            }
        }
        int row [] = new int [counter]; // makes a new array of the size of occurences
        for (int row0 = 0 ; row0 < grid.length ; row0++)
        {
            for (int col0 = 0 ; col0 < grid [0].length ; col0++)
            {
                if (grid [row0] [col0].getType ().equals (str))
                {
                    counter--;
                    row [counter] = row0; // adds the row where it finds the occurences of such lifeform
                }
            }
        }
        return row; // returns an array of rows where such lifeform was found
    }

    public int [] getCols (String str) // finds and returns the column of a certain type of lifeform
    {
        int counter = 0;

        for (int row = 0 ; row < grid.length ; row++)
        {
            for (int col = 0 ; col < grid [0].length ; col++)
            {
                if (grid [row] [col].getType ().equals (str))
                    counter++; // finds how many occurences there are of such lifeform
            }
        }
        int col [] = new int [counter]; // makes a new array of the size of occurences
        for (int row0 = 0 ; row0 < grid.length ; row0++)
        {
            for (int col0 = 0 ; col0 < grid [0].length ; col0++)
            {
                if (grid [row0] [col0].getType ().equals (str))
                {
                    counter--;
                    col [counter] = col0; // adds the column where it finds the occurences of such lifeform
                }
            }
        }
        return col; // returns an array of columns where such lifeform was found
    }

    public void addTNT (int row, int col) // adds tnt to the row and column accepted
    {
        int rand = (int) (Math.random () * 100 + 1); // generates a random number from 1-100
        if (rand >= 1 && rand <= 10 && grid [row] [col].getType ().equals ("Space")) // 10% chance for TNT to be spawned
            grid [row] [col] = new Lifeform ("TNT");
    }

    public void addFood (int row, int col) // adds food to the row and column accepted
    {
        int rand = (int) (Math.random () * 100 + 1); // generates a random number from 1-100
        if (rand >= 1 && rand <= 5 && grid [row] [col].getType ().equals ("Space")) // 5% chance for food to be spawned
            grid [row] [col] = new Lifeform ("Food");
    }

    public void moveAIUp (int row, int col) // moves AI up if there is nothing up except for food
    {
        if (row > 0)
        {
            if ((grid [row - 1] [col].getType ().equals ("Space")) || (grid [row - 1] [col].getType ().equals ("Food")))
            {
                if (grid [row - 1] [col].getType ().equals ("Food")) // increases health and moves the AI up if it is clear
                    grid [row] [col].eat ();
                grid [row - 1] [col] = grid [row] [col];
                grid [row] [col] = new Lifeform ();
            }
        }
    }   

    public void moveAIDown (int row, int col) // moves AI down if there is nothing down except for food
    {
        if (row < grid.length - 1)
        {
            if ((grid [row + 1] [col].getType ().equals ("Space")) || (grid [row + 1] [col].getType ().equals ("Food")))
            {
                if (grid [row + 1] [col].getType ().equals ("Food")) // increases health and moves the AI down if it is clear
                    grid [row] [col].eat ();
                grid [row + 1] [col] = grid [row] [col];
                grid [row] [col] = new Lifeform ();
            }
        }
    }   

    public void moveAIRight (int row, int col) // moves AI to the right if there is nothing to the right except for food
    {
        if (col < grid [0].length - 1)
        {
            if ((grid [row] [col + 1].getType ().equals ("Space")) || (grid [row] [col + 1].getType ().equals ("Food")))
            {
                if (grid [row] [col + 1].getType ().equals ("Food")) // increases health and moves the AI to the right if it is clear
                    grid [row] [col].eat ();
                grid [row] [col + 1] = grid [row] [col];
                grid [row] [col] = new Lifeform ();
            }
        }
    }

    public void moveAILeft (int row, int col) // moves AI to the left if there is nothing to the left except for food
    {
        if (col > 0)
        {
            if ((grid [row] [col - 1].getType ().equals ("Space")) || (grid [row] [col - 1].getType ().equals ("Food")))
            {
                if (grid [row] [col - 1].getType ().equals ("Food")) // increases health and moves the AI to the left if it is clear
                    grid [row] [col].eat ();
                grid [row] [col - 1] = grid [row] [col];
                grid [row] [col] = new Lifeform ();
            }
        }
    }

    public void checker ()
    {
        int row [] = getRows ("AI"); // gets the rows where there are AI
        int col [] = getCols ("AI"); // gets the columns where there are AI
        String checkUp, checkDown, checkRight, checkLeft;
        double repNo;//percent chance of reproduction

        //assigning repNo a value
        if(gametype.equals("Easy"))
            repNo=0.3;

        else if(gametype.equals("Medium"))
            repNo=0.2;

        else 
            repNo=0.1;

        for (int x = 0 ; x < row.length ; x++)
        {
            checkUp = checkUp (row [x], col [x]); // checks what is above the AI
            checkDown = checkDown (row [x], col [x]); // checks what is below the AI
            checkRight = checkRight (row [x], col [x]); // checks what is to the right of the AI
            checkLeft = checkLeft (row [x], col [x]); // checks what is to the left of the AI
            if (checkUp.equals ("TNT")) // AI loses health is tnt is above
            {
                grid [row [x]] [col [x]].loseHealth ();
                playSound("explosion");//plays explosion sound
                grid [row [x] - 1] [col [x]] = new Lifeform ();
            }
            else if ((checkUp.equals ("AI")) || (checkUp.equals ("Player")))
            {
                if ((grid [row [x] - 1] [col [x]].getSkill () != grid [row [x]] [col [x]].getSkill ()) || checkUp.equals ("Player")) // AI fights if there is a player/different AI above
                {    
                    if(checkUp.equals ("Player"))
                        playSound("battle");//play battle sound
                    grid [row [x] - 1] [col [x]] = grid [row [x]] [col [x]].fight (grid [row [x] - 1] [col [x]]);
                }
                else // AI reproduces if there is a same AI above (70-90% chance)
                {    
                    if (Math.random() > repNo)
                        reproduce (row [x], col [x]);
                }
            }
            if (checkDown.equals ("TNT")) // AI loses health is tnt is below
            {
                grid [row [x]] [col [x]].loseHealth ();
                playSound("explosion");//plays explosion sound
                grid [row [x] + 1] [col [x]] = new Lifeform ();
            }
            else if ((checkDown.equals ("AI")) || (checkDown.equals ("Player")))
            {
                if ((grid [row [x] + 1] [col [x]].getSkill () != grid [row [x]] [col [x]].getSkill ()) || checkDown.equals ("Player")) // AI fights if there is a player/different AI below
                {   
                    if(checkDown.equals ("Player"))
                        playSound("battle");//play battle sound
                    grid [row [x] + 1] [col [x]] = grid [row [x]] [col [x]].fight (grid [row [x] + 1] [col [x]]);
                }
                else // AI reproduces if there is a same AI above (70-90% chance)
                {    
                    if (Math.random() > repNo)
                        reproduce (row [x], col [x]);
                }
            }
            if (checkRight.equals ("TNT")) // AI loses health is tnt is to the right
            {
                grid [row [x]] [col [x]].loseHealth ();
                playSound("explosion");//plays explosion sound
                grid [row [x]] [col [x] + 1] = new Lifeform ();
            }
            else if ((checkRight.equals ("AI")) || (checkRight.equals ("Player")))
            {
                if ((grid [row [x]] [col [x] + 1].getSkill () != grid [row [x]] [col [x]].getSkill ()) || checkRight.equals ("Player")) // AI fights if there is a player/different AI to the right
                {    
                    if(checkRight.equals ("Player"))
                        playSound("battle");//play battle sound
                    grid [row [x]] [col [x] + 1] = grid [row [x]] [col [x]].fight (grid [row [x]] [col [x] + 1]);
                }
                else // AI reproduces if there is a same AI above (70-90% chance)
                {    
                    if (Math.random() > repNo)
                        reproduce (row [x], col [x]);
                }
            }
            if (checkLeft.equals ("TNT")) // AI loses health is tnt is to the left
            {
                grid [row [x]] [col [x]].loseHealth ();
                playSound("explosion");//plays explosion sound;
                grid [row [x]] [col [x] - 1] = new Lifeform ();
            }
            else if ((checkLeft.equals ("AI")) || (checkLeft.equals ("Player")))
            {
                if ((grid [row [x]] [col [x] - 1].getSkill () != grid [row [x]] [col [x]].getSkill ()) || checkLeft.equals ("Player")) // AI fights if there is a player/different AI to the left
                {
                    if(checkLeft.equals ("Player"))
                        playSound("battle");//play battle sound
                    grid [row [x]] [col [x] - 1] = grid [row [x]] [col [x]].fight (grid [row [x]] [col [x] - 1]);
                }
                else // AI reproduces if there is a same AI above (70-90% chance)
                {    
                    if (Math.random() > repNo)
                        reproduce (row [x], col [x]);
                }
            }
            if (grid [row [x]] [col [x]].getHealth () <= 0) // removes the AI from the map if it's health is 0 or below
                grid [row [x]] [col [x]] = new Lifeform ();
        }
    }

    public void reproduce (int row, int col)
    {
        int count=0;
        for ( ; row < grid.length ; row++)
        {
            for ( ; col < grid [0].length ; col++)
            {
                if (checkUp (row, col).equals ("Space") && checkDown (row, col).equals ("Space") && checkRight (row, col).equals ("Space") && checkLeft (row, col).equals ("Space"))//if space is available with four spots arounf empty
                {
                    grid [row] [col] = new Lifeform ("Baby");//create new lifeform
                    count++;
                    col = 100;
                    row = 100;
                }
            }
        }
        if (count==0)
        {
            for (row = 0 ; row < grid.length ; row++)
            {
                for (col = 0 ; col < grid [0].length ; col++)
                {
                    if (checkUp (row, col).equals ("Space") && checkDown (row, col).equals ("Space") && checkRight (row, col).equals ("Space") && checkLeft (row, col).equals ("Space"))//if space is available with four spots arounf empty
                    {
                        grid [row] [col] = new Lifeform ("Baby");//create new lifeform

                        col = 100;
                        row = 100;
                    }
                }
            }
        }
    }   

    public void playerChecker()//checking around player
    {
        String checkUp, checkDown, checkRight, checkLeft;

        int row = getPlayerRow (), col = getPlayerCol ();

        checkUp = checkUp (row,col);
        checkDown = checkDown (row,col);
        checkRight = checkRight (row,col);
        checkLeft = checkLeft (row,col);

        if (checkUp.equals ("TNT") && grid[row][col].getState()!=2)//affects the player if tnt is not behind player
        {
            grid [row] [col].loseHealth ();
            playSound("explosion");//plays explosion sound
            grid [row - 1] [col] = new Lifeform ();
        }

        if (checkDown.equals ("TNT")&& grid[row][col].getState()!=1)//affects the player if tnt is not behind player
        {
            grid [row] [col].loseHealth ();
            playSound("explosion");//plays explosion sound
            grid [row + 1] [col] = new Lifeform ();
        }

        if (checkRight.equals ("TNT")&& grid[row][col].getState()!=4)//affects the player if tnt is not behind player
        {
            grid [row] [col].loseHealth ();
            playSound("explosion");//plays explosion sound
            grid [row] [col + 1] = new Lifeform ();
        }

        if (checkLeft.equals ("TNT")&& grid[row][col].getState()!=3)//affects the player if tnt is not behind player
        {
            grid [row] [col].loseHealth ();
            playSound("explosion");//plays explosion sound
            grid [row] [col- 1] = new Lifeform ();
        }

        if (grid [row] [col].getHealth () <= 0) // removes the player from the map if it's health is 0 or below
            grid [row] [col] = new Lifeform ();

    }   

    public String checkUp (int row, int col) // accept a row and column and returns what is above
    {
        if (row > 0)
            return grid [row - 1] [col].getType (); // returns the type of lifeform above
        else
            return "None"; // if out of bounds, returns "None"
    }

    public String checkDown (int row, int col) // accept a row and column and returns what is below
    {
        if (row < grid.length - 1)
            return grid [row + 1] [col].getType (); // returns the type of lifeform below
        else
            return "None"; // if out of bounds, returns "None"
    }

    public String checkRight (int row, int col) // accept a row and column and returns what is to the right
    {
        if (col < grid [0].length - 1)
            return grid [row] [col + 1].getType (); // returns the type of lifeform to the right
        else
            return "None"; // if out of bounds, returns "None"
    }

    public String checkLeft (int row, int col) // accept a row and column and returns what is to the left
    {
        if (col > 0)
            return grid [row] [col - 1].getType (); // returns the type of lifeform to the left
        else
            return "None"; // if out of bounds, returns "None"
    }

    public void dropTNT () // drops tnt behind the player if it can
    {
        int row = getPlayerRow (), col = getPlayerCol (); // gets the row and column that the player is in

        if (grid [row] [col].getState () == 1) // drops tnt below the player if player is facing up
        {
            if (checkDown (row, col).equals ("Space"))
                grid [row + 1] [col] = new Lifeform ("TNT");
        }
        else if (grid [row] [col].getState () == 2) // drops tnt above the player if player is facing down
        {
            if (checkUp (row, col).equals ("Space"))
                grid [row - 1] [col] = new Lifeform ("TNT");
        }
        else if (grid [row] [col].getState () == 3) // drops tnt to the left of the player if player is facing right
        {
            if (checkLeft (row, col).equals ("Space"))
                grid [row] [col - 1] = new Lifeform ("TNT");
        }
        else if (grid [row] [col].getState () == 4) // drops tnt to the right of the player if player is facing left
        {
            if (checkRight (row, col).equals ("Space"))
                grid [row] [col + 1] = new Lifeform ("TNT");
        }
    }

    public void tnt()//over fills the grid with TNT
    {
        for(int row=0; row<grid.length; row++)
        {
            for(int col=0; col<grid[0].length; col++)
            {
                if(grid[row][col].getType()=="Space")
                {
                    if(Math.random()>0.5)
                        grid[row][col]=new Lifeform("TNT");
                }
            }
        }

    }  

    public int getPlayerHealth () // returns the health of the player
    {
        int row = getPlayerRow (), col = getPlayerCol (); // gets the row and column where the player is
        return grid[row][col].getHealth(); // returns the health of the player
    }

    public int getPlayerSkill () // returns the skill of the player
    {
        int row = getPlayerRow (), col = getPlayerCol (); // gets the row and column where the player is
        return grid[row][col].getSkill(); // returns the skill of the player
    }

    public boolean allEmpty() //returns if grid is empty or no
    {
        int count=0;
        for(int row=0;row<grid.length;row++)
        {
            for(int col=0;col<grid[0].length;col++)
            {
                if(grid[row][col].getType()!="Space")
                {
                    count++;//adds to the counter
                    row=100;
                    col=100;
                }
            }
        }
        if(count==0)
            return true;
        else
            return false;
    }

}