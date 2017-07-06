import com.company.data.AtomicTrie;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tests for Atomic Trie
 */
public class AtomicTrieTest {
    @Test
    public void testWords() {
        AtomicTrie trie = new AtomicTrie(256);
        trie.insertWord("this", 1);
        trie.insertWord("then", 2);
        trie.insertWord("those", 3);
        trie.insertWord("thy", 4);
        trie.insertWord("test", 5);
        trie.insertWord("tier", 6);
        trie.insertWord("null", 0);

        Set<Integer> result = trie.searchAll("th");
        Assert.assertTrue(result.contains(1));
        Assert.assertTrue(result.contains(2));
        Assert.assertTrue(result.contains(3));
        Assert.assertTrue(result.contains(4));
        // Does not contain check
        Assert.assertTrue(!result.contains(5));
        Assert.assertTrue(!result.contains(6));
    }

    @Test
    public void testSentences() {
        AtomicTrie trie = new AtomicTrie(256);
        trie.insertSentence("this is a", 1);
        trie.insertSentence("then is b", 2);
        trie.insertSentence("c is those", 3);
        trie.insertSentence("d thy", 4);
        trie.insertSentence("test for e", 5);

        Set<Integer> result = trie.searchAll("th");
        Assert.assertTrue(result.contains(1));
        Assert.assertTrue(result.contains(2));
        Assert.assertTrue(result.contains(3));
        Assert.assertTrue(result.contains(4));
        // Does not contain check
        Assert.assertTrue(!result.contains(5));
    }

    @Test
    public void testMultiThreaded() {
        int nThreads = 2000 ;
        AtomicTrie trie = new AtomicTrie(256);
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < nThreads; i++) {
            final int sentenceNumber = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    String sentence = "A pretty pretty long sentence with sentence number that is essentially a"
                    + " counter" + sentenceNumber;
                    trie.insertSentence(sentence, sentenceNumber);
                }
            });
        }

        // wait for the insert to finish
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<Integer> result = trie.searchAll("num");
        Assert.assertTrue(result.size() == nThreads);
        for (int i = 0; i < nThreads; i++) {
            Assert.assertTrue(result.contains(i));
        }
    }
}
