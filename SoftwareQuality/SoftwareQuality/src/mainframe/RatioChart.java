package mainframe;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.general.DefaultPieDataset;

public class RatioChart extends ApplicationFrame {
    ChartPanel ratioPanel;
    public RatioChart(String title) {
        super(title);
        setLayout(null);
        setSize(1000,600);
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        ratioPanel = new ChartPanel(ChartFactory.createPieChart(title, dataset));
        setContentPane(ratioPanel);
    }

    public void updateDataset(String title, DefaultPieDataset<String> dataset) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset);
        ratioPanel.setChart(chart);
    }
}
