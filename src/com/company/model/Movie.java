package com.company.model;

import java.util.Objects;

/**
 * Class to hold Movie data
 */
public class Movie {
    private int yearOfRelease;
    private String countryCode;
    private String movieTitle;

    public Movie(int yearOfRelease, String countryCode, String movieTitle) {
        Objects.requireNonNull(countryCode);
        Objects.requireNonNull(movieTitle);

        this.yearOfRelease = yearOfRelease;
        this.countryCode = countryCode;
        this.movieTitle = movieTitle;
    }

    @Override
    public String toString() {
        return yearOfRelease + "\t" +  countryCode + "\t" + movieTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        if (yearOfRelease != movie.yearOfRelease) return false;
        if (!countryCode.equals(movie.countryCode)) return false;
        return movieTitle.equals(movie.movieTitle);
    }

    @Override
    public int hashCode() {
        int result = yearOfRelease;
        result = 31 * result + countryCode.hashCode();
        result = 31 * result + movieTitle.hashCode();
        return result;
    }

    public int getYearOfRelease() {
        return yearOfRelease;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
}
