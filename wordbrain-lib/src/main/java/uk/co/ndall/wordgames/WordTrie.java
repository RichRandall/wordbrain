package uk.co.ndall.wordgames;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is a collection of words stored in a Trie (or "prefix-tree") data structure. This can be used to solve
 * word-search type problems in a performant way. Words are represented in a tree structure where each node has a number
 * of possible branches, each representing a single letter. The path from the root of the tree to a given node
 * represents a possible word.
 * <p>
 * Calling isItem() on TrieNodes representing valid words will return true. Otherwise, the node represents an incomplete
 * word (a prefix), and false will be returned. When attempting to build a valid word letter by letter, this data
 * structure is efficient because it enables the exclusion of prefixes which do not appear in any valid word.
 * <p>
 * eg.(root)--->h--->e*-->l--->l*-->o*    -    nodes marked with * represent valid words
 * <p>
 * eg (root)--->h--->e--->l--->l-x->q     -    give up here because no words have this prefix.
 */
public class WordTrie extends TrieNode<Character> implements Dictionary {

	/**
	 * Constructor to create an empty WordTrie.
	 */
	public WordTrie() {
	}

	/**
	 * Constructor to create a WordTrie and populate it with a bunch of words.
	 *
	 * @param words The valid words to put into the trie.
	 */
	public WordTrie(Iterable<String> words) {
		this();
		words.forEach(s -> this.put(s.toLowerCase()));
	}

	/**
	 * Constructor to create a WordTrie and populate it with a bunch of words.
	 *
	 * @param words The valid words to put into the trie.
	 */
	public WordTrie(Stream<String> words) {
		this();
		words.forEach(s -> this.put(s.toLowerCase()));
	}

	@Override
	public boolean has(String word) {
		return hasItem(getCharList(word));
	}

	@Override
	public void put(String word) {
		createItem(getCharList(word));
	}

	/**
	 * Converts a word into a list of letters, used for querying WordNodes.
	 *
	 * @param word
	 * @return A list of letters.
	 */
	private List<Character> getCharList(String word) {
		List<Character> letters = new ArrayList<>();
		for (int i = 0; i < word.length(); i++) {
			letters.add(word.charAt(i));
		}
		return letters;
	}

}

