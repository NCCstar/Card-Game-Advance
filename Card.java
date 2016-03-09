/*
types: well, unit, magic, chant 
constant: color, type, wCost, bCost, yCost, rCost, dCost, 
well: wPlus, bPlus, yPlus, rPlus, dPlus
unit: atk, def, ability, power
magic: effect
chant: effect
*/
public class Card
{
   private int owner;
   private Rect rect;
   private String name;
   int numColor;
   private String color[];
   private String type;
   private int wCost;
   private int bCost;
   private int yCost;
   private int rCost;
   private int dCost;
   
   //wells
   private int wPlus;
   private int bPlus;
   private int yPlus;
   private int rPlus;
   private int dPlus;
   
   //unit
   private int atk;
   private int def;
   private String ability;
   private String power;
   private boolean moved;
   
   //magic
   private String target;//unit or player
   private String effect;//to be interpreted by Tabletop.java

   //chant
   //private String target;//same var as magic, would be any, ally, or enemy
   //private String effect;//see above
   public Card()
   {
      name="Village";
      color=new String[1];
      color[0]="w";
      type="well";
      wCost=1;
      bCost=0;
      yCost=0;
      rCost=0;
      dCost=0;
      wPlus=1;
      bPlus=0;
      yPlus=0;
      rPlus=0;
      dPlus=0;
      rect=new Rect();
   }
   public Card(String base,int o)//color, type, cost
   {
      int i=0;
      owner=o;
      String[] input = base.split(" ");
      name=input[i++];
      numColor=Integer.parseInt(input[i++]);
      color=new String[numColor];
      for(int k=0;k<numColor;k++)
         color[k]=input[i++];
      type=input[i++];
      wCost=Integer.parseInt(input[i++]);
      bCost=Integer.parseInt(input[i++]);
      yCost=Integer.parseInt(input[i++]);
      rCost=Integer.parseInt(input[i++]);
      dCost=Integer.parseInt(input[i++]);
      if(type.equals("well"))
      {
         wPlus=Integer.parseInt(input[i++]);
         bPlus=Integer.parseInt(input[i++]);
         yPlus=Integer.parseInt(input[i++]);
         rPlus=Integer.parseInt(input[i++]);
         dPlus=Integer.parseInt(input[i++]);
      }
      if(type.equals("unit"))
      {
         atk=Integer.parseInt(input[i++]);
         def=Integer.parseInt(input[i++]);
         ability=input[i++];
         power=input[i++];
         if(input[i++].equals("true"))
            moved=true;
         else
            moved=false;
      }
      if(type.equals("magic")||type.equals("chant"))
      {
         target=input[i++];
         effect=input[i++];
      }
      rect = new Rect();
   }
   public String getTarget()
   {
      return target;
   }
   public String getEffect()
   {
      return effect;
   }
   public int getAtk()
   {
      return atk;
   }
   public void plusAtk(int a)
   {
      atk+=a;
   }
   public int getDef()
   {
      return def;
   }
   public void plusDef(int d)
   {
      def+=d;
   }
   public int getOwner()
   {
      return owner;
   }
   public void setOwner(int o)
   {
      owner=o;
   }
   public boolean getMoved()
   {
      return moved;
   }
   public void setMoved(boolean m)
   {
      moved=m;
   }
   public void setRect(int l, int t, int r,int b)
   {
      rect.set(l,t,r,b);
   }
   public Rect getRect()
   {
      return rect;
   }
   public String getType()
   {
      return type;
   }
   public String getName()
   {
      return name;
   }
   public String getAbility()
   {
      return ability;
   }
   public String getPower()
   {
      return power;
   }
   public int getSumCost()
   {
      return wCost+bCost+yCost+rCost+dCost;
   }
   public int getCost(String color)
   {
      if(color.equals("w"))
         return wCost;
      if(color.equals("b"))
         return bCost;
      if(color.equals("y"))
         return yCost;
      if(color.equals("r"))
         return rCost;
      if(color.equals("d"))
         return dCost;
      return 0;
   }
   public int getPlus(String color)
   {
      if(type.equals("well"))
      {
         if(color.equals("w"))
            return wPlus;
         if(color.equals("b"))
            return bPlus;
         if(color.equals("y"))
            return yPlus;
         if(color.equals("r"))
            return rPlus;
         if(color.equals("d"))
            return dPlus;
      }
      return 0;
   }
   public String toString()
   {
      String ans=name+"\n | ";
      
      if(wCost>0)
         ans+="White Energy Cost: "+wCost+" | ";
      if(bCost>0)
         ans+="Blue Energy Cost: "+bCost+" | ";
      if(yCost>0)
         ans+="Yellow Energy Cost: "+yCost+" | ";
      if(rCost>0)
         ans+="Red Energy Cost: "+rCost+" | ";
      if(dCost>0)
         ans+="Dark Energy Cost: "+dCost+" | ";
      ans+="\n";
      //well
      if(type.equals("well"))
      {
         ans+=" | ";
         if(wPlus>0)
            ans+="White Energy Plus: "+wPlus+" | ";
         if(bPlus>0)
            ans+="Blue Energy Plus: "+bPlus+" | ";
         if(yPlus>0)
            ans+="Yellow Energy Plus: "+yPlus+" | ";
         if(rPlus>0)
            ans+="Red Energy Plus: "+rPlus+" | ";
         if(dPlus>0)
            ans+="Dark Energy Plus: "+dPlus+" | ";
         ans+="\n";    
      }
      if(type.equals("unit"))
      {
         ans+=" | Atk: "+atk+" | Def: "+def+" |\n | Power: "+power+" | Ability: "+ability+" |\n | ";
         if(moved)
            ans+="Has moved. |\n";
         else
            ans+="Has not moved. |\n";
      }
      if(type.equals("magic"))
      {
         ans+=effect+"\n";
      }
      return ans;
   }
   public String[] getColor()
   {
      return color;
   }
}