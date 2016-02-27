public class Rect
{
   private int top;
   private int left;
   private int bottom;
   private int right;
   private int width;
   private int height;
   public Rect()
   {
      left=0;
      top=0;
      right=0;
      bottom=0;
      width=0;
      height=0;
   }
   public Rect(int l,int t,int r,int b)
   {
      left=l;
      top=t;
      right=r;
      bottom=b;
      width=right-left;
      height=bottom-top;
   }
   public boolean intersects(Rect ex)
   {
      return false;
   }
   public boolean contains(int xCo,int yCo)
   {
      if(xCo>left&&xCo<right&&yCo<bottom&&yCo>top)
         return true;
      return false;
   }
   public int getLeft(){
      return left;}
   public int getTop(){
      return top;}
   public int getRight(){
      return right;}
   public int getBottom(){
      return bottom;}
   public int getWidth(){
      return width;}
   public int getHeight(){
      return height;}
   public void set(int l, int t, int r,int b)
   {
      left=l;
      top=t;
      right=r;
      bottom=b;
      width=right-left;
      height=bottom-top;
   }
}