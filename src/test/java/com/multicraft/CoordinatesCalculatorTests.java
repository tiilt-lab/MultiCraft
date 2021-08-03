package com.multicraft;

import org.junit.Test;
import org.junit.Assert;

public class CoordinatesCalculatorTests {

    @Test
    public void testGeneralDirection() {
        Assert.assertEquals("south", CoordinatesCalculator.getGeneralDirection(8));
    }

}
