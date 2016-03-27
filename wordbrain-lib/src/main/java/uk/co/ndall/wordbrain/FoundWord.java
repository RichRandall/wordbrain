package uk.co.ndall.wordbrain;

import uk.co.ndall.wordgames.Coords2D;

import java.util.List;

/**
 * Class to represent a valid word found on the wordbrain board. Includes the word, and the coordinates of the letters
 * used.
 */
public class FoundWord {
	// Array of tiles used to make this word.
	final Coords2D[] positions;

	// The word.
	final String word;

	/**
	 * Get the found word as a string.
	 *
	 * @return
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Constructor.
	 *
	 * @param positions The positions of the tiels
	 * @param word
	 */
	protected FoundWord(List<Coords2D> positions, String word) {
		this.positions = positions.toArray(new Coords2D[positions.size()]);
		this.word = word;
	}

	/**
	 * Gets the positions on the board of the letters used to make this word, in order.
	 *
	 * @return Array containing the positions of the letters on the board.
	 */
	public Coords2D[] getPositions() {
		return positions.clone();
	}
}
