package com.company.data;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class to define each node in the Trie
 * When setting up the TrieNode, the character space needs to be defined. Eg. for UTF-8 its 256.
 */
public class TrieNode {
    AtomicReference<TrieNode>[] nextCharacter;
    Vector<Integer> metadata;
    boolean isLeaf;

    TrieNode(int characterSpace) {
        if (characterSpace <= 0) {
            throw new IllegalArgumentException();
        }
        nextCharacter = new AtomicReference[characterSpace];
        for (int i = 0; i < characterSpace; i++) {
            nextCharacter[i] = new AtomicReference<TrieNode>(null);
        }
        metadata = new Vector<Integer>();
        isLeaf = false;
    }
}
