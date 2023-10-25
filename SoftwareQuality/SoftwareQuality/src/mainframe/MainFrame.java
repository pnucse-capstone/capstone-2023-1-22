package mainframe;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;

public class MainFrame extends JFrame {
    private final JPanel mainPanel;
    private static final String GUIDELINE_PATH = "Coding_Guideline.txt";
    private static final String METRIC_PATH = "Code_Metrics.txt";
    JPanel scrollPane;
    JTree systemTree;
    DefaultMutableTreeNode root;
    System system = new System("");
    ArrayList<CodingGuideline> codingGuideLines;
    ViolationChart violationChart;

    private int searchCount = 0;
    private String searchWord = "";

    public MainFrame() {
        setTitle("Main Frame");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ActionListener menuActionListener = e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            String menuText = menuItem.getText();
            switch (menuText) {
                case "Create" -> createReport();
                case "Open" -> {
                    openReport();
                    setFileTree();
                    systemTree = systemTreeMake();
                    JScrollPane scrollPanel = new JScrollPane(systemTree);
                    scrollPanel.setBounds(0,0,300,700);
                    scrollPane.removeAll();
                    scrollPane.add(scrollPanel);
                    scrollPane.revalidate();
                    scrollPane.repaint();
                    JOptionPane.showMessageDialog(MainFrame.this, "Read Complete");
                }
                case "Save" -> saveReport();
                case "Help" -> {
                    String help = "Left Click -> Select Cell\nLeft Double Click -> Select Cell Contents\n(After Select Cell)Right Double Click -> Show Cell Contents\n";
                    JOptionPane.showMessageDialog(MainFrame.this,help);
                }
                default -> JOptionPane.showMessageDialog(MainFrame.this, "Error");
            }
        };

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Menu");
        menuBar.add(fileMenu);

        JMenuItem createMenuItem = new JMenuItem("Create");
        createMenuItem.addActionListener(menuActionListener);
        fileMenu.add(createMenuItem);

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(menuActionListener);
        fileMenu.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(menuActionListener);
        fileMenu.add(saveMenuItem);

        JMenuItem helpMenuItem = new JMenuItem("Help");
        helpMenuItem.addActionListener(menuActionListener);
        fileMenu.add(helpMenuItem);


