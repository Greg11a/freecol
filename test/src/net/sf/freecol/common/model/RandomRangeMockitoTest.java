package net.sf.freecol.common.model;

import static org.mockito.Mockito.*;

import java.util.Random;

import junit.framework.TestCase;

public class RandomRangeMockitoTest extends TestCase {

    public void testGetAmount_UsesProvidedRandom() {
        Random rng = mock(Random.class);

        RandomRange rr = new RandomRange(100, 0, 7, 1);

        when(rng.nextInt(anyInt())).thenReturn(0);

        rr.getAmount("mockito-test", rng, false);

        verify(rng, atLeastOnce()).nextInt(anyInt());
        verifyNoMoreInteractions(rng);
    }
}