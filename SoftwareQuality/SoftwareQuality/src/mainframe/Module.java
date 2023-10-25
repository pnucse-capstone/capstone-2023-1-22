package mainframe;

import java.io.Serializable;
import java.util.*;

public class Module implements Serializable {
    private final String name;
    private String shortName;
    private final QualityScore qualityScore = new QualityScore();
    private final Map<AbstractMap.SimpleEntry<String, Double>, Integer> codeMetric =  new HashMap<>();
    private final List<Function> functions = new ArrayList<>();
    private final List<Violation> violations = new ArrayList<>();

    public void add(String functionName, String metricName, String metricValue) {
        Function function = functions.stream().filter(e->e.getName().equals(functionName)).findAny().orElse(new Function(functionName));
        if(functions.stream().noneMatch(e->e.getName().equals(functionName))) {
            functions.add(function);
        }
        Map<AbstractMap.SimpleEntry<String, Double>, Integer> codeMetrics = function.getCodeMetric();
        for(Map.Entry<AbstractMap.SimpleEntry<String, Double>, Integer> metric : codeMetrics.entrySet()) {
            if(metric.getKey().getKey().equals(metricName)) {
                int limit = codeMetrics.remove(metric.getKey());
                double toDouble;
                if(!metricName.equals("Language Scope")) {
                    toDouble = Integer.parseInt(metricValue);
                } else {
                    toDouble = Double.parseDouble(metricValue);
                }
                codeMetrics.put(new AbstractMap.SimpleEntry<>(metricName,toDouble),limit);
                break;
            }
        }
    }

    public void add(Violation violation) {
        String functionName = violation.getFunction();
        if(functionName.equals("File Scope")) {
            violations.add(violation);
            return;
        }
        Function function = functions.stream().filter(e->e.getName().equals(functionName)).findAny().orElse(new Function(functionName));
        function.add(violation);
    }
    public Module(String name) {
        this.name = name;
    }

    public void setMetricViolation() {

    }

    @Override
    public String toString() {
        return shortName;
    }
    public String getName() {
        return name;
    }
    public void setShortName(String name) {
        shortName = name;
    }
    public QualityScore getQualityScore() {
        return qualityScore;
    }
    public List<Function> getFunctions() {
        return functions;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public Map<AbstractMap.SimpleEntry<String, Double>, Integer> getCodeMetric() {
        return codeMetric;
    }
}
