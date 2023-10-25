package mainframe;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ViolationPanel extends JPanel{
    enum Severity {Not_Measured,Low,Medium,High}
    JTable table;
    DefaultTableModel tableModel;
    JScrollPane scrollPane;
    JComboBox<String> standards;
    String[] header = {"CodingStandard", "Guideline Name", "Description", "Violation Count", "Severity"};
    ViolationChart violationChart;
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private final Object target;
    ViolationPanel(QualityScore qs, List<CodingGuideline> codingGuideLines, Object target, ViolationChart violationChart) {
        this.violationChart = violationChart;
        setLayout(new BorderLayout());
        this.target = target;

        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(getColumnName(columnIndex).equals("Violation Count")) {
                    return Integer.class;
                }
                if(getColumnName(columnIndex).equals("Severity")) {
                    return Severity.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        table = new JTable();
        defaultTableModelSet(qs,codingGuideLines);

        table.setModel(tableModel);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resizeTable();

        setComboBox(qs, codingGuideLines);
        scrollPane = new JScrollPane(table);

        add(standards, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()>1) {
                    int row = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(row);
                    int col = table.getSelectedColumn();
                    int modelCol = table.convertColumnIndexToModel(col);
                    JOptionPane.showMessageDialog(null, table.getModel().getValueAt(modelRow, modelCol));
                }
            }
        });
    }

    public void defaultTableModelSet(QualityScore qs, List<CodingGuideline> codingGuideLines) {
        Object[][] content = contentBuilder("All",qs.getGuidelineCount(),codingGuideLines, Boolean.FALSE);
        tableModel.setDataVector(content,header);
    }

    public void setComboBox(QualityScore qs, List<CodingGuideline> codingGuideLines) {
        String[] codingStandards = {"All","ISO 5055","SEI CERT C","MISRA C:2012", "Code Metric"};
        standards = new JComboBox<>(codingStandards);
        standards.addActionListener(e->{
            Object[][] nextContent = contentBuilder(Objects.requireNonNull(standards.getSelectedItem()).toString(),qs.getGuidelineCount(),codingGuideLines, Boolean.FALSE);
            tableModel.setDataVector(nextContent, header);
            table.setModel(tableModel);
            resizeTable();
        });
    }

    public Object[][] contentBuilder(String codingStandards, Map<String, Integer> guidelineCount, List<CodingGuideline> codingGuideLines, Boolean forCalculate) {
        ArrayList<Object[]> violations = new ArrayList<>();
        int index = 0;
        for(Map.Entry<String, Integer> guideline : guidelineCount.entrySet()) {
            String identifier = guideline.getKey();
            CodingGuideline codingGuideline = codingGuideLines.stream().filter(e->e.identifier.equals(identifier)).findAny().orElse(null);
            assert codingGuideline != null;
            if(codingStandards.equals("All") || codingStandards.equals(codingGuideline.codingStandard)) {
                if(Boolean.FALSE.equals(forCalculate) && codingGuideline.codingStandard.equals("Code Metric")) {
                    continue;
                }
                Object[] violation = new Object[5];
                violation[0] = codingGuideline.codingStandard;
                violation[1] = identifier;
                violation[2] = codingGuideline.description;
                violation[3] = guideline.getValue();
                switch(codingGuideline.severity) {
                    case 1 -> violation[4] = Severity.Low;
                    case 2 -> violation[4] = Severity.Medium;
                    case 3 -> violation[4] = Severity.High;
                    default -> violation[4] = Severity.Not_Measured;
                }
                violations.add(violation);
                index++;
            }
        }
        Object[][] content = new Object[index][5];
        violations.toArray(content);
        return content;
    }

    public void contentLabel(Object[][] content, Object[][] labeledContent, Map<String, List<String>> childList) {
        for(int i=0;i<content.length;i++) {
            labeledContent[i][0] = content[i][0];
            labeledContent[i][1] = content[i][1];
            labeledContent[i][2] = content[i][2];
            labeledContent[i][3] = content[i][3];
            labeledContent[i][4] = content[i][4];
            labeledContent[i][5] = childList.get(labeledContent[i][1].toString());
        }
    }

    public Object getTarget() {
        return target;
    }
    public void guidelineSum(Map<String, Integer> guidelineCount, Map<String, Integer> originGuidelineCount) {
        for (Map.Entry<String, Integer> count : originGuidelineCount.entrySet()) {
            String key = count.getKey();
            int value = count.getValue();
            if (guidelineCount.containsKey(key)) {
                int totalCount = guidelineCount.remove(key)+ value;
                guidelineCount.put(key,totalCount);
            } else {
                guidelineCount.put(key, value);
            }
        }
    }

    public void functionSum(Map<String, Integer> guidelineCount, Map<String, List<String>> functionList, Module module) {
        for(Function function:module.getFunctions()) {
            for (Map.Entry<String, Integer> count : function.getQualityScore().getGuidelineCount().entrySet()) {
                String key = count.getKey();
                int value = count.getValue();
                if (guidelineCount.containsKey(key)) {
                    int totalCount = guidelineCount.remove(key)+ value;
                    guidelineCount.put(key,totalCount);
                    List<String> functions = functionList.get(key);
                    functions.add(function.getName());
                } else {
                    guidelineCount.put(key, value);
                    List<String> functions = listMaker();
                    functions.add(function.getName());
                    functionList.put(key,functions);
                }
            }
        }
    }

    public List<String> listMaker() {
        return new ArrayList<>() {
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                int i = 0;
                for(String name: this) {
                    sb.append(name);
                    i++;
                    if(i==10) {
                        sb.append("\n");
                        i=0;
                    } else {
                        sb.append(", ");
                    }
                }
                sb.deleteCharAt(sb.length()-2);
                return sb.toString();
            }
        };
    }

    public void resizeTable() {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        /*
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        if(table.getColumnCount()==5) {
            table.getColumnModel().getColumn(2).setPreferredWidth(440);
        } else if (table.getColumnCount()==6) {
            table.getColumnModel().getColumn(2).setPreferredWidth(360);
            table.getColumnModel().getColumn(5).setPreferredWidth(80);
        }
        */
    }
}
