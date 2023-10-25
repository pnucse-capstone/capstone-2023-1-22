package mainframe;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Folder implements Serializable {
    private final String name;
    private final String path;
    private final QualityScore qualityScore = new QualityScore();

    public QualityScore getQualityScore() {
        return qualityScore;
    }
    private Boolean check = false;

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    private final List<Object> children = new ArrayList<>();

    public List<Object> getChildren() {
        return children;
    }

    private final List<QualityScore> qualityScores = new ArrayList<>();

    public Folder(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void add(String moduleName, String functionName, String metricRange, String metricName, String metricValue, int index) {
        String[] address = moduleName.split(Pattern.quote(File.separator),-1);
        StringBuilder nextPath = new StringBuilder(path + address[index + 1]);
        if(index<address.length-2) {
            Folder newFolder = (Folder)(children.stream().filter(e->folderCheck(e,address[index+1])).findAny().orElse(new Folder(address[index+1],nextPath.toString())));
            if(children.stream().noneMatch(e->folderCheck(e,address[index+1]))) {
                children.add(newFolder);
            }
            newFolder.add(moduleName, functionName, metricRange, metricName, metricValue, index+1);
        } else {
            Module newModule = (Module)(children.stream().filter(e->moduleCheck(e,moduleName)).findAny().orElse(new Module(moduleName)));
            if(children.stream().noneMatch(e->moduleCheck(e,moduleName))) {
                children.add(newModule);
                newModule.setShortName(address[index+1]);
            }
            if(metricRange.equals("File Metrics")) {
                if(metricName.equals("Comment Density")) {
                    newModule.getCodeMetric().put(new AbstractMap.SimpleEntry<>(metricName, Double.parseDouble(metricValue)),20);
                }
                if(metricName.equals("Estimated Function Coupling")) {
                    newModule.getCodeMetric().put(new AbstractMap.SimpleEntry<>(metricName, Double.parseDouble(metricValue)),20);
                }
            }
            else {
                newModule.add(functionName, metricName, metricValue);
            }
        }
    }
    public void setFileTree(DefaultMutableTreeNode parentNode) {
        for(Object object: children) {
            if(object instanceof Folder folder) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(folder);
                parentNode.add(childNode);
                folder.setFileTree(childNode);
            } else if(object instanceof Module module) {
                DefaultMutableTreeNode moduleNode = new DefaultMutableTreeNode(module);
                for (Function function : module.getFunctions()) {
                    moduleNode.add(new DefaultMutableTreeNode(function));
                }
                parentNode.add(moduleNode);
            }
        }
    }
    private Boolean folderCheck(Object object, String name) {
        boolean exist = false;
        if(object instanceof Folder folder) {
            exist = folder.getName().equals(name);
        }
        return exist;
    }

    private Boolean moduleCheck(Object object, String name) {
        boolean exist = false;
        if(object instanceof Module module) {
            exist = module.getName().equals(name);
        }
        return exist;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void scoreCalculate() {
        for(QualityScore qs:qualityScores) {
            qualityScore.totalQualityScore += qs.totalQualityScore;
            qualityScore.maintainabilityScore += qs.maintainabilityScore;
            // qualityScore.performanceScore += qs.performanceScore;
            qualityScore.reliabilityScore += qs.reliabilityScore;
            qualityScore.securityScore += qs.securityScore;
            qualityScore.portabilityScore += qs.portabilityScore;
        }
        qualityScore.maintainabilityScore /= qualityScores.size();
        // qualityScore.performanceScore /= qualityScores.size();
        qualityScore.reliabilityScore /= qualityScores.size();
        qualityScore.securityScore /= qualityScores.size();
        qualityScore.portabilityScore /= qualityScores.size();
        qualityScore.totalQualityScore /= qualityScores.size();
    }

    public Module findModule(String moduleName) {
        for(Object child: children) {
            if(child instanceof Module module) {
                if(module.getName().equals(moduleName)) {
                    return module;
                }
            }
            else if(child instanceof Folder folder) {
                Module module = folder.findModule(moduleName);
                if(module != null) {
                    return module;
                }
            }
        }
        return null;
    }

    public void setScores(List<CodingGuideline> codingGuideLines) {
        for(Object child: children) {
            if (child instanceof Module module) {
                if(!module.getViolations().isEmpty()) {
                    module.getQualityScore().violationCount(module.getViolations());
                    module.getQualityScore().scoreCount(codingGuideLines);
                } else {
                    module.getQualityScore().setNoViolation();
                }
                module.getFunctions().forEach(ex->{
                    if(!ex.getViolations().isEmpty()) {
                        ex.getQualityScore().violationCount(ex.getViolations());
                        ex.getQualityScore().scoreCount(codingGuideLines);
                    } else {
                        ex.getQualityScore().setNoViolation();
                    }
                });
                module.getQualityScore().moduleScoreCalculate(module.getFunctions());
                qualityScores.add(module.getQualityScore());
            } else if (child instanceof Folder folder) {
                folder.setScores(codingGuideLines);
                folder.scoreCalculate();
                List<QualityScore> subScores = folder.getQualityScores();
                qualityScores.addAll(subScores);
            }
        }
    }

    public List<QualityScore> getQualityScores() {
        return qualityScores;
    }
}
