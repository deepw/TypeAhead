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
 * Tests for QueryHandler
 */
public class QueryHandlerTest {
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
    public void testQueries() throws IOException {
        String pathPrefix = new File("").getAbsolutePath();
        insertHandler.processNewFile(pathPrefix.concat("/test/resources/ValidFileBig"));

        // Allow for insert to finish
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Movie> list;

        // test empty prefix
        list = queryHandler.runQuery("", 0);
        Assert.assertTrue(list.size() == 100);

        list = queryHandler.runQuery("Transy", 0);
        Assert.assertTrue(list.size() == 79);
        Assert.assertTrue(list.get(0).getMovieTitle().equals("Hotel Transylvania 1"));
        Assert.assertTrue(list.get(list.size()-1).getMovieTitle().equals("Hotel Transylvania 9"));

        list = queryHandler.runQuery("Transy", 10);
        Assert.assertTrue(list.size() == 10);
        Assert.assertTrue(list.get(0).getMovieTitle().equals("Hotel Transylvania 1"));
        Assert.assertTrue(list.get(list.size()-1).getMovieTitle().equals("Hotel Transylvania 18"));

        list = queryHandler.runQuery("Ci", 0);
        Assert.assertTrue(list.size() == 6);
        Assert.assertTrue(list.get(0).getMovieTitle().equals("Cinderella (2011)"));
        Assert.assertTrue(list.get(list.size()-1).getMovieTitle().equals("Cinderella (2016)"));

        list = queryHandler.runQuery("doesnotexist", 0);
        Assert.assertTrue(list.size() == 0);

        // test number prefix
        list = queryHandler.runQuery("2", 0);
        Assert.assertTrue(list.size() == 12);

        // test special character prefix
        list = queryHandler.runQuery("ö", 0);
        Assert.assertTrue(list.size() == 0);

        // test matching word
        list = queryHandler.runQuery("Trainwreck", 0);
        Assert.assertTrue(list.size() == 1);

        // test long prefix
        list = queryHandler.runQuery("averyveryveryveryverylongrandomstringforprefix", 0);
        Assert.assertTrue(list.size() == 0);
    }
}
