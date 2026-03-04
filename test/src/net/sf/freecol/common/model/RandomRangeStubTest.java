package net.sf.freecol.common.model;

import net.sf.freecol.util.test.FreeColTestCase;
import net.sf.freecol.util.test.MockPseudoRandom;

import java.util.Arrays;

public class RandomRangeStubTest extends FreeColTestCase {

//    testCaptureConvert
    public void testRandomRangeGetAmount_UsesStubbedRandom() {

        RandomRange rr = new RandomRange(100, 10, 20, 1);


        MockPseudoRandom stub = new MockPseudoRandom();
        stub.setNextNumbers(Arrays.asList(0, 0, 0), true);

        int amount = rr.getAmount("stub-test", stub, false);


        assertTrue("Expected amount to be within [10, 20), got: " + amount,
                amount >= 10 && amount < 20);


    }
}