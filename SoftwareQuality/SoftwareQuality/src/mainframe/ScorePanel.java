package mainframe;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {
    ScorePanel(QualityScore qs, String t1, String c1) {
        setLayout(new GridLayout(2,6,5,5));
        JTextField title = new JTextField(t1);
        JTextField ttName = new JTextField("Total");
        JTextField mtName = new JTextField("Maintainability");
        JTextField rlName = new JTextField("Reliability");
        JTextField scName = new JTextField("Security");
        JTextField ptName = new JTextField("Portability");

        JTextField name = new JTextField(c1);
        JTextField totalScore = new JTextField(String.format("%.3f",qs.totalQualityScore));
        JTextField maintainabilityScore = new JTextField(String.format("%.3f",qs.maintainabilityScore));
        JTextField reliabilityScore = new JTextField(String.format("%.3f",qs.reliabilityScore));
        JTextField securityScore = new JTextField(String.format("%.3f",qs.securityScore));
        JTextField portabilityScore = new JTextField(String.format("%.3f",qs.portabilityScore));

        title.setEditable(false);
        ttName.setEditable(false);
        mtName.setEditable(false);
        rlName.setEditable(false);
        scName.setEditable(false);
        ptName.setEditable(false);
        name.setEditable(false);
        totalScore.setEditable(false);
        maintainabilityScore.setEditable(false);
        reliabilityScore.setEditable(false);
        securityScore.setEditable(false);
        portabilityScore.setEditable(false);

        add(title);
        add(ttName);
        add(mtName);
        add(rlName);
        add(scName);
        add(ptName);
        add(name);
        add(totalScore);
        add(maintainabilityScore);
        add(reliabilityScore);
        add(securityScore);
        add(portabilityScore);
    }
}
