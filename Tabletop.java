/*
To Do Short: Fighting 

TO DO Long: 

TO DO list: 

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
   private int p=0;
   private int pNum=2;
   private int mode=0;//0=first main,1=attack,2=defend,3=second main
   private int[] life={20,20};
   private String[] names;
   private final int DIM=30;//dimensions of items
   private String input=null;
   private Card actMagic=null;
   private String chantStr[] = new String[pNum];
   private boolean landPlayed[] = new boolean[2];
   
   private int[][] mana=new int[2][6];//[player][color] - tap land to add, cast cards to remove
   //0=white,1=blue,2=green,3=red,4=black(d),5=colorless(n)
   
   private ArrayList<Card>[] lands;
   private ArrayList<Card>[] hand;
   private ArrayList<Card>[] deck;
   private ArrayList<Card>[] dis;
   private ArrayList<Card>[] field;
   private ArrayList<Card>[] chants;
   
   private ArrayList<Card> atkers;
   private ArrayList<Card> defers;

   private Rect pass; //next phase
   private Rect myDeck;//deck
   private Rect myDis;//discard
   private Rect yoDeck;
   private Rect yoDis;
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
      chants=(ArrayList<Card>[])new ArrayList[pNum];
      
      for(int i=0;i<pNum;i++)
      {
         lands[i] = new ArrayList<Card>();
         hand[i] = new ArrayList<Card>();
         deck[i] = new ArrayList<Card>();
         dis[i] = new ArrayList<Card>();
         field[i] = new ArrayList<Card>();
         //hill[i] = new ArrayList<Card>();
         //base[i] = new ArrayList<Card>();
         chants[i] = new ArrayList<Card>();
      }
      
      for(int i=0;i<pNum;i++)
      {
         Scanner input = new Scanner(new FileReader("Decks/"+decks[i]));
         while(input.hasNextLine())
         {
            deck[i].add(new Card(input.nextLine()));
         }
      }
      draw(7,0);
      draw(7,1);  
   }
   private boolean play(int i)
   {
      Card temp=hand[p].get(i);
      int[] cost=temp.getCost();
      boolean canPlay=true;
      for(int k=0;k<5;k++)
      {
         if(cost[k]>mana[p][k])
         {
            canPlay=false;
            break;
         }
      }
      if(cost[5]>0)
      {
         int manaSum=0;
         for(int k=0;k<5;k++)
         {
            manaSum+=mana[p][k];
         }
         if(manaSum<temp.getSumCost())
            canPlay=false;
      }
      if(canPlay)
      {
         for(int k=0;k<5;k++)
            mana[p][k]-=cost[k];
         for(int k=0;k<cost[5];k++)
         {
            ArrayList<Object> preop=new ArrayList();
         
            if(mana[p][0]>0)
               preop.add("White");
            if(mana[p][1]>0)
               preop.add("Blue");
            if(mana[p][2]>0)
               preop.add("Green");
            if(mana[p][3]>0)
               preop.add("Red");
            if(mana[p][4]>0)
               preop.add("Black");
            
            Object[] options=preop.toArray();
            String exe;
            if(options.length>1)
               exe=(String)JOptionPane.showInputDialog(null,"What color mana to use for colorless?","Mana Color",JOptionPane.INFORMATION_MESSAGE, null,options, options[0]);
            else
               exe=(String)options[0];
               
            switch(exe)
            {
               case "White":
                  mana[p][0]--;
                  break;
               case "Blue":
                  mana[p][0]--;
                  break;
               case "Green":
                  mana[p][0]--;
                  break;
               case "Red":
                  mana[p][0]--;
                  break;
               case "Black":
                  mana[p][0]--;
                  break;
            }
         }
         
         if(temp.getType().equals("land")&&!landPlayed[p])
         {
            lands[p].add(temp);
            landPlayed[p]=true;
            hand[p].remove(i);
         }
         else
            if(temp.getType().equals("unit"))
            {
               field[p].add(temp);
               hand[p].remove(i);
            }
            else
               if(temp.getType().equals("chant"))
               {
                  chants[p].add(temp);
                  hand[p].remove(i);
               }
               else 
                  if(temp.getType().equals("magic"))
                  {
                     //fix target later
                     hand[p].remove(i);
                  }
         
         return true;
      }
      return false;
   }
   private void doAbility(String exe,Card card)
   {
      String[] abi=exe.split("_");
      if(abi[0].equals("manaAdd"))
      {
         mana[p][Integer.parseInt(abi[2])]+=Integer.parseInt(abi[3]);
         card.tap();
      }
      if(abi[0].equals("lifeAdd"))
      {
         life[p]+=Integer.parseInt(abi[2]);
         card.tap();
      }
   }
   /*
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
   
   
   private void makeChantString()
   {
      for(int k=0;k<chantStr.length;k++)
      {
         chantStr[k]="";
         for(int i=0;i<chants[k].size();i++)
         {
            chantStr[k]+="_"+chants[k].get(i).getTarget()+"_"+chants[k].get(i).getEffect();
         }
         if(chantStr[k].length()>0)
            chantStr[k]=chantStr[k].substring(1);
      }
   }
   */
   private void nextTurn()
   {  
      for(int k=0;k<6;k++)
      {
         mana[p][k]=0;
      }
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
      for(int i=0;i<field[p].size();i++)
      {
         field[p].get(i).heal();
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
      JOptionPane.showMessageDialog(null,"Player "+(p+1)+" take control.");
      if(p==pNum-1)
         p=0;
      else
         p++;
      nP=0;
      if(p==0)
         nP++;
      draw(1,p);
      mode=0;
      //upkeep
      landPlayed[p]=false;
      for(int i=0;i<field[p].size();i++)
      {
         field[p].get(i).untap();
      }
      for(int i=0;i<lands[p].size();i++)
      {
         lands[p].get(i).untap();
      }
      //makeChantString();
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
   //master IO program - whenever mouse is clicked will check if on a card
   public void mouseClicked(MouseEvent e)
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
            mode++;
            if(mode==4)
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
            for(Card card : lands[p])
            {
               if(card.getRect().contains(x,y))
               {
                  Object[] options = card.getAbility();
                  Object exe=JOptionPane.showInputDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE, null,options, options[0]);
                  if(!card.isTapped()&&exe!=null)
                  {
                     doAbility((String)exe,card);
                  }
                  break allLoop;
               }
            }
            for(Card card : chants[p])
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
               if(card.getRect().contains(x,y))
               {
                  if(mode==0||mode==3)
                  {
                     int ans=JOptionPane.showConfirmDialog(null,card.toString()+"Do you want to play this card?",card.getName(),JOptionPane.YES_NO_OPTION);
                     if(ans==JOptionPane.YES_OPTION)
                     {
                        play(i);
                        break allLoop;
                     }
                  }
                  else
                     JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
               }  
            }
            
            for(int i=0;i<field[p].size();i++)
            {
               Card card=field[p].get(i);  
               if(card.getRect().contains(x,y))
               {
                  ArrayList<Object> preop = new ArrayList();
                  Object[] temp=card.getAbility();
                  for(Object o:temp)
                  {
                     preop.add(o);
                  }
                  
                  if(mode==1)
                  {
                     preop.add("Attack");
                  }
                  if(mode==2)
                  {
                     preop.add("Defend");
                  }
                  if(preop.size()>0)
                  {
                     Object[] options = preop.toArray();
                     Object exe=JOptionPane.showInputDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE, null,options, options[0]);
                     if(!card.isTapped()&&exe!=null)
                     {
                        doAbility((String)exe,card);
                     }
                  }
                  else
                  {
                     JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE, null);
                  }
                  break allLoop;
               }
            }
            for(Card card : lands[nP])
            {
               if(card.getRect().contains(x,y))
               {
                  JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE);
                  break allLoop;
               }
            }
            //enemy hand - does nothing            
            for(int i=0;i<field[nP].size();i++)
            {
               Card card=field[nP].get(i);
               if(card.getRect().contains(x,y))
               {
                  JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE, null);
                  break allLoop;
               }
            }
         }while(false);
      }
      /* //-target
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
      */
      repaint();
   }
   private boolean attack(int index,int which)
   {
      int nP=0;
      if(p==0)
         nP=1;
      ArrayList<Card> atking;
      ArrayList<Card> defing;
      
      //right fight code
      
      return false;
      
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
         yoDeck=new Rect(getWidth()-60,halfY-30,getWidth()-10,halfY-80);
         yoDis=new Rect(getWidth()-60,halfY-90,getWidth()-10,halfY-140);
         g.setColor(Color.gray);
         g.fillRect(0, 0, getWidth(), getHeight());
         g.setColor(Color.yellow);
         g.drawLine(0, halfY,getWidth(),halfY);
         double ref=0;
         g.setFont(new Font("Ariel",Font.PLAIN,20));
         for(int i=0;i<hand[p].size();i++)//draw hand
         {
            hand[p].get(i).setRect((int)(DIM*(ref+1)),getHeight()-DIM*2,(int)(DIM*(ref+2)),getHeight()-DIM);
            ref+=1.5;
            Rect temp = hand[p].get(i).getRect();
            g = setColor(g,hand[p].get(i));
            if(hand[p].get(i).getType().equals("land"))
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
            int xDim=0;
            int yDim=0;
            if(field[p].get(i).isTapped())
               xDim+=DIM/3;
            else
               yDim+=DIM/3;
            field[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5))+xDim,halfY+DIM*(dis+1)+yDim);
            ref+=2;
            Rect temp = field[p].get(i).getRect();
            g = setColor(g,field[p].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
            g = setTextColor(g);
            g.drawString("",temp.getLeft()+10,temp.getBottom()-5);
         }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY+DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY+DIM*(dis+1.5)));
         ref=0;
         dis+=2;
         for(int i=0;i<lands[p].size();i++) //draw wells
         {
            int xDim=0;
            int yDim=0;
            if(lands[p].get(i).isTapped())
               xDim+=DIM/3;
            else
               yDim+=DIM/3;
            lands[p].get(i).setRect((int)(DIM*(ref+4)),halfY+DIM*dis,(int)(DIM*(ref+5))+xDim,halfY+DIM*(dis+1)+yDim);
            ref+=2;
            Rect temp = lands[p].get(i).getRect();
            g = setColor(g,lands[p].get(i));
            g.fillRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
         }
      
         int nP=0;
         nP=0;
         if(p==0)
            nP=1;//nP is not-player number
         dis=1;//y ref
         ref=0;//x ref
         for(int i=0;i<field[nP].size();i++)
         {
            int xDim=0;
            int yDim=0;
            if(field[nP].get(i).isTapped())
               xDim+=DIM/3;
            else
               yDim+=DIM/3;
            field[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1)-yDim,(int)(DIM*(ref+5))+xDim,halfY-DIM*dis);
            ref+=2;
            Rect temp = field[nP].get(i).getRect();
            g = setColor(g,field[nP].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
            g = setTextColor(g);
            g.drawString("",temp.getLeft()+10,temp.getBottom()-5);
         }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine(DIM*4,(int)(halfY-DIM*(dis+1.5)),(int)(getWidth()-DIM*5.5),(int)(halfY-DIM*(dis+1.5)));
         dis+=2;
         ref=0;
         for(int i=0;i<lands[nP].size();i++)
         {
            int xDim=0;
            int yDim=0;
            if(lands[nP].get(i).isTapped())
               xDim+=DIM/3;
            else
               yDim+=DIM/3;
            lands[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM*(dis+1)-yDim,(int)(DIM*(ref+5))+xDim,halfY-DIM*dis);
            ref+=2;
            Rect temp = lands[nP].get(i).getRect();
            g = setColor(g,lands[nP].get(i));
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
         for(int i=0;i<chants[p].size();i++)
         {
            chants[p].get(i).setRect(getWidth()-DIM*5,(int)(halfY+DIM*(ref)),getWidth()-DIM*4,(int)(halfY+DIM*(ref+1)));
            ref+=1.5;
            Rect temp = chants[p].get(i).getRect();
            g = setColor(g,chants[p].get(i));
            g.fillOval(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
         }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine((int)(getWidth()-DIM*5.5),halfY+DIM,(int)(getWidth()-DIM*5.5),halfY+DIM*(dis+1));
         g.drawLine((int)(getWidth()-DIM*3.5),halfY+DIM,(int)(getWidth()-DIM*3.5),halfY+DIM*(dis+1));
         ref=1;
         for(int i=0;i<chants[nP].size();i++)
         {
            chants[nP].get(i).setRect(getWidth()-DIM*5,(int)(halfY-DIM*(ref)),getWidth()-DIM*4,(int)(halfY-DIM*(ref+1)));
            ref+=1.5;
            Rect temp = chants[nP].get(i).getRect();
            g = setColor(g,chants[nP].get(i));
            g.fillOval(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight());
         }
         g.setColor(Color.LIGHT_GRAY);
         g.drawLine((int)(getWidth()-DIM*5.5),halfY-DIM,(int)(getWidth()-DIM*5.5),halfY-DIM*(dis+1));
         g.drawLine((int)(getWidth()-DIM*3.5),halfY-DIM,(int)(getWidth()-DIM*3.5),halfY-DIM*(dis+1));
         
         g.setFont(new Font("Arial",Font.PLAIN,15));
         g.setColor(Color.white);
         g.drawString(mana[p][0]+"",DIM,halfY+25);
         g.setColor(Color.blue);
         g.drawString(mana[p][1]+"",DIM,halfY+45);
         g.setColor(Color.green);
         g.drawString(mana[p][2]+"",DIM,halfY+65);
         g.setColor(Color.red);
         g.drawString(mana[p][3]+"",DIM,halfY+85);
         g.setColor(Color.black);
         g.drawString(mana[p][4]+"",DIM,halfY+105);
         g.setColor(Color.lightGray);
         g.drawString(mana[p][5]+"",DIM,halfY+125);
      
         g.setColor(Color.yellow);
         g.drawString(p+1+" | "+pNum,DIM,halfY+145);
         g.fillRect(pass.getLeft(),pass.getTop(),pass.getWidth(),pass.getHeight());
         
         g.setColor(Color.red.darker());
         g.fillRect(myDeck.getLeft(),myDeck.getTop(),myDeck.getWidth(),myDeck.getHeight());
         g.setColor(Color.white.darker());
         g.fillRect(myDis.getLeft(),myDis.getTop(),myDis.getWidth(),myDis.getHeight());
         g.setColor(Color.yellow);
         g.setFont(new Font("Dialog",Font.PLAIN,30));
         g.drawString(life[p]+"",myDis.getLeft(),myDis.getTop()+myDis.getHeight()+DIM);
         
         g.setColor(Color.red.darker());
         g.fillRect(yoDeck.getLeft(),yoDeck.getTop(),yoDeck.getWidth(),yoDeck.getHeight());
         g.setColor(Color.white.darker());
         g.fillRect(yoDis.getLeft(),yoDis.getTop(),yoDis.getWidth(),yoDis.getHeight());
         g.setColor(Color.yellow);
         g.setFont(new Font("Dialog",Font.PLAIN,30));
         g.drawString(life[nP]+"",yoDis.getLeft(),yoDis.getTop()+yoDis.getHeight()-DIM/5);
         
         String modeStr="ERROR";
         switch(mode)
         {
            case 0:
            case 3:
               modeStr="Main";
               break;
            case 1:
               modeStr="Attack";
               break;
            case 2:
               modeStr="Defend";
               break;
            default:
               break;
         }
         g.setFont(new Font("Dialog",Font.PLAIN,20));
         g.drawString(modeStr,getWidth()-70,getHeight()-10);
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