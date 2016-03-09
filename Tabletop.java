/*
To Do Short: armour | Chant checking and modification

TO DO Long: 

Bugs: Chant have no effect
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
   private int p=0;
   private int pNum=2;
   private int[] life={20,20};
   private String[] names;
   private final int DIM=30;//dimensions of items
   private String input=null;
   private Card actMagic=null;
   private String chantStr[] = new String[pNum];
   
   private int[][] mana=new int[2][5];//[player][color]
   //0=white,1=blue,2=green,3=red,4=black(d)
   
   //wells - base - hill - field | field - hill - base - wells
   private ArrayList<Card>[] lands;
   private ArrayList<Card>[] hand;
   private ArrayList<Card>[] deck;
   private ArrayList<Card>[] dis;
   private ArrayList<Card>[] field;
   //private ArrayList<Card>[] hill;
   //private ArrayList<Card>[] base;
   private ArrayList<Card>[] chant;

   private Rect pass;   
   private Rect myDeck;
   private Rect myDis;
   private Rect otherDeck;
   private Rect otherDis;
   public Tabletop(String[] decks)throws IOException
   {
      addMouseListener( this );
      addMouseMotionListener( this );
   
      lands=(ArrayList<Card>[])new ArrayList[pNum];
      hand=(ArrayList<Card>[])new ArrayList[pNum];
      deck=(ArrayList<Card>[])new ArrayList[pNum];
      dis=(ArrayList<Card>[])new ArrayList[pNum];
      field=(ArrayList<Card>[])new ArrayList[pNum];
      //hill=(ArrayList<Card>[])new ArrayList[pNum];
      //base=(ArrayList<Card>[])new ArrayList[pNum];
      chant=(ArrayList<Card>[])new ArrayList[pNum];
      
      for(int i=0;i<pNum;i++)
      {
         lands[i] = new ArrayList<Card>();
         hand[i] = new ArrayList<Card>();
         deck[i] = new ArrayList<Card>();
         dis[i] = new ArrayList<Card>();
         field[i] = new ArrayList<Card>();
         //hill[i] = new ArrayList<Card>();
         //base[i] = new ArrayList<Card>();
         chant[i] = new ArrayList<Card>();
      }
      
      for(int i=0;i<pNum;i++)
      {
         Scanner input = new Scanner(new FileReader("Decks/"+decks[i]));
         while(input.hasNextLine())
         {
            deck[i].add(new Card(input.nextLine(),i));
         }
      }
      draw(7,0);
      draw(7,1);  
   }
   private boolean play(int i)
   {
      Card temp=hand[p].get(i);
      if(temp.getCost("w")<=mana[p][0]&&temp.getCost("b")<=bEnergy[p]&&temp.getCost("y")<=yEnergy[p]&&temp.getCost("r")<=rEnergy[p]&&temp.getCost("d")<=dEnergy[p])
      {
         wEnergy[p]-=temp.getCost("w");
         bEnergy[p]-=temp.getCost("b");
         yEnergy[p]-=temp.getCost("y");
         rEnergy[p]-=temp.getCost("r");
         dEnergy[p]-=temp.getCost("d");
         if(temp.getType().equals("well"))
         {
            wells[p].add(temp);
         }
         else
            if(temp.getType().equals("unit"))
            {
               base[p].add(temp);
            }
            else
               if(temp.getType().equals("chant"))
               {
                  chant[p].add(temp);
               }
               else 
                  if(temp.getType().equals("magic"))
                  {
                     if(!temp.getTarget().equals("player"))
                     {
                        input=temp.getTarget();
                        actMagic=temp;
                     }
                     else
                     {
                        actMagic=temp;
                        playMagic(null,null,0);
                     }
                  }
                  else
                     if(temp.getType().equals("chant"))
                     {
                        chant[p].add(temp);
                     }
         hand[p].remove(i);
         return true;
      }
      return false;
   }
   private void playMagic(Card aimed,ArrayList<Card> aimedList,int inx)
   {
      String effects[] = actMagic.getEffect().split("_");
      for(int i=0;i<=effects.length/2;i++)
      {
         String tar=effects[i];
         int plus=Integer.parseInt(effects[++i]);
         if(tar.equals("draw"))
         {
            draw(plus,Integer.parseInt(JOptionPane.showInputDialog(null,"Choose target player number. (You are player "+(p+1)+")","Choose A Target",JOptionPane.PLAIN_MESSAGE))-1);
         }
         if(tar.equals("ping"))
         {
            life[Integer.parseInt(JOptionPane.showInputDialog(null,"Choose target player number. (You are player "+(p+1)+")","Choose A Target",JOptionPane.PLAIN_MESSAGE))-1]-=plus;
         }
         if(tar.equals("discard"))
         {
            if(plus<2)
            {
               dis[aimed.getOwner()].add(aimed);
               aimedList.remove(inx);
            }
         }
         if(tar.equals("atk"))
         {
            aimed.plusAtk(plus);
         }
         if(tar.equals("def"))
         {
            aimed.plusDef(plus);
         }
      }
      input=null;
      actMagic=null;
   }
   private void check()
   {
      wFlow[p]=1;
      bFlow[p]=0;
      yFlow[p]=0;
      rFlow[p]=0;
      dFlow[p]=0;
      
      for(int i=0;i<wells[p].size();i++)
      {
         Card temp=wells[p].get(i);
         wFlow[p]+=temp.getPlus("w");
         bFlow[p]+=temp.getPlus("b");
         yFlow[p]+=temp.getPlus("y");
         rFlow[p]+=temp.getPlus("r");
         dFlow[p]+=temp.getPlus("d");
      }
   }
   private void makeChantString()
   {
      for(int k=0;k<chantStr.length;k++)
      {
         chantStr[k]="";
         for(int i=0;i<chant[k].size();i++)
         {
            chantStr[k]+="_"+chant[k].get(i).getTarget()+"_"+chant[k].get(i).getEffect();
         }
         if(chantStr[k].length()>0)
            chantStr[k]=chantStr[k].substring(1);
      }
   }
   private void nextTurn()
   {  
      //check passing player's hand size (<=10)
      while(hand[p].size()>10)
      {
         String out="";
         for(int i=0;i<hand[p].size();i++)
         {
            out+=i+1+".)"+hand[p].get(i).toString();
         }
         int rid=Integer.parseInt(JOptionPane.showInputDialog(null,"Choose a card to discard.\n"+out,"Discard Card",JOptionPane.PLAIN_MESSAGE))-1;
         dis[p].add(hand[p].get(rid));
         hand[p].remove(rid);
      }
      //who's dead
      int nP=0;
      if(p==0)
         nP++;
      if(life[nP]<1)
      {
         JOptionPane.showMessageDialog(null,"Player "+(p+1)+" wins!");
         System.exit(0);
      }
      if(life[p]<1)
      {
         JOptionPane.showMessageDialog(null,"Player "+(nP+1)+" wins!");
         System.exit(0);
      }
      //switch player and draw 1
      if(p==pNum-1)
         p=0;
      else
         p++;
      nP=0;
      if(p==0)
         nP++;
      draw(1,p);
      JOptionPane.showMessageDialog(null,"Player "+(p+1)+" take control.");
      for(int i=0;i<base[p].size();i++)
      {
         if(base[p].get(i).getOwner()==p)
            base[p].get(i).setMoved(false);
      }
      for(int i=0;i<hill[p].size();i++)
      {
         if(hill[p].get(i).getOwner()==p)
            hill[p].get(i).setMoved(false);
      }
      for(int i=0;i<field[p].size();i++)
      {
         if(field[p].get(i).getOwner()==p)
            field[p].get(i).setMoved(false);
      }
      for(int i=0;i<field[nP].size();i++)
      {
         if(field[nP].get(i).getOwner()==p)
            field[nP].get(i).setMoved(false);
      }
      for(int i=0;i<hill[nP].size();i++)
      {
         if(hill[nP].get(i).getOwner()==p)
            hill[nP].get(i).setMoved(false);
      }
      
      check();
      makeChantString();
      wEnergy[p]+=wFlow[p];
      bEnergy[p]+=bFlow[p];
      yEnergy[p]+=yFlow[p];
      rEnergy[p]+=rFlow[p];
      dEnergy[p]+=dFlow[p];
   }
   public void draw(int num,int p)
   {
      //draw num cards
      if(deck[p].size()>0)
      {
         for(int i=0;i<num;i++)
         {
            int ran = u.ranI(0,deck[p].size()-1);
            Card temp = deck[p].get(ran);
            deck[p].remove(ran);
            hand[p].add(temp);
         }
      }
      else
      {
         life[p]=0;
      }
   }
   private void viewCards(ArrayList<Card> list,String name)
   {
      String ans="";
      for(int i=0;i<list.size();i++)
      {
         ans+=list.get(i).toString()+"\n";
      }
      if(ans.equals(""))
         ans="There are no cards here.";
      JOptionPane.showMessageDialog(null,ans,name,JOptionPane.INFORMATION_MESSAGE);
   }
   public void mouseClicked( MouseEvent e )
   {
      int nP=0;
      if(p==0)
         nP=1;
      int x=e.getX();
      int y=e.getY();
      if(input==null)
      {
         if(pass.contains(x,y))
         {
            nextTurn();
         }
         if(myDeck.contains(x,y))
         {
            int ans=JOptionPane.showConfirmDialog(null,"You have "+deck[p].size()+" cards left.\nDo you want to view them?","Deck",JOptionPane.YES_NO_OPTION);
            if(ans==JOptionPane.YES_OPTION)
            {
               viewCards(deck[p],"Deck");
            }
         }
         if(myDis.contains(x,y))
         {
            int ans=JOptionPane.showConfirmDialog(null,"You have "+dis[p].size()+" cards discarded.\nDo you want to view them?","Discard",JOptionPane.YES_NO_OPTION);
            if(ans==JOptionPane.YES_OPTION)
            {
               viewCards(dis[p],"Discard Pile");
            }
         }
         allLoop:
         do
         {
            for(Card card : wells[p])
            {
               if(card.getRect().contains(x,y))
               {
                  JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  break allLoop;
               }
            }
            for(Card card : chant[p])
            {
               if(card.getRect().contains(x,y))
               {
                  JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  break allLoop;
               }
            }
            for(int i=0;i<hand[p].size();i++)
            {
               Card card=hand[p].get(i);
               if(card.getOwner()==p)
               {
                  if(card.getRect().contains(x,y))
                  {
                     int ans=JOptionPane.showConfirmDialog(null,card.toString()+"Do you want to play this card?",card.getName(),JOptionPane.YES_NO_OPTION);
                     if(ans==JOptionPane.YES_OPTION)
                     {
                        play(i);
                        check();
                        break allLoop;
                     }
                  }
               }
               else
               {
                  if(card.getRect().contains(x,y))
                  {
                     JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                     break allLoop;
                  }
               }
            }
            
            for(int i=0;i<base[p].size();i++)
            {
               Card card=base[p].get(i);
               if(card.getDef()<1)
               {
                  dis[card.getOwner()].add(base[p].get(i));
                  base[p].remove(i);
                  i--;
               }
               else
                  if(card.getOwner()==p)
                  {
                     if(card.getRect().contains(x,y))
                     {
                        int ans=JOptionPane.showConfirmDialog(null,card.toString()+"'Yes' to advance, 'no' to retreat, 'cancel' to cancel.",card.getName(),JOptionPane.YES_NO_CANCEL_OPTION);
                        if(ans==JOptionPane.YES_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                           
                              if(attack(i,0))
                              {
                                 hill[p].add(card); //invade
                                 base[p].remove(i);
                              }
                           }
                        }
                        break allLoop;
                     }
                  }
                  else
                  {
                  //not possible
                  }
            }
         
            for(int i=0;i<hill[p].size();i++)
            {
               Card card=hill[p].get(i);
               if(card.getDef()<1)
               {
                  dis[card.getOwner()].add(hill[p].get(i));
                  hill[p].remove(i);
                  i--;
               }
               else
                  if(card.getOwner()==p)
                  {
                     if(card.getRect().contains(x,y))
                     {
                        int ans=JOptionPane.showConfirmDialog(null,card.toString()+"'Yes' to advance, 'no' to retreat, 'cancel' to cancel.",card.getName(),JOptionPane.YES_NO_CANCEL_OPTION);
                        if(ans==JOptionPane.YES_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                           
                              if(attack(i,1))
                              {
                                 field[p].add(card); //invade
                                 hill[p].remove(i);
                              }
                           }
                        }
                        if(ans==JOptionPane.NO_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                              base[p].add(card);
                              hill[p].remove(i);
                           }
                        }
                        break allLoop;
                     }
                  }
                  else
                  {
                     if(card.getRect().contains(x,y))
                     {
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                        break allLoop;
                     }
                  }
            }
         
            for(int i=0;i<field[p].size();i++)
            {
               Card card=field[p].get(i);
               if(card.getDef()<1)
               {
                  dis[card.getOwner()].add(field[p].get(i));
                  field[p].remove(i);
                  i--;
               }
               else
                  if(card.getOwner()==p)
                  {
                     if(card.getRect().contains(x,y))
                     {
                        int ans=JOptionPane.showConfirmDialog(null,card.toString()+"'Yes' to advance, 'no' to retreat, 'cancel' to cancel.",card.getName(),JOptionPane.YES_NO_CANCEL_OPTION);
                        if(ans==JOptionPane.YES_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                           
                              if(attack(i,2))
                              {
                                 field[nP].add(card); //invade
                                 field[p].remove(i);
                              }
                           }
                        }
                        if(ans==JOptionPane.NO_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                              hill[p].add(card);
                              field[p].remove(i);
                           }
                        }
                        break allLoop;
                     }
                  }
                  else
                  {
                     if(card.getRect().contains(x,y))
                     {
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                        break allLoop;
                     }
                  }
            }
            for(Card card : wells[nP])
            {
               if(card.getRect().contains(x,y))
               {
                  JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
               }
            }
            for(int i=0;i<hand[nP].size();i++)
            {
               Card card=hand[nP].get(i);
               if(card.getOwner()==p)
               {
               //not possible
               }
               else
               {
               //not logical
               }
            }
            
            for(int i=0;i<base[nP].size();i++)
            {
               Card card=base[nP].get(i);
               if(card.getDef()<1)
               {
                  dis[card.getOwner()].add(base[nP].get(i));
                  base[nP].remove(i);
                  i--;
               }
               else
                  if(card.getOwner()==p)
                  {
                  //not possible
                  }
                  else
                  {
                     if(card.getRect().contains(x,y))
                     {
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                        break allLoop;
                     }
                  }
            }
         
            for(int i=0;i<hill[nP].size();i++)
            {
               Card card=hill[nP].get(i);
               if(card.getDef()<1)
               {
                  dis[card.getOwner()].add(hill[nP].get(i));
                  hill[nP].remove(i);
                  i--;
               }
               else
                  if(card.getOwner()==p)
                  {
                     if(card.getRect().contains(x,y))
                     {
                        int ans=JOptionPane.showConfirmDialog(null,card.toString()+"'Yes' to advance, 'no' to retreat, 'cancel' to cancel.",card.getName(),JOptionPane.YES_NO_CANCEL_OPTION);
                        if(ans==JOptionPane.YES_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                              if(attack(i,4))
                              {
                              //not wanted
                              }
                           }
                        }
                        if(ans==JOptionPane.NO_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                              field[nP].add(card);
                              hill[nP].remove(i);
                           }
                        }
                        break allLoop;
                     }
                  }
                  else
                  {
                     if(card.getRect().contains(x,y))
                     {
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                        break allLoop;
                     }
                  }
            }
         
            for(int i=0;i<field[nP].size();i++)
            {
               Card card=field[nP].get(i);
               if(card.getDef()<1)
               {
                  dis[card.getOwner()].add(field[nP].get(i));
                  field[nP].remove(i);
                  i--;
               }
               else
                  if(card.getOwner()==p)
                  {
                     if(card.getRect().contains(x,y))
                     {
                        int ans=JOptionPane.showConfirmDialog(null,card.toString()+"'Yes' to advance, 'no' to retreat, 'cancel' to cancel.",card.getName(),JOptionPane.YES_NO_CANCEL_OPTION);
                        if(ans==JOptionPane.YES_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                              if(attack(i,3))
                              {
                                 hill[nP].add(card); //invade
                                 field[nP].remove(i);
                              }
                           }
                        }
                        if(ans==JOptionPane.NO_OPTION)
                        {
                           if(!card.getMoved())
                           {
                              card.setMoved(true);
                              field[p].add(card);
                              field[nP].remove(i);
                           }
                        }
                        break allLoop;
                     }
                  }
                  else
                  {
                     if(card.getRect().contains(x,y))
                     {
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                        break allLoop;
                     }
                  }
            }
         }while(false);
      }
      else
      {
         int lClick=MouseEvent.BUTTON1;
         allLoop:
         do
         {
            if(input.equals("any")||input.equals("well"))
               for(int i=0;i<wells[p].size();i++)
               {
                  Card card=base[p].get(i);
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,wells[p],i);
                        break allLoop;
                     }
                     else
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  }
               }  
            if(input.equals("any")||input.equals("chant"))
               for(int i=0;i<chant[p].size();i++)
               {
                  Card card=chant[p].get(i);
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,chant[p],i);
                        break allLoop;
                     }
                     else
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  }
               } 
            if(input.equals("any")||input.equals("unit"))
               for(int i=0;i<base[p].size();i++)
               {
                  Card card=base[p].get(i);
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,base[p],i);
                        break allLoop;
                     }
                     else
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  }
               }
            if(input.equals("any")||input.equals("unit"))
               for(int i=0;i<hill[p].size();i++)
               {
                  Card card=hill[p].get(i);
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,hill[p],i);
                        break allLoop;
                     }
                     else
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  }
               }
            if(input.equals("any")||input.equals("unit"))
               for(int i=0;i<field[p].size();i++)
               {
                  Card card=field[p].get(i);
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,field[p],i);
                        break allLoop;
                     }
                     else
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  }
               }  
            if(input.equals("any")||input.equals("unit"))   
               for(int i=0;i<base[nP].size();i++)
               {
                  Card card=base[nP].get(i);
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,base[nP],i);
                        break allLoop;
                     }
                     else
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  }
               }
            if(input.equals("any")||input.equals("unit"))
               for(int i=0;i<hill[nP].size();i++)
               {
                  Card card=hill[nP].get(i);
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,hill[nP],i);
                        break allLoop;
                     }
                     else
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  }
               }
            if(input.equals("any")||input.equals("unit"))
               for(int i=0;i<field[nP].size();i++)
               {
                  Card card=field[nP].get(i); 
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,field[nP],i);
                        break allLoop;
                     }
                     else
                        JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  }
               }
            if(input.equals("any"))
               for(int i=0;i<hand[nP].size();i++)
               {
                  Card card=hand[nP].get(i);
                  if(card.getRect().contains(x,y))
                  {
                     if(e.getButton()==lClick)
                     {
                        playMagic(card,hand[nP],i);
                        break allLoop;
                     }
                  }
               }
         
         }while(false);
      }
      repaint();
   }
   private boolean hasEnemy(ArrayList<Card> x)
   {
      for(int i=0;i<x.size();i++)
      {
         if(x.get(i).getOwner()!=p)
            return true;
      }
      return false;
   }
   private boolean attack(int index,int which)
   {
      int nP=0;
      if(p==0)
         nP=1;
      ArrayList<Card> atking;
      ArrayList<Card> defing;
      switch(which)//figure out where
      {
         case 0:
            atking=base[p];
            defing=hill[p];
            break;
         case 1:
            atking=hill[p];
            defing=field[p];
            break;
         case 2:
            atking=field[p];
            defing=field[nP];
            break;
         case 3:
            atking=field[nP];
            defing=hill[nP];
            break;
         case 4:
            atking=hill[nP];
            defing=base[nP];
            break;
         default:
            return false;
      }
      if(!hasEnemy(defing))//if not battle, move
      {
         if(defing==base[nP])
         {
            life[nP]-=atking.get(index).getAtk();
            return false;
         }
         return true;
      }
      else
      {
         ArrayList<Card> defers=new ArrayList<Card>();
         JOptionPane.showMessageDialog(null,"Player "+(nP+1)+" take control.");
         String options="";
         for(int i=0;i<defing.size();i++)
         {
            options+=i+1+".) "+defing.get(i).toString();
         }
         int def=Integer.parseInt(JOptionPane.showInputDialog(null,"Choose blocker.\n"+options,"Defenders",JOptionPane.PLAIN_MESSAGE))-1;
         defers.add(defing.get(def));
         //fight
         Card atacker = atking.get(index);
         int atks[] = {atacker.getAtk(),defers.get(0).getAtk()};
         if(chantStr[p].contains("atk")||chantStr[nP].contains("armour"))
         {
            String[] tempMod=chantStr[p].split("_");
            for(int i=0;i<tempMod.length;i++)
            {
               if(tempMod[i].equals("any")||tempMod[i].equals("ally"))
                  if(tempMod[i+1].equals("atk"))
                     atks[p]+=Integer.parseInt(tempMod[i+2]);
            }
            chantStr[nP].split("_");
            for(int i=0;i<tempMod.length;i++)
            {
               if(tempMod[i].equals("any")||tempMod[i].equals("enemy"))
                  if(tempMod[i+1].equals("armour"))
                     atks[p]+=Integer.parseInt(tempMod[i+2]);
            }
         }
         if(atacker.getAbility().contains("armour"))
         {
            String[] abi = atacker.getAbility().split("_");
            atks[1]-=Integer.parseInt(abi[1]);
         }
         if(atacker.getAbility().contains("range"))
         {
            atks[1]=0;
         }
         
         if(atks[1]>0)
            atacker.plusDef(-1*atks[1]);
            
         if(chantStr[nP].contains("atk")||chantStr[p].contains("armour"))
         {
            String[] tempMod=chantStr[nP].split("_");
            for(int i=0;i<tempMod.length;i++)
            {
               if(tempMod[i].equals("any")||tempMod[i].equals("ally"))
                  if(tempMod[i+1].equals("atk"))
                     atks[nP]+=Integer.parseInt(tempMod[i+2]);
            }
            chantStr[p].split("_");
            for(int i=0;i<tempMod.length;i++)
            {
               if(tempMod[i].equals("any")||tempMod[i].equals("enemy"))
                  if(tempMod[i+1].equals("armour"))
                     atks[nP]+=Integer.parseInt(tempMod[i+2]);
            }
         }
         if(defers.get(0).getAbility().contains("armour"))
         {
            String[] abi = defers.get(0).getAbility().split("_");
            atks[0]-=Integer.parseInt(abi[1]);
         }
         if(atks[0]>0)
            defers.get(0).plusDef(-1*atks[0]);
         if(atking.get(index).getDef()<1)
         {
            dis[p].add(atking.get(index));
            atking.remove(index);
         }
         if(defing.get(0).getDef()<1)
         {
            dis[nP].add(defers.get(0));
            defers.remove(0);
            defing.remove(def);
         }
         //end fight
         return false;
      }
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
      if(input==null)
      {
         int halfY=getHeight()/2;
         pass=new Rect(getWidth()-40,halfY-20,getWidth(),halfY+20);
         myDeck=new Rect(getWidth()-60,halfY+30,getWidth()-10,halfY+80);
         myDis=new Rect(getWidth()-60,halfY+90,getWidth()-10,halfY+140);
         g.setColor(Color.gray);
         g.fillRect(0, 0, getWidth(), getHeight());
         g.setColor(Color.green);
         g.drawLine(0, halfY,getWidth(),halfY);
         double ref=0;
         g.setFont(new Font("Ariel",Font.PLAIN,20));
         for(int i=0;i<hand[p].size();i++)//draw hand
         {
            hand[p].get(i).setRect((int)(DIM*(ref+1)),getHeight()-DIM*2,(int)(DIM*(ref+2)),getHeight()-DIM);
            ref+=1.5;
            Rect temp = hand[p].get(i).getRect();
            g = setColor(g,hand[p].get(i));
            if(hand[p].get(i).getType().equals("well"))
            {
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
            else
               if(hand[p].get(i).getType().equals("unit"))
               {
                  g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
               }
               else//chant & magic
               {
                  g.fillOval(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
               }
            g = setTextColor(g);
            g.drawString(hand[p].get(i).getSumCost()+"",temp.getLeft()+10,temp.getBottom()-5);
         }
         ref=0;
         int dis=1;
         g.setFont(new Font("Ariel",Font.PLAIN,25));
         for(int i=0;i<field[p].size();i++) //draw field
         {
            field[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5)),halfY+DIM*(dis+1));//l,halfY+30,r,halfY+60
            ref+=1.5;
            Rect temp = field[p].get(i).getRect();
            if(!field[p].get(i).getMoved())
            {
               g.setColor(Color.green);
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
            g = setColor(g,field[p].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
            g = setTextColor(g);
            g.drawString(field[p].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
         }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY+DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY+DIM*(dis+1.5)));
         ref=0;
         dis+=2;
         for(int i=0;i<hill[p].size();i++) //draw hill
         {
            hill[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5)),halfY+DIM*(dis+1));//l,halfY+90
            ref+=1.5;
            Rect temp = hill[p].get(i).getRect();
            if(!hill[p].get(i).getMoved())
            {
               g.setColor(Color.green);
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
            g = setColor(g,hill[p].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
            g = setTextColor(g);
            g.drawString(hill[p].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
         }
         g.setColor(Color.DARK_GRAY);
         g.drawLine(DIM*4,(int)(halfY+DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY+DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         for(int i=0;i<base[p].size();i++) //draw base
         {
            base[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5)),halfY+DIM*(dis+1));
            ref+=1.5;
            Rect temp = base[p].get(i).getRect();
            if(!base[p].get(i).getMoved())
            {
               g.setColor(Color.green);
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
            g = setColor(g,base[p].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
            g = setTextColor(g);
            g.drawString(base[p].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
         }
         g.setColor(Color.black);
         g.drawString("|"+(p+1)+"|"+life[p]+"|",(int)(DIM),halfY+DIM*6);
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY+DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY+DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         for(int i=0;i<wells[p].size();i++) //draw wells
         {
            wells[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5)),halfY+DIM*(dis+1));
            ref+=1.5;
            Rect temp = wells[p].get(i).getRect();
            g = setColor(g,wells[p].get(i));
            g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
         }
      
         int nP=0;
         nP=0;
         if(p==0)
            nP=1;//nP is not-player number
         dis=1;
         ref=0;
         for(int i=0;i<field[nP].size();i++)
         {
            field[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1),(int)(DIM*(ref+5)),halfY-DIM*dis);
            ref+=1.5;
            Rect temp = field[nP].get(i).getRect();
            if(!field[nP].get(i).getMoved())
            {
               g.setColor(Color.green);
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
            g = setColor(g,field[nP].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
            g = setTextColor(g);
            g.drawString(field[nP].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
         }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY-DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY-DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         for(int i=0;i<hill[nP].size();i++)
         {
            hill[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1),(int)(DIM*(ref+5)),halfY-DIM*dis);
            ref+=1.5;
            Rect temp = hill[nP].get(i).getRect();
            if(!hill[nP].get(i).getMoved())
            {
               g.setColor(Color.green);
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
            g = setColor(g,hill[nP].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
            g = setTextColor(g);
            g.drawString(hill[nP].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
         }
         g.setColor(Color.DARK_GRAY);
         g.drawLine(DIM*4,(int)(halfY-DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY-DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         for(int i=0;i<base[nP].size();i++)
         {
            base[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1),(int)(DIM*(ref+5)),halfY-DIM*dis);
            ref+=1.5;
            Rect temp = base[nP].get(i).getRect();
            if(!base[nP].get(i).getMoved())
            {
               g.setColor(Color.green);
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
            g = setColor(g,base[nP].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
            g = setTextColor(g);
            g.drawString(base[nP].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
         }
         g.setColor(Color.black);
         g.drawString("|"+(nP+1)+"|"+life[nP]+"|",(int)(DIM),halfY-DIM*5);
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY-DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY-DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         for(int i=0;i<wells[nP].size();i++)
         {
            wells[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1),(int)(DIM*(ref+5)),halfY-DIM*dis);
            ref+=1.5;
            Rect temp = wells[nP].get(i).getRect();
            g = setColor(g,wells[nP].get(i));
            g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
         }
         ref=0;
         for(int i=0;i<hand[nP].size();i++)
         {
            hand[nP].get(i).setRect((int)(DIM*(ref+1)),DIM,(int)(DIM*(ref+2)),DIM*2);
            ref+=1.5;
            Rect temp = hand[nP].get(i).getRect();
            g.setColor(new Color(u.ranI(0,255),u.ranI(0,255),u.ranI(0,255)));
            g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
         }
         ref=1;
         for(int i=0;i<chant[p].size();i++)
         {
            chant[p].get(i).setRect(getWidth()-DIM*5,(int)(halfY+DIM*(ref)),getWidth()-DIM*4,(int)(halfY+DIM*(ref+1)));
            ref+=1.5;
            Rect temp = chant[p].get(i).getRect();
            g = setColor(g,chant[p].get(i));
            g.fillOval(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
         }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine((int)(getWidth()-DIM*5.5),halfY+DIM,(int)(getWidth()-DIM*5.5),halfY+DIM*(dis+1));
         g.drawLine((int)(getWidth()-DIM*3.5),halfY+DIM,(int)(getWidth()-DIM*3.5),halfY+DIM*(dis+1));
         ref=1;
         for(int i=0;i<chant[nP].size();i++)
         {
            chant[nP].get(i).setRect(getWidth()-DIM*5,(int)(halfY-DIM*(ref)),getWidth()-DIM*4,(int)(halfY-DIM*(ref+1)));
            ref+=1.5;
            Rect temp = chant[nP].get(i).getRect();
            g = setColor(g,chant[nP].get(i));
            g.fillOval(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
         }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine((int)(getWidth()-DIM*5.5),halfY-DIM,(int)(getWidth()-DIM*5.5),halfY-DIM*(dis+1));
         g.drawLine((int)(getWidth()-DIM*3.5),halfY-DIM,(int)(getWidth()-DIM*3.5),halfY-DIM*(dis+1));
         
         g.setFont(new Font("Arial",Font.PLAIN,15));
         g.setColor(Color.white);
         g.drawString(wEnergy[p]+" | "+wFlow[p],DIM,halfY+25);
         g.setColor(Color.blue);
         g.drawString(bEnergy[p]+" | "+bFlow[p],DIM,halfY+45);
         g.setColor(Color.yellow);
         g.drawString(yEnergy[p]+" | "+yFlow[p],DIM,halfY+65);
         g.setColor(Color.red);
         g.drawString(rEnergy[p]+" | "+rFlow[p],DIM,halfY+85);
         g.setColor(Color.black);
         g.drawString(dEnergy[p]+" | "+dFlow[p],DIM,halfY+105);
      
         g.setColor(Color.green);
         g.drawString(p+1+" | "+pNum,DIM,halfY+125);
         g.fillRect(pass.getLeft(),pass.getTop(),pass.getWidth(),pass.getHeight());
         g.setColor(Color.red.darker());
         g.fillRect(myDeck.getLeft(),myDeck.getTop(),myDeck.getWidth(),myDeck.getHeight());
         g.setColor(Color.white.darker());
         g.fillRect(myDis.getLeft(),myDis.getTop(),myDis.getWidth(),myDis.getHeight());
      }
      else
      {
         if(input.contains("all"))
            playMagic(null,null,0);
         int halfY=getHeight()/2;
         g.setColor(Color.gray);
         g.fillRect(0, 0, getWidth(), getHeight());
         g.setColor(Color.green);
         g.drawLine(0, halfY,getWidth(),halfY);
         double ref=0;
         int dis=1;
         g.setFont(new Font("Ariel",Font.PLAIN,25));
         if(input.equals("any")||input.equals("unit"))
            for(int i=0;i<field[p].size();i++)
            {
               field[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5)),halfY+DIM*(dis+1));//l,halfY+30,r,halfY+60
               ref+=1.5;
               Rect temp = field[p].get(i).getRect();
               g = setColor(g,field[p].get(i));
               g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
               g = setTextColor(g);
               g.drawString(field[p].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
            }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY+DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY+DIM*(dis+1.5)));
         ref=0;
         dis+=2;
         if(input.equals("any")||input.equals("unit"))
            for(int i=0;i<hill[p].size();i++)
            {
               hill[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5)),halfY+DIM*(dis+1));//l,halfY+90
               ref+=1.5;
               Rect temp = hill[p].get(i).getRect();
               g = setColor(g,hill[p].get(i));
               g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
               g = setTextColor(g);
               g.drawString(hill[p].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
            }
         g.setColor(Color.DARK_GRAY);
         g.drawLine(DIM*4,(int)(halfY+DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY+DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         if(input.equals("any")||input.equals("unit"))
            for(int i=0;i<base[p].size();i++)
            {
               base[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5)),halfY+DIM*(dis+1));
               ref+=1.5;
               Rect temp = base[p].get(i).getRect();
               g = setColor(g,base[p].get(i));
               g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
               g = setTextColor(g);
               g.drawString(base[p].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
            }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY+DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY+DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         if(input.equals("any")||input.equals("well"))
            for(int i=0;i<wells[p].size();i++)
            {
               wells[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5)),halfY+DIM*(dis+1));
               ref+=1.5;
               Rect temp = wells[p].get(i).getRect();
               g = setColor(g,wells[p].get(i));
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
      
         int nP=0;
         nP=0;
         if(p==0)
            nP=1;
         dis=1;
         ref=0;
         if(input.equals("any")||input.equals("unit"))
            for(int i=0;i<field[nP].size();i++)
            {
               field[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1),(int)(DIM*(ref+5)),halfY-DIM*dis);
               ref+=1.5;
               Rect temp = field[nP].get(i).getRect();
               g = setColor(g,field[nP].get(i));
               g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
               g = setTextColor(g);
               g.drawString(field[nP].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
            }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY-DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY-DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         if(input.equals("any")||input.equals("unit"))
            for(int i=0;i<hill[nP].size();i++)
            {
               hill[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1),(int)(DIM*(ref+5)),halfY-DIM*dis);
               ref+=1.5;
               Rect temp = hill[nP].get(i).getRect();
               g = setColor(g,hill[nP].get(i));
               g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
               g = setTextColor(g);
               g.drawString(hill[nP].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
            }
         g.setColor(Color.DARK_GRAY);
         g.drawLine(DIM*4,(int)(halfY-DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY-DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         if(input.equals("any")||input.equals("unit"))
            for(int i=0;i<base[nP].size();i++)
            {
               base[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1),(int)(DIM*(ref+5)),halfY-DIM*dis);
               ref+=1.5;
               Rect temp = base[nP].get(i).getRect();
               g = setColor(g,base[nP].get(i));
               g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
               g = setTextColor(g);
               g.drawString(base[nP].get(i).getOwner()+1+"",temp.getLeft()+10,temp.getBottom()-5);
            }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY-DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY-DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         if(input.equals("any")||input.equals("well"))
            for(int i=0;i<wells[nP].size();i++)
            {
               wells[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1),(int)(DIM*(ref+5)),halfY-DIM*dis);
               ref+=1.5;
               Rect temp = wells[nP].get(i).getRect();
               g = setColor(g,wells[nP].get(i));
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
         ref=0;
         if(input.equals("any")||input.equals("hand"))
            for(int i=0;i<hand[nP].size();i++)
            {
               hand[nP].get(i).setRect((int)(DIM*(ref+1)),DIM,(int)(DIM*(ref+2)),DIM*2);
               ref+=1.5;
               Rect temp = hand[nP].get(i).getRect();
               g.setColor(new Color(u.ranI(0,255),u.ranI(0,255),u.ranI(0,255)));
               g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
         ref=1;
         if(input.equals("any")||input.equals("chant"))
            for(int i=0;i<chant[p].size();i++)
            {
               chant[p].get(i).setRect(getWidth()-DIM*5,(int)(halfY+DIM*(ref)),getWidth()-DIM*4,(int)(halfY+DIM*(ref+1)));
               ref+=1.5;
               Rect temp = chant[p].get(i).getRect();
               g = setColor(g,chant[p].get(i));
               g.fillOval(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine((int)(getWidth()-DIM*5.5),halfY+DIM,(int)(getWidth()-DIM*5.5),halfY+DIM*(dis+1));
         g.drawLine((int)(getWidth()-DIM*3.5),halfY+DIM,(int)(getWidth()-DIM*3.5),halfY+DIM*(dis+1));
         ref=1;
         if(input.equals("any")||input.equals("chant"))
            for(int i=0;i<chant[nP].size();i++)
            {
               chant[nP].get(i).setRect(getWidth()-DIM*5,(int)(halfY-DIM*(ref)),getWidth()-DIM*4,(int)(halfY-DIM*(ref+1)));
               ref+=1.5;
               Rect temp = chant[nP].get(i).getRect();
               g = setColor(g,chant[nP].get(i));
               g.fillOval(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
            }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine((int)(getWidth()-DIM*5.5),halfY-DIM,(int)(getWidth()-DIM*5.5),halfY-DIM*(dis+1));
         g.drawLine((int)(getWidth()-DIM*3.5),halfY-DIM,(int)(getWidth()-DIM*3.5),halfY-DIM*(dis+1));
      }
   }
   private Graphics setColor(Graphics g,Card card)
   {
      if(card.getColor()[0].equals("w"))
         g.setColor(Color.white);
      else
         if(card.getColor()[0].equals("b"))
            g.setColor(Color.blue);
         else
            if(card.getColor()[0].equals("y"))
               g.setColor(Color.yellow);
            else
               if(card.getColor()[0].equals("r"))
                  g.setColor(Color.red);
               else
                  if(card.getColor()[0].equals("d"))
                     g.setColor(Color.black);
      return g;
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