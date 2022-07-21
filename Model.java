import java.awt.*;
import java.io.Serializable;
import java.util.*;

public class Model {
   enum ActionType {
      SCRIBBLE, LINE, ELLIPSE, CIRCLE, RECTANGLE, SQUARE, 
      OPEN_POLYGON, CLOSED_POLYGON, UNDO, REDO, 
      GROUP, UNGROUP, COPY, CUT, PASTE, MOVE,SAVE, LOAD
        };
   /************************************************************ */       
   public abstract class Shape implements Serializable{
      public Point fPoint;
      public Point sPoint;
      int width, height;
      public Color color;
      public int isSelected = 0;
      


      public Shape(Color clr, Point pt1, Point pt2)
      {
         color = clr;
         fPoint = new Point(Math.min(pt1.x,pt2.x), Math.min(pt1.y,pt2.y));
         sPoint = new Point(Math.max(pt1.x,pt2.x), Math.max(pt1.y,pt2.y));
         width = Math.abs(fPoint.x - sPoint.x);
         height =  Math.abs(fPoint.y - sPoint.y);

      
          
      }
      public Shape(Color clr){
         color = clr;
      }
      Shape(final Shape orig)  	// Copy constructor: Create a copy of a shape
      {
         this.fPoint = new Point(orig.fPoint);
         this.sPoint = new Point(orig.sPoint);
         this.width = orig.width;
         this.height = orig.height;
         this.color = orig.color;
      
      }

      public Shape(){}
   
         public abstract void draw(Graphics g);
         public abstract boolean contains(int x, int y);
         public abstract void move(int x, int y);
         public abstract Shape copy(); 
   }

/************************************************************ */       

   public class Scribble extends Shape {
      
      public ArrayList<Point> listOfPoints = new ArrayList<Point>();
      public int smX, bgX, smY, bgY;
      public Scribble(Color clr, Point pt1, Point pt2) {
         super(clr, pt1, pt2);
         addPoints(pt1);
         smX = pt1.x;
         bgX = pt1.x;
         smY = pt1.y;
         bgY = pt1.y;
   
      }

      private static final long serialVersionUID = 1L;
      public void addPoints(Point pt) {
         listOfPoints.add(pt);
         smX = pt.x < smX ? pt.x : smX;
         smY = pt.y < smY ? pt.y : smY;
         bgX = pt.x > bgX ? pt.x : bgX;
         bgY = pt.y > bgY ? pt.y : bgY;
 
     }

     public Scribble ( final  Scribble s){
      super((Model.Shape)s);

        this.smX = s.smX;
        this.smY = s.smY;
        this.bgX = s.bgX;
        this.bgY = s.bgY;

        this.listOfPoints = new ArrayList<Point>();
        for (Point p : s.listOfPoints) {
            this.addPoints(new Point(p));
         }

      }
      @Override
      public Shape copy(){
         return new Scribble(this);
      } 
      @Override
      public void draw(Graphics g) {
         
        Graphics2D g2d = (Graphics2D) g;
         if(this.isSelected == 1){
            g2d.setStroke(new BasicStroke(7));
            g2d.setPaint(Color.RED);  
        }
         else{
            g2d.setPaint(color);
            g2d.setStroke(new BasicStroke(5));
         }
         
         int i = 0;
         int j = 1;
         while (j < listOfPoints.size()-2 ) {
            g2d.drawLine(listOfPoints.get(i).x, listOfPoints.get(i).y, listOfPoints.get(j).x, listOfPoints.get(j).y);
            i++;
            j++;
         }
      }
      @Override
      public boolean contains(int x, int y){
         return (x <= bgX && y <= bgY && x >= smX && y >= smY) ? true : false;
         
      }
      @Override
      public  void move(int x, int y){
         smX += x;
         bgX += x;
         smY += y;
         bgY += y;
 
         for (Point p : listOfPoints) {
            p.x += x;
            p.y += y;
         }
      }

   }
/************************************************************ */       

   public class Line extends Shape {
      public Line(Color clr, Point pt1, Point pt2) {
         super(clr);
         this.fPoint = pt1;
         this.sPoint = pt2;

      }
      public Line ( final Model.Line l){
         super((Model.Shape)l);
         
      }
      @Override
      public Shape copy(){
         return new Line(this);
      }

