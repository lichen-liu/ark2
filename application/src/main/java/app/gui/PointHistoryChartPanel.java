package app.gui;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtils;

public class PointHistoryChartPanel extends ChartPanel {
    private CategoryDataset pointBalanceHistoryDataset = null;

    public PointHistoryChartPanel() {
        super(null);

        super.setFillZoomRectangle(true);
        super.setMouseWheelEnabled(true);
    }

    public void setDataModel(final double[] pointBalanceHistory) {
        this.pointBalanceHistoryDataset = DatasetUtils.createCategoryDataset(
                new String[] { "Point Balance By Transaction Order" },
                IntStream.rangeClosed(0, pointBalanceHistory.length - 1).boxed().collect(Collectors.toList())
                        .toArray(Integer[]::new),
                new double[][] { pointBalanceHistory });
    }

    public void displayPointBalanceHistory() {
        super.setChart(null);

        final JFreeChart chart = ChartFactory.createLineChart("Point Balance By Transaction Order", "Transaction",
                "Point Balance", this.pointBalanceHistoryDataset, PlotOrientation.VERTICAL, true, true, false);
        chart.getLegend().setFrame(BlockBorder.NONE);

        final LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, java.awt.Color.RED);
        renderer.setSeriesStroke(0, new java.awt.BasicStroke(2.0f));

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(java.awt.Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(java.awt.Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(java.awt.Color.BLACK);

        super.setChart(chart);
    }

}