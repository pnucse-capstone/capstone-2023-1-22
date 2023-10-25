package mainframe;

import java.io.Serializable;
import java.util.*;

public class QualityScore implements Serializable {
    static final int TOTAL_MAINTAINABILITY_GUIDELINE = 11;
    static final int TOTAL_MAINTAINABILITY_SEVERITY = 22;
    static final int TOTAL_PERFORMANCE_GUIDELINE = 2;
    static final int TOTAL_PERFORMANCE_SEVERITY = 3;
    static final int TOTAL_RELIABILITY_GUIDELINE = 301;
    static final int TOTAL_RELIABILITY_SEVERITY = 560;
    static final int TOTAL_SECURITY_GUIDELINE = 226;
    static final int TOTAL_SECURITY_SEVERITY = 401;
    static final int TOTAL_PORTABILITY_GUIDELINE = 59;
    static final int TOTAL_PORTABILITY_SEVERITY = 107;

    double totalQualityScore;
    double maintainabilityCount;
    double maintainabilitySeverity;
    double performanceCount;
    double performanceSeverity;
    double reliabilityCount;
    double reliabilitySeverity;
    double securityCount;
    double securitySeverity;
    double portabilityCount;
    double portabilitySeverity;

    double maintainabilityScore;
    double performanceScore;
    double reliabilityScore;
    double securityScore;
    double portabilityScore;

    private final Map<String, Integer> guidelineCount =  new HashMap<>();

    public Map<String, Integer> getGuidelineCount() {
        return guidelineCount;
    }

    QualityScore() {}
    public void setNoViolation() {
        totalQualityScore = 0;
        maintainabilityCount = 0;
        performanceCount = 0;
        reliabilityCount = 0;
        securityCount = 0;
        portabilityCount = 0;
    }
    public void violationCount(List<Violation> violations) {
        violations.forEach(e-> {
            if(guidelineCount.containsKey(e.getIdentifier())) {
                int count = guidelineCount.remove(e.getIdentifier())+1;
                guidelineCount.put(e.getIdentifier(),count);
            } else {
                guidelineCount.put(e.getIdentifier(),1);
            }
        });
    }

    public void scoreCount(List<CodingGuideline> codingGuidelines) {
        int order = 0;
        for(Map.Entry<String, Integer> entry:guidelineCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList()) {
            String identifier = entry.getKey();

            CodingGuideline guideline = codingGuidelines.stream().filter(e->identifier.equals(e.identifier)).findAny().orElse(null);
            assert guideline != null;

            int countScore;
            if(order<guidelineCount.size()*0.1) {
                countScore = 5;
            } else if(order<guidelineCount.size()*0.35) {
                countScore = 4;
            } else if(order<guidelineCount.size()*0.65) {
                countScore = 2;
            } else if(order<guidelineCount.size()*0.9) {
                countScore = 2;
            } else {
                countScore = 1;
            }

            if(Boolean.TRUE.equals(guideline.maintainability)) {
                maintainabilityCount += countScore*guideline.severity;
                maintainabilitySeverity += guideline.severity;
            }
            if(Boolean.TRUE.equals(guideline.performance)) {
                performanceCount += countScore*guideline.severity;
                performanceSeverity += guideline.severity;
            }
            if(Boolean.TRUE.equals(guideline.reliability)) {
                reliabilityCount += countScore*guideline.severity;
                reliabilitySeverity += guideline.severity;
            }
            if(Boolean.TRUE.equals(guideline.security)) {
                securityCount += countScore*guideline.severity;
                securitySeverity += guideline.severity;
            }
            if(Boolean.TRUE.equals(guideline.portability)) {
                portabilityCount += countScore*guideline.severity;
                portabilitySeverity += guideline.severity;
            }
            order++;

        }

        /*
        guidelineCount.forEach((identifier, count)-> {
            CodingGuideline guideline = codingGuidelines.stream().filter(e->identifier.equals(e.identifier)).findAny().orElse(null);
            assert guideline != null;

            int countScore;
            if(count<=1) {countScore=1;}
            else if (count<=3) {countScore=2;}
            else {countScore=3;}

            if(Boolean.TRUE.equals(guideline.maintainability)) {
                maintainabilityCount += countScore*guideline.severity;
                maintainabilitySeverity += guideline.severity;
            }
            if(Boolean.TRUE.equals(guideline.performance)) {
                performanceCount += countScore*guideline.severity;
                performanceSeverity += guideline.severity;
            }
            if(Boolean.TRUE.equals(guideline.reliability)) {
                reliabilityCount += countScore*guideline.severity;
                reliabilitySeverity += guideline.severity;
            }
            if(Boolean.TRUE.equals(guideline.security)) {
                securityCount += countScore*guideline.severity;
                securitySeverity += guideline.severity;
            }
            if(Boolean.TRUE.equals(guideline.portability)) {
                portabilityCount += countScore*guideline.severity;
                portabilitySeverity += guideline.severity;
            }
        });
        */
    }

