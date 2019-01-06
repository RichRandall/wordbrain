package uk.co.ndall.wordbrain_cli;

import uk.co.ndall.wordbrain.FoundWord;
import uk.co.ndall.wordbrain.WordBrainSolver;
import uk.co.ndall.wordbrain.WordbrainBoard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * Entry class for WordBrain-CLI. Asks for WordBrain puzzles on the commandline and solves them.
 */
public class Main {

	/**
	 * Name of the resource file containing a list of valid words to look for.
	 */
	private static final String DICT_FILENAME = "/enable1.txt";

	/**
	 * String the user should enter to exit the program.
	 */
	private static final String EXIT = "exit";

	/**
	 * Main method for the program.
	 *
	 * @param args Not used.
	 * @throws IOException There was a problem reading words from the dictionary file.
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		WordBrainSolver solver;


		// Load valid words into a prefix tree.
		try (InputStream in = openWordList(args)) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
				solver = new WordBrainSolver(br.lines());
			}
		}

		try (Scanner reader = new Scanner(System.in)) {
			Optional<Puzzle> puzzle = getPuzzle(reader);

			// Main program loop.
			while (puzzle.isPresent()) {
				Stream<List<FoundWord>> solutions = solver.solve(
						puzzle.get().board,
						puzzle.get().wordLengths);
				PrintSolutions(solutions);
				puzzle = getPuzzle(reader);
			}
		}
	}

	private static InputStream openWordList(String[] cmdline) throws IOException, ClassNotFoundException {

		if (cmdline.length > 0) {
			return Files.newInputStream(Paths.get(cmdline[0]));
		} else {
			return Main.class.getResourceAsStream(DICT_FILENAME);
		}
	}

	/**
	 * Print valid solutions to standard out. Note that this does not attempt to represent the positions of the
	 * characters making up the words. Therefore, solutions that would appear as duplicates (eg. different ways to make
	 * the same words) are not printed.
	 *
	 * @param wordLists Lists of words found. Each list represents a complete solution to the puzzle.
	 */
	private static void PrintSolutions(Stream<List<FoundWord>> wordLists) {
		// Map FoundWord lists to distinct comma-separated strings.
		Stream<String> solutionStrings = wordLists
				.map(wordList -> wordList.stream()
						.map(FoundWord::getWord)
						.collect(Collectors.joining(", ")))
				.unordered() // convert to unordered, else distinct() blocks until all solutions have been found.
				.distinct();

		solutionStrings
				.forEach(commaSeparatedWords -> System.out.println(commaSeparatedWords));
	}

	/**
	 * Gets a WordBrain puzzle from the user on the command line.
	 *
	 * @param reader Scanner used for reading input.
	 * @return A valid wordbrain puzzle (or empty if user wants to exit).
	 */
	private static Optional<Puzzle> getPuzzle(Scanner reader) {
		System.out.println(MessageFormat.format("Enter board as lines separated by commas (or \"{0}\"):", EXIT));
		WordbrainBoard board;

		while (true) {
			String boardString = reader.nextLine().toLowerCase();
			if (boardString.toLowerCase().equals(EXIT)) {
				return Optional.empty();
			}

			board = new WordbrainBoard(boardString);
			if (board.isValid()) {
				break;
			}

			System.out.println("Board must be square, and contain only letters or spaces:");
		}

		Optional<int[]> optLengths = getWordLengths(reader, board.countLetters());
		if (!optLengths.isPresent()) {
			return Optional.empty();
		}

		return Optional.of(new Puzzle(board, optLengths.get()));
	}

	/**
	 * Gets the lengths of the words to find on a board from the user.
	 *
	 * @param reader        Scanner used to read command line input.
	 * @param expectedTotal Total number of characters on the board, to validate user input.
	 * @return The lengths of the words to find.
	 */
	private static Optional<int[]> getWordLengths(Scanner reader, int expectedTotal) {
		System.out.println("Enter word lengths, separated by commas:");

		while (true) {
			String lengthString = reader.nextLine().toLowerCase();
			if (lengthString.equals(EXIT.toLowerCase())) {
				return Optional.empty();
			}

			String[] lengthTokens = lengthString.split(",");
			int[] lengths = new int[lengthTokens.length];

			try {
				for (int i = 0; i < lengths.length; i++) {
					lengths[i] = Integer.parseInt(lengthTokens[i]);
				}
			} catch (NumberFormatException e) {
				System.out.println(MessageFormat.format("Please enter numbers separated by commas (or \"{0}\"):", EXIT));
				continue;
			}

			if (IntStream.of(lengths).sum() != expectedTotal) {
				System.out.println(MessageFormat.format("Expected a total of {0}, try again:", expectedTotal));
				continue;
			}
			return Optional.of(lengths);
		}
	}

	/**
	 * Class to represent a wordbrain puzzle to solve.
	 */
	private static class Puzzle {

		// The board.
		private final WordbrainBoard board;

		// Lengths of the words to find on the board (in order).
		private final int[] wordLengths;

		/**
		 * Constructor.
		 *
		 * @param board       The board.
		 * @param wordLengths Lengths of the words to find on the board (in order).
		 */
		Puzzle(WordbrainBoard board, int... wordLengths) {
			this.board = board;
			this.wordLengths = wordLengths;
		}
	}
}
