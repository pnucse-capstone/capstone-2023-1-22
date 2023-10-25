package mainframe;

import javax.swing.*;
import java.util.*;

public class FolderViolationPanel extends ViolationPanel{
    public FolderViolationPanel(QualityScore qs, List<CodingGuideline> codingGuideLines, Object target, ViolationChart violationChart) {
        super(qs, codingGuideLines, target, violationChart);
    }

    @Override
    public void defaultTableModelSet(QualityScore qs, List<CodingGuideline> codingGuideLines) {
        Folder folder = (Folder)super.getTarget();
        Object[][] nextContent = contentBuilder("All",folder, codingGuideLines);
        String[] newHeader = {"CodingStandard", "Guideline Name", "Description", "Violation Count", "Severity", "Modules"};
        tableModel.setDataVector(nextContent, newHeader);
    }

    @Override
    public void setComboBox(QualityScore qs, List<CodingGuideline> codingGuideLines) {
        String[] type = {"All", "ISO 5055","SEI CERT C","MISRA C:2012", "Code Metric"};
        Folder folder = (Folder)super.getTarget();
        standards = new JComboBox<>(type);
        standards.addActionListener(e->{
            Object[][] nextContent = contentBuilder(standards.getSelectedItem().toString(),folder, codingGuideLines);
            String[] newHeader = {"CodingStandard", "Guideline Name", "Description", "Violation Count", "Severity", "Modules"};
            tableModel.setDataVector(nextContent, newHeader);
            table.setModel(tableModel);
            resizeTable();
        });
    }
    private Object[][] contentBuilder(String codingStandard, Folder folder, List<CodingGuideline> codingGuideLines) {
        List<Object> objects = folder.getChildren();
        Map<String, Integer> totalCount = new HashMap<>();
        Map<String, List<String>> childList = new HashMap<>();
        for(Object obj :objects) {
            Map<String, Integer> guidelineCount = new HashMap<>();
            if(obj instanceof Module module) {
                for(Function function: module.getFunctions()) {
                    guidelineSum(guidelineCount,function.getQualityScore().getGuidelineCount());
                }
                guidelineSum(guidelineCount,module.getQualityScore().getGuidelineCount());
                addData(guidelineCount, module.toString());
                setLabel(guidelineCount,childList, module.toString());
            }
            if(obj instanceof Folder childFolder) {
                if (Boolean.FALSE.equals(childFolder.getCheck())) {
                    setViolationCount(childFolder);
                }
                guidelineSum(guidelineCount,childFolder.getQualityScore().getGuidelineCount());
                addData(guidelineCount, childFolder.getName());
                setLabel(guidelineCount,childList, childFolder.getName());
            }

            guidelineSum(totalCount,guidelineCount);
        }
        violationChart.updateDataset("Highest Violation 10", dataset);
        Object[][] totalContent = super.contentBuilder(codingStandard,totalCount,codingGuideLines, Boolean.TRUE);
        Object[][] labeledTotalContent = new Object[totalContent.length][6];
        contentLabel(totalContent,labeledTotalContent,childList);

        return labeledTotalContent;
    }

    private void addData(Map<String, Integer> guidelineCount, String name) {
        int sum = 0;
        for(Map.Entry<String, Integer> entry:guidelineCount.entrySet()) {
            int value = entry.getValue();
            sum += value;
        }
        dataset.addValue(sum,"", name);
    }

    private void setLabel(Map<String, Integer> guidelineCount, Map<String, List<String>> childList, String name) {
        for(Map.Entry<String, Integer> guideline : guidelineCount.entrySet()) {
            String key = guideline.getKey();
            if(childList.containsKey(key)) {
                List<String> children = childList.get(key);
                children.add(name);
            } else {
                List<String> children = listMaker();
                children.add(name);
                childList.put(key,children);
            }
        }
    }

    private void setViolationCount(Folder folder) {
        Map<String, Integer> guidelineCount = new HashMap<>();
        List<Object> objects = folder.getChildren();
        for(Object obj :objects) {
            if(obj instanceof Module module) {
                for(Function function: module.getFunctions()) {
                    guidelineSum(guidelineCount,function.getQualityScore().getGuidelineCount());
                }
                guidelineSum(guidelineCount,module.getQualityScore().getGuidelineCount());
            }
            if(obj instanceof Folder childFolder) {
                setViolationCount(childFolder);
                guidelineSum(guidelineCount,childFolder.getQualityScore().getGuidelineCount());
            }
        }
        Map<String, Integer> originGuidelineCount = folder.getQualityScore().getGuidelineCount();
        originGuidelineCount.putAll(guidelineCount);
        folder.setCheck(true);
    }
}
