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
   private int[] cost=new int[6];////0=white,1=blue,2=green,3=red,4=black(d),5=colorless
   
   private int strong;
   private int tough;
   private String[] ability;
   private boolean tapped;
   
   public Card()
   {
      name="Village";
      color=new String[1];
      color[0]="w";
      type="land";
      rect=new Rect();
   }
   public Card(String base)//color, type, cost
   {
      int i=0;
      String[] input = base.split(" ");
      name=input[i++];
      numColor=Integer.parseInt(input[i++]);
      color=new String[numColor];
      for(int k=0;k<numColor;k++)
         color[k]=input[i++];
      type=input[i++];
      //mana cost
      cost[0]=Integer.parseInt(input[i++]);
      cost[1]=Integer.parseInt(input[i++]);
      cost[2]=Integer.parseInt(input[i++]);
      cost[3]=Integer.parseInt(input[i++]);
      cost[4]=Integer.parseInt(input[i++]);
      cost[5]=Integer.parseInt(input[i++]);
      //ability
      String[] ability=new String[Integer.parseInt(input[i++])];
      for(int k=0;k<ability.length;k++)
      {
         ability[k]=input[i++];
      }
      //unit stuff
      strong=Integer.parseInt(input[i++]);
      tough=Integer.parseInt(input[i++]);
      
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