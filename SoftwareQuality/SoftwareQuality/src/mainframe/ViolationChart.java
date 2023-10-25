package mainframe;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;


import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViolationChart extends ApplicationFrame {
    ChartPanel chartPanel;
    public ViolationChart(String title) {
        super(title);
        setLayout(null);
        setSize(1000,600);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        chartPanel = new ChartPanel(createBarChart(title, dataset));
        setContentPane(chartPanel);
    }

    public void updateDataset(String title, Map<String, Integer> guidelineCount, List<CodingGuideline> codingGuideLines) {
        JFreeChart chart = createBarChart(title,sortDataset(createDataset(guidelineCount, codingGuideLines)));
        chartPanel.setChart(chart);
        revalidate();
        repaint();
    }

    public void updateDataset(String title, DefaultCategoryDataset dataset) {
        DefaultCategoryDataset newDateset = sortDataset(dataset);
        JFreeChart chart = createBarChart(title,newDateset);
        chartPanel.setChart(chart);
        revalidate();
        repaint();
    }

    public DefaultCategoryDataset sortDataset(DefaultCategoryDataset oldDataset) {
        DefaultCategoryDataset newDataset = new DefaultCategoryDataset();
        Map<String, Integer> data = new HashMap<>();
        for(int i=0; i<oldDataset.getColumnCount();i++) {
            String key = oldDataset.getColumnKey(i).toString();
            int value = (int) Math.ceil((double)oldDataset.getValue("",key));
            data.put(key,value);
        }
        for(Map.Entry<String, Integer> entry:data.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList()) {
            String key = entry.getKey();
            int value = entry.getValue();
            newDataset.addValue(value, "", key);
        }
        return newDataset;
    }

    private JFreeChart createBarChart(String title, DefaultCategoryDataset dataset) {
        SlidingCategoryDataset slidingDataset= new SlidingCategoryDataset(dataset,0,10);
        JFreeChart chart = ChartFactory.createBarChart(title, "Guideline Name","Violation Count", slidingDataset);
        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis yAxis = (NumberAxis)plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer barRenderer = new BarRenderer();
        barRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        barRenderer.setDefaultItemLabelsVisible(true);
        plot.setRenderer(barRenderer);
        return chart;
    }

    private DefaultCategoryDataset createDataset(Map<String, Integer> guidelineCount, List<CodingGuideline> codingGuideLines) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(Map.Entry<String, Integer> guideline : guidelineCount.entrySet()) {
            String key = guideline.getKey();
            int value = guideline.getValue();
            CodingGuideline codingGuideline = codingGuideLines.stream().filter(e->e.identifier.equals(key)).findAny().orElse(null);
            assert codingGuideline!=null;
            dataset.addValue(value, "", key);
        }
        return dataset;
    }
}
