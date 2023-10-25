package mainframe;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class CreateFrame extends JFrame {
    private JTextArea cpPathArea;
    private JTextArea bfPathArea;
    public CreateFrame(String[] paths, CountDownLatch latch) {
        setTitle("File Select");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel pName = new JLabel("Enter project name");
        pName.setBounds(100, 100, 300, 100);
        JTextArea projectName =  new JTextArea();
        projectName.setEditable(true);
        projectName.setBounds(410,100,300,100);

        JButton openCPButton = new JButton("Select Polyspace Code Prover Report");
        openCPButton.addActionListener(e -> cpPathArea.setText(openAddressChooser()));
        openCPButton.setBounds(100,300,300,100);
        cpPathArea = new JTextArea();
        cpPathArea.setBounds(410,300,300,100);

        JButton openBFButton = new JButton("Select Polyspace Bug Finder Report");
        openBFButton.addActionListener(e -> bfPathArea.setText(openAddressChooser()));
        openBFButton.setBounds(100,500,300,100);
        bfPathArea = new JTextArea();
        bfPathArea.setBounds(410,500,300,100);

        JButton closeButton = new JButton("완료");
        closeButton.addActionListener(e->{
            paths[0] = projectName.getText();
            paths[1] = cpPathArea.getText();
            paths[2] = bfPathArea.getText();
            dispose();
            latch.countDown();
        });
        closeButton.setBounds(800,600,100,100);

        setLayout(null);
        add(pName);
        add(projectName);
        add(openCPButton);
        add(cpPathArea);
        add(openBFButton);
        add(bfPathArea);
        add(closeButton);
    }
    private String openAddressChooser() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return "";
    }
}

