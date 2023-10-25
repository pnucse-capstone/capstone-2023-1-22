package mainframe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.List;

public class ScoreTable extends JPanel {
    String[] header = {"Total", "Name", "Maintainability", "Reliability", "Security", "Portability"};
    public ScoreTable(List children) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(!getColumnName(columnIndex).equals("Name")) {
                    return Double.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        JTable table = new JTable();
        Object[][] contents = contentBuilder(children);
        table.setModel(tableModel);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel.setDataVector(contents,header);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    public Object[][] contentBuilder(List<Object> children) {
        Object[][] contents = new Object[children.size()][6];
        int index = 0;
        for(Object child :children) {
            QualityScore qs = new QualityScore();
            String name = "";
            if(child instanceof Folder folder) {
                qs = folder.getQualityScore();
                name = folder.getName();
            } else if (child instanceof Module module) {
                qs = module.getQualityScore();
                name = module.toString();
            } else if(child instanceof Function function) {
                qs = function.getQualityScore();
                name = function.getName();
            }
            contents[index][0] = qs.totalQualityScore;
            contents[index][1] = name;
            contents[index][2] = qs.maintainabilityScore;
            contents[index][3] = qs.reliabilityScore;
            contents[index][4] = qs.securityScore;
            contents[index][5] = qs.portabilityScore;
            index++;
        }
        return contents;
    }
}
