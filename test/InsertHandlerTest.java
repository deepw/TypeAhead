import com.company.data.AtomicTrie;
import com.company.main.InsertHandler;
import com.company.main.QueryHandler;
import com.company.model.Movie;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test Class for InsertHandler
 */
public class InsertHandlerTest {
    private static AtomicTrie trie;
    private static List<Movie> allMovies;
    private static InsertHandler insertHandler;
    private static QueryHandler queryHandler;

    @Before
    public void setUp() {
        trie = new AtomicTrie(256);
        allMovies = Collections.synchronizedList(new ArrayList<Movie>());
        insertHandler = new InsertHandler(trie, allMovies, 100);
        queryHandler = new QueryHandler(trie, allMovies);
    }

    @Test
    public void testInsertFiles() throws IOException {
        String pathPrefix = new File("").getAbsolutePath();

        insertHandler.processNewFile(pathPrefix.concat("/test/resources/emptyFile"));
        assertListSize(queryHandler, "", 0, 500);

        insertHandler.processNewFile(pathPrefix.concat("/test/resources/InvalidCharacterFile"));
        assertListSize(queryHandler, "", 1, 500);

        insertHandler.processNewFile(pathPrefix.concat("/test/resources/MalformedFile1"));
        assertListSize(queryHandler, "", 1, 500);

        insertHandler.processNewFile(pathPrefix.concat("/test/resources/MalformedFile2"));
        assertListSize(queryHandler, "", 1, 500);

        insertHandler.processNewFile(pathPrefix.concat("/test/resources/ValidFile"));
        assertListSize(queryHandler, "", 4, 500);

        insertHandler.processNewFile(pathPrefix.concat("/test/resources/ValidFileBig"));
        assertListSize(queryHandler, "", 101, 500);
    }

    /**
     * This tests starts all inserts at once.
     * @throws IOException
     */
    @Test
    public void testMultiThreaded() throws IOException {
        String pathPrefix = new File("").getAbsolutePath();
        insertHandler.processNewFile(pathPrefix.concat("/test/resources/emptyFile"));
        insertHandler.processNewFile(pathPrefix.concat("/test/resources/InvalidCharacterFile"));
        insertHandler.processNewFile(pathPrefix.concat("/test/resources/MalformedFile1"));
        insertHandler.processNewFile(pathPrefix.concat("/test/resources/MalformedFile2"));
        insertHandler.processNewFile(pathPrefix.concat("/test/resources/ValidFile"));
        insertHandler.processNewFile(pathPrefix.concat("/test/resources/ValidFileBig"));

        assertListSize(queryHandler, "", 101, 1000);
    }

    private void assertListSize(QueryHandler queryHandler, String prefix, int size, int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Movie> list = queryHandler.runQuery(prefix, 0);
        System.out.println("size = " + list.size());
        for (Movie movie : list) {
            System.out.println(movie);
        }
        Assert.assertTrue(list.size() == size);
    }
}
