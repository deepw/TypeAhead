package com.company.main;

import com.company.data.AtomicTrie;
import com.company.model.Movie;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to handle requests to insert new movies
 */
public class InsertHandler {
    private AtomicTrie trie;
    private List<Movie> allMovies;
    private ExecutorService executorService;

    /**
     * Constructor for Insert Handler
     * @param trie to be used to insert movie titles
     * @param movies to be used to keep track of all movies
     */
    public InsertHandler(AtomicTrie trie, List<Movie> movies, int nThreads) {
        Objects.requireNonNull(trie);
        Objects.requireNonNull(movies);
        this.trie = trie;
        this.allMovies = movies;

        // Set up a thread pool so user does not overwhelm the system
        // by submitting too many insert requests
        this.executorService = Executors.newFixedThreadPool(nThreads);
    }

    /**
     * Shutdown executor service
     */
    public void shutDown() {
        executorService.shutdown();
    }

    /**
     * Method to read a new file line by line and insert all movies.
     * A thread pool thread is assigned to handle the file and caller returns instantly
     * @param fileName containing all movies
     * @throws IOException when file provided is not readable
     */
    public void processNewFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        // Read this file in a new thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                processFile(br, fileName);
            }
        });
    }

    /**
     * Process the file pointed by the buffered reader
     * @param bufferedReader pointing to the file
     * @param fileName name of the file
     */
    public void processFile(BufferedReader bufferedReader, String fileName) {
        // A movie could be returned more than once if the prefix matches multiple
        // words in the title, setup a HashSet to keep unique indices of movies
        Set<Movie> fileMovies = new HashSet<Movie>();

        // First read all the movies in a set and then insert all of them at once
        // to provide all-or-none insertion of movies in a particular file
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                if (tokenizer.countTokens() != 3) {
                    System.out.println(fileName + " is malformed at line:" + line + ". No new movies added.");
                    return;
                }
                // Create a new movie object from the line
                int year = Integer.parseInt(tokenizer.nextToken());
                String countryCode = tokenizer.nextToken();
                String title = tokenizer.nextToken();
                Movie movie = new Movie(year, countryCode, title);
                fileMovies.add(movie);
            }
        } catch (IOException | NoSuchElementException | NumberFormatException exception) {
            System.out.println(fileName + " is malformed at line: " + line + ". No new movies added.");
            return;
        }

        addMovies(fileMovies);
    }

    /**
     * This method is synchronized on allMovies list because it needs to check existence of the movie
     * before adding it to the list, to avoid duplicate entries. A HashSet cannot be used because indexOf operation
     * is required.
     * Basically its a trade off against these two other choices:
     * 1. Allow multiple entries of the same movie in the List [when race occurs]. Since only the index to a movie is
     * used it will not affect the functionality, but could cause space wastage for duplicate movie entries in case
     * of a race condition.
     * 2. Save the complete movie object in the leaf Node of the Trie so there is no requirement of this list at all.
     * That would mean a movie object for a title with N words will be saved N times in the trie. If assumed movie
     * titles are short, this might not be too bad of a choice either.
     * Although the critical synchronized section is minimal in the overall insert operation, this approach
     * takes memory efficiency over complete concurrency.
     * @param newMovies is the set of new movies to be added to allMovies list. Titles are converted to lower case
     *                  when adding to the trie
     */
    public void addMovies(Set<Movie> newMovies) {
        // Add the movie to the list if its not already existing
        for (Movie movie : newMovies) {
            // synchronization is required to ensure a movie is only added once.
            synchronized (allMovies) {
                if (!allMovies.contains(movie)) {
                    allMovies.add(movie);
                }
            }

            // Add all the words of the title to the Trie with the index in the list as metadata
            trie.insertSentence(movie.getMovieTitle().toLowerCase(), allMovies.indexOf(movie));
        }
    }
}
