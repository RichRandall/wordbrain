package uk.co.ndall.wordgames;

/**
 * Interface representing a collection of valid words.
 */
public interface Dictionary {

	/**
	 * Check whether the given word exists in the dictionary.
	 *
	 * @param word The word to look for
	 * @return true if the word is valid, false otherwise.
	 */
	boolean has(String word);

	/**
	 * Add a valid word to the dictionary.
	 *
	 * @param word The word to add.
	 */
	void put(String word);
}
