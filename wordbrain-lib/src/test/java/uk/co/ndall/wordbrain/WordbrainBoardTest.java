package uk.co.ndall.wordbrain;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import uk.co.ndall.wordgames.Coords2D;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

/**
 * Tests the WordbrainBoard.
 */
public class WordbrainBoardTest {

	/**
	 * Full 3x3 board:  abc
	 *                  def
	 *                  ghi
	 */
	private WordbrainBoard board;

	/**
	 * Board with gaps:  a.c
	 *                   .ef
	 *                   gh.
	 */
	private WordbrainBoard gappyBoard;

	@Before
	public void CreateBoard() {
		board = new WordbrainBoard("abc,def\nghi");
		gappyBoard = new WordbrainBoard("a c, ef\ngh ");
	}

	@Test
	public void testToString() throws Exception {
		assertEquals(board.toString(), "abc\ndef\nghi");
	}

	@Test
	public void getSquareReturnsLetter() throws Exception {
		assertEquals(board.getSquare(new Coords2D(0, 0)).get(), Character.valueOf('a'));
		assertEquals(board.getSquare(new Coords2D(1, 1)).get(), Character.valueOf('e'));
		assertEquals(board.getSquare(new Coords2D(2, 2)).get(), Character.valueOf('i'));
	}

	@Test
	public void getSquareReturnsNoLetter() throws Exception {
		assertEquals(gappyBoard.getSquare(new Coords2D(1, 0)), Optional.empty());
		assertEquals(gappyBoard.getSquare(new Coords2D(0, 1)), Optional.empty());
		assertEquals(gappyBoard.getSquare(new Coords2D(2, 2)), Optional.empty());
		assertEquals(gappyBoard.getSquare(new Coords2D(1, 2)).get(), Character.valueOf('h'));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void getSquareThrowsWhenXOutOfRange() throws Exception {
		board.getSquare(new Coords2D(3, 0));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void getSquareThrowsWhenYOutOfRange() throws Exception {
		board.getSquare(new Coords2D(0, 3));
	}

	@Test
	public void getNeighboursTopLeft() throws Exception {
		assertThat(board.getNeighbours(new Coords2D(0, 0)), containsInAnyOrder(
				equalTo(new Coords2D(0, 1)),
				equalTo(new Coords2D(1, 0)),
				equalTo(new Coords2D(1, 1))));
	}

	@Test
	public void getNeighboursBottomRight() throws Exception {
		assertThat(board.getNeighbours(new Coords2D(2, 2)), containsInAnyOrder(
				equalTo(new Coords2D(1, 2)),
				equalTo(new Coords2D(2, 1)),
				equalTo(new Coords2D(1, 1))));
	}

	/**
	 * Tests that asking for the neighbours of the middle square of a 3x3 gives all the surrounding positions, as long
	 * as those positions aren't empty.
	 *
	 * @throws Exception
	 */
	@Test
	public void getNeighboursMiddleWithBlanks() throws Exception {
		// a.c
		// .ef
		// gh.
		assertThat(gappyBoard.getNeighbours(new Coords2D(1, 1)), containsInAnyOrder(
				equalTo(new Coords2D(0, 0)),
				equalTo(new Coords2D(2, 0)),
				equalTo(new Coords2D(2, 1)),
				equalTo(new Coords2D(0, 2)),
				equalTo(new Coords2D(1, 2))));
	}

	/**
	 * Tests that getPositions returns positions of occupied squares.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetPositions() throws Exception {
		// a.c
		// .ef
		// gh.
		assertThat(gappyBoard.getPositions(), containsInAnyOrder(
				equalTo(new Coords2D(0, 0)),
				equalTo(new Coords2D(2, 0)),
				equalTo(new Coords2D(1, 1)),
				equalTo(new Coords2D(2, 1)),
				equalTo(new Coords2D(0, 2)),
				equalTo(new Coords2D(1, 2))));
	}

	/**
	 * Tests removing squares from the board. Remaining squares should shuffle down.
	 *
	 * @throws Exception
	 */
	@Test
	public void testWithLettersRemoved() throws Exception {
		Coords2D[] remove = new Coords2D[]{
				new Coords2D(0, 2),
				new Coords2D(1, 1),
				new Coords2D(2, 2),
				new Coords2D(2, 1)
		};
		WordbrainBoard newBoard = board.withLettersRemoved(remove);
		// abc      ...
		// def  ->  ab.
		// ghi      dhc
		assertEquals("   \nab \ndhc", newBoard.toString());
	}

	/**
	 * Tests happy path validation.
	 *
	 * @throws Exception
	 */
	@Test
	public void validBoardValidates() throws Exception {
		assertTrue(board.isValid());
		assertTrue(gappyBoard.isValid());
	}

	/**
	 * Tests that non-square rectangles are valid
	 */
	@Test
	public void nonSquareBoardIsValid()  {
		WordbrainBoard fat = new WordbrainBoard("abcd,efgh,ijkl");
		WordbrainBoard tall = new WordbrainBoard("abc,def,ghi,jkl");
		assertTrue(fat.isValid());
		assertTrue(tall.isValid());
	}

	/**
	 * Tests that all rows must be of equal length.
	 *
	 * @throws Exception
	 */
	@Test
	public void jaggedBoardIsInvalid() throws Exception {
		WordbrainBoard jagged = new WordbrainBoard("abcd,efgh,ijklm,nop");
		assertFalse(jagged.isValid());
	}

	/**
	 * Tests that tiles must be letters or space.
	 *
	 * @throws Exception
	 */
	@Test
	public void tilesMustBeLetterOrSpace() throws Exception {
		WordbrainBoard symbol = new WordbrainBoard("abcd,efgh,i%kl,mnop");
		assertFalse(symbol.isValid());
	}

	/**
	 * Tests that the letter count returns the correct value.
	 *
	 * @throws Exception
	 */
	@Test
	public void letterCountIsAccurate() throws Exception {
		assertEquals(9, board.countLetters());
	}

	/**
	 * Tests that the letter count does not include empty tiles (spaces).
	 *
	 * @throws Exception
	 */
	@Test
	public void letterCountIgnoresSpaces() throws Exception {
		assertEquals(6, gappyBoard.countLetters());
	}
}
