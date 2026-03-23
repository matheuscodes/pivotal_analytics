package software.matheus.pivotal_analytics.managers;

import org.junit.Test;
import static org.junit.Assert.*;

public class CalculationManagerTest {

    @Test
    public void testCalculateVelocityShortArray() {
        int[] base = {5, 3, 7};
        int[] result = CalculationManager.calculateVelocity(base);
        assertArrayEquals(base, result);
    }

    @Test
    public void testCalculateVelocityEmptyArray() {
        int[] base = {};
        int[] result = CalculationManager.calculateVelocity(base);
        assertArrayEquals(base, result);
    }

    @Test
    public void testCalculateVelocityOneElement() {
        int[] base = {10};
        int[] result = CalculationManager.calculateVelocity(base);
        assertArrayEquals(base, result);
    }

    @Test
    public void testCalculateVelocityTwoElements() {
        int[] base = {10, 20};
        int[] result = CalculationManager.calculateVelocity(base);
        assertArrayEquals(base, result);
    }

    @Test
    public void testCalculateVelocityThreeElements() {
        int[] base = {10, 20, 30};
        int[] result = CalculationManager.calculateVelocity(base);
        assertArrayEquals(base, result);
    }

    @Test
    public void testCalculateVelocityFourElements() {
        int[] base = {10, 20, 30, 40};
        int[] result = CalculationManager.calculateVelocity(base);
        assertEquals(base[0], result[0]);
        assertEquals(base[1], result[1]);
        assertEquals(base[2], result[2]);
        // result[3] = (base[3] + result[2] + result[1]) / 3 = (40 + 30 + 20) / 3 = 30
        assertEquals(30, result[3]);
    }

    @Test
    public void testCalculateVelocityLongerArray() {
        int[] base = {6, 6, 6, 6, 6, 6};
        int[] result = CalculationManager.calculateVelocity(base);
        for (int v : result) {
            assertEquals(6, v);
        }
    }

    @Test
    public void testCalculateVelocityZeros() {
        int[] base = {0, 0, 0, 0, 0};
        int[] result = CalculationManager.calculateVelocity(base);
        for (int v : result) {
            assertEquals(0, v);
        }
    }
}
