package sco.climb.domain.test;

import org.junit.Assert;
import org.junit.Test;

public class TestXOR {

	@Test
	public void testXOR() {
		 boolean A = true;
     boolean B = false;
     
     Assert.assertTrue(A^B);
     Assert.assertTrue(B^A);
     Assert.assertFalse(A^A);
     Assert.assertFalse(B^B);
     
     Assert.assertFalse(!(A^B));
     Assert.assertFalse(!(B^A));
     Assert.assertTrue(!(A^A));
     Assert.assertTrue(!(B^B));
	}
}
