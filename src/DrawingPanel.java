import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JPanel {

    private List<Shape> shapes;
    private ShapeType currentShapeType;
    private Point startPoint;
    private Point endPoint;
    private Color color = Color.BLACK;
    private int strokeWidth = 1;
    private ShapesTable shapesTable;
    private JTextArea svgTextArea;



    public DrawingPanel() {
        shapes = new ArrayList<>();
        currentShapeType = ShapeType.RECTANGLE;
        startPoint = null;
        endPoint = null;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                endPoint = startPoint;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint();
                if (currentShapeType != null && startPoint != null) {
                    Shape shape = null;
                    ShapeInfo shapeInfo = null;
                    if (currentShapeType == ShapeType.RECTANGLE) {
                        shape = new Rectangle2D.Double(
                                Math.min(startPoint.x, endPoint.x),
                                Math.min(startPoint.y, endPoint.y),
                                Math.abs(endPoint.x - startPoint.x),
                                Math.abs(endPoint.y - startPoint.y)
                        );
                        shapeInfo = new ShapeInfo(shape);
                    } else if (currentShapeType == ShapeType.ELLIPSE) {
                        shape = new Ellipse2D.Double(
                                Math.min(startPoint.x, endPoint.x),
                                Math.min(startPoint.y, endPoint.y),
                                Math.abs(endPoint.x - startPoint.x),
                                Math.abs(endPoint.y - startPoint.y)
                        );
                        shapeInfo = new ShapeInfo(shape);
                    } else if (currentShapeType == ShapeType.LINE) {
                        shape = new Line2D.Double(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
                        shapeInfo = new ShapeInfo(shape);
                    }
                    shapes.add(shape);
                    if (shapesTable != null && shapeInfo != null) {
                        shapesTable.addShape(currentShapeType.toString(), shapeInfo);
                    }
                    updateSVGCode();
                    repaint();
                }
                startPoint = null;
                endPoint = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                repaint();
            }
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (Shape shape : shapes) {
            g2d.draw(shape);
        }
        if (startPoint != null && endPoint != null && currentShapeType != null) {
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.setColor(color);
            if (currentShapeType == ShapeType.RECTANGLE) {
                g2d.drawRect(
                        Math.min(startPoint.x, endPoint.x),
                        Math.min(startPoint.y, endPoint.y),
                        Math.abs(endPoint.x - startPoint.x),
                        Math.abs(endPoint.y - startPoint.y)
                );
            } else if (currentShapeType == ShapeType.ELLIPSE) {
                g2d.drawOval(
                        Math.min(startPoint.x, endPoint.x),
                        Math.min(startPoint.y, endPoint.y),
                        Math.abs(endPoint.x - startPoint.x),
                        Math.abs(endPoint.y - startPoint.y)
                );
            } else if (currentShapeType == ShapeType.LINE) {
                g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
            }
        }
    }

    public void setCurrentShapeType(ShapeType shapeType) {
        currentShapeType = shapeType;
    }

    public List<Shape> getShapes() {
        return shapes;
    }
    public void addShape(Shape shape){
        shapes.add(shape);
        repaint();
    }
    public void clearShapes() {
        shapes.clear();
        updateSVGCode();
        repaint();
    }



    public void setSVGTextArea(JTextArea svgTextArea) {
        this.svgTextArea = svgTextArea;
        this.svgTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDrawingFromSVG();
                updateShapesTable(shapes);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDrawingFromSVG();
                updateShapesTable(shapes);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDrawingFromSVG();
                updateShapesTable(shapes);
            }
        });
    }
    private void updateDrawingFromSVG() {
        // Get the updated SVG code from the text area
        String updatedSVGCode = svgTextArea.getText();
        // Load the updated SVG code into the drawing panel
        loadSVG(updatedSVGCode);
    }

    public void setShapesTable(ShapesTable shapesTable) {
        this.shapesTable = shapesTable;
    }

    void updateSVGCode() {
        if (svgTextArea != null) {
            String svgCode = generateSVGCode();
            svgTextArea.setText(svgCode);
        }
    }


    // Method to generate SVG code based on the current state of the drawing
    String generateSVGCode() {
        StringBuilder svgCode = new StringBuilder();
        svgCode.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        svgCode.append("<svg viewBox=\"0 0 ").append(getWidth()).append(" ").append(getHeight()).append("\">\n");
        svgCode.append("    <g>\n");
        for (Shape shape : shapes) {
            svgCode.append("        ").append(getShapeSVG(shape)).append("\n");
        }
        svgCode.append("    </g>\n");
        svgCode.append("</svg>");
        return svgCode.toString();
    }

    // Method to generate SVG code for a specific shape
    private String getShapeSVG(Shape shape) {
        if (shape instanceof Rectangle2D) {
            Rectangle2D rectangle = (Rectangle2D) shape;
            return String.format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" stroke-width=\"%d\" fill=\"%s\" stroke=\"%s\"/>",
                    (int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight(), strokeWidth, colorToHex(color), colorToHex(color));
        } else if (shape instanceof Ellipse2D) {
            Ellipse2D ellipse = (Ellipse2D) shape;
            int cx = (int) ellipse.getCenterX();
            int cy = (int) ellipse.getCenterY();
            int rx = (int) (ellipse.getWidth() / 2);
            int ry = (int) (ellipse.getHeight() / 2);
            return String.format("<ellipse cx=\"%d\" cy=\"%d\" rx=\"%d\" ry=\"%d\" stroke-width=\"%d\" fill=\"%s\" stroke=\"%s\"/>",
                    cx, cy, rx, ry, strokeWidth, colorToHex(color), colorToHex(color));
        } else if (shape instanceof Line2D) {
            Line2D line = (Line2D) shape;
            int x1 = (int) line.getX1();
            int y1 = (int) line.getY1();
            int x2 = (int) line.getX2();
            int y2 = (int) line.getY2();
            return String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"%d\" stroke=\"%s\"/>",
                    x1, y1, x2, y2, strokeWidth, colorToHex(color));
        }
        return "";
    }

    // Method to convert color to hexadecimal string
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public void updateShapesTable(List<Shape> shapes) {
        shapesTable.clearTable();
        for (Shape shape : shapes) {
            if (shape instanceof Rectangle2D) currentShapeType = ShapeType.RECTANGLE;
            else if (shape instanceof Ellipse2D) currentShapeType = ShapeType.ELLIPSE;
            else if (shape instanceof Line2D) currentShapeType = ShapeType.LINE;
            shapesTable.addShape(currentShapeType.toString(), new ShapeInfo(shape));
        }
    }

    // Method to load SVG content into the drawing panel
    public void loadSVG(String svgContent) {
        List<Shape> loadedShapes = parseSVGContent(svgContent);
        if (loadedShapes != null) {
            shapes.clear();
            shapes.addAll(loadedShapes);
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Error loading SVG content", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to parse SVG content and create shapes
    private List<Shape> parseSVGContent(String svgContent) {
        List<Shape> shapes = new ArrayList<>();
        try {
            Document document = SVGUtils.loadXMLFromString(svgContent);
            if (document != null) {
                NodeList nodeList = document.getElementsByTagName("svg");
                if (nodeList.getLength() > 0) {
                    Node svgNode = nodeList.item(0);
                    if (svgNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element svgElement = (Element) svgNode;
                        NodeList shapeList = svgElement.getElementsByTagName("*");
                        for (int i = 0; i < shapeList.getLength(); i++) {
                            Node shapeNode = shapeList.item(i);
                            if (shapeNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element shapeElement = (Element) shapeNode;
                                String nodeName = shapeElement.getNodeName().toLowerCase();
                                switch (nodeName) {
                                    case "rect":
                                        int x = Integer.parseInt(shapeElement.getAttribute("x"));
                                        int y = Integer.parseInt(shapeElement.getAttribute("y"));
                                        int width = Integer.parseInt(shapeElement.getAttribute("width"));
                                        int height = Integer.parseInt(shapeElement.getAttribute("height"));
                                        Shape rectangle = new Rectangle2D.Double(x, y, width, height);
                                        setCurrentShapeType(ShapeType.RECTANGLE);
                                        shapes.add(rectangle);
                                        break;
                                    case "ellipse":
                                        int cx = Integer.parseInt(shapeElement.getAttribute("cx"));
                                        int cy = Integer.parseInt(shapeElement.getAttribute("cy"));
                                        int rx = Integer.parseInt(shapeElement.getAttribute("rx"));
                                        int ry = Integer.parseInt(shapeElement.getAttribute("ry"));
                                        Shape ellipse = new Ellipse2D.Double(cx - rx, cy - ry, rx * 2, ry * 2);
                                        setCurrentShapeType(ShapeType.ELLIPSE);
                                        shapes.add(ellipse);
                                        break;
                                    case "line":
                                        int x1 = Integer.parseInt(shapeElement.getAttribute("x1"));
                                        int y1 = Integer.parseInt(shapeElement.getAttribute("y1"));
                                        int x2 = Integer.parseInt(shapeElement.getAttribute("x2"));
                                        int y2 = Integer.parseInt(shapeElement.getAttribute("y2"));
                                        Shape line = new Line2D.Double(x1, y1, x2, y2);
                                        setCurrentShapeType(ShapeType.LINE);
                                        shapes.add(line);
                                        break;
                                    default:
                                        // Unsupported shape
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return shapes;
    }
}
