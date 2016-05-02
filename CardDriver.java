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
      }
      String ans[]=new String[2];
      
      ans[0]=(String)JOptionPane.showInputDialog(null,"Player 1:","Choose A Deck",JOptionPane.INFORMATION_MESSAGE, null,(Object[])decks,(Object)decks[0]);
      ans[1]=(String)JOptionPane.showInputDialog(null,"Player 3:","Choose A Deck",JOptionPane.INFORMATION_MESSAGE, null,(Object[])decks,(Object)decks[0]);
           
      Tabletop tabletop = new Tabletop(ans);
      JFrame frame = new JFrame("Card Game");	//window title
      frame.setSize(700, 700);					//Size of game window
      frame.setLocation(0, 0);				//location of game window on the screen
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(tabletop);
      frame.setVisible(true);
   }
}