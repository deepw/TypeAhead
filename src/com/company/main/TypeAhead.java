package com.company.main;

import com.company.data.AtomicTrie;
import com.company.model.Movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Class for Controlling insert and query operations
 */
public class TypeAhead {
    private static InsertHandler insertHandler;
    private static QueryHandler queryHandler;

    // this is configured to use UTF-8
    private static int CHARACTER_SPACE = 256;
    private static int MAX_RESULTS = 10;
    private static int MAX_THREADS = 100;

    /**
     * Method to print the help for the user
     */
    private static void printHelp() {
        System.out.println("Usage: [process-file|query|quit] [parameters..]");
    }

    /**
     * Method to handle command read from stdin
     * @param command read from stdin
     * @return true if user has requested to quit, false otherwise
     */
    static boolean handleCommand(String command) {
        StringTokenizer tokenizer = new StringTokenizer(command);
        String directive = tokenizer.nextToken();
        switch (directive) {
            case "process-file": {
                if (!tokenizer.hasMoreTokens()) {
                    System.out.println("Please provide a valid file name.");
                    printHelp();
                    break;
                }
                String fileName = tokenizer.nextToken();
                try {
                    insertHandler.processNewFile(fileName);
                } catch (IOException exception) {
                    System.out.println("File " + fileName + " cannot be read. Please provide a valid file.");
                }
                break;
            }
            case "query": {
                if (!tokenizer.hasMoreTokens()) {
                    System.out.println("Please provide a prefix.");
                    printHelp();
                    break;
                }
                String prefix = tokenizer.nextToken();
                queryHandler.printQueryResult(prefix, MAX_RESULTS);
                break;
            }
            case "quit": {
                insertHandler.shutDown();
                return true;
            }
            default: {
                System.out.println("Invalid input.");
                printHelp();
            }
        }
        return false;
    }

    public static void main(String[] args) {
        // Initialize the Trie and List of Movies for storage
        AtomicTrie trie = new AtomicTrie(CHARACTER_SPACE);
        List<Movie> allMovies = Collections.synchronizedList(new ArrayList<Movie>());
        insertHandler = new InsertHandler(trie, allMovies, MAX_THREADS);
        queryHandler = new QueryHandler(trie, allMovies);

        // Run cmd loop till user quits
        boolean quit = false;
        while (!quit) {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            // ignore empty input
            if (command.length() > 0) {
                quit = handleCommand(command);
            }
        }
    }
}
