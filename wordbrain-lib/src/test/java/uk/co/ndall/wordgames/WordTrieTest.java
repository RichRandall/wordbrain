package uk.co.ndall.wordgames;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the WordTrie class.
 */
public class WordTrieTest {

	private Dictionary dict;

	@Before
	public void Setup() {
		this.dict = new WordTrie(Arrays.asList("donut", "donate", "don"));
	}

	@Test
	public void hasTrueWhenWordPresent() throws Exception {
		assertTrue(dict.has("donut"));
		assertTrue(dict.has("donate"));
		assertTrue(dict.has("don"));
	}

	@Test
	public void prefixIsNotAWord() throws Exception {
		assertFalse(dict.has("do"));
	}

	@Test
	public void emptyStringIsNotAWord() throws Exception {
		assertFalse(dict.has(""));
	}

	@Test
	public void wordNotValidJustBecausePrefixIsAWord() throws Exception {
		assertFalse(dict.has("donated"));
	}

	@Test
	public void hasFalseWhenWordNotPresent() throws Exception {
		assertFalse(dict.has(":-)"));
		assertFalse(dict.has("antidisestablishmentarianism"));
	}

	@Test
	public void addedWordIsPresent() throws Exception {
		dict.put("egg");
		dict.put("doable");
		assertTrue(dict.has("egg"));
		assertTrue(dict.has("doable"));
	}
}