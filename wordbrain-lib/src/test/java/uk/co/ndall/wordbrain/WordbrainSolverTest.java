package uk.co.ndall.wordbrain;

import org.junit.Before;
import org.junit.Test;
import uk.co.ndall.wordgames.WordTrie;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests for WordbrainSolver.
 */
public class WordbrainSolverTest {

	/**
	 * Board layout for an actual Wordbrain puzzle. With the lengths defined by WORD_LENGTHS, there is a single solution
	 * (with a standard dictionary) of "smell", "biscuit", "crab".
	 */
	private static final WordbrainBoard BOARD = new WordbrainBoard("labb,rlti,ecis,mscu");

	/**
	 * Word lengths to find on BOARD.
	 */
	private static final int[] WORD_LENGTHS = new int[]{5, 7, 4};

	/**
	 * Words in the expected solution for BOARD.
	 */
	private static final String[] SOLUTION = new String[]{"smell", "biscuit", "crab"};

	/**
	 * Words to put in the dictionary.
	 */
	public String[] dictionaryWords;

	/**
	 * Setup dictionaryWords, which may be mutated for each test.
	 */
	@Before
	public void setupDict() {
		dictionaryWords = new String[]{"bell", "biscuit", "crab", "meal", "smell"};
	}

	/**
	 * Test that the solver finds the expected solution.
	 *
	 * @throws Exception
	 */
	@Test
	public void solverFindsSingleSolution() throws Exception {
		WordTrie dict = new WordTrie(Arrays.asList(dictionaryWords));

		WordBrainSolver solver = new WordBrainSolver(dict);
		Stream<List<FoundWord>> solutions = solver.solve(BOARD, WORD_LENGTHS);

		assertStreamContainsOnly(solutions, SOLUTION);
	}

	/**
	 * Tests that the solver returns an empty stream if the dictionary does not contain one of the words in the real
	 * solution.
	 *
	 * @throws Exception
	 */
	@Test
	public void solverFindsNoSolution() throws Exception {
		// switch "crab" for "carb". Should not be possible to find this due to position of the letters, so
		// there should be no solutions.
		dictionaryWords[2] = "carb";
		WordTrie dict = new WordTrie(Arrays.asList(dictionaryWords));

		WordBrainSolver solver = new WordBrainSolver(dict);
		Stream<List<FoundWord>> solutions = solver.solve(BOARD, WORD_LENGTHS);

		assertStreamContainsOnly(solutions, new String[0][]);
	}

	/**
	 * Tests that the solver returns two solutions if there are two present (achieved by adding a nonsense word to the
	 * dictionary)
	 *
	 * @throws Exception
	 */
	@Test
	public void solverFindsTwoSolutions() throws Exception {
		// switch "bell" for "llems". There should now be two solutions, one with "smell" and one with "llems"
		dictionaryWords[0] = "llems";
		WordTrie dict = new WordTrie(Arrays.asList(dictionaryWords));

		WordBrainSolver solver = new WordBrainSolver(dict);
		Stream<List<FoundWord>> solutions = solver.solve(BOARD, WORD_LENGTHS);

		assertStreamContainsOnly(solutions, new String[][]{{"llems", "biscuit", "crab"}, SOLUTION});
	}


	/**
	 * Test helper that takes a stream of solutions from the solver, and asserts that it contains the expected
	 * solutions.
	 *
	 * @param solutions         Solutions from the WordbrainSolver
	 * @param expectedSolutions Zero or more string arrays representing expected solutions
	 */
	private void assertStreamContainsOnly(Stream<List<FoundWord>> solutions, String[]... expectedSolutions) {
		List<List<FoundWord>> solutionsList = solutions.collect(Collectors.toList());
		assertEquals(expectedSolutions.length, solutionsList.size());

		for (String[] expectedSolution : expectedSolutions) {
			String[] solution = solutionsList.remove(0).stream()
					.map(FoundWord::getWord)
					.toArray(String[]::new);
			assertArrayEquals(expectedSolution, solution);
		}
	}

}
