import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

public class Controller {

    public Model myModel;
    public View myView;
    public Model.CanvasShapes myCanvas;
    public String mode = "SCRIBBLE";
    public Point sPoint, ePoint, tempPoint;
    public Model.Scribble myScribble;
    public Model.Polygon myPolygon;
    public Model.Polygon myPolyline;
    public Model.Shape groupBoundry;
    public Color groupBoundryColor = Color.lightGray;
    public boolean firstCase = true;
    public Model.Shape selectedShape = null;
    public Model.Shape cuttedShape = null;
    int prevDragX, prevDragY, cutX, cutY;
    public Model.SaveLoadCanvas saveLoadCanvas;
    

    public Controller(Model m, View v) {
        myModel = m;
        myView = v;
        myCanvas = myModel.new CanvasShapes();
        autoSave();// take snapshot of the canvasShapes
        }

    public class MouseHndller extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            
            if (selectedShape != null)
                selectedShape.isSelected = 0;
            sPoint = new Point(e.getX(), e.getY());
            updatestatusBar("DOWN PRESS YOUR MOUSE AND DRAG TO DRAW");
            switch (mode) {
                case "SCRIBBLE": updatestatusBar("DOWN PRESS YOUR MOUSE AND DRAG TO DRAW");
                    myScribble = myModel.new Scribble(myView.currentColor, sPoint, sPoint);
                    addShape(myScribble);
                    addShape(myModel.new Scribble(myView.currentColor, sPoint, sPoint));
                    break;
                case "LINE":
                    addShape(myModel.new Line(myView.currentColor, sPoint, sPoint));
                    break;
                case "ELLIPSE":
                    addShape(myModel.new Ellipse(myView.currentColor, sPoint, sPoint));
                    break;
                case "CIRCLE":
                    addShape(myModel.new Circle(myView.currentColor, sPoint, sPoint));
                    break;
                case "RECTANGLE":
                    addShape(myModel.new Rectangle(myView.currentColor, sPoint, sPoint));
                    break;
                case "SQUARE":
                    addShape(myModel.new Square(myView.currentColor, sPoint, sPoint));
                    break;
                case "OPEN_POLYGON":
                    updatestatusBar("DOUBLE CLICK TO START NEW ONE");
                    if (firstCase == true) {
                        myPolyline = myModel.new Polygon(myView.currentColor, sPoint);
                        addShape(myPolyline);
                        tempPoint = new Point(e.getX(), e.getY());
                    } else
                        tempPoint = ePoint;
                    addShape(myModel.new Line(myView.currentColor, tempPoint, tempPoint));
                    break;
                case "CLOSED_POLYGON":
                    updatestatusBar("DOUBLE CLICK TO FINSH POLYGON AND START NEW ONE");
                    if (firstCase == true) {
                        myPolygon = myModel.new Polygon(myView.currentColor, sPoint);
                        addShape(myPolygon);
                        tempPoint = new Point(e.getX(), e.getY());
                    } else
                        tempPoint = ePoint;
                    addShape(myModel.new Line(myView.currentColor, tempPoint, tempPoint));
                    break;
                case "MOVE":updatestatusBar("DOWN PRESS YOUR MOUSE ON THE CANVAS TO SELECT A SHAPE AND DRAG TO MOVE IT");
                    selectShape(sPoint.x, sPoint.y); 
                    break;
                case "COPY":updatestatusBar("DOWN PRESS YOUR MOUSE ON THE CANVAS TO SELECT A SHAPE");
                    selectShape(sPoint.x, sPoint.y);
                    break;
                case "CUT":updatestatusBar("DOWN PRESS YOUR MOUSE ON THE CANVAS TO SELECT A SHAPE");
                    selectShape(sPoint.x, sPoint.y);
                    break;
                case "GROUP":updatestatusBar("DOWN PRESS YOUR MOUSE ON THE CANVAS TO DRAW A BOUNDARY AROUN SHAPES TO GROUP THEM");
                    groupBoundry = myModel.new Rectangle(groupBoundryColor, sPoint, sPoint);
                    addShape(groupBoundry);
                    autoSave();//take snapshot
                    break;
                case "UNGROUP":updatestatusBar("DOWN PRESS YOUR MOUSE ON THE CANVAS TO SELECT A GROUP");
                    selectShape(sPoint.x, sPoint.y);
                    ungroup();
                    break;
            }
            
