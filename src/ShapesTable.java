import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShapesTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<ShapeInfo> shapeInfoList;
    private int selectedShapeIndex = -1;

    public ShapesTable() {
        setLayout(new BorderLayout());
        shapeInfoList = new ArrayList<>();
        tableModel = new DefaultTableModel(new Object[]{"Number", "Shape"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }


    public void addShape(String shapeType, ShapeInfo shapeInfo) {
        int row = tableModel.getRowCount();
        tableModel.addRow(new Object[]{row + 1, shapeType});
        shapeInfoList.add(shapeInfo);

        // Select the newly added shape
        table.setRowSelectionInterval(row, row);
    }

    // update the shapeInfoList
    public void updateTableList(int rowIndex, ShapeInfo shapeInfo) {
        shapeInfoList.set(rowIndex, shapeInfo);
    }

    public void clearTable() {
        tableModel.setRowCount(0);
        shapeInfoList.clear();
    }
    public ShapeInfo getShapeInfo(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < shapeInfoList.size()) {
            return shapeInfoList.get(rowIndex); // Return the ShapeInfo object at the specified index
        } else {
            return null;
        }
    }

    public JTable getTable() {
        return table;
    }

    public void updateTable() {
        tableModel.fireTableDataChanged(); // Notify the table model that the data has changed
    }


    public void setSelectedShapeIndex(int selectedShapeIndex) {
        this.selectedShapeIndex = selectedShapeIndex;
    }

    public int getSelectedShapeIndex() {
        return selectedShapeIndex;
    }

}