      private static final long serialVersionUID = 1L;
      @Override
      public void draw(Graphics g) {
         Graphics2D g2d = (Graphics2D) g;
         if(this.isSelected == 1){
            g2d.setStroke(new BasicStroke(7));
            g2d.setPaint(Color.RED);  
        }
         else{
         g2d.setPaint(color);
         g2d.setStroke(new BasicStroke(5));
         }
         g2d.drawLine(fPoint.x, fPoint.y, sPoint.x, sPoint.y);
      }
      @Override
      public boolean contains(int x, int y){
         return (((sPoint.x >= x && fPoint.x <= x) || (fPoint.x >= x && sPoint.x <= x))
               && ((fPoint.y >= y && sPoint.y <= y) || (sPoint.y >= y && fPoint.y <= y))) ? true : false;
      }
      @Override
      public  void move(int x, int y){
         this.fPoint.x += x;
         this.fPoint.y += y;
         this.sPoint.x += x;
         this.sPoint.y += y;
         
      }
   }
        
   /************************************************************ */       

   public class Ellipse extends Shape {
      
      
      public Ellipse(Color clr, Point pt1, Point pt2) {
         super(clr, pt1,pt2);
      }
      public Ellipse (final Ellipse e){
         super((Model.Shape) e);
      }
      @Override
      public Shape copy(){
         return new Ellipse(this);
      }

      private static final long serialVersionUID = 1L;
      @Override
      public void draw(Graphics g) {
         Graphics2D g2d = (Graphics2D) g;
         
         if(this.isSelected == 1){
            g2d.setStroke(new BasicStroke(6));
            g2d.setPaint(Color.RED);  
        }
         else{
            g2d.setPaint(color);
         g2d.setStroke(new BasicStroke(3));
         }
         
         g2d.drawOval(fPoint.x,fPoint.y, width, height );
      }
      @Override
      public boolean contains(int x, int y){
         int sumX = fPoint.x + width;
         int sumY = fPoint.y + height;
         return (fPoint.x <= x && fPoint.y <= y && 
               x <= sumX && y <= sumY) ? true : false;
      }
      @Override
      public  void move(int x, int y){
         this.fPoint.x += x;
         this.fPoint.y += y;
         
      }
   }

/************************************************************ */       
   public class Circle extends Ellipse { 
      public Circle(Color clr, Point pt1, Point pt2) {
         super(clr, pt1, pt2);
      }
      public Circle(final Circle c){
         super((Model.Ellipse) c);
      }
     
      @Override
      public Shape copy(){
         return new Circle(this);
      }

      private static final long serialVersionUID = 1L;
      @Override
       public void draw(Graphics g) {
         Graphics2D g2d = (Graphics2D) g;
         if(this.isSelected == 1){
            g2d.setStroke(new BasicStroke(6));
            g2d.setPaint(Color.RED);  
        }
         else{
            g2d.setPaint(color);
         g2d.setStroke(new BasicStroke(3));
         }
         
         g2d.fillOval(fPoint.x,fPoint.y, width, width  );
      }
     
   }
/************************************************************ */       

    public class Rectangle extends Shape { 
      

      public Rectangle(Color clr, Point pt1, Point pt2) {
         super(clr,pt1,pt2);
         
      }
      
      public Rectangle ( final Rectangle r){
         super((Model.Shape) r);
      }

      @Override
      public Shape copy(){
         return new Rectangle(this);
      }

      private static final long serialVersionUID = 1L;
      @Override
      public void draw(Graphics g) {
         Graphics2D g2d = (Graphics2D) g;
         if(this.isSelected == 1){
            g2d.setStroke(new BasicStroke(6));
            g2d.setPaint(Color.RED);  
        }
         else{
            g2d.setPaint(color);
         g2d.setStroke(new BasicStroke(3));
         }
         
         g2d.drawRect(fPoint.x,fPoint.y, width, height );
       }
      @Override
      public boolean contains(int x, int y){
         int sumX = fPoint.x + width;
         int sumY = fPoint.y + height;
         return (fPoint.x <= x && fPoint.y <= y && 
               x <= sumX && y <= sumY) ? true : false;
      }
      @Override
      public  void move(int x, int y){
         this.fPoint.x += x;
         this.fPoint.y += y;
        
      }
   }
 /************************************************************ */       
   
