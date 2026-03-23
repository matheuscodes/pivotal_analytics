package software.matheus.pivotal_analytics.printers;

import org.junit.Test;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class SVGPrinterTest {

    @Test
    public void testLabeledLineGraphEmpty() {
        Map<String, int[]> data = new LinkedHashMap<String, int[]>();
        String result = SVGPrinter.labeledLineGraph(data, 0, 10, new String[]{"a", "b"}, "", 1);
        assertNotNull(result);
        assertTrue(result.contains("<svg"));
        assertTrue(result.contains("</svg>"));
    }

    @Test
    public void testLabeledLineGraphWithData() {
        Map<String, int[]> data = new LinkedHashMap<String, int[]>();
        data.put("Series1", new int[]{5, 10, 3, 8});
        String result = SVGPrinter.labeledLineGraph(data, 0, 10, new String[]{"W1", "W2", "W3", "W4"}, "  ", 1);
        assertNotNull(result);
        assertTrue(result.contains("Series1"));
        assertTrue(result.contains("<svg"));
    }

    @Test
    public void testLabeledLineGraphMultipleSeries() {
        Map<String, int[]> data = new LinkedHashMap<String, int[]>();
        data.put("Alpha", new int[]{1, 2, 3});
        data.put("Beta", new int[]{3, 2, 1});
        String result = SVGPrinter.labeledLineGraph(data, 0, 5, new String[]{"A", "B", "C"}, "", 2);
        assertTrue(result.contains("Alpha"));
        assertTrue(result.contains("Beta"));
    }

    @Test
    public void testPercentualPieChartEmpty() {
        Map<String, float[]> data = new LinkedHashMap<String, float[]>();
        String result = SVGPrinter.percentualPieChart(data, 300, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("<svg"));
        assertTrue(result.contains("</svg>"));
    }

    @Test
    public void testPercentualPieChartWithData() {
        Map<String, float[]> data = new LinkedHashMap<String, float[]>();
        data.put("SliceA", new float[]{0.3f});
        data.put("SliceB", new float[]{0.7f});
        String result = SVGPrinter.percentualPieChart(data, 400, 300, "  ");
        assertNotNull(result);
        assertTrue(result.contains("SliceA"));
        assertTrue(result.contains("SliceB"));
        assertTrue(result.contains("<svg"));
    }

    @Test
    public void testPercentualPieChartFullSlice() {
        Map<String, float[]> data = new LinkedHashMap<String, float[]>();
        data.put("Full", new float[]{1.0f});
        String result = SVGPrinter.percentualPieChart(data, 300, 200, "");
        assertTrue(result.contains("circle")); // Full circle rendered as <circle>
    }

    @Test
    public void testPercentualPieChartLargeSlice() {
        Map<String, float[]> data = new LinkedHashMap<String, float[]>();
        data.put("LargeSlice", new float[]{0.6f});
        String result = SVGPrinter.percentualPieChart(data, 300, 200, "");
        assertTrue(result.contains("LargeSlice"));
        // Large-arc flag should be set since > 0.5
        assertTrue(result.contains("1 1"));
    }

    @Test
    public void testHorizontalProgressBar() {
        String result = SVGPrinter.horizontalProgressBar(50, 100, 20, 200, "blue", "white", "");
        assertNotNull(result);
        assertTrue(result.contains("<svg"));
        assertTrue(result.contains("50"));
    }

    @Test
    public void testHorizontalProgressBarZero() {
        String result = SVGPrinter.horizontalProgressBar(0, 100, 20, 200, "green", "black", "  ");
        assertNotNull(result);
        assertTrue(result.contains("0"));
    }

    @Test
    public void testHorizontalLoadBarBelowOne() {
        String result = SVGPrinter.horizontalLoadBar(0.5f, 1.2f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("<svg"));
        // Below 100%, no red fill
        assertTrue(result.contains("fill='black'"));
    }

    @Test
    public void testHorizontalLoadBarAboveOne() {
        String result = SVGPrinter.horizontalLoadBar(1.1f, 1.2f, 20, 200, "");
        assertNotNull(result);
        // Above 100%, should use red fill
        assertTrue(result.contains("0000")); // red color component
    }

    @Test
    public void testHorizontalLoadBarAtMax() {
        String result = SVGPrinter.horizontalLoadBar(1.2f, 1.2f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("FF")); // 255 level red
    }

    @Test
    public void testLabeledLineGraphWithSkip() {
        Map<String, int[]> data = new LinkedHashMap<String, int[]>();
        data.put("Data", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        String[] points = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10"};
        String result = SVGPrinter.labeledLineGraph(data, 0, 10, points, "", 2);
        assertNotNull(result);
        assertTrue(result.contains("<svg"));
    }
}
