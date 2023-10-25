package mainframe;

import javax.swing.*;
import java.awt.*;

public class SystemPanel extends JPanel {
    SystemPanel(System system) {
        ScorePanel scorePanel = new ScorePanel(system.getQualityScore(), "Project Name", system.getName());

        setLayout(new FlowLayout());
        add(scorePanel);

        JPanel metricPanel1 = new JPanel();
        if(system.getNumberOfRecursion()>0) {
            JTextField title = new JTextField("Number of recursions");
            JTextField description = new JTextField("Number of recursion's Recommended Upper Limit is 0. But value is " +  system.getNumberOfRecursion());
            metricPanel1.add(title);
            metricPanel1.add(description);
        }
        JPanel metricPanel2 = new JPanel();
        if(system.getNumberOfDirectRecursion()>0){
            JTextField title = new JTextField("Number of direct recursions");
            JTextField description = new JTextField("Number of direct recursion's Recommended Upper Limit is 0. But value is " +  system.getNumberOfDirectRecursion());
            metricPanel2.add(title);
            metricPanel2.add(description);
        }
        add(metricPanel1);
        add(metricPanel2);


        scorePanel.setVisible(true);
    }
}
