import java.io.*;				
import java.util.*;
public class u
{
   public static void sop(String stuff)
   {
      System.out.print(stuff);
   }
   public static String next()
   {
      Scanner input=new Scanner(System.in);
      return input.next();
   }
   public static int nextInt()
   {
      Scanner input=new Scanner(System.in);
      return input.nextInt();
   }
   public static String nextLine()
   {
      Scanner input=new Scanner(System.in);
      return input.nextLine();
   }
   public static double nextDouble()
   {
      Scanner input=new Scanner(System.in);
      return input.nextDouble();
   }
   public static void SOP(Object stuff)
   {
      System.out.println(stuff);
   }
   public static void SOP(int thing)
   {
      System.out.println(thing);
   }
   public static void SOP(long thing)
   {
      System.out.println(thing);
   }
   public static void SOP(double thing)
   {
      System.out.println(thing);
   }
   public static int ranI(int low,int high)
   {
      return (int)(Math.random()*(high-low+1)+low);
   }
}