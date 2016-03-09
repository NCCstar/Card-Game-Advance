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
   private int[] counters=new int[2];//(atk,def)
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
   public int getStrong()
   {
      return strong;
   }
   public int[] getCounters()
   {
      return counters;
   }
   public int getTough()
   {
      return tough;
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
   public String[] getAbility()
   {
      return ability;
   }
   public int[] getCost()
   {
      return cost;
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