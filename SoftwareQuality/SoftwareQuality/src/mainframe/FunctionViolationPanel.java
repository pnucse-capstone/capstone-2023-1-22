package mainframe;

import java.util.*;
import java.util.stream.Stream;

public class FunctionViolationPanel extends ViolationPanel{
    FunctionViolationPanel(QualityScore qs, List<CodingGuideline> codingGuideLines, Function function, ViolationChart violationChart) {
        super(qs, codingGuideLines, function, violationChart);
        violationChart.updateDataset("Highest Violation 10", qs.getGuidelineCount(), codingGuideLines);
    }

    @Override
    public Object[][] contentBuilder(String codingStandards, Map<String, Integer> guidelineCount, List<CodingGuideline> codingGuideLines, Boolean forCalculate) {
        Function function = (Function) super.getTarget();
        Object[][] content = super.contentBuilder(codingStandards, guidelineCount, codingGuideLines, Boolean.FALSE);

        if(codingStandards.equals("All") || codingStandards.equals("Code Metric")) {
            Object[][] content2 = metricBuilder(function.getCodeMetric());
            return Stream.concat(Arrays.stream(content),Arrays.stream(content2)).toArray(Object[][]::new);
        }
        return content;
    }
    private Object[][] metricBuilder(Map<AbstractMap.SimpleEntry<String, Double>, Integer> codeMetric) {
        ArrayList<Object[]> metricViolation = new ArrayList<>();
        int size = 0;
        for(Map.Entry<AbstractMap.SimpleEntry<String, Double>, Integer> metric : codeMetric.entrySet()) {
            String metricName = metric.getKey().getKey();
            double metricValue = metric.getKey().getValue();
            int limitValue = metric.getValue();

            if(limitValue<metricValue) {
                size++;
                Object[] violation = new Object[5];
                violation[0] = "Code Metric";
                violation[1] = metricName;
                violation[2] = new StringBuilder(metricName + "'s Recommended Upper Limit is " + limitValue + ". But value is " +  metricValue);
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
