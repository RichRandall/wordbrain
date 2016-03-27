package uk.co.ndall.wordgames;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests the Coords2D class.
 */
public class PositionTest {

	private Coords2D OneTwo;

	@Before
	public void Setup() {
		this.OneTwo = new Coords2D(1, 2);
	}

	@Test
	public void testIsEqual() throws Exception {
		assertEquals(OneTwo, new Coords2D(1, 2));
	}

	@Test
	public void testNotEqual() throws Exception {
		assertNotEquals(OneTwo, new Coords2D(1, 1));
	}

	@Test
	public void testGetX() throws Exception {
		assertEquals(OneTwo.getX(), 1);
	}

	@Test
	public void testGetY() throws Exception {
		assertEquals(OneTwo.getY(), 2);
	}

	@Test
	public void testToString() throws Exception {
		assertEquals("(1,2)", OneTwo.toString());

	}
}