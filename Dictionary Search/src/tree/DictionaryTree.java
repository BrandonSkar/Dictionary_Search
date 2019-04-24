package tree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * A tree class that stores letters in nodes that link together to create words.
 * Also has a map that takes a word and gives the definition of that word.
 * @author Brandon Skar
 * @version 1.0
 */
public class DictionaryTree
{
    private static final String FILE_NAME = "files/dictionary80000.txt";
    private static final int ALPHABET_SIZE = 26;
    private static final int AMOUNT_OF_MATCHES = 15;
    private static final int ASCII_A_CHAR = 97;
    private static final int ASCII_Z_CHAR = 122;
    private ArrayList<String> wordMatches = new ArrayList<>();
    private Node root = new Node();
    private Map<String, String> words = new HashMap<>();
    private Map<Character, Integer> charsToIndices = new HashMap<>();
    private Map<Integer, Character> indicesToChars = new HashMap<>();
    private String result;
    private String match;

    /**
     * Default constructor of tree that stores letters in nodes that will link to other letters to create words
     */
    public DictionaryTree()
    {
        //create map of chars and indices
        assignIndices();

        //create the tree from a dictionary file
        getFileContents();
        getWords();
    }

    /**
     * Takes a word parameter and returns the definition
     * @param word Word used to get the matching definition
     * @return Returns the string representation of the definition of given word
     */
    public String getDefinition(String word)
    {
        return words.get(word);
    }

    /**
     * Takes a word and gathers similar words from the tree
     * @param word Word used to get similar words from the tree
     * @return Returns an array of completed words based on the given word
     */
    public String[] getPartialWords(String word)
    {
        //convert search word to lower case
        word = word.toLowerCase();

        //reset all data each time the user enters a new character to search for
        int listSize;
        wordMatches.clear();
        match = "";
        boolean specialCharacter = false;

        //check if special character is found in the search word
        if(!word.isEmpty()) {
            for(int i = 0; i < word.length(); i++) {
                int ascii = (int)word.charAt(i);
                //if anything not a-z is found then flag an error and return empty string array
                if(ascii < ASCII_A_CHAR || ascii > ASCII_Z_CHAR) {
                    specialCharacter = true;
                    break;
                }
            }
        }
        //if the search word is empty return an empty String array
        if(word.isEmpty() || specialCharacter) {
            return new String[0];
        }

        //begin creating words based on users search word
        getPartialWords(word, root);

        //determine the size of the array based on number of matches found or
        //maximum number of matches to display
        if(wordMatches.size() < AMOUNT_OF_MATCHES) {
            listSize = wordMatches.size();
        }
        //if more than 15 matches are found, make the results display AMOUNT_OF_MATCHES
        else {
            listSize = AMOUNT_OF_MATCHES;
        }

        //create String array and add words to it
        String[] list = new String[listSize];
        for(int i = 0; i < wordMatches.size() && i < AMOUNT_OF_MATCHES; i++) {
            list[i] = wordMatches.get(i);
        }

        return list;
    }

    //partial word helper that builds words as it traverses the tree
    private void getPartialWords(String word, Node current)
    {
        //if node is null then do not traverse
        if(current == null || current.children == null) {
            return;
        }

        //if arraylist is filled with the maximum amount of search values, then do not traverse
        if(wordMatches.size() == AMOUNT_OF_MATCHES) {
            return;
        }

        //if search word is not empty then recursively traverse to the parent node of
        //where we begin finding matches from the tree
        if (!word.isEmpty()) {
            match += word.charAt(0);

            //remove first character from search word and recursively go to next node
            getPartialWords(word.substring(1),
                    current.children[charsToIndices.get(word.charAt(0))]);
        }
        //check if the there are nodes to search for under the current node
        else {

            //search each node until a child is found that has nodes to begin searching
            //from the tree
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                if (current.children[i] != null) {
                    match += indicesToChars.get(i);

                    //if the node is flagged as a word, add it to the wordMatches arraylist
                    if (current.children[i].isWord) {
                        wordMatches.add(match);
                    }

                    //when a leaf is found, remove the last character
                    if(current.children[i].children == null) {
                        match = match.substring(0, match.length() - 1);
                    }

                    //recursively go to the next child of the given index to begin searching for
                    //more matches
                    getPartialWords(word, current.children[i]);
                }
            }
        }
        //remove the last character of the match before returning to the previous node
        if(!match.isEmpty()){
            match = match.substring(0, match.length() - 1);
        }
    }

    /**
     * Traverses through the tree and creates words by linking together letters
     * @param word Word used to insert each letter into the tree
     */
    public void insert(String word)
    {
        insert(word, root);
    }

    //insert helper, recursively traverse the tree to add letters
    private Node insert(String word, Node current)
    {
        //when string is empty go back up the tree
        if(word.isEmpty()) {
            return current;
        }

        //if current nodes children are null then create a node array
        if(current.children == null) {
            current.children = new Node[ALPHABET_SIZE];
        }

        //if child node at given character is null then create a new node with character
        //and recursively insert the next character
        if(current.children[charsToIndices.get(word.charAt(0))] == null){

            current.children[charsToIndices.get(word.charAt(0))] = new Node();

            //if it is the last letter of the word then it is flagged for end word
            if(word.length() == 1) {
                current.children[charsToIndices.get(word.charAt(0))].isWord = true;
            }

            current.children[charsToIndices.get(word.charAt(0))] = insert(word.substring(1),
                        current.children[charsToIndices.get(word.charAt(0))]);
        }

        //if node exists with that character then continue with word
        else {
            insert(word.substring(1), current.children[charsToIndices.get(word.charAt(0))]);
        }

        //done, connect the tree back up
        return current;
    }

    //get the file contents and store them into a single string to manipulate later
    private void getFileContents()
    {
        try {
            BufferedReader file = new BufferedReader(new FileReader(FILE_NAME));
            StringBuilder str = new StringBuilder();
            String line = file.readLine();

            //get lines from file
            while(line != null) {
                str.append(line);
                str.append(System.lineSeparator());
                line = file.readLine();
            }

            result = str.toString();
            file.close();
        } catch(Exception e) {
            System.out.println("Could not find the file: " + FILE_NAME);
        }
    }

    //get the words from the string retrieved from the file earlier
    //also begin adding each letter to the tree
    private void getWords() {
        String word;
        String definition;
        Scanner scan = new Scanner(result);

        while (scan.hasNext()) {
            String tmp = scan.nextLine();

            //separate word and definition
            word = tmp.substring(0, tmp.indexOf(":"));
            definition = tmp.substring(tmp.indexOf(":") + 2);

            //insert word into tree
            insert(word);

            //add word and definition to a map
            words.put(word, definition);
        }
    }

    //create a map of chars and ints to match each letter to the node array index
    private void assignIndices()
    {
        char[] characters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        for(int i = 0; i < ALPHABET_SIZE; i++) {
            charsToIndices.put(characters[i], i);
            indicesToChars.put(i, characters[i]);
        }

    }

    @Override
    public String toString() {
        return "DictionaryTree{" +
                "root=" + root +
                ", words=" + words +
                ", charsToIndices=" + charsToIndices +
                ", IndicesToChars=" + indicesToChars +
                ", result='" + result + '\'' +
                '}';
    }

    //node class to create
    private class Node
    {
        private Node[] children;
        private boolean isWord;

        @Override
        public String toString() {
            return "Node{" +
                    "children=" + Arrays.toString(children) +
                    ", isWord=" + isWord +
                    '}';
        }
    }
}
