import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class ShapeInfoTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;

    public ShapeInfoTable(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        this.mainFrame = mainFrame;
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Attribute");
        tableModel.addColumn("Value");
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void displayShapeInfo(ShapeInfo shapeInfo) {
        clearShapeInfo();
        if (shapeInfo != null) {
            Shape shape = shapeInfo.getShape();
            if (shape instanceof Rectangle2D) {
                Rectangle2D rectangle = (Rectangle2D) shape;
                tableModel.addRow(new Object[]{"X", (int) rectangle.getX()});
                tableModel.addRow(new Object[]{"Y", (int) rectangle.getY()});
                tableModel.addRow(new Object[]{"Width", (int) rectangle.getWidth()});
                tableModel.addRow(new Object[]{"Height", (int) rectangle.getHeight()});
            } else if (shape instanceof Ellipse2D) {
                Ellipse2D ellipse = (Ellipse2D) shape;
                tableModel.addRow(new Object[]{"Center X", (int) ellipse.getCenterX()});
                tableModel.addRow(new Object[]{"Center Y", (int) ellipse.getCenterY()});
                tableModel.addRow(new Object[]{"Width", (int) ellipse.getWidth()});
                tableModel.addRow(new Object[]{"Height", (int) ellipse.getHeight()});
            } else if (shape instanceof Line2D) {
                Line2D line = (Line2D) shape;
                tableModel.addRow(new Object[]{"X1", (int) line.getX1()});
                tableModel.addRow(new Object[]{"Y1", (int) line.getY1()});
                tableModel.addRow(new Object[]{"X2", (int) line.getX2()});
                tableModel.addRow(new Object[]{"Y2", (int) line.getY2()});
            }
        }
    }

    public TableModel getModel() {
        return table.getModel();
    }

    public void clearShapeInfo() {
        tableModel.setRowCount(0);
    }

    void handleCellEdit(int selectedShapeIndex, int row, int newValue) {
        if (selectedShapeIndex != -1) {
            updateShapeAttribute(selectedShapeIndex, row, newValue);
        }
    }

    private void updateShapeAttribute(int selectedShapeIndex, int row, int newValue) {
        // get selected shape by index
        Shape shape = mainFrame.getDrawingPanel().getShapes().get(selectedShapeIndex);

        // Update the attribute value based on the row index
        if (shape instanceof Rectangle2D) {
            Rectangle2D rectangle = (Rectangle2D) shape;
            switch (row) {
                case 0: // X
                    rectangle.setRect(newValue, rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                    break;
                case 1: // Y
                    rectangle.setRect(rectangle.getX(), newValue, rectangle.getWidth(), rectangle.getHeight());
                    break;
                case 2: // Width
                    rectangle.setRect(rectangle.getX(), rectangle.getY(), newValue, rectangle.getHeight());
                    break;
                case 3: // Height
                    rectangle.setRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), newValue);
                    break;
                default:
                    // Handle other rows if needed
                    break;
            }
        } else if (shape instanceof Ellipse2D) {
            Ellipse2D ellipse = (Ellipse2D) shape;
            switch (row) {
                case 0: // Center X
                    ellipse.setFrame(newValue - ellipse.getWidth() / 2, ellipse.getY(), ellipse.getWidth(), ellipse.getHeight());
                    break;
                case 1: // Center Y
                    ellipse.setFrame(ellipse.getX(), (newValue) - ellipse.getHeight() / 2, ellipse.getWidth(), ellipse.getHeight());
                    break;
                case 2: // Width (Radius X)
                    ellipse.setFrame(ellipse.getX(), ellipse.getY(), newValue, ellipse.getHeight());
                    break;
                case 3: // Height (Radius Y)
                    ellipse.setFrame(ellipse.getX(), ellipse.getY(), ellipse.getWidth(), newValue);
                    break;
                default:
                    // Handle other rows if needed
                    break;
            }
        } else if (shape instanceof Line2D) {
            Line2D line = (Line2D) shape;
            switch (row) {
                case 0: // X1
                    line.setLine((newValue), line.getY1(), line.getX2(), line.getY2());
                    break;
                case 1: // Y1
                    line.setLine(line.getX1(), (newValue), line.getX2(), line.getY2());
                    break;
                case 2: // X2
                    line.setLine(line.getX1(), line.getY1(), (newValue), line.getY2());
                    break;
                case 3: // Y2
                    line.setLine(line.getX1(), line.getY1(), line.getX2(), (newValue));
                    break;
                default:
                    // Handle other rows if needed
                    break;
            }
        }

        // Update drawing panel shapes list
        mainFrame.getDrawingPanel().getShapes().set(selectedShapeIndex, shape);
        // update shapesTable shapeInfo list
        mainFrame.getShapesTable().updateTableList(selectedShapeIndex, new ShapeInfo(shape));
        // Repaint the drawing panel to reflect the changes
        mainFrame.getDrawingPanel().repaint();
        // Update the SVG text area
        mainFrame.getDrawingPanel().updateSVGCode();
    }
}
