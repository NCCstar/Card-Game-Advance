/*
To Do Short: 

TO DO Long: 

Bugs:
*/
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Tabletop extends JPanel implements MouseListener, MouseMotionListener
{
   private int pNum;
   public Tabletop(String[] decks)throws IOException
   {
      addMouseListener( this );
      addMouseMotionListener( this );
      
      for(int i=0;i<pNum;i++)
      {
         Scanner input = new Scanner(new FileReader("Decks/"+decks[i]));
         while(input.hasNextLine())
         {
            String line=input.nextLine();
         }
      }
   }
   //Master IO program - whenever mouse is clicked will check if on a card and execute
   public void mouseClicked(MouseEvent e)
   {
      repaint();
   }
   public void mouseDragged( MouseEvent e){}
   public void mouseExited( MouseEvent e ){}
   public void mousePressed( MouseEvent e ){}
   public void mouseReleased( MouseEvent e ){}
   public void mouseEntered( MouseEvent e ){}
   public void mouseMoved( MouseEvent e){}
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g); 
   }
   private Graphics setTextColor(Graphics x)
   {
      int r=x.getColor().getRed();
      int g=x.getColor().getGreen();
      int b=x.getColor().getBlue();
      r=255-r;
      g=255-g;
      b=255-b;
      x.setColor(new Color(r,g,b));
      return x;
   }
}