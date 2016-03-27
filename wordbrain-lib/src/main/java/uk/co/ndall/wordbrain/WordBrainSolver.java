package uk.co.ndall.wordbrain;

import com.google.common.collect.Iterables;
import uk.co.ndall.wordgames.Coords2D;
import uk.co.ndall.wordgames.TrieNode;
import uk.co.ndall.wordgames.WordTrie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class used to find solutions to WordBrain puzzles.
 */
public class WordBrainSolver {

	// WordTrie containing all possible valid words.
	private final WordTrie wordTrie;

	/**
	 * Constructor.
	 *
	 * @param dict The dictionary of words to search for.
	 */
	public WordBrainSolver(WordTrie dict) {
		wordTrie = dict;
	}

	/**
	 * Find all valid solutions for a WordBrain puzzle (using this object's dictionary).
	 *
	 * @param board       The board layout
	 * @param wordLengths The lengths of words to find (ordered).
	 * @return Ordered Stream of solutions, each represented by an ordered list of words.
	 */
	public Stream<List<FoundWord>> solve(WordbrainBoard board, int... wordLengths) {
		List<String[]> solutions = new ArrayList<>();

		// Take Stream of List<FoundWord> and convert to Stream of List<String>
		return solveWords(board, wordLengths)
				.map(lfw -> lfw.stream()
						.collect(Collectors.toList())
				);
	}

	/**
	 * Recursively find all valid solutions for a WordBrain board.
	 *
	 * @param board       The board layout
	 * @param wordLengths The lengths of words (ordered).
	 * @return Stream of solutions, each represented by an ordered list of FoundWord objects.
	 */
	private Stream<List<FoundWord>> solveWords(WordbrainBoard board, int[] wordLengths) {
		// base case: caller has found a complete solution.
		if (wordLengths.length == 0) {
			return Stream.of(new ArrayList<FoundWord>());
		}

		// Create a stream of possible first words. For each, call helper to update board
		// and recursively call this function for the next word.
		Stream<Coords2D> positions = board.getPositions().parallelStream();
		return positions
				.map(p -> wordsAt(board, p, wordLengths[0]))
				.flatMap(foundwords -> foundwords.stream())
				.flatMap(foundword -> solveWordsHelper(foundword, board, wordLengths));
	}

	/**
	 * Helper to find all valid solutions starting with a particular FoundWord for a WordBrain board.
	 *
	 * @param word        The word already found.
	 * @param board       The board layout (before the found word has been removed).
	 * @param wordLengths The lengths of words on the board (in order in which they must be found) including the already
	 *                    found word.
	 * @return Stream of solutions starting with the specified FoundWord.
	 */
	private Stream<List<FoundWord>> solveWordsHelper(FoundWord word, WordbrainBoard board, int[] wordLengths) {
		WordbrainBoard newBoard = board.withLettersRemoved(word.positions);
		// Recursive call to solveWords, with modified board and remaining wordLengths.
		return solveWords(newBoard, Arrays.copyOfRange(wordLengths, 1, wordLengths.length))
				.map(fw -> {
					ArrayList<FoundWord> solution = new ArrayList<>();
					solution.add(word);
					solution.addAll(fw);
					return solution;
				});
	}

	/**
	 * Find all the words of a certain length starting at a certain position on the board.
	 *
	 * @param board    The board layout.
	 * @param position The starting position for the words.
	 * @param length   The length of the words.
	 * @return List of words starting at this position.
	 */
	private List<FoundWord> wordsAt(WordbrainBoard board, Coords2D position, int length) {
		final List<Coords2D> firstPosition = new ArrayList<>();
		firstPosition.add(position);

		final char letter = board.getSquare(position).get();
		if (!wordTrie.hasChild(letter)) {
			// No words in the dictionary starting with this letter!
			return new ArrayList<>();
		}
		final TrieNode<Character> node = wordTrie.getChild(letter);
		final StringBuilder sb = new StringBuilder().append(letter);

		return findWords(board, firstPosition, sb, node, length - 1);
	}

	/**
	 * Method to find words on the board, recursing letter by letter.
	 *
	 * @param board            The board containing the letters.
	 * @param usedPositions    The board positions already used by letters in the word so far. The the next position
	 *                         must be a neighbour of the last position currently in the list, and not already appear in
	 *                         the list.
	 * @param wordNode         The TrieNode node representing progress towards finding a word
	 * @param remainingLetters Remaining number of letters required to complete the word
	 * @return List of found words.
	 */
	private List<FoundWord> findWords(
			WordbrainBoard board,
			List<Coords2D> usedPositions,
			StringBuilder letters,
			TrieNode<Character> wordNode,
			int remainingLetters) {

		// Base case
		if (remainingLetters == 0) {
			if (wordNode.isItem()) {
				return Arrays.asList(new FoundWord(usedPositions, letters.toString()));
			} else {
				return Collections.emptyList();
			}
		}

		List<FoundWord> finds = new ArrayList<>();

		for (Coords2D neighbour : board.getNeighbours(usedPositions.get(usedPositions.size() - 1))) {
			// Can't re-use a letter we've already used.
			if (usedPositions.contains(neighbour)) {
				continue;
			}

			// getNeighbours() only returns non-blank squares
			char letter = board.getSquare(neighbour).get();
			if (wordNode.hasChild(letter)) {
				StringBuilder newLetters = new StringBuilder(letters);
				newLetters.append(letter);
				List<Coords2D> updatedPositions = new ArrayList<>(usedPositions);
				updatedPositions.add(neighbour);
				Iterables.addAll(
						finds,
						findWords(board, updatedPositions, newLetters, wordNode.getChild(letter), remainingLetters - 1));
			}
		}
		return finds;
	}

}
