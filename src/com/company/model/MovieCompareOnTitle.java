package com.company.model;

import java.util.Comparator;

/**
 * This is a comparator class to compare movies based on their title
 * This is explicitly placed outside the Movie class assuming movies can be sorted based on other fields too.
 * Eg. year.
 */
public class MovieCompareOnTitle implements Comparator<Movie> {
    @Override
    public int compare(Movie o1, Movie o2) {
        return o1.getMovieTitle().compareTo(o2.getMovieTitle());
    }
}
