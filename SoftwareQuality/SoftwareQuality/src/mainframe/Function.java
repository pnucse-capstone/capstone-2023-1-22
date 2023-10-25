package mainframe;

import java.io.Serializable;
import java.util.*;

public class Function implements Serializable {
    private final String name;
    private final Map<AbstractMap.SimpleEntry<String, Double>, Integer> codeMetric =  new HashMap<>();
    private final List<Violation> violations = new ArrayList<>();
    private final QualityScore qualityScore = new QualityScore();

    public List<Violation> getViolations() {
        return violations;
    }

    public void add(Violation violation) {
        violations.add(violation);
    }

    public Function(String name) {
        this.name = name;
        initializeMetric();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
    public QualityScore getQualityScore() {
        return qualityScore;
    }
    public Map<AbstractMap.SimpleEntry<String, Double>, Integer> getCodeMetric() {
        return codeMetric;
    }
    private void initializeMetric() {
        codeMetric.put(new AbstractMap.SimpleEntry<>("Cyclomatic Complexity",0.0),10);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Language Scope",0.0),4);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Number of Call Levels",0.0),4);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Number of Calling Functions",0.0),5);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Number of Called Functions",0.0),7);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Number of Function Parameters",0.0),5);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Number of Goto Statements",0.0),0);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Number of Instructions",0.0),50);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Number of Paths",0.0),80);
        codeMetric.put(new AbstractMap.SimpleEntry<>("Number of Return Statements",0.0),1);
    }
}
