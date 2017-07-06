package com.company.main;

import com.company.data.AtomicTrie;
import com.company.model.Movie;
import com.company.model.MovieCompareOnTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Class to Handle Queries
 */
public class QueryHandler {
    private AtomicTrie trie;
    private List<Movie> allMovies;

    /**
     * Constructor for Query Handler
     * @param trie to be used to search prefixes
     * @param movies to be used to look up movie objects based on indices received from trie
     */
    public QueryHandler(AtomicTrie trie, List<Movie> movies) {
        Objects.requireNonNull(trie);
        Objects.requireNonNull(movies);
        this.trie = trie;
        this.allMovies = movies;
    }

    /**
     * Method to find all the Movies that match the prefix. All words of the movie title are searched against
     * the prefix. Prefixes are converted to lower case for searching.
     * @param prefix to use for searching for titles
     * @param maxResults limit the number of movie objects returned. Should be set to 0 if all the results are required
     * @return a list of movie objects sorted on the movie title
     */
    public List<Movie> runQuery(String prefix, int maxResults) {
        Set<Integer> resultSet;
        List<Movie> resultList = new ArrayList<Movie>();

        // Search the trie for the given index
        resultSet = trie.searchAll(prefix.toLowerCase());
        for (int index : resultSet) {
            resultList.add(allMovies.get(index));
        }

        // Sort the list based on movie titles
        Collections.sort(resultList, new MovieCompareOnTitle());

        // If maxResults is 0 return the complete list, otherwise return subList
        if (maxResults == 0 || resultList.size() <= maxResults) {
            return resultList;
        } else {
            return resultList.subList(0, maxResults);
        }
    }

    /**
     * Method to find and print all the Movies that match the prefix. All words of the movie title are searched against
     * the prefix. Prefixes are converted to lower case for searching.
     * @param prefix to use for searching titles
     * @param maxResults limit the number of movie objects returned. Should be set to 0 if all the results are
     *                   required to be printed.
     */
    public void printQueryResult(String prefix, int maxResults) {
        List<Movie> resultList = runQuery(prefix, maxResults);
        for (Movie movie : resultList) {
            System.out.println(movie);
        }
    }
}
