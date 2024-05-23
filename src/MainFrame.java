
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import java.awt.*;

import java.util.List;

public class MainFrame extends JFrame {

    private DrawingPanel drawingPanel;
    private ShapesTable shapesTable;
    private ShapeInfoTable shapeInfoTable;
    private final JTextArea svgTextArea;

    public MainFrame(){
        setTitle("SVG Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

//      Control panel - zavedení všech tlačítek
        JPanel controlPanel = new JPanel();

        JButton rectangleButton = new JButton("Rectangle");
        rectangleButton.addActionListener(e -> {
            drawingPanel.setCurrentShapeType(ShapeType.RECTANGLE);
        });

        JButton ellipseButton = new JButton("Ellipse");
        ellipseButton.addActionListener(e -> {
            drawingPanel.setCurrentShapeType(ShapeType.ELLIPSE);
        });

        JButton lineButton = new JButton("Line");
        lineButton.addActionListener(e -> {
            drawingPanel.setCurrentShapeType(ShapeType.LINE);
        });


        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> {
            drawingPanel.clearShapes();
            shapesTable.clearTable();
            shapeInfoTable.clearShapeInfo();
            drawingPanel.updateSVGCode();
        });



        JButton saveSVGButton = new JButton("Save SVG");
        saveSVGButton.addActionListener(e -> {
            FileHandler.saveSVG(drawingPanel);
        });

        JButton saveJSONButton = new JButton("Save JSON");
        saveJSONButton.addActionListener(e -> {
            List<Shape> shapes = drawingPanel.getShapes();
            FileHandler.saveJSON(shapes);
        });

        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            FileHandler.openSVG(this);
        });

        //přídání tlačítek do control panelu
        controlPanel.add(rectangleButton);
        controlPanel.add(ellipseButton);
        controlPanel.add(lineButton);
        controlPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        controlPanel.add(clearButton);
        controlPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        controlPanel.add(saveSVGButton);
        controlPanel.add(saveJSONButton);
        controlPanel.add(openButton);


        //Vytvoření multipanelu - slouží k zobrazení tabulek a textu
        JPanel multiPanel = new JPanel(new GridLayout(3, 0));


        //Tabulka se seznamem nakreslených tvarů, listener pro zobrazení druhé tabulky s infomracemi o tvaru
        shapesTable = new ShapesTable();
        shapesTable.getTable().getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                int selectedRow = shapesTable.getTable().getSelectedRow();
                if (selectedRow != -1) {
                    ShapeInfo shapeInfo = shapesTable.getShapeInfo(selectedRow);
                    shapesTable.setSelectedShapeIndex(selectedRow);
                    shapeInfoTable.displayShapeInfo(shapeInfo);
                }
            }
        });

        //druhá tabulka s infem o tvaru, listener ktery se stara o to kdyz je tabulka upravena
        shapeInfoTable = new ShapeInfoTable(this);
        TableModel model = shapeInfoTable.getModel();
        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column != -1) {
                    int newValue = Integer.parseInt(model.getValueAt(row, column).toString());
                    shapeInfoTable.handleCellEdit(shapesTable.getSelectedShapeIndex(), row, newValue);
                }
            }
        });


//      panel s textem SVG
        svgTextArea = new JTextArea();
        svgTextArea.setEditable(true);
        JScrollPane scrollPaneSVG = new JScrollPane(svgTextArea);



        multiPanel.add(shapesTable);
        multiPanel.add(shapeInfoTable);
        multiPanel.add(scrollPaneSVG);




        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setShapesTable(shapesTable);
        drawingPanel.setSVGTextArea(svgTextArea);


        add(multiPanel, BorderLayout.EAST);
        add(drawingPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);

    }

    public ShapesTable getShapesTable() {
        return shapesTable;
    }

    public JTextArea getSVGTextArea() {
        return svgTextArea;
    }

    public DrawingPanel getDrawingPanel(){
        return drawingPanel;
    }
}
