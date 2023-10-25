package mainframe;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FolderPanel extends JPanel {

    public FolderPanel(Folder folder, List<CodingGuideline> codingGuideLines, ViolationChart violationChart) {
        ScorePanel scorePanel = new ScorePanel(folder.getQualityScore(), "Package Name", folder.getName());
        setLayout(new BorderLayout(5,5));
        add(scorePanel, BorderLayout.NORTH);
        scorePanel.setVisible(true);

        ScoreTable scoreTable = new ScoreTable(folder.getChildren());
        scoreTable.setLayout(new BoxLayout(scoreTable, BoxLayout.X_AXIS));

        ViolationPanel violationPanel = new FolderViolationPanel(folder.getQualityScore(), codingGuideLines,folder, violationChart);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scoreTable,violationPanel);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(5);
        splitPane.setEnabled(true);
        add(splitPane);
    }
}
