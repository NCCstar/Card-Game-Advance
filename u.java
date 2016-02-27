import java.io.*;				
import java.util.*;
public class u
{
   public void sop(String stuff)
   {
      System.out.print(stuff);
   }
   public String next()
   {
      Scanner input=new Scanner(System.in);
      return input.next();
   }
   public int nextInt()
   {
      Scanner input=new Scanner(System.in);
      return input.nextInt();
   }
   public String nextLine()
   {
      Scanner input=new Scanner(System.in);
      return input.nextLine();
   }
   public double nextDouble()
   {
      Scanner input=new Scanner(System.in);
      return input.nextDouble();
   }
   public void SOP(String stuff)
   {
      System.out.println(stuff);
   }
   public void SOP(int thing)
   {
      System.out.println(thing);
   }
   public void SOP(long thing)
   {
      System.out.println(thing);
   }
   public void SOP(double thing)
   {
      System.out.println(thing);
   }
   public int ranI(int low,int high)
   {
      return (int)(Math.random()*(high-low+1)+low);
   }
}