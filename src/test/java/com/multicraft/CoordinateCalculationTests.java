package com.multicraft;

import org.junit.Test;
import org.junit.Assert;

public class CoordinateCalculationTests {

    @Test
    public void testGeneralDirection() {
        Assert.assertEquals("south", CoordinateCalculations.getGeneralDirection(8));
    }

}
