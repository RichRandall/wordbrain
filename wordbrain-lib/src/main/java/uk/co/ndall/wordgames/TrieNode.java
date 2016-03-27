package uk.co.ndall.wordgames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class representing a node in a Trie, or "Prefix Tree".
 */
public class TrieNode<T> {

	/**
	 * Whether this node represents an item in the collection, or just a prefix.
	 */
	private boolean isItem = false;

	/**
	 * This node's children.
	 */
	private Map<T, TrieNode> children = new HashMap<>();

	/**
	 * Create an the item represented by a particular sequence of branches as a descendant of this node.
	 *
	 * @param sequence Sequence of values representing the branch path to the item to create.
	 */
	public void createItem(List<T> sequence) {
		// Add the child represented by the next element, if not already present.
		if (!children.containsKey(sequence.get(0))) {
			children.put(sequence.get(0), new TrieNode());
		}

		// Now that the child exists, get it.
		TrieNode child = children.get(sequence.remove(0));
		if (sequence.isEmpty()) {
			// Base case: the child represents the added item.
			child.setItem(true);
		} else {
			// Recursive call.
			child.createItem(sequence);
		}
	}

	/**
	 * Check whether this node has a child on a particular branch.
	 *
	 * @param branch The branch to test
	 * @return True if the child exists
	 */
	public boolean hasChild(T branch) {
		return children.containsKey(branch);
	}

	/**
	 * Get a the child on a particular branch of this node.
	 *
	 * @param branch The branch. This must exist (use hasChild method)
	 * @return The child node.
	 * @throws NoSuchElementException if there is no child at the specified branch.
	 */
	public TrieNode getChild(T branch) throws NoSuchElementException {
		if (!hasChild(branch)) {
			throw new NoSuchElementException("No such child: " + branch);
		}
		return children.get(branch);
	}

	/**
	 * Check whether an item represented by a particular sequence of branches is a descendent of this node.
	 *
	 * @param sequence Sequence of values representing the branch path to check.
	 * @return Whether the item is present.
	 */
	boolean hasItem(List<T> sequence) {
		if (sequence.size() == 0) {
			// Base case: this is the node specified by the sequence. Is this an item, or just part of a prefix?
			return isItem();
		}

		T branch = sequence.remove(0);
		if (children.containsKey(branch)) {
			return children.get(branch).hasItem(sequence);
		} else {
			return false;
		}
	}

	/**
	 * Set this node as an "item", rather than just part of a prefix to an item.
	 *
	 * @param value
	 */
	private void setItem(boolean value) {
		this.isItem = value;
	}

	/**
	 * Return whether this node represents an item.
	 *
	 * @return True if this node represents an item. Otherwise false (the node is just part of the path to one or more
	 * items).
	 */
	public boolean isItem() {
		return isItem;
	}
}
