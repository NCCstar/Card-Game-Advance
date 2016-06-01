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
   private String name="";
   int numColor;
   private String color[];
   private String type;
   private int[] cost=new int[6];////0=white,1=blue,2=green,3=red,4=black(d),5=colorless
   
   private int strong;
   private int tough;
   private int maxHP;
   private int[] counters=new int[2];//(atk,def)
   private String[] ability;
   private String[] attribute;
   private String[] trigger;
   private boolean tapped;
   private boolean sumSick;
   private Card target;
   
   public Card()
   {
      this("1 Plains 1 w land 0 0 0 0 0 0 0 1 manaAdd_true_0_1 0 0 0");
   }
   public Card(String base)//color, type, cost
   {
      int i=0;
      String[] input = base.split(" ");
      for(int j=Integer.parseInt(input[i++]);j>0;j--)
      {
         name+=input[i++];
         if(j!=1)
            name+=" ";
      }
      numColor=Integer.parseInt(input[i++]);
      color=new String[numColor];
      for(int k=0;k<numColor;k++)
         color[k]=input[i++];
      if(color.length<=0)
         color=new String[]{"c"};
      type=input[i++];
      //mana cost
      cost[0]=Integer.parseInt(input[i++]);
      cost[1]=Integer.parseInt(input[i++]);
      cost[2]=Integer.parseInt(input[i++]);
      cost[3]=Integer.parseInt(input[i++]);
      cost[4]=Integer.parseInt(input[i++]);
      cost[5]=Integer.parseInt(input[i++]);
      //attribute
      attribute=new String[Integer.parseInt(input[i++])];
      for(int k=0;k<attribute.length;k++)
      {
         attribute[k]=input[i++];
      }
      //ability
      ability=new String[Integer.parseInt(input[i++])];
      for(int k=0;k<ability.length;k++)
      {
         ability[k]=input[i++];
      }
      trigger=new String[Integer.parseInt(input[i++])];
      for(int k=0;k<trigger.length;k++)
      {
         trigger[k]=input[i++];
      }
      //unit stuff
      strong=Integer.parseInt(input[i++]);
      tough=Integer.parseInt(input[i++]);
      
      if(type.equals("unit"))
         sumSick=true;
      maxHP=tough;
      rect = new Rect();
   }
   public boolean isTapped()
   {
      return tapped;
   }
   public void tap()
   {
      tapped=true;
   }
   public void untap()
   {
      tapped=false;
   }
   public void unSick()
   {
      sumSick=false;
   }
   public boolean isSick()
   {
      return sumSick;
   }
   public void heal()
   {
      tough=maxHP+counters[1];
   }
   public int getStrong()
   {
      return strong;
   }
   public void unCount(int n)
   {
      strong-=n;
   }
   public void tempCount(int n)
   {
      strong+=n;
      tough+=n;
   }
   public int getTough()
   {
      return tough;
   }
   public void hurt(int x)
   {
      /*
      while(counters[1]>0)
      {
         x--;
         counters[1]--;
      }
      */
      tough-=x;
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
   public String[] getTrigger()
   {
      return trigger;
   }
   public boolean conAtt(String key)//containsAttributes
   {
      for(String i:attribute)
      {
         if(i.equals(key))
            return true;
      }
      return false;
   }
   public void countUp(int n)
   {
      for(int i=0;i<n;i++)
      {
         counters[0]+=1;
         counters[1]+=1;
         strong+=1;
         tough+=1;
      }
   }
   public void doTrig(String trig)//execute given trigger if exists
   {
      String effect=null;
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
               counters[0]+=1;
               counters[1]+=1;
               strong+=1;
               tough+=1;
            }
         }
         if(exp[1].equals("untap"))
         {
            tapped=false;
         }
      }
   }
   public int[] getCost()
   {
      return cost;
   }
   public int getSumCost()
   {
      int sum=0;
      for(int i:cost)
         sum+=i;
      return sum;
   }
   public String toString()
   {
      String ans=name+"\n| ";
      if(cost[0]>0)
         ans+="White cost: "+cost[0]+" | ";
      if(cost[1]>0)
         ans+="Blue cost: "+cost[1]+" | ";
      if(cost[2]>0)
         ans+="Green cost: "+cost[2]+" | ";
      if(cost[3]>0)
         ans+="Red cost: "+cost[3]+" | ";
      if(cost[4]>0)
         ans+="Black cost: "+cost[4]+" | ";
      if(cost[5]>0)
         ans+="Colorless cost: "+cost[5]+" | ";
      if(getSumCost()>0)
         ans+="\n";
      if(attribute.length>0)
      {
         for(int i=0;i<attribute.length;i++)
         {
            ans+="- "+attribute[i]+" ";
         }
         ans+="-\n";
      }
      //if(ability.length>0)
      for(int i=0;i<ability.length;i++)
      {
         String[] exp=ability[i].split("_");
         if(exp[0].equals("manaAdd"))
         {
            ans+=": ";
            if(exp[1].equals("true"))
               ans+="Tap and add ";
            else
               ans+="Add ";
            ans+=exp[3];
            switch(Integer.parseInt(exp[2]))
            {
               case 0:
                  ans+=" white mana.\n";
                  break;
               case 1:
                  ans+=" blue mana.\n";
                  break;
               case 2:
                  ans+=" green mana.\n";
                  break;
               case 3:
                  ans+=" red mana.\n";
                  break;
               case 4:
                  ans+=" black mana.\n";
                  break;
               default:
                  ans+=" colorless mana.\n";
                  break;
            }
         }
         if(exp[0].equals("lifeAdd"))
         {
            ans+=": ";
            if(exp[1].equals("true"))
               ans+="Tap and gain ";
            else
               ans+="Gain ";
            ans+=exp[2];
            ans+=" life.\n";
         }
         if(exp[0].equals("destroy"))
         {
            String res;
            switch(exp[1])
            {
               case "atk-def":
                  res=" attacking or blocking ";
                  break;
               case "atk":
                  res=" attacking ";
                  break;
               case "def":
                  res=" blocking ";
                  break;
               default:
                  res=" ";
                  break;
            }
            ans+="Destroy target"+res+"creature.\n";
         }
      }
      for(int i=0;i<trigger.length;i++)
      {
         String[] exp=trigger[i].split("_");
         if(exp[0].equals("lifeAdd"))
         {
            ans+="When you gain life, ";            
         }
         if(exp[0].equals("onOtherEnter"))
         {
            ans+="When another unit enters the battlefield ";
         }
         if(exp[0].equals("onEnter"))
         {
            if(!type.equals("instant")&&!type.equals("sorcery"))
               ans+="When this unit enters the battlefield ";
         }
         if(exp[0].equals("onWhiteEnter"))
         {
            ans+="When you play a white card ";
         }
         if(exp[1].equals("counterAdd"))
         {
            ans+="add "+exp[2]+" +1/+1 counters.\n";
         }
         if(exp[1].equals("lifeAdd"))
         {
            ans+="gain "+exp[2]+" life.\n";
         }
         if(exp[1].equals("untap"))
         {
            ans+="untap this card.\n";
         }
      }
      if(type.equals("unit"))
      {
         ans+="Strength: "+strong+" | Toughness: "+tough+"\n";
         ans+="Counters: "+counters[0]+"/"+counters[1]+"\n";
      }
      if(tapped)
         ans+="Tapped\n";
      else
         ans+="Untapped\n";
      return ans;
   }
   public String[] getColor()
   {
      return color;
   }
   public boolean hasColor(String x)
   {
      for(String c:color)
      {
         if(c.equals(x))
            return true;
      }
      return false;
   }
}