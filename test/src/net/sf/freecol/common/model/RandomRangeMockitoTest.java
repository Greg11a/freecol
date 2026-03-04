package net.sf.freecol.common.model;

import static org.mockito.Mockito.*;

import java.util.Random;

import junit.framework.TestCase;

/**
 * JUnit3-style Mockito test so it can be added via:
 *   suite.addTestSuite(RandomRangeMockitoTest.class)
 */
public class RandomRangeMockitoTest extends TestCase {

    public void testGetAmount_UsesProvidedRandom() {
        Random rng = mock(Random.class);

        // Use the 4-int constructor that exists in your FreeCol codebase.
        RandomRange rr = new RandomRange(100, 0, 7, 1);

        // Stub a likely-used Random method:
        when(rng.nextInt(anyInt())).thenReturn(0);

        // Call the real signature from your error message:
        rr.getAmount("mockito-test", rng, false);

        // Behavior verification:
        verify(rng, atLeastOnce()).nextInt(anyInt());
        verifyNoMoreInteractions(rng);
    }
}