    public class Square extends Rectangle { 
      public Square(Color clr, Point pt1, Point pt2) {
         super(clr, pt1, pt2);
      }

      public Square ( Square sq){
         super((Model.Rectangle) sq);
      }

      @Override
      public Shape copy(){
         return new Square(this);
      }

      private static final long serialVersionUID = 1L;

      @Override
       public void draw(Graphics g) {
         Graphics2D g2d = (Graphics2D) g;
         
         if(this.isSelected == 1){
            g2d.setStroke(new BasicStroke(6));
            g2d.setPaint(Color.RED);  
        }
         else{
            g2d.setPaint(color);
            g2d.setStroke(new BasicStroke(3));
         }
         
         g2d.fillRect(fPoint.x,fPoint.y, width, width);
      }
     
   }
/************************************************************ */       

    public class Polygon extends Shape { 
      public ArrayList<Point > PolygonPoints = new ArrayList<Point>();
      public int smX, bgX, smY, bgY;
      
      public Polygon(Color clr, Point pt1) {
         super(clr,pt1,pt1);
         addPoints(pt1);
         smX = pt1.x;     
         bgX = pt1.x;
         smY = pt1.y;
         bgY = pt1.y;

         
      }
      public Polygon ( final Polygon p){
         
         this.smX = p.smX;
         this.smY = p.smY;
         this.bgX = p.bgX;
         this.bgY = p.bgY;

         this.PolygonPoints = new ArrayList<Point>();
         for (Point pt : p.PolygonPoints) {
            this.addPoints(new Point(pt));
         }
      }

      @Override
      public Shape copy(){
         return new Polygon(this);
      }

      public void addPoints(Point pt1) {
         PolygonPoints.add(new Point(pt1));

         smX = pt1.x < smX ? pt1.x : smX;
         smY = pt1.y < smY ? pt1.y : smY;
         bgX = pt1.x > bgX ? pt1.x : bgX;
         bgY = pt1.y > bgY ? pt1.y : bgY; 
         
         width  = bgX - smX ; 
         height = bgY - smY ;
 
      } 

      public Point getFirstPoint(){
         return PolygonPoints.get(0);
      }
      public Point getLastPoint(){
         return PolygonPoints.get(PolygonPoints.size()-1);
      }

      public void addLastPoint(){
         addPoints((this.getFirstPoint()));
      }
      @Override
      public void draw(Graphics g) {
         Graphics2D g2d = (Graphics2D) g;
         if(this.isSelected == 1){
            g2d.setStroke(new BasicStroke(7));
            g2d.setPaint(Color.RED);  
        }
         else{
            g2d.setPaint(color);
            g2d.setStroke(new BasicStroke(5));
         }
         
         int i = 0;
         int j = 1;
         while (j < PolygonPoints.size() ) {
            g2d.drawLine(PolygonPoints.get(i).x, PolygonPoints.get(i).y, PolygonPoints.get(j).x, PolygonPoints.get(j).y);
            i++;
            j++;
         }
      }
      @Override
      public boolean contains(int x, int y){
         return (x <= bgX && y <= bgY && x >= smX && y >= smY) ? true : false;
         
      }
      @Override
      public  void move(int x, int y){
         smX += x;
         bgX += x;
         smY += y;
         bgY += y;
 
         for (Point p : PolygonPoints) {
            p.x += x;
            p.y += y;
         }
   }
}
      
/************************************************************ */       
// complex shape that is composed of an array of other shape types
public class Group extends Model.Shape
  {
  Vector<Model.Shape> members;      // members of the group
  public Group( Model.Shape wrapper, Vector<Model.Shape> shapes)
    {
    super(wrapper.color, wrapper.fPoint, wrapper.sPoint);	
    members = initMembers(shapes, wrapper);
    }

  public Group(final Group group)                    
    {                                   
    super((Shape)group);
    this.fPoint = group.fPoint;
    this.width = group.width;
    this.height = group.height;
    this.members = new Vector<Model.Shape>();
    for( final Enumeration<Model.Shape> e = group.members.elements(); e.hasMoreElements(); )
      {
      this.members.addElement(((Shape)e.nextElement()).copy());
      }
    }

   public Shape copy()
    {
    return new Group(this);
    }


  // initialize the members of a group by setting their coordinates to be
  // relative.

