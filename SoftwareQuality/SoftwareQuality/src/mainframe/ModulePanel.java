package mainframe;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ModulePanel extends JPanel {
    ModulePanel(Module module, List<CodingGuideline> codingGuideLines, ViolationChart violationChart) {
        ScorePanel scorePanel = new ScorePanel(module.getQualityScore(), "Module Name", module.toString());
        setLayout(new BorderLayout(5,5));
        add(scorePanel, BorderLayout.NORTH);
        scorePanel.setVisible(true);

        ScoreTable scoreTable = new ScoreTable(module.getFunctions());
        scoreTable.setLayout(new BoxLayout(scoreTable, BoxLayout.X_AXIS));

        ModuleViolationPanel violationPanel = new ModuleViolationPanel(module.getQualityScore(), codingGuideLines, module, violationChart);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scoreTable,violationPanel);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(5);
        splitPane.setEnabled(true);
        add(splitPane);
    }
}
