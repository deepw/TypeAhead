package com.company.data;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Class for storing Trie data structure.
 * Insertion in the trie is lock free and is made thread safe by using atomic operations to update
 * trie connections. Inserts allow an integer to be added which are returned as part of queries.
 * When setting up the trie, the character space needs to be defined. Eg. for UTF-8 its 256.
 */
public class AtomicTrie {
    private TrieNode root;
    private int characterSpace;

    /**
     * Constructor for AtomicTrie
     * @param characterSpace defines the character space used for each node in the Trie
     */
    public AtomicTrie(int characterSpace) {
        if (characterSpace <=0 ) {
            throw new IllegalArgumentException();
        }
        this.characterSpace = characterSpace;
        this.root = new TrieNode(this.characterSpace);
    }

    /**
     * This method is used to insert a string into the Trie.
     * This is also marked as public if user wants to insert a single word into the trie.
     * The insert uses atomic operation and hence is thread safe.
     * @param word to be inserted into the Trie
     * @param metadata is an integer value associated with the word
     */
    public void insertWord(String word, int metadata) {
        int index = 0;
        TrieNode node = root;
        while (index < word.length()) {
            char nextChar = word.charAt(index);
            if (nextChar > characterSpace) {
                System.out.println("Not a valid character. Not adding word " + word + " to the trie.");
                return;
            }
            TrieNode nextNode = node.nextCharacter[nextChar].get();
            if (nextNode == null) {
                TrieNode newTrieNode = new TrieNode(characterSpace);
                // Atomically compare and set the next node for this character
                node.nextCharacter[nextChar].compareAndSet(null, newTrieNode);
            }
            node = node.nextCharacter[nextChar].get();
            index++;
        }
        // Add metadata integer to the vector associated with this node
        node.metadata.add(metadata);
        // Mark the last node as Leaf to mark ending of the word.
        node.isLeaf = true;
    }

    /**
     * Method to allow user to insert all words in a sentence into the trie.
     * The words are assumed to be delimited by a space.
     * @param sentence list of words delimited by space
     * @param metadata integer value associated with each word in the list of words
     */
    public void insertSentence(String sentence, int metadata) {
        insertSentence(sentence, " ", metadata);
    }

    /**
     * Method to allow user to insert all words in a sentence into the trie.
     * @param sentence list of words
     * @param delimiter this separates the words in the sentence
     * @param metadata integer value associated with each word in the list of words
     */
    public void insertSentence(String sentence, String delimiter, int metadata) {
        StringTokenizer tokenizer = new StringTokenizer(sentence, delimiter);
        while (tokenizer.hasMoreElements()) {
            insertWord(tokenizer.nextToken(), metadata);
        }
    }

    /**
     * Method to search for all words in the trie that have the given prefix
     * @param prefix to search in the trie
     * @return a set of integers corresponding to the metadata at all the matching leaf nodes
     */
    public Set<Integer> searchAll(String prefix) {
        int index = 0;
        TrieNode node = root;
        Set<Integer> resultMetadata = new HashSet<>();
        while (index < prefix.length()) {
            char nextChar = prefix.charAt(index);
            if (node.nextCharacter[nextChar].get() == null) {
                return resultMetadata;
            } else {
                node = node.nextCharacter[nextChar].get();
            }
            index++;
        }
        // If the prefix exists in the trie, then add all the leaves in the sub trie to the set
        searchSubTrie(node, resultMetadata);
        return resultMetadata;
    }

    /**
     * Method to exhaustively walk the sub trie and add metadata integers at the leaf nodes to the set
     * @param node root node of the sub trie
     * @param resultMetadata set of integers associated with all the leaf nodes in the sub trie
     */
    private void searchSubTrie(TrieNode node, Set<Integer> resultMetadata) {
        if (node.isLeaf) {
            // read the size of the vector first and then add those elements.
            // Not using an iterator because node.metadata could change while inserting new words.
            // Reading the size is a snapshot of the metadata when query is hit.
            int count = node.metadata.size();
            for (int i = 0; i < count; i++) {
                resultMetadata.add(node.metadata.get(i));
            }
        }
        for (int i = 0; i < characterSpace; i++) {
            if (node.nextCharacter[i].get() != null) {
                searchSubTrie(node.nextCharacter[i].get(), resultMetadata);
            }
        }
    }
}
