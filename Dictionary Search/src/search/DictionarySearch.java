package search;

import tree.DictionaryTree;

/**
 * Stores a dictionary that provides definitions given a word
 * or partial matching for words in the dictionary.
 *
 * @author Brandon Skar
 * @version 1.0
 */
public class DictionarySearch implements IDictionary
{
    private DictionaryTree tree;

    /**
     * Creates a new search object with a dictionary loaded and
     * ready for searching.
     */
    public DictionarySearch()
    {
        tree = new DictionaryTree();
    }

    @Override
    public String getDefinition(String word)
    {
        return tree.getDefinition(word);
    }

    @Override
    public String[] getPartialMatches(String search)
    {
        return tree.getPartialWords(search);
    }

    @Override
    public String toString() {
        return "DictionarySearch{" +
                "tree=" + tree +
                '}';
    }
}
