package mainframe;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class System implements Serializable {
    private String name;

    public System(String name) {
        this.name = name;
    }
    private final QualityScore qualityScore = new QualityScore();
    private int numberOfRecursion;
    private int numberOfDirectRecursion;
    private final List<Folder> folders = new ArrayList<>();

    private final List<Violation> violations = new ArrayList<>();

    public void add(String moduleName, String functionName, String metricRange, String metricName, String metricValue) {
        String[] address = moduleName.split(Pattern.quote(File.separator),-1);
        Folder newFolder = folders.stream().filter(e->address[1].equals(e.getName())).findAny().orElse(new Folder(address[1],address[1]));
        if(folders.stream().noneMatch(e->e.getName().equals(address[1]))) {
            folders.add(newFolder);
        }
        newFolder.add(moduleName, functionName, metricRange, metricName, metricValue, 1);
    }

    public void add(Violation violation) {
        String moduleName = violation.getModule();
        Module module;
        for(Folder folder: folders) {
            module = folder.findModule(moduleName);
            if(module!=null) {
                module.add(violation);
                return;
            }
        }
    }

    public void scoreCalculate(List<CodingGuideline> codingGuideLines) {
        folders.forEach(e->{
            e.setScores(codingGuideLines);
            e.scoreCalculate();
        });
        qualityScore.systemScoreCalculate(folders);
    }

    public void setTree(DefaultMutableTreeNode root) {
        for(Folder folder: folders) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(folder);
            root.add(childNode);
            folder.setFileTree(childNode);
        }
    }

    @Override
    public String toString() {
        return name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public QualityScore getQualityScore() {
        return qualityScore;
    }
    public int getNumberOfRecursion() {
        return numberOfRecursion;
    }
    public void setNumberOfRecursion(int numberOfRecursion) {
        this.numberOfRecursion = numberOfRecursion;
    }
    public int getNumberOfDirectRecursion() {
        return numberOfDirectRecursion;
    }
    public void setNumberOfDirectRecursion(int numberOfDirectRecursion) {
        this.numberOfDirectRecursion = numberOfDirectRecursion;
    }

    public List<Violation> getViolations() {
        return violations;
    }
}
