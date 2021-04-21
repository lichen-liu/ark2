package app.gui;

import org.jfree.chart.ChartPanel;

public class PointHistoryChartPanel extends ChartPanel {

    public PointHistoryChartPanel() {
        super(null);

        super.setFillZoomRectangle(true);
        super.setMouseWheelEnabled(true);
    }

}