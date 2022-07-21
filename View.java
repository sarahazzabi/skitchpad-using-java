import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class View extends JFrame {

    public DrawingCanvas drawingCanvas;
    public ColorGrid colorGrid;
    public PaintToolsPanel paintToolsPanel;
    public StateBar stateBar;
    public Model model;
    public Controller controller;
    public Color currentColor;
    

    public static void main(String[] args) {
        new View();
    }

    public View() {
        model = new Model();
        controller = new Controller(model, this);
        this.setSize(1024, 768);
        this.setTitle("Sketchpad Project < Sarah Azzabi >");
        setLayout(new BorderLayout());
        drawingCanvas = new DrawingCanvas();
        colorGrid = new ColorGrid();
        paintToolsPanel = new PaintToolsPanel();
        stateBar = new StateBar();

        add(colorGrid, BorderLayout.NORTH);
        add(stateBar, BorderLayout.SOUTH);
        add(drawingCanvas, BorderLayout.CENTER);
        add(paintToolsPanel, BorderLayout.WEST);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        synchronizeStartingColor();
    }

    public void synchronizeStartingColor() {
        colorGrid.currentClrPanel.setBackground(Color.black);
        colorGrid.color = colorGrid.currentClrPanel.getBackground();
        currentColor = colorGrid.color;
    }

    /***************************************************************** */

    public class ColorGrid extends JPanel {
        ColorOptionBox[] colorOptions;
        protected JPanel currentClrPanel;
        protected Color[] colors;
        protected Color color;

        public ColorGrid() {
            setBackground(Color.lightGray);
            setPreferredSize(new Dimension(92, 92));
            setLayout(new BorderLayout());

            Color[] colors = { new Color(0, 204, 0), Color.yellow, Color.red, Color.pink, Color.orange, Color.magenta,
                    new Color(255, 153, 0), Color.green, Color.gray, Color.cyan, Color.blue, Color.black,
                    new Color(123, 111, 222), new Color(0.5f, 0.3f, 0.1f), Color.white, new Color(150),
                    new Color(153, 0, 0), new Color(51, 204, 255) };

            color = Color.black;

            currentClrPanel = new JPanel();
            currentClrPanel.setBackground(Color.black);
            currentClrPanel.setPreferredSize(new Dimension(92, 92));

            JPanel colorGrid = new JPanel();
            colorGrid.setBackground(Color.lightGray);
            colorGrid.setLayout(new GridLayout(2, 18, 6, 6));

            colorOptions = new ColorOptionBox[colors.length];

            for (int i = 0; i < colorOptions.length; i++) {
                colorOptions[i] = new ColorOptionBox(colors[i]);
                colorGrid.add(colorOptions[i]);
            }

            JPanel holder = new JPanel();
            holder.setBackground(Color.lightGray);
            holder.setLayout(new BorderLayout(6, 6));

            holder.add(currentClrPanel, BorderLayout.WEST);
            holder.add(colorGrid, BorderLayout.CENTER);

            JPanel holder2 = new JPanel();
            holder2.setLayout(new BorderLayout());
            JPanel p1 = new JPanel();
            p1.setBackground(Color.lightGray);
            JPanel p2 = new JPanel();
            p2.setBackground(Color.lightGray);
            JPanel p3 = new JPanel();
            p3.setBackground(Color.lightGray);
            JPanel p4 = new JPanel();
            p4.setBackground(Color.lightGray);
            holder2.add(p1, BorderLayout.WEST);
            holder2.add(p2, BorderLayout.EAST);
            holder2.add(p3, BorderLayout.SOUTH);
            holder2.add(p4, BorderLayout.NORTH);

            holder2.add(holder, BorderLayout.CENTER);
            add(holder2, BorderLayout.WEST);

        }

        public class ColorOptionBox extends JPanel {
            Color boxColor;

            public ColorOptionBox(Color clr) {
                boxColor = clr;
                setBackground(boxColor);
                setPreferredSize(new Dimension(50, 50));
                addMouseListener(new ColorOptionBoxHandller());
            }

            private class ColorOptionBoxHandller extends MouseAdapter {

                public void mousePressed(MouseEvent event) {
                    colorGrid.currentClrPanel.setBackground(boxColor);
                    currentColor = boxColor;

                }
            }
        }
    }

    /****************************************************************** */
    public class DrawingCanvas extends JComponent {
            DrawingCanvas() {
            setBackground(Color.white);
            setPreferredSize(new Dimension(1050, 900));
            setLayout(new BorderLayout());
            JPanel boarder = new JPanel();
            boarder.setBackground(Color.lightGray);
            add(boarder, BorderLayout.EAST);
            addMouseListener(controller.new MouseHndller());
            addMouseMotionListener(controller.new MouseMotionHandller());
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));

            if (controller.myCanvas.canvasShapes.size() != 0) {
                for (Model.Shape s : controller.myCanvas.canvasShapes) {
                    s.draw(g2);
                }
                g2.setColor(Color.black);
                g2.drawRect(0, 0, getSize().width - 1, getSize().height - 1); // border
            }
            else {
                return;
            } 

        }

    }

    /**************************************************************** */

    public class PaintToolsPanel extends JPanel {

        protected ToolButton[] buttons;

        public PaintToolsPanel() {
            setBackground(Color.lightGray);
            setPreferredSize(new Dimension(180, 0));
            setLayout(new BorderLayout(8, 8));

            JPanel toolPanel = new JPanel();
            toolPanel.setLayout(new GridLayout(9, 2));
            toolPanel.setBackground(Color.lightGray);
            toolPanel.setPreferredSize(new Dimension(300, 200));

            buttons = new ToolButton[18];
            Icon pencil = new ImageIcon(getClass().getResource("pencil.png"));
            Icon line = new ImageIcon(getClass().getResource("line.png"));
            Icon rectangle = new ImageIcon(getClass().getResource("rectangle.png"));
            Icon square = new ImageIcon(getClass().getResource("square.png"));
            Icon ellipse = new ImageIcon(getClass().getResource("ellipse.png"));
            Icon circle = new ImageIcon(getClass().getResource("circle.png"));
            Icon polygon = new ImageIcon(getClass().getResource("polygon.png"));
            Icon polyline = new ImageIcon(getClass().getResource("polyline.png"));
            Icon undo = new ImageIcon(getClass().getResource("undo.png"));
            Icon redo = new ImageIcon(getClass().getResource("redo.png"));
            Icon move = new ImageIcon(getClass().getResource("move.png"));
            Icon copy = new ImageIcon(getClass().getResource("copy.png"));
            Icon cut = new ImageIcon(getClass().getResource("cut.png"));
            Icon paste = new ImageIcon(getClass().getResource("paste.png"));
            Icon group = new ImageIcon(getClass().getResource("group.png"));
            Icon ungroup = new ImageIcon(getClass().getResource("ungroup.png"));
            Icon save = new ImageIcon(getClass().getResource("save.png"));
            Icon load = new ImageIcon(getClass().getResource("load.png"));

            buttons[0] = new ToolButton(pencil, Model.ActionType.SCRIBBLE);
            buttons[1] = new ToolButton(line, Model.ActionType.LINE);
            buttons[2] = new ToolButton(rectangle, Model.ActionType.RECTANGLE);
            buttons[3] = new ToolButton(square, Model.ActionType.SQUARE);
            buttons[4] = new ToolButton(ellipse, Model.ActionType.ELLIPSE);
            buttons[5] = new ToolButton(circle, Model.ActionType.CIRCLE);
            buttons[6] = new ToolButton(polygon, Model.ActionType.CLOSED_POLYGON);
            buttons[7] = new ToolButton(polyline, Model.ActionType.OPEN_POLYGON);
            buttons[8] = new ToolButton(undo, Model.ActionType.UNDO);
            buttons[9] = new ToolButton(redo, Model.ActionType.REDO);
            buttons[10] = new ToolButton(move, Model.ActionType.MOVE);
            buttons[11] = new ToolButton(copy, Model.ActionType.COPY);
            buttons[12] = new ToolButton(cut, Model.ActionType.CUT);
            buttons[13] = new ToolButton(paste, Model.ActionType.PASTE);
            buttons[14] = new ToolButton(group, Model.ActionType.GROUP);
            buttons[15] = new ToolButton(ungroup, Model.ActionType.UNGROUP);
            buttons[16] = new ToolButton(save, Model.ActionType.SAVE);
            buttons[17] = new ToolButton(load, Model.ActionType.LOAD);

            for (int i = 0; i < buttons.length; i++) {
                toolPanel.add(buttons[i]);
            }

            JPanel pan = new JPanel();
            pan.setLayout(new BorderLayout());
            JPanel b1 = new JPanel();
            b1.setBackground(Color.lightGray);
            JPanel b2 = new JPanel();
            b2.setBackground(Color.lightGray);
            pan.add(b1, BorderLayout.WEST);
            pan.add(b2, BorderLayout.EAST);
            pan.add(toolPanel, BorderLayout.CENTER);
            add(pan, BorderLayout.CENTER);
        }

        public class ToolButton extends JButton {
            JLabel label;
            Model.ActionType tool;

            public ToolButton(Icon icon, Model.ActionType tool) {
                label = new JLabel(icon);
                this.setLayout(new BorderLayout());
                add(label);
                this.tool = tool;
                this.setActionCommand((String) this.tool.toString());
                addActionListener(controller.new PaintPanelHandller());
            }

        }
    }

    /**************************************************************** */
    public class StateBar extends JPanel {

        public JLabel msg;

        public StateBar() {
            setBackground(Color.white);
            setPreferredSize(new Dimension(100, 50));
            setLayout(new BorderLayout());
            msg = new JLabel();
            this.updateStatus("CURRENT MODE:    " + controller.mode);
            msg.setBorder(BorderFactory.createLineBorder(Color.darkGray));
            JPanel boarder1 = new JPanel();
            boarder1.setBackground(Color.lightGray);
            JPanel boarder2 = new JPanel();
            boarder2.setBackground(Color.lightGray);
            JPanel boarder3 = new JPanel();
            boarder3.setBackground(Color.lightGray);
            JPanel boarder4 = new JPanel();
            boarder4.setBackground(Color.lightGray);
            add(boarder1, BorderLayout.WEST);
            add(boarder2, BorderLayout.EAST);
            add(boarder3, BorderLayout.SOUTH);
            add(boarder4, BorderLayout.NORTH);
            add(msg, BorderLayout.CENTER);
        }

        public void updateStatus(String text) {
            msg.setText(text);
        }

    }
}
