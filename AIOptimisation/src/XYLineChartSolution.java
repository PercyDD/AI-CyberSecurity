import java.util.ArrayList;

import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class XYLineChartSolution extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Generate a dataset so each series can be added to it
	 * Generate XY series and add them to them.
	 * If there is a lot of data add to the series every
	 * ten. Otherwise add them all. 
	 * Then populate the JFree chart with the given parameters.
	 */
	public XYLineChartSolution(String title, ArrayList<Double> functionData1, ArrayList<Double> functionData2, ArrayList<Double> functionData3) {
		super(title);
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		XYSeries seriesF1 = new XYSeries("Current Solution Error");
		if(functionData1.size() > 100) {
			for(int i = 0; i < functionData1.size(); i++) {
				if(i % 10 == 0) {
					seriesF1.add(i, functionData1.get(i));
				}
			}
		} else {
			for(int i = 0; i < functionData1.size(); i++) {
				seriesF1.add(i, functionData1.get(i));
			}
		}
		dataset.addSeries(seriesF1);
		
		XYSeries seriesF2 = new XYSeries("Average Solutions Error");
		if(functionData2.size() > 100) {
			for(int i = 0; i < functionData2.size(); i++) {
				if(i % 10 == 0) {
					seriesF2.add(i, functionData2.get(i));
				}
			}
		} else {
			for(int i = 0; i < functionData2.size(); i++) {
				seriesF2.add(i, functionData2.get(i));
			}
		}
		dataset.addSeries(seriesF2);
		
		XYSeries seriesF3 = new XYSeries("Best Solution Error");
		if(functionData3.size() > 100) {
			for(int i = 0; i < functionData3.size(); i++) {
				if(i % 10 == 0) {
					seriesF3.add(i, functionData3.get(i));
				}
			}
		} else {
			for(int i = 0; i < functionData3.size(); i++) {
				seriesF3.add(i, functionData3.get(i));
			}
		}
		dataset.addSeries(seriesF3);
		
	    JFreeChart chart = ChartFactory.createXYLineChart(
	            "XY Line Chart",
	            "X-Axis",
	            "Y-Axis",
	            dataset,
	            PlotOrientation.VERTICAL,
	            true, true, false);
	    
	    ChartPanel panel = new ChartPanel(chart);
	    setContentPane(panel);
	}
}