    public void functionScoreCalculate() {
        maintainabilityScore = (1-(maintainabilityCount/TOTAL_MAINTAINABILITY_SEVERITY))*100;
        // performanceScore = (1-((performanceCount*performanceSeverity)/(3*TOTAL_PERFORMANCE_GUIDELINE*TOTAL_PERFORMANCE_SEVERITY)))*100;
        reliabilityScore = (1-(reliabilityCount/TOTAL_RELIABILITY_SEVERITY))*100;
        securityScore = (1-(securityCount/TOTAL_SECURITY_SEVERITY))*100;
        portabilityScore = (1-(portabilityCount/TOTAL_PORTABILITY_SEVERITY))*100;
        totalQualityScore = (maintainabilityScore+reliabilityScore+securityScore+portabilityScore)/4;
    }

    public void moduleScoreCalculate(List<Function> functions) {
        double functionScore = 0;
        double fMaintainabilityScore = 0;
        double fPerformanceScore = 0;
        double fReliabilityScore = 0;
        double fSecurityScore = 0;
        double fPortabilityScore = 0;
        for(Function function:functions) {
            function.getQualityScore().functionScoreCalculate();
            functionScore+=function.getQualityScore().totalQualityScore;
            fMaintainabilityScore += function.getQualityScore().maintainabilityScore;
            // fPerformanceScore += function.getQualityScore().performanceScore;
            fReliabilityScore += function.getQualityScore().reliabilityScore;
            fSecurityScore += function.getQualityScore().securityScore;
            fPortabilityScore += function.getQualityScore().portabilityScore;
        }
        functionScoreCalculate();
        if(!functions.isEmpty()) {
            totalQualityScore = 0.5 * (totalQualityScore + functionScore / functions.size());
            maintainabilityScore = 0.5 * (maintainabilityScore + fMaintainabilityScore / functions.size());
            // performanceScore = 0.5 * (performanceScore + fPerformanceScore / functions.size());
            reliabilityScore = 0.5 * (reliabilityScore + fReliabilityScore / functions.size());
            securityScore = 0.5 * (securityScore + fSecurityScore / functions.size());
            portabilityScore = 0.5 * (portabilityScore + fPortabilityScore / functions.size());
        }
    }

    public void systemScoreCalculate(List<Folder> folders) {
        List<QualityScore> qss = new ArrayList<>();
        for(Folder folder:folders) {
            qss.addAll(folder.getQualityScores());
        }
        for(QualityScore qs:qss) {
            totalQualityScore += qs.totalQualityScore;
            maintainabilityScore += qs.maintainabilityScore;
            // performanceScore += qs.performanceScore;
            reliabilityScore += qs.reliabilityScore;
            securityScore += qs.securityScore;
            portabilityScore += qs.portabilityScore;
        }
        maintainabilityScore /= qss.size();
        // performanceScore /= qss.size();
        reliabilityScore /= qss.size();
        securityScore /= qss.size();
        portabilityScore /= qss.size();
        totalQualityScore /= qss.size();
    }
}
