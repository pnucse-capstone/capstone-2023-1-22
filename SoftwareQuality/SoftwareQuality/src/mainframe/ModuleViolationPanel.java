package mainframe;

import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.util.*;
import java.util.stream.Stream;

public class ModuleViolationPanel extends ViolationPanel{
    public ModuleViolationPanel(QualityScore qs, List<CodingGuideline> codingGuideLines, Module module, ViolationChart violationChart) {
        super(qs, codingGuideLines, module, violationChart);
    }

    @Override
    public void setComboBox(QualityScore qs, List<CodingGuideline> codingGuideLines) {
        String[] type = {"Module", "Function"};
        standards = new JComboBox<>(type);
        standards.addActionListener(e->{
            Object[][] nextContent = contentBuilder(Objects.requireNonNull(standards.getSelectedItem()).toString(), qs.getGuidelineCount(), codingGuideLines, Boolean.FALSE);
            if(standards.getSelectedItem().toString().equals("Module")) {
                tableModel.setDataVector(nextContent, header);

            }
            else {
                String[] newHeader = {"CodingStandard", "Identifier", "Description", "Violation Count", "Severity", "Functions"};
                tableModel.setDataVector(nextContent,newHeader);
            }
            table.setModel(tableModel);
            resizeTable();
        });
    }

    @Override
    public Object[][] contentBuilder(String codingStandards, Map<String, Integer> guidelineCount, List<CodingGuideline> codingGuideLines, Boolean forCalculate) {
        Module module = (Module) super.getTarget();
        if(codingStandards.equals("Module") || codingStandards.equals("All")) {
            Object[][] content = super.contentBuilder("All", guidelineCount, codingGuideLines, Boolean.FALSE);
            Object[][] content2 = metricBuilder(module.getCodeMetric());
            violationChart.updateDataset("Highest Violation 10", guidelineCount, codingGuideLines);
            violationChart.setVisible(true);
            return Stream.concat(Arrays.stream(content),Arrays.stream(content2)).toArray(Object[][]::new);
        }
        return functionContentBuilder(codingGuideLines, module);
    }

    public Object[][] functionContentBuilder(List<CodingGuideline> codingGuideLines, Module module) {
        Map<String, Integer> guidelineCount = new HashMap<>();
        Map<String, List<String>> functionList = new HashMap<>();
        functionSum(guidelineCount,functionList,module);
        createData();
        violationChart.updateDataset("Highest Violation 10", dataset);

        Object[][] originContent = super.contentBuilder("All",guidelineCount,codingGuideLines, Boolean.TRUE);
        Object[][] functionContent = new Object[originContent.length][6];

        contentLabel(originContent,functionContent,functionList);

        return functionContent;
    }

    private void createData() {
        Module module = (Module)super.getTarget();
        List<Function> functions = module.getFunctions();
        for(Function function:functions) {
            Map<String, Integer> guidelineCount = function.getQualityScore().getGuidelineCount();
            int sum = 0;
            for(Map.Entry<String, Integer> entry:guidelineCount.entrySet()) {
                int value = entry.getValue();
                sum += value;
            }
            dataset.addValue(sum, "", function.getName());
        }
    }

    private Object[][] metricBuilder(Map<AbstractMap.SimpleEntry<String, Double>, Integer> codeMetric) {
        ArrayList<Object[]> metricViolation = new ArrayList<>();
        int size = 0;
        for(Map.Entry<AbstractMap.SimpleEntry<String, Double>, Integer> metric : codeMetric.entrySet()) {
            String metricName = metric.getKey().getKey();
            double metricValue = metric.getKey().getValue();
            int limitValue = metric.getValue();

            if(limitValue>metricValue) {
                size++;
                Object[] violation = new Object[5];
                violation[0] = "Code Metric";
                violation[1] = metricName;
                violation[2] = new StringBuilder(metricName + "'s Recommended Lower Limit is " + limitValue + ". But value is " +  metricValue);
                violation[3] = 1;
                violation[4] = Severity.Not_Measured;
                metricViolation.add(violation);
            }
        }
        Object[][] result = new Object[size][5];
        metricViolation.toArray(result);

        return result;
    }
}
