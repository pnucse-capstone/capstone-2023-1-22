package mainframe;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FunctionPanel extends JPanel {
    FunctionPanel(Function function, List<CodingGuideline> codingGuideLines, ViolationChart violationChart) {
        ScorePanel scorePanel = new ScorePanel(function.getQualityScore(), "Function Name", function.getName());

        setLayout(new BorderLayout(5,5));
        add(scorePanel, BorderLayout.NORTH);

        scorePanel.setVisible(true);
        if(!function.getViolations().isEmpty()) {
            ViolationPanel violationPanel = new FunctionViolationPanel(function.getQualityScore(), codingGuideLines,function, violationChart);
            add(violationPanel, BorderLayout.CENTER);
            violationPanel.setVisible(true);
        }
    }
}
