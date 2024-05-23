import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.w3c.dom.*;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.List;

public class FileHandler {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Method to save the drawing as an SVG file
    public static void saveSVG(DrawingPanel drawingPanel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As");
        fileChooser.setFileFilter(new FileNameExtensionFilter("SVG Files (*.svg)", "svg"));
        int userSelection = fileChooser.showSaveDialog(drawingPanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".svg")) {
                filePath += ".svg";
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                String svgCode = drawingPanel.generateSVGCode();
                writer.write(svgCode);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(drawingPanel, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void openSVG(MainFrame mainFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open SVG File");
        FileNameExtensionFilter svgFilter = new FileNameExtensionFilter("SVG Files", "svg");
        fileChooser.addChoosableFileFilter(svgFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                org.w3c.dom.Document doc = builder.parse(fileToOpen);

                NodeList shapeNodes = doc.getElementsByTagName("rect");
                addShapesFromNodeList(mainFrame, shapeNodes, "Rectangle");

                NodeList ellipseNodes = doc.getElementsByTagName("ellipse");
                addShapesFromNodeList(mainFrame, ellipseNodes, "Ellipse");

                NodeList lineNodes = doc.getElementsByTagName("line");
                addShapesFromNodeList(mainFrame, lineNodes, "Line");

                mainFrame.getSVGTextArea().setText(readFile(fileToOpen.getAbsolutePath()));

                // Update shapes table after loading SVG
                mainFrame.getShapesTable().updateTable();
                mainFrame.getDrawingPanel().repaint();
            } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException e) {
                e.printStackTrace();
            }
        }
    }
    private static void addShapesFromNodeList(MainFrame mainFrame, NodeList nodeList, String shapeType) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node shapeNode = nodeList.item(i);
            if (shapeNode.getNodeType() == Node.ELEMENT_NODE) {
                Element shapeElement = (Element) shapeNode;

                // Check the tag name to determine the shape type
                String tagName = shapeElement.getTagName();
                switch (tagName) {
                    case "rect":
                        addRectangle(mainFrame, shapeElement);
                        break;
                    case "ellipse":
                        addEllipse(mainFrame, shapeElement);
                        break;
                    case "line":
                        addLine(mainFrame, shapeElement);
                        break;
                    default:
                        // Unsupported shape, do nothing
                        break;
                }
            }
        }
    }

    private static void addEllipse(MainFrame mainFrame, Element shapeElement) {
        int cx = Integer.parseInt(shapeElement.getAttribute("cx"));
        int cy = Integer.parseInt(shapeElement.getAttribute("cy"));
        int rx = Integer.parseInt(shapeElement.getAttribute("rx"));
        int ry = Integer.parseInt(shapeElement.getAttribute("ry"));

        Ellipse2D ellipse = new Ellipse2D.Double(cx - rx, cy - ry, rx * 2, ry * 2);
        mainFrame.getDrawingPanel().addShape(ellipse);
        mainFrame.getShapesTable().addShape("Ellipse", new ShapeInfo(ellipse));
    }

    private static void addRectangle(MainFrame mainFrame, Element shapeElement) {
        int x = Integer.parseInt(shapeElement.getAttribute("x"));
        int y = Integer.parseInt(shapeElement.getAttribute("y"));
        int width = Integer.parseInt(shapeElement.getAttribute("width"));
        int height = Integer.parseInt(shapeElement.getAttribute("height"));

        Rectangle2D rectangle = new Rectangle2D.Double(x, y, width, height);
        mainFrame.getDrawingPanel().addShape(rectangle);
        mainFrame.getShapesTable().addShape("Rectangle", new ShapeInfo(rectangle));
    }

    private static void addLine(MainFrame mainFrame, Element shapeElement) {
        int x1 = Integer.parseInt(shapeElement.getAttribute("x1"));
        int y1 = Integer.parseInt(shapeElement.getAttribute("y1"));
        int x2 = Integer.parseInt(shapeElement.getAttribute("x2"));
        int y2 = Integer.parseInt(shapeElement.getAttribute("y2"));

        Line2D line = new Line2D.Double(x1, y1, x2, y2);
        mainFrame.getDrawingPanel().addShape(line);
        mainFrame.getShapesTable().addShape("Line", new ShapeInfo(line));
    }


    // Helper method to read file content
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }


    public static void saveJSON(List<Shape> shapes) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save JSON File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".json")) {
                filePath += ".json";
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                GSON.toJson(shapes, writer);
            } catch (IOException e) {
                e.printStackTrace(); // Handle error
            }
        }
    }
}
