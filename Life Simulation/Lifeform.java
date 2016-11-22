import java.awt.*;
import javax.swing.*;
import javax.imageio.*; // allows image loading
import java.io.*;

class Lifeform
{
    //declaration of instance variables
    private int health, skill, state;
    private String type;
    private Image image,healthImg;

    public Lifeform ()//default constructor
    {
        health = 0;
        skill = 0;
        state = 0;
        type = "Space";
        picture ("blank"); //Blank image will be loaded
        healthImg=null;
    }

    public Lifeform (String type0)
    {
        health = 0;
        skill = 0;
        type = type0;
        int rand = (int) (Math.random () * 3 + 1); // generates skill level from 1-3

        if (type.equals ("AI"))
        {
            picture ("ghost" + rand); // Loads image of AI based on skill level and initializes other aspects of the AI
            health = 100;
            skill = rand;
            pictureHealth();
        }
        else if (type.equals ("Food"))
        {
            picture ("food"); // Loads image of food
        }
        else if (type.equals ("Rock"))
        {
            picture ("block");// Loads image of rock
        }
        else if (type.equals ("Player"))
        {
            picture ("playerUp"); // Loads image of player and initializes other aspects of the AI
            health = 100;
            skill=1;
            state = 1;
            pictureHealth();
        }
        else if (type.equals ("TNT"))
        {
            picture ("tnt"); // Loads image of tnt
        }
        else if (type.equals ("Baby")) // Loads image of AI skill level 1 and initializes other aspects of the AI
        {
            picture ("ghost1");
            health = 100;
            skill = 1;
            pictureHealth();
            type = "AI";
        }
    }

    public void picture (String name)//loads image into variable
    {
        try
        {
            image = ImageIO.read (new File ("Images"+ File.separator + name + ".gif")); // load file into Image object

        }
        catch (IOException e)
        {
            System.out.println ("File not found pic");
        }
    }

    public void pictureHealth ()//loads health bar image
    {
        try
        {
            if(health<100 && health>0) 
                healthImg = ImageIO.read (new File ("Images"+ File.separator +"health"+(health-health%10)+".gif")); // load file into Image object
            else if(health>=100)
                healthImg = ImageIO.read (new File ("Images"+ File.separator +"health"+100+".gif")); // load file into Image object
        }
        catch (IOException e)
        {
            System.out.println ("File not found ");
        }
    }

    public void show (Graphics g, int x, int y)//shows image of lifeform 
    {
        g.drawImage (image, x, y, null);
    }

    public void showHealth (Graphics g, int x, int y)//calls the pictureHealth method
    { 
        if(health>0)
        {
            pictureHealth();
            g.drawImage (healthImg, x, y, null);
        }
    }

    public String getType () // returns the type of the lifeform
    {
        return type;
    }

    public int getHealth () // returns the health of the lifeform
    {
        return health;
    }

    public int getSkill () // returns the skill of the lifeform
    {
        return skill;
    }

    public int getState () // returns the state of the lifeform
    {
        return state;
    }

    public void eat () // increases the health of the lifeform by 50
    {
        health = health + 50;
        pictureHealth();
    }

    public void loseHealth () // decreases the health of the lifeform by 50
    {
        health = health - 50;
        pictureHealth();
    }

    public void gainSkill () // increases the skill of the lifeform by 1 if it is less than 3
    {
        if (skill < 3)
        {
            skill++;
        }
    }

    public void changeState (int num) // accepts a number to change the state to
    {
        state = num;
    }

    public void setSpace () // sets the lifeform as an empty space
    {
        health = 0;
        skill = 0;
        type = "Space";
        picture ("blank"); // Blank image will be loaded
    }

    public void setOrientation (String orientation) // accepts a string to load the approriate player image
    {
        picture ("player" + orientation);
    }

    public Lifeform fight (Lifeform a) // accepts another lifeform and fights
    {
        int hitpoints1 = skill * health, hitpoints2 = a.skill * a.health; // hitpoints are determined by multiplying health by skill level

        if (hitpoints1 > hitpoints2) // if the invoking lifeform has more hitpoints, they win and gain a skill point and the other lifeform dies
        {
            hitpoints1 = hitpoints1 - hitpoints2;
            health = hitpoints1 / skill;
            gainSkill ();
            a.setSpace ();
            if (getType ().equals ("AI"))
                picture ("ghost" + getSkill ());
            return a;
        }
        else if (hitpoints1 < hitpoints2) // if the accepted lifeform has more hitpoints, they win and gain a skill point and the other lifeform dies
        {
            hitpoints2 = hitpoints2 - hitpoints1;
            a.health = hitpoints2 / a.skill;
            a.gainSkill ();
            setSpace ();
            if (a.getType ().equals ("AI"))
                picture ("ghost" + a.getSkill ());
            return a;
        }
        else // if both lifeforms have the same hitpoints, both die
        {
            setSpace ();
            a.setSpace ();
            return a;
        }
    }

}