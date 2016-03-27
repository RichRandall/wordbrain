package uk.co.ndall.wordbrain;

import com.google.common.collect.Lists;
import uk.co.ndall.wordgames.Coords2D;

import java.text.MessageFormat;
import java.util.*;

/**
 * Class representing the positions of letters in a game of WordBrain.
 */
public class WordbrainBoard {

	// 2D array representing the board.
	private char[][] board;

	/**
	 * Constructor
	 *
	 * @param layout A string containing the letters in the board. This should be encoded in comma-separated rows of
	 *               letters. Eg. "abcd,efgh,ijkl,mnop". Spaces will be interpreted as no letter at that position.
	 */
	public WordbrainBoard(String layout) {
		board = Arrays.stream(layout.split("[;,:\n\r]"))
				.map(String::toLowerCase)
				.map(String::toCharArray)
				.toArray(size -> new char[size][]);
	}

	/**
	 * Constructor
	 *
	 * @param board A 2 dimensional array representing the position of letters on the board. Spaces should occupy
	 *              positions where there is no letter present.
	 */
	private WordbrainBoard(char[][] board) {
		this.board = board;
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner("\n");
		Arrays.stream(board)
				.forEach(row -> sj.add(new String(row)));
		return sj.toString();
	}

	/**
	 * Get the character at a given position.
	 *
	 * @param pos The position.
	 * @return The character at the specified position, if present.
	 * @throws IndexOutOfBoundsException If the coordinates are not a valid position.
	 */
	public Optional<Character> getSquare(Coords2D pos) throws IndexOutOfBoundsException {
		final int x = pos.getX();
		final int y = pos.getY();

		validateCoords(x, y);
		return board[y][x] == ' ' ? Optional.empty() : Optional.of(board[y][x]);
	}

	/**
	 * Check whether a given pair of coordinates is within range for the size of this board.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @throws IndexOutOfBoundsException
	 */
	private void validateCoords(int x, int y) throws IndexOutOfBoundsException {
		if (y < 0 || y >= board.length) {
			throw new IndexOutOfBoundsException(MessageFormat.format("x of {0} out of range", x));
		}
		if (x < 0 || x >= board[y].length) {
			throw new IndexOutOfBoundsException(MessageFormat.format("y of {0} out of range", y));
		}
	}

	/**
	 * Gets the positions of any neighbouring letters for a given square. Neighbouring positions that do not contain
	 * letters are omitted.
	 *
	 * @return The positions of any occupied neighbours.
	 */
	public Iterable<Coords2D> getNeighbours(Coords2D pos) throws IndexOutOfBoundsException {
		final int x = pos.getX();
		final int y = pos.getY();

		validateCoords(x, y);
		ArrayList<Coords2D> neighbours = new ArrayList<Coords2D>();

		for (int y1 = y - 1; y1 <= y + 1; y1++) {
			if (y1 < 0 || y1 >= board.length) {
				continue;
			}
			for (int x1 = x - 1; x1 <= x + 1; x1++) {
				if (x1 < 0 || x1 >= board[y1].length) {
					continue;
				}
				Coords2D pos1 = new Coords2D(x1, y1);
				if (pos1.equals(pos)) {
					continue;
				}
				if (getSquare(pos1).isPresent()) {
					neighbours.add(new Coords2D(x1, y1));
				}
			}
		}

		return neighbours;
	}

	/**
	 * Get the positions on the board containing letters, from top left to bottom right.
	 *
	 * @return
	 */
	public List<Coords2D> getPositions() {
		List<Coords2D> positions = new ArrayList<>();
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				Coords2D pos = new Coords2D(x, y);
				if (getSquare(pos).isPresent()) {
					positions.add(pos);
				}
			}
		}
		return positions;
	}

	/**
	 * Count the number of letters on the board.
	 *
	 * @return The number of letters (ie, tiles which aren't spaces, assuming this board is valid).
	 */
	public int countLetters() {
		int count = 0;
		for (char[] row : board) {
			for (char letter : row) {
				if (letter != ' ') {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Build a new board, based on the current board, but with certain tiles removed. When tiles are removed from a
	 * WordBrain board, the letters above them fall down to ensure the columns are filled up from the bottom, with any
	 * spaces towards the top of the board.
	 *
	 * @param squaresToRemove The coordinates of tiles to remove from the board.
	 * @return The new board with the tiles removed.
	 */
	public WordbrainBoard withLettersRemoved(Coords2D[] squaresToRemove) {
		List<Coords2D> removeList = Arrays.asList(squaresToRemove);
		//Create blank board of correct size;
		int yLen = board.length;
		int xLen = board[0].length;
		char[][] newBoard = new char[yLen][xLen];
		for (int y = 0; y < yLen; y++) {
			for (int x = 0; x < xLen; x++) {
				newBoard[y][x] = ' ';
			}
		}

		// Fill letters up from bottom, if not removed
		for (Coords2D pos : Lists.reverse(getPositions())) {
			if (removeList.contains(pos)) {
				continue;
			}
			for (int y = yLen - 1; y >= 0; y--) {
				if (newBoard[y][pos.getX()] == ' ') {
					newBoard[y][pos.getX()] = getSquare(pos).get();
					break;
				}
			}
		}

		return new WordbrainBoard(newBoard);
	}

	/**
	 * Check whether this is a valid WordBrain board. Returns false if the board is not square, or if any characters are
	 * not alphabetic.
	 *
	 * @return Whether the board is valid.
	 */
	public boolean isValid() {
		// Check rows and columns of equal length
		int yLen = board.length;
		for (char[] row : board) {
			if (row.length != yLen) {
				return false;
			}
		}

		for (Coords2D pos : getPositions()) {
			Optional<Character> optChar = getSquare(pos);
			if (optChar.isPresent() && !Character.isAlphabetic(optChar.get())) {
				return false;
			}
		}
		return true;
	}
}