  Vector<Model.Shape> initMembers( Vector<Model.Shape> members,  Model.Shape wrapper)
    {
    final Vector<Model.Shape> v = new Vector<Model.Shape>();
    
    // find maxes and mins
    int smX = wrapper.fPoint.x + wrapper.width, smY = wrapper.fPoint.y + wrapper.height, bgX = -1, bgY = -1;
    for( final Enumeration<Model.Shape> e = members.elements(); e.hasMoreElements(); )
      {
      final Shape s = (Shape)e.nextElement();

      if( s.width >= 0 )
        {
         smX = Math.min(smX, s.fPoint.x);
         bgX = Math.max(bgX, s.fPoint.x + s.width-1);
        }
      else
        {
        smX = Math.min(smX, s.fPoint.x + s.width+1);
        bgX = Math.max(bgX, s.fPoint.x);
        }
                
      if( s.height >= 0 )
        {
        smY = Math.min(smY, s.fPoint.y);
        bgY = Math.max(bgY, s.fPoint.y + s.height-1);
        }
      else
        {
        smY = Math.min(smY, s.fPoint.y + s.height+1);
        bgY = Math.max(bgY, s.fPoint.y);
        }
      }
      

      this.fPoint.x = smX; 
      this.fPoint.y = smY;
      this.width  = bgX - smX ; 
      this.height = bgY - smY ;

   
    for( final Enumeration<Model.Shape> e = members.elements(); e.hasMoreElements(); )
      {
      final Shape s = (Shape)e.nextElement();
     
      v.addElement(s);
      }
    return v;
    }


  // draw a Group by drawing its members, with offset
   @Override 
  public void draw(final Graphics g)
    {
      Graphics2D g2d = (Graphics2D) g;
        
      
      for( final Enumeration<Model.Shape> e = members.elements(); e.hasMoreElements(); )
      {
         ((Model.Shape)e.nextElement()).draw(g2d);
      }
    }

      @Override
      public boolean contains(int x, int y){
         for( final Enumeration<Model.Shape> e = members.elements(); e.hasMoreElements(); )
         {
            if(((Model.Shape)e.nextElement()).contains(x,y)) 
               return true;
         
         }
            return false;
      }
      public  void move(int x, int y){
         for( final Enumeration<Model.Shape> e = members.elements(); e.hasMoreElements(); )
         {
            ((Model.Shape)e.nextElement()).move(x,y);

         
         }
      }
}



 /************************************************************ */       
// will be used to save shapes drawn on the canvas
   public class CanvasShapes {
      public ArrayList<Model.Shape> canvasShapes;
      CanvasShapes(){
         canvasShapes = new ArrayList<Model.Shape>();

      }

      public CanvasShapes (final CanvasShapes cShapes){
         
         this.canvasShapes = new ArrayList<Model.Shape>();
         for (Model.Shape cShape : cShapes.canvasShapes) {
            canvasShapes.add((Model.Shape)cShape.copy());
          }
      }

      public CanvasShapes copy(){
         return new CanvasShapes(this);
      }
      
      public void addToCanvas(Model.Shape s){
         canvasShapes.add(s);
      }
      public void removeLastShape(){
         canvasShapes.remove(canvasShapes.size()-1);
      }

      public void cutShape(Model.Shape s){
         canvasShapes.remove(s);
      }

   }
     
    // will be used to save a copy of a version of canvasshapes states to load later using its name
   public class SaveLoadCanvas{
      public Model.CanvasShapes savedCanvas;
      public String versionName;
      SaveLoadCanvas(String n, Model.CanvasShapes s){
         versionName = n;
         savedCanvas = s;
      }
      public String getversionName(){
         return this.versionName;
      }
      public Model.CanvasShapes getSavedCanvas(){
         return this.savedCanvas;
      }

   }
   public Stack<Model.CanvasShapes> undoStack = new Stack<Model.CanvasShapes>(); // to save multiple snapshots of canvasShapes array afer each command that changes its stetes
   public Stack<Model.CanvasShapes> redoStack = new Stack<Model.CanvasShapes>();
   public ArrayList<Model.SaveLoadCanvas> savedCanvases = new ArrayList<Model.SaveLoadCanvas>(); // represents an array of saved saveLoadCanvas obj 
                                                                                                //that holds a copy of canvasShapes obj array states along with a name entered by a user



}