package uk.co.ndall.wordgames;

import java.text.MessageFormat;

/**
 * Denotes position on a board as x and y co-ordinates, zero indexed with origin at top left
 */
public class Coords2D {

	// distance from the left of the board (0-indexed).
	private final int x;

	// distance from the top of the board (0-indexed).
	private final int y;

	/**
	 * Constructor.
	 *
	 * @param x distance from the left of the board (0-indexed).
	 * @param y distance from the top of the board (0-indexed).
	 */
	public Coords2D(int x, int y) {
		if (x < 0 || y < 0) {
			throw new IndexOutOfBoundsException("Index less than zero");
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * Tests whether two Coords2D objects have the same x and y values
	 *
	 * @param object the object to compare to
	 * @return True if the coordinates are the same, otherwise false
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Coords2D)) {
			return false;
		}
		Coords2D other = (Coords2D) object;
		return (this.getX() == other.getX()) && (this.getY() == other.getY());
	}

	/**
	 * Get the distance from the left of the board (0-indexed).
	 *
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the distance from the top of the board (0-indexed).
	 *
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get a string representation of the Coordinates.
	 *
	 * @return string of the form "(x,y)"
	 */
	public String toString() {
		return MessageFormat.format("({0},{1})", getX(), getY());
	}
}
