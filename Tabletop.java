/*
To Do Short: enchants, artifacts

TO DO Long: Spells: Instant/Sorcery

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
   private boolean hideHand=false;
   private Map<Integer, Integer> defOn = new TreeMap();

   
   private int[][] mana=new int[2][6];//[player][color] - tap land to add, cast cards to remove
   //0=white,1=blue,2=green,3=red,4=black(d),5=colorless(n)
   
   private ArrayList<Card>[] lands;
   private ArrayList<Card>[] hand;
   private ArrayList<Card>[] deck;
   private ArrayList<Card>[] dis;
   private ArrayList<Card>[] field;
   private ArrayList<Card>[] chants;
   private ArrayList<Card>[] battle;
   
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
      chants=(ArrayList<Card>[])new ArrayList[pNum];
      battle=(ArrayList<Card>[])new ArrayList[pNum];
      
      for(int i=0;i<pNum;i++)
      {
         lands[i] = new ArrayList<Card>();
         hand[i] = new ArrayList<Card>();
         deck[i] = new ArrayList<Card>();
         dis[i] = new ArrayList<Card>();
         field[i] = new ArrayList<Card>();
         chants[i] = new ArrayList<Card>();
         battle[i] = new ArrayList<Card>();
      }
      String last = "";
      for(int i=0;i<pNum;i++)
      {
         Scanner input = new Scanner(new FileReader("Decks/"+decks[i]));
         while(input.hasNextLine())
         {
            String line=input.nextLine();
            try{
               int repeat=Integer.parseInt(line);
               for(int j=0;j<repeat;j++)
               {
                  deck[i].add(new Card(last));
               }
            }
            catch(Exception e)
            {
               deck[i].add(new Card(line));
               last=line;
            }
         }
      }
      draw(7,0);
      draw(7,1); 
      field[0].add(new Card("1 Plains 1 w land 0 0 0 0 0 0 0 1 manaAdd_true_0_1 0 0 0"));
      field[0].add(new Card("1 Plains 1 w land 0 0 0 0 0 0 0 1 manaAdd_true_0_1 0 0 0"));
      field[0].add(new Card("1 Plains 1 w land 0 0 0 0 0 0 0 1 manaAdd_true_0_1 0 0 0"));
      field[0].add(new Card("1 Plains 1 w land 0 0 0 0 0 0 0 1 manaAdd_true_0_1 0 0 0"));
      //add mulligan
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
      if(temp.getType().equals("land")&&landPlayed[p])
      {
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
            if(mana[p][5]>0)
               preop.add("Colorless");
            
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
                  mana[p][1]--;
                  break;
               case "Green":
                  mana[p][2]--;
                  break;
               case "Red":
                  mana[p][3]--;
                  break;
               case "Black":
                  mana[p][4]--;
                  break;
               case "Colorless":
                  mana[p][5]--;
                  break;
            }
         }
         if(temp.hasColor("w"))
         {
            checkAllTrig("onWhiteEnter");
         }
         if(temp.getType().equals("land"))
         {
            lands[p].add(temp);
            //------------------------------------------------------------------------------------------------------------------------------------------------
            landPlayed[p]=true;//----------------------------------------------------------------------------------------------------------------------------
            //------------------------------------------------------------------------------------------------------------------------------------------------
            hand[p].remove(i);
         }
         else
            if(temp.getType().equals("unit"))
            {
               doAbility("onOtherEnter",temp);
               checkTrig("onEnter",temp);
               field[p].add(temp);
               if(temp.conAtt("Haste"))
               {
                  temp.unSick();
               }
               hand[p].remove(i);
            }
            else
               if(temp.getType().equals("enchant"))
               {
                  chants[p].add(temp);
                  hand[p].remove(i);
               }
               else
                  if(temp.getType().equals("instant"))
                  {
                     String[] triggers=temp.getTrigger();
                     for(int t=0;t<triggers.length;t++)
                        doAbility(triggers[t],temp);
                  }
         
         return true;
      }
      return false;
   }
   private Card getTarget(String param)
   {
      return null;
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
      if(abi[0].equals("destroy"))
      {
         Card target=getTarget(abi[1]);
         target.hurt(target.getTough()+1);
         allLoop:
         for(int k=0;k<pNum;k++)
            for(int i=0;i<field[p].size();i++)
            {
               if(field[p].get(i).getTough()<0)
               {
                  field[p].remove(i);
                  break allLoop;  
               }
            }
      }
      if(abi[0].equals("onOtherEnter"))
      {
         int nP=(p+1)%2;
         for(Card c:field[nP])
         {
            checkTrig(abi[0],c);
         }
      }
      checkAllTrig(abi[0]);
   }
   private void checkAllTrig(String abi)
   {
      for(Card c:field[p])
      {
         checkTrig(abi,c);
      }
      for(Card c:chants[p])
      {
         checkTrig(abi,c);
      }
   }
   private void checkTrig(String trig,Card c)
   {
      String effect=null;
      String[] trigger = c.getTrigger();
      for(String i:trigger)
      {
         if(i.split("_")[0].equals(trig))
         {
            effect=i;
         }
      }
      if(effect!=null)
      {
         String[] exp=effect.split("_");
         if(exp[1].equals("counterAdd"))
         {
            for(int i=0;i<Integer.parseInt(exp[2]);i++)
            {
               c.countUp(1);
            }
         }
         if(exp[1].equals("untap"))
         {
            c.untap();
         }
         if(exp[1].equals("lifeAdd"))
         {
            life[p]+=Integer.parseInt(exp[2]);
         }
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
   private void fight()
   {
      int nP=(p+1)%2;
      
      Map<Integer, ArrayList<Integer>> atkOn = u.flip(defOn);
      for(int i=0;i<battle[p].size();i++)
      {
         ArrayList<Integer> defers=atkOn.get(i);
         if(defers==null)
         {
            life[nP]-=battle[p].get(i).getStrong();
         }
         else
         {
            for(int k=0;k<defers.size();k++)
            {
               if(battle[p].get(i).getTough()>0)
                  damage(battle[nP].get(defers.get(k)),battle[p].get(i));
            }
         }
      }
      
      for(int i=battle[p].size()-1;i>=0;i--)
      {
         if(battle[p].get(i).getTough()>0)
            field[p].add(battle[p].get(i));
         else
            dis[p].add(battle[p].get(i));
         battle[p].remove(i);
      }
      for(int i=battle[nP].size()-1;i>=0;i--)
      {
         if(battle[nP].get(i).getTough()>0)
            field[nP].add(battle[nP].get(i));
         else
            dis[nP].add(battle[nP].get(i));
         battle[nP].remove(i);
      }
   }
   private void damage(Card c1,Card c2)
   {
      c1.hurt(c2.getStrong());
      c2.hurt(c1.getStrong());
   }
   private void nextTurn()
   {  
      for(int k=0;k<6;k++)
      {
         mana[p][k]=0;
      }
      //check passing player's hand size (<=10)
      while(hand[p].size()>10)
      {
         Object[] options = (Object[])(hand[p].toArray());
         Object exe=JOptionPane.showInputDialog(null,"Your hand is too large.\nChoose a card to discard:","Discard Card",JOptionPane.INFORMATION_MESSAGE, null,options, options[0]);
         int rid=-1;
         for(int i=0;i<hand[p].size();i++)
         {
            if(exe==hand[p].get(i))
            {
               rid=i;
               break;
            }
         }
         dis[p].add(hand[p].get(rid));
         hand[p].remove(rid);
      }
      for(int i=0;i<field[p].size();i++)
      {
         field[p].get(i).heal();
      }
      //who's dead
      int nP=(p+1)%2;
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
      hideHand=true;
      repaint();
      JOptionPane.showMessageDialog(null,"Player "+(p+1)+" take control.");
      hideHand=false;
      repaint();
      p=(p+1)%pNum;
      nP=(p+1)%pNum;
      draw(1,p);
      mode=0;
      //upkeep
      landPlayed[p]=false;
      for(int i=0;i<field[p].size();i++)
      {
         field[p].get(i).untap();
         field[p].get(i).unSick();
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
   //master IO program - whenever mouse is clicked will check if on a card and execute
   public void mouseClicked(MouseEvent e)
   {
      int nP=(p+1)%2;
      int x=e.getX();
      int y=e.getY();
      if(input==null)
      {
         if(pass.contains(x,y))//mode: 0=main1,1=atk,2=def,3=main2
         {
            mode++;
            if(mode==2)
            {
               hideHand=true;
               repaint();
               JOptionPane.showMessageDialog(null,"Player "+(p+1)+" take control.");
               p++;
               p%=2;
               hideHand=false;
               repaint();
            }
            if(mode==3)
            {
               hideHand=true;
               repaint();
               JOptionPane.showMessageDialog(null,"Player "+(p+1)+" take control.");
               p++;
               p%=2;
               fight();
               hideHand=false;
               repaint();
            }
            if(mode>=4)
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
                  if(mode==1&&!card.isSick())
                  {
                     preop.add("Attack");
                  }
                  if(mode==2)
                  {
                     boolean canBlock=false;
                     if(card.conAtt("Flying")||card.conAtt("Reach"))
                     {
                        canBlock=true;
                     }
                     else
                     {
                        for(Card c:battle[nP])
                        {
                           if(!c.conAtt("Flying"))
                           {
                              canBlock=true;
                              break;
                           }
                        }
                     }
                     if(canBlock)
                        preop.add("Block");
                  }
                  if(preop.size()>0)
                  {
                     Object[] options = preop.toArray();
                     Object exe=JOptionPane.showInputDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE, null,options, options[0]);
                     if(exe!=null)
                     {
                        if(exe.equals("Attack"))
                        {
                           battle[p].add(card);
                           if(!card.conAtt("Vigilance"))
                              card.tap();
                           field[p].remove(i);
                        }
                        else
                           if(exe.equals("Block"))
                           {
                              battle[p].add(card);
                              card.tap();
                              field[p].remove(i);
                              Object[] toBlock = battle[nP].toArray();
                              for(int j=0;j<toBlock.length;j++)
                              {
                                 if(!battle[nP].get(j).conAtt("Flying"))
                                    toBlock[j]=j+1+".) "+toBlock[j];
                              }
                              Object input=JOptionPane.showInputDialog(null,"Which card to block?","Block",JOptionPane.INFORMATION_MESSAGE, null,toBlock, toBlock[0]);
                              int rid=-1;
                              for(int j=0;j<toBlock.length;j++)
                              {
                                 if(input==toBlock[j])
                                 {
                                    rid=j;
                                    break;
                                 }
                              }
                              defOn.put(i,rid);
                           }
                           else
                              if(!card.isTapped())
                              {
                                 doAbility((String)exe,card);
                              }
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
            for(int i=0;i<battle[nP].size();i++)
            {
               Card card=battle[nP].get(i);
               if(card.getRect().contains(x,y))
               {
                  JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE, null);
                  break allLoop;
               }
            }
            for(int i=0;i<battle[p].size();i++)
            {
               Card card=battle[p].get(i);
               if(card.getRect().contains(x,y))
               {
                  JOptionPane.showMessageDialog(null,card.toString(),card.getName(),JOptionPane.INFORMATION_MESSAGE, null);
                  break allLoop;
               }
            }
         }while(false);
      }
      repaint();
   }
   private boolean attack(int index,int which)
   {
      int nP=(1+p)%2;
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
            if(hideHand)
               g.setColor(Color.red.darker());
            else
               g = setColor(g,hand[p].get(i));
            if(hand[p].get(i).getType().equals("land")||hideHand)
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
            if(!hideHand)
               g.drawString(hand[p].get(i).getSumCost()+"",temp.getLeft()+10,temp.getBottom()-5);
         }
         ref=0;
         int dis=2;
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
         ref=0;
         for(int i=0;i<battle[p].size();i++)
         {
            int xDim=0;
            int yDim=0;
            if(battle[p].get(i).isTapped())
               xDim+=DIM/3;
            else
               yDim+=DIM/3;
            battle[p].get(i).setRect((int)(DIM*(ref+4)),halfY+5,(int)(DIM*(ref+5))+xDim,halfY+DIM+yDim+5);
            ref+=2;
            Rect temp = battle[p].get(i).getRect();
            g = setColor(g,battle[p].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
         }
         
         int nP=(p+1)%2;//nP is not-player number
         dis=2;//y ref
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
            //g.setColor(new Color(u.ranI(0,255),u.ranI(0,255),u.ranI(0,255)));
            g.setColor(Color.red.darker());
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
         ref=0;
         for(int i=0;i<battle[nP].size();i++)
         {
            int xDim=0;
            int yDim=0;
            if(battle[nP].get(i).isTapped())
               xDim+=DIM/3;
            else
               yDim+=DIM/3;
            battle[nP].get(i).setRect((int)(DIM*(ref+4)),halfY-DIM-yDim-5,(int)(DIM*(ref+5))+xDim,halfY-5);
            ref+=2;
            Rect temp = battle[nP].get(i).getRect();
            g = setColor(g,battle[nP].get(i));
            g.fillRoundRect(temp.getLeft(),temp.getTop(),temp.getWidth(),temp.getHeight(),DIM/2,DIM/2);
         }
         
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
      switch(card.getColor()[0])
      {
         case "w":
            g.setColor(Color.white);
            break;
         case "b":
            g.setColor(Color.blue);
            break;
         case "g":
            g.setColor(Color.yellow);
            break;
         case "r":
            g.setColor(Color.red);
            break;
         case "d":
            g.setColor(Color.black);
            break;
         default:
            g.setColor(new Color(204,207,188));
            break;
      }  
      if(card.isSick())
      {
         Color c=g.getColor();
         g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),220)); 
      }     
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