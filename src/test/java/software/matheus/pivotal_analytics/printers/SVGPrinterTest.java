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

    /**
     * Tests the if-true branch of the radius calculation in percentualPieChart,
     * where the height constrains the radius (8*(width-LABEL_GAP)/10 > 8*height/10).
     * With width=800 and height=100: 8*(800-120)/10=544 > 8*100/10=80, so the if-branch is taken.
     */
    @Test
    public void testPercentualPieChartRadiusConstrainedByHeight() {
        Map<String, float[]> data = new LinkedHashMap<String, float[]>();
        data.put("SliceA", new float[]{0.4f});
        data.put("SliceB", new float[]{0.6f});
        String result = SVGPrinter.percentualPieChart(data, 800, 100, "");
        assertNotNull(result);
        assertTrue(result.contains("<svg"));
        assertTrue(result.contains("SliceA"));
    }

    /**
     * Tests hex nibble case 10 (A) for the lower nibble of to2ByteHex.
     * With max_value=256f and data=11f: redism_level=(int)((10/255)*255)=10=0x0A, lower nibble=A.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleLowerA() {
        String result = SVGPrinter.horizontalLoadBar(11f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#0A0000"));
    }

    /**
     * Tests hex nibble case 11 (B) for the lower nibble of to2ByteHex.
     * With max_value=256f and data=12f: redism_level=11=0x0B, lower nibble=B.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleLowerB() {
        String result = SVGPrinter.horizontalLoadBar(12f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#0B0000"));
    }

    /**
     * Tests hex nibble case 12 (C) for the lower nibble of to2ByteHex.
     * With max_value=256f and data=13f: redism_level=12=0x0C, lower nibble=C.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleLowerC() {
        String result = SVGPrinter.horizontalLoadBar(13f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#0C0000"));
    }

    /**
     * Tests hex nibble case 13 (D) for the lower nibble of to2ByteHex.
     * With max_value=256f and data=14f: redism_level=13=0x0D, lower nibble=D.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleLowerD() {
        String result = SVGPrinter.horizontalLoadBar(14f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#0D0000"));
    }

    /**
     * Tests hex nibble case 14 (E) for the lower nibble of to2ByteHex.
     * With max_value=256f and data=15f: redism_level=14=0x0E, lower nibble=E.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleLowerE() {
        String result = SVGPrinter.horizontalLoadBar(15f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#0E0000"));
    }

    /**
     * Tests hex nibble case 10 (A) for the upper nibble of to2ByteHex.
     * With max_value=256f and data=161f: redism_level=160=0xA0, upper nibble=A.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleUpperA() {
        String result = SVGPrinter.horizontalLoadBar(161f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#A00000"));
    }

    /**
     * Tests hex nibble case 11 (B) for the upper nibble of to2ByteHex.
     * With max_value=256f and data=177f: redism_level=176=0xB0, upper nibble=B.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleUpperB() {
        String result = SVGPrinter.horizontalLoadBar(177f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#B00000"));
    }

    /**
     * Tests hex nibble case 12 (C) for the upper nibble of to2ByteHex.
     * With max_value=256f and data=193f: redism_level=192=0xC0, upper nibble=C.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleUpperC() {
        String result = SVGPrinter.horizontalLoadBar(193f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#C00000"));
    }

    /**
     * Tests hex nibble case 13 (D) for the upper nibble of to2ByteHex.
     * With max_value=256f and data=209f: redism_level=208=0xD0, upper nibble=D.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleUpperD() {
        String result = SVGPrinter.horizontalLoadBar(209f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#D00000"));
    }

    /**
     * Tests hex nibble case 14 (E) for the upper nibble of to2ByteHex.
     * With max_value=256f and data=225f: redism_level=224=0xE0, upper nibble=E.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleUpperE() {
        String result = SVGPrinter.horizontalLoadBar(225f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#E00000"));
    }

    /**
     * Tests hex nibble case 15 (F) for the upper nibble of to2ByteHex.
     * With max_value=256f and data=241f: redism_level=240=0xF0, upper nibble=F.
     */
    @Test
    public void testHorizontalLoadBarHexNibbleUpperF() {
        String result = SVGPrinter.horizontalLoadBar(241f, 256f, 20, 200, "");
        assertNotNull(result);
        assertTrue(result.contains("#F00000"));
    }
}