        codingGuideLines = new ArrayList<>();
        try {
            guidelineReader();
            metricReader();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Error reading guideline File.");
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
        scrollPane = new JPanel();
        scrollPane.setLayout(new BoxLayout(scrollPane,BoxLayout.X_AXIS));
        violationChart = new ViolationChart("Violation Chart");
        JPanel treePanel = new JPanel();
        JTextArea searchArea = new JTextArea();
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e-> {
            String name = searchArea.getText();
            if(searchCount == 0) {
                searchWord = name;
                searchCount++;
            } else if(searchWord.equals(name)) {
                searchCount++;
            } else {
                searchCount = 0;
            }
            Enumeration<TreeNode> node = root.depthFirstEnumeration();
            int count = 0;
            while(node.hasMoreElements()) {
                DefaultMutableTreeNode next = (DefaultMutableTreeNode)node.nextElement();
                if(next.toString().equals(name) || next.toString().contains(name)) {
                    if(searchCount-1>count) {
                        count++;
                        continue;
                    }
                    TreePath path = new TreePath(next.getPath());
                    systemTree.setSelectionPath(path);
                    systemTree.scrollPathToVisible(path);
                    break;
                }
                if(!node.hasMoreElements()) {
                    JOptionPane.showMessageDialog(null, "There is no more search result");
                }
            }
        });
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.X_AXIS));
        searchPanel.add(searchArea);
        searchPanel.add(searchButton);

        treePanel.setLayout(new BorderLayout());
        treePanel.add(searchPanel, BorderLayout.NORTH);
        treePanel.add(scrollPane, BorderLayout.CENTER);

        JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,treePanel,mainPanel);
        panel.setDividerLocation(300);
        panel.setDividerSize(5);
        panel.setEnabled(true);
        add(panel);
    }

    private JTree systemTreeMake() {
        JTree tree = new JTree(root);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                Object selectedObject = selectedNode.getUserObject();
                if (selectedObject instanceof System rootSystem) {
                    JPanel systemPanel = new SystemPanel(rootSystem);
                    systemPanel.setBounds(0,0,860,700);
                    mainPanel.removeAll();
                    mainPanel.add(systemPanel);
                    systemPanel.setEnabled(true);
                    systemPanel.setVisible(true);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                    violationChart.setVisible(false);
                } else if (selectedObject instanceof Module module) {
                    JPanel modulePanel =  new ModulePanel(module, codingGuideLines, violationChart);
                    modulePanel.setBounds(0,0,860,700);
                    mainPanel.removeAll();
                    mainPanel.add(modulePanel);
                    modulePanel.setEnabled(true);
                    modulePanel.setVisible(true);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                    violationChart.setVisible(true);

                } else if (selectedObject instanceof Function function) {
                    JPanel functionPanel =  new FunctionPanel(function, codingGuideLines, violationChart);
                    functionPanel.setBounds(0,0,860,700);
                    mainPanel.removeAll();
                    mainPanel.add(functionPanel);
                    functionPanel.setEnabled(true);
                    functionPanel.setVisible(true);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                    violationChart.setVisible(true);

                } else if (selectedObject instanceof Folder folder) {
                    JPanel folderPanel =  new FolderPanel(folder, codingGuideLines, violationChart);
                    folderPanel.setBounds(0,0,860,700);
                    mainPanel.removeAll();
                    mainPanel.add(folderPanel);
                    folderPanel.setEnabled(true);
                    folderPanel.setVisible(true);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                    violationChart.setVisible(true);

                }
            }
        });
        return tree;
    }

    private void bfReportReader(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath)))
        {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\t");
                if(splitLine[1].equals("Defect")) {
                    continue;
                }
                String[] idAndDescription = splitLine[5].split(" ",2);
                Violation violation;
                if(idAndDescription[0].equals("Dir")) {
                    String[] idnAndDescription = idAndDescription[1].split(" ",2);
                    idAndDescription[0]+= " " +idnAndDescription[0];
                    violation = new Violation(splitLine[0], splitLine[1], idAndDescription[0],idnAndDescription[1],splitLine[7],splitLine[8]);
                }
                else {
                    if(splitLine[1].equals("MISRA C:2012")) {idAndDescription[0] = "Rule " + idAndDescription[0];}
                    if(splitLine[1].equals("CWE")) {idAndDescription[0] = "CWE-" + idAndDescription[0];}
                    violation = new Violation(splitLine[0], splitLine[1], idAndDescription[0],idAndDescription[1],splitLine[7],splitLine[8]);
                }
                if(codingGuideLines.stream().noneMatch(e->e.identifier.equals(violation.getIdentifier()))) {
                    continue;
                }
                system.add(violation);
            }
            system.scoreCalculate(codingGuideLines);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Error reading Polyspace Bug Finder Report file.");
        }
    }

    private void cpReportReader(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath)))
        {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\t");
                if (!splitLine[1].equals("Code Metric") || splitLine[8].contains("Polyspace")) {
                    continue;
                }
                if(splitLine[6].equals("Not computed")) {
                    splitLine[6] = "-1";
                }
                if (splitLine[2].equals("Project Metrics")) {
                    if(splitLine[5].equals("Number of Recursions")) {
                        system.setNumberOfRecursion(Integer.parseInt(splitLine[6]));
                    }
                    if(splitLine[5].equals("Number of Direct Recursions")) {
                        system.setNumberOfDirectRecursion(Integer.parseInt(splitLine[6]));
                    }
                    continue;
                }
                system.add(splitLine[8],splitLine[7], splitLine[2], splitLine[5], splitLine[6]);
                checkMetrics(splitLine[2],splitLine[5],splitLine[6],splitLine[7],splitLine[8]);
            }
        }
    }

    private void checkMetrics(String scope, String identifier, String value, String function, String module) {
        CodingGuideline guideline = codingGuideLines.stream().filter(e->e.identifier.equals(identifier)).findAny().orElse(null);
        if(guideline != null) {
            if(scope.equals("Project Metrics")||scope.equals("Function Metrics")) {
                if(Double.parseDouble(value)>guideline.limitValue) {
                    Violation violation = new Violation("","Code Metric",identifier,"",function,module);
                    system.add(violation);
                }
            } else if(scope.equals("File Metrics") && Double.parseDouble(value)<guideline.limitValue) {
                Violation violation = new Violation("","Code Metric",identifier,"",function,module);
                system.add(violation);
            }
        }
    }

    private void guidelineReader() throws IOException {
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(MainFrame.GUIDELINE_PATH)))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\t", -1);
                CodingGuideline guideline = new CodingGuideline(splitLine[0], splitLine[1]);
                guideline.description = splitLine[2];
                if (splitLine[3].equals("O")) {
                    guideline.maintainability = true;
                }
                if (splitLine[4].equals("O")) {
                    guideline.performance = true;
                }
                if (splitLine[5].equals("O")) {
                    guideline.reliability = true;
                }
                if (splitLine[6].equals("O")) {
                    guideline.security = true;
                }
                if (splitLine[7].equals("O")) {
                    guideline.portability = true;
                }
                guideline.severity = Integer.parseInt(splitLine[8]);
                codingGuideLines.add(guideline);
            }
        }
    }

    private void metricReader() throws IOException {
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(MainFrame.METRIC_PATH)))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\t", -1);
                CodingGuideline guideline = new CodingGuideline(splitLine[0], splitLine[2]);
                guideline.description = "Limit value is " + splitLine[3];
                guideline.limitValue = Integer.parseInt(splitLine[3]);
                guideline.severity = Integer.parseInt(splitLine[4]);
                codingGuideLines.add(guideline);
            }
        }
    }

    private void createReport(){
        CountDownLatch latch;
        String[] paths = new String[3];
        latch = new CountDownLatch(1);
        CreateFrame createFrame = new CreateFrame(paths, latch);
        createFrame.setVisible(true);
        system = new System("");
        Thread createThread = new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            system.setName(paths[0]);
            try{
                cpReportReader(paths[1]);
            }
            catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(MainFrame.this, "Error reading Polyspace Code Prover Report file.");
            }

            try{
                bfReportReader(paths[2]);
            }
            catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(MainFrame.this, "Error reading Polyspace Bug Finder Report file.");
            }
            setFileTree();
            systemTree = systemTreeMake();
            JScrollPane scrollPanel = new JScrollPane(systemTree);
            scrollPanel.setBounds(0,0,300,700);
            scrollPane.removeAll();
            scrollPane.add(scrollPanel);
            scrollPane.revalidate();
            scrollPane.repaint();


            JDialog success = new JDialog();
            JLabel succeed = new JLabel("Succeed");
            JButton succeedB = new JButton("완료");
            success.setSize(300,200);
            succeedB.addActionListener(e->success.setVisible(false));
            success.setLayout(new FlowLayout());
            success.add(succeed);
            success.add(succeedB);
            success.setVisible(true);
        });
        createThread.start();
    }

    private void setFileTree() {
        root = new DefaultMutableTreeNode(system);
        system.setTree(root);
        while(true) {
            if(root.getChildAt(0).getChildCount()==1) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0);
                root.removeAllChildren();
                root.add(treeNode);
            }
            else {
                break;
            }
        }
    }

    private void openReport() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        String selectedAddress;

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedAddress = fileChooser.getSelectedFile().getAbsolutePath();
            try (InputStream roadFile = Files.newInputStream(Paths.get(selectedAddress)); ObjectInputStream road = new ObjectInputStream(roadFile)) {
                system = (System) road.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Load Failed");
            }
        }
    }

    private void saveReport() {
        try (OutputStream saveFile = Files.newOutputStream(Paths.get(system.getName() +".ser")); ObjectOutputStream save = new ObjectOutputStream(saveFile)) {
            save.writeObject(system);
            JOptionPane.showMessageDialog(null, "Save Complete");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Save Failed");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}