            updateCanvas();
        }

        public void mouseReleased(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();
            switch (mode) {
                case "SCRIBBLE":
                    myCanvas.removeLastShape();
                    autoSave();
                break;
                case "LINE":
                case "ELLIPSE":
                case "CIRCLE":
                case "RECTANGLE":
                case "SQUARE": autoSave();// take snapshot of the canvasShapes after adding new shape
                break;
                case "OPEN_POLYGON":
                    myCanvas.removeLastShape();
                    myPolyline.addPoints(ePoint);
                    autoSave(); // take snapshot of the canvasShapes after adding new Polyline shape
                    firstCase = false;
                    break;
                case "CLOSED_POLYGON":
                    myCanvas.removeLastShape();
                    myPolygon.addPoints(ePoint);
                    autoSave(); // take snapshot of the canvasShapes after adding new Polygon shape
                    firstCase = false;
                    break;
                case "MOVE":if (selectedShape != null){
                    autoSave();// take snapshot of the canvasShapes after moving a shape
                }else
                    updatestatusBar("NO SHAPE WAS SELECTED   ");
                break;
                case "CUT":
                    if (selectedShape != null){
                        cut(x, y);
                        autoSave(); // take snapshot of the canvasShapes after cutting a shape
                    }else updatestatusBar("NO SHAPE WAS SELECTED   ");
                break;
                case "PASTE":
                    paste(x - cutX, y - cutY);
                    autoSave(); // take snapshot of the canvasShapes after pasting a shape
                    break;
                case "COPY":
                    if (selectedShape != null) {
                        cutX = x;
                        cutY = y;
                        cuttedShape = selectedShape.copy();
                        updatestatusBar(cuttedShape.getClass().getSimpleName()+ "     WAS COPIED SUCCESSFULLY, CHOOSE PASTE AND CLICK ON THE CANVAS TO PASTE IT   ");
                    }else
                    updatestatusBar("NO SHAPE WAS SELECTED   ");
                break;
                case "GROUP":
                    group(2);
                break;
            }
               
            updateCanvas();

        }

        public void mouseClicked(MouseEvent e) { // no repaint

            switch (mode) {
                case "CLOSED_POLYGON":
                    if (e.getClickCount() == 2) {
                        myPolygon.addLastPoint();
                        firstCase = true;
                    }
                break;
                case "OPEN_POLYGON":
                    if (e.getClickCount() == 2) {
                        firstCase = true;
                    }
                break;
            }
        }
    }

    public class MouseMotionHandller extends MouseMotionAdapter {

        public void mouseDragged(MouseEvent e) {

            ePoint = new Point(e.getX(), e.getY());
            switch (mode) {
                case "SCRIBBLE":
                    myCanvas.removeLastShape();
                    addShape(myModel.new Scribble(myView.currentColor, sPoint, sPoint));
                    myScribble.addPoints(ePoint);
                break;
                case "LINE":
                    myCanvas.removeLastShape();
                    addShape(myModel.new Line(myView.currentColor, sPoint, ePoint));
                break;
                case "ELLIPSE":
                    myCanvas.removeLastShape();
                    addShape(myModel.new Ellipse(myView.currentColor, sPoint, ePoint));
                break;
                case "CIRCLE":
                    myCanvas.removeLastShape();
                    addShape(myModel.new Circle(myView.currentColor, sPoint, ePoint));
                break;
                case "RECTANGLE":
                    myCanvas.removeLastShape();
                    addShape(myModel.new Rectangle(myView.currentColor, sPoint, ePoint));
                break;
                case "SQUARE":
                    myCanvas.removeLastShape();
                    addShape(myModel.new Square(myView.currentColor, sPoint, ePoint));
                break;
                case "OPEN_POLYGON":
                case "CLOSED_POLYGON":
                    myCanvas.removeLastShape();
                    addShape(myModel.new Line(myView.currentColor, tempPoint, ePoint));
                break;
                case "MOVE":
                    int x = e.getX();
                    int y = e.getY();
                    if (selectedShape != null) {
                        selectedShape.move(x - prevDragX, y - prevDragY);
                        prevDragX = x;
                        prevDragY = y;
                        selectedShape.isSelected = 1;
                    }
                break;
                case "GROUP":
                    myCanvas.removeLastShape();
                    addShape(myModel.new Rectangle(Color.lightGray, sPoint, ePoint));
                break;
            }
            
            updateCanvas();
        }
    }

    public class PaintPanelHandller implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            firstCase = true;
            mode = event.getActionCommand();
            updatestatusBar(null);
            switch (mode){
                case "UNDO":
                    undo();
                break;
                case "REDO":
                    redo();
                break;
                case "SAVE":
                   save();
                break;
                case "LOAD":
                    load();
                break; 
            }
        }

    }

    // Helping functions

    public void updatestatusBar(String msg){
        if(msg == null){
            myView.stateBar.updateStatus( "CURRENT MODE:    " + mode);
        }
        else
            myView.stateBar.updateStatus( "CURRENT MODE:     " + mode + "             "+ msg );
    }
    public void addShape(Model.Shape shapeName) {
        myCanvas.addToCanvas((Model.Shape) shapeName);
    }

    public void selectShape(int x, int y) {
        if (myCanvas.canvasShapes.size() != 0) {

            for (int i = 0; i < myCanvas.canvasShapes.size(); i++) { 
                Model.Shape s = (Model.Shape) myCanvas.canvasShapes.get(i);
                s.isSelected = 0;
                if (s.contains(x, y)) {
                    selectedShape = s;
                    selectedShape.isSelected = 1;
                    prevDragX = x;
                    prevDragY = y;
                    updateCanvas();
                    return;
                }
            }
            selectedShape = null;
        }
    }

    public void autoSave(){
        myModel.undoStack.push((myCanvas.copy()));
    }

    public void updateCanvas(){
        myView.drawingCanvas.repaint();
    }

    public void undo() {
        if(myModel.undoStack.size() >= 1){
           
            myModel.redoStack.push(myModel.undoStack.pop());
           
            switch (myModel.undoStack.size()){
                case 1:
                    myCanvas.canvasShapes = new ArrayList<>();
                    updatestatusBar("NO UNDO ACTION IS AVAILABLE ");
                    updateCanvas();
                break;
                default:
                myCanvas = myModel.undoStack.pop();
                updateCanvas();
                break;
            }
            autoSave();// take snapshot of the canvasShapes after undoing action
        } else
             return;
    }
  

    public void redo() {
        if(myModel.redoStack.size() > 0){
            myCanvas.canvasShapes.clear();
            myCanvas = myModel.redoStack.pop();
            autoSave();// take snapshot of the canvasShapes after redoing action
            updateCanvas();
        
          } else {
            updatestatusBar("NO REDO ACTION IS AVAILABLE  ");
            return;
       }
       
    }

    public void paste(final int x, final int y) {
        if (cuttedShape == null && selectedShape == null)
        {
            updatestatusBar("NO SHAPE WAS COPIED OR CUTTED   ");
            return;
        }

        cuttedShape.move(x, y);
        myCanvas.addToCanvas(cuttedShape);
        updatestatusBar(cuttedShape.getClass().getSimpleName()+ "     WAS PASTED SUCCESSFULLY   ");
        cuttedShape = null;
        selectedShape = null;
    }

    public void cut(final int x, final int y) {
        if (selectedShape == null){
            updatestatusBar("NO SHAPE WAS SELECTED   ");
            return;
        }
        cutX = x;
        cutY = y;
        cuttedShape = selectedShape.copy();
        myCanvas.cutShape(selectedShape);
        updatestatusBar(cuttedShape.getClass().getSimpleName()+ "   WAS CUTTED SUCCESSFULLY, CHOOSE PASTE AND CLICK ON THE CANVAS TO PASTE IT  ");
        selectedShape = null;
    }

    void group(final int minGroupSize) {
        final Vector<Model.Shape> members = establishGroup();
        if (members.size() <= minGroupSize) {
            updatestatusBar("GROUP OF ONE SHAPE CAN'T BE CREATED  ");
            myCanvas.removeLastShape();
            groupBoundry = null;
            return;
        }
        for (final Enumeration<Model.Shape> e = members.elements(); e.hasMoreElements();)
            myCanvas.cutShape(e.nextElement());

            Model.Group groupBoundry1 = myModel.new Group(groupBoundry, members);
            myCanvas.addToCanvas(groupBoundry1);
            autoSave(); //take snapshot of the canvasShapes after adding new grouped shape
            updatestatusBar("GROUP OF " + members.size() + " SHAPES WAS CREATED SUCCESSFULLY   ");

    }

    public Vector<Model.Shape> establishGroup() {
        final Vector<Model.Shape> v = new Vector<Model.Shape>();
        // pulling the last dragged groupBoundry shape from the canvas array
        groupBoundry = myCanvas.canvasShapes.get(myCanvas.canvasShapes.size() - 1);

        for (Model.Shape shape : myCanvas.canvasShapes) {
            // check whether the shape in the canvas is inside boundary of groupBoundry
            // shape
            if (shape.fPoint.x >= groupBoundry.fPoint.x && shape.fPoint.y >= groupBoundry.fPoint.y
                & shape.fPoint.x + shape.width <= groupBoundry.fPoint.x + groupBoundry.width
                && shape.fPoint.y + shape.height <= groupBoundry.fPoint.y + groupBoundry.height) {
                
                    v.addElement(shape);
            }
        }
        return v; // put shape on new Vector
    }

    public void ungroup() {
        if (selectedShape == null){
            updatestatusBar("NO SHAPE WAS SELECTED   ");
            return;
        }
           
        if (!(selectedShape instanceof Model.Group)){
            updatestatusBar(selectedShape.getClass().getSimpleName() + "  IS NOT A GROUP   ");
             return;
        }
         
        myCanvas.canvasShapes.remove(selectedShape);  //remove the group from the canvas arrey
        Model.Group group = (Model.Group) selectedShape;
        for (final Enumeration<Model.Shape> e = group.members.elements(); e.hasMoreElements();) {
            final Model.Shape s = ((Model.Shape) e.nextElement());
            myCanvas.addToCanvas(s); // add the pulled shapes from the members vector back to the canvas array
            
        }
        autoSave(); // take snapshot of the canvasShapes after ungroup action
        updatestatusBar("UNGROUP WAS DONE SUCCESSFULLY   ");
        selectedShape = null;
    }

    public void save(){
        Model.CanvasShapes result = myCanvas.copy(); //copy the current canvas array
        String enteredName = JOptionPane.showInputDialog(myView.drawingCanvas, "Please enter a name for this canvas version");
        if(enteredName == null || enteredName.isEmpty() ){
            JOptionPane.showMessageDialog(myView.drawingCanvas, "You need to enter a name to save the current drawings.",
            "Inane error",JOptionPane.ERROR_MESSAGE);
            return;
        } 
        //create intiate new obj from SaveLoadCanvas class by sending the copied canvas array and the name recieved from the user
        saveLoadCanvas = myModel.new SaveLoadCanvas(enteredName, result);
        myModel.savedCanvases.add(saveLoadCanvas); //add saveLoadCanvas obj in savedCanvases array
        updatestatusBar("THE CURRENT CANVAS WAS SAVED SUCCESSFULLY   ");
    
    }
    public void load(){
        String [] pulledNames = new String[10];
                  
        for(int i =0; i < myModel.savedCanvases.size(); i++){ //creating array with 
            pulledNames[i] =  (String) myModel.savedCanvases.get(i).versionName.toString();
        }
        Object selectedName = JOptionPane.showInputDialog(myView.drawingCanvas,
            "Pick name", "Input", JOptionPane.QUESTION_MESSAGE, null, pulledNames,
            "Titan");
        if( selectedName == null) {
            JOptionPane.showMessageDialog(myView.drawingCanvas, "You need to choose the canvas version name.",
            "Inane error",JOptionPane.ERROR_MESSAGE);
            return;
        }
            autoSave();// take snapshot of the canvasShapes before loading saved canvasShapes
            myCanvas.canvasShapes.clear();
                    
            for(Model.SaveLoadCanvas c : myModel.savedCanvases){
                if(c.getversionName().equals((String) selectedName)){
                    myCanvas = c.getSavedCanvas().copy();
                    updateCanvas();
                    updatestatusBar("THE CHOSEN CANVAS VERSION WAS LOADED SUCCESSFULLY, NOW YOU CAN EDIT IT AND SAVE IT WITH DIFFERENT NAME TO LOAD IT LATER");
                    return;
                }
            }
            
    }

}
