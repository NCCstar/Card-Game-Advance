import java.io.*;
import javax.swing.*;
public class CardDriver
{
   public static void main(String[] args)throws IOException
   {
      File folder = new File("Decks");
      File[] list = folder.listFiles();
      String decks[] = new String[list.length];
      String out="";
      for(int i=0;i<list.length;i++)
      {
         decks[i]=list[i].getName();
         out+=(i+1)+") "+list[i].getName()+"\n";
      }
      String ans[]=new String[2];
      while(true)
      {
         try
         {
            ans[0]=decks[Integer.parseInt(JOptionPane.showInputDialog(null,"Player 1:\n"+out,"Choose A Deck",JOptionPane.PLAIN_MESSAGE))-1];
            ans[1]=decks[Integer.parseInt(JOptionPane.showInputDialog(null,"Player 2:\n"+out,"Choose A Deck",JOptionPane.PLAIN_MESSAGE))-1];
            break;
         }
         catch(ArrayIndexOutOfBoundsException|NumberFormatException e){}
      }
      Tabletop tabletop = new Tabletop(ans);
      JFrame frame = new JFrame("Card Game");	//window title
      frame.setSize(700, 700);					//Size of game window
      frame.setLocation(0, 0);				//location of game window on the screen
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(tabletop);
      frame.setVisible(true);
   }
}