package com.app.palestineapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MovieList {
    private static Map<String, Set<Movie>> allMovies;
    private static long count = 0;
    private static Set<String> filter = Set.of("Germany", "Islamic","Syrian","Palestine","YouTube", "Qatar","Saudi Arabia", "Jordan","General","Kids","News","Religious","Education","Sports","Music","Movies");

    private static Set<String>movieIds = new HashSet<>();

    public static List<Movie> getList() {
        List<Movie> movieList = new ArrayList<>();
        // Flatten all movies across all groups
        for (Set<Movie> groupMovies : allMovies.values()) {
            movieList.addAll(groupMovies);
        }
        return movieList;
    }

    public static Map<String, Set<Movie>> parseM3U(InputStream inputStream) {
        allMovies = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String title = null;
            String url = null;
            String tvgName = null;
            String tvgLogo = null;
            String groupTitle = null;

// Pre-compiling regex pattern with named capturing groups
            Pattern extInfPattern = Pattern.compile(
                    "#EXTINF:-1(?: tvg-id=\"(?<tvgId>[^\"]*)\")?" +
                            "(?: tvg-logo=\"(?<tvgLogo>[^\"]*)\")?" +
                            "(?: group-title=\"(?<groupTitle>[^\"]*)\")?," +
                            "(?<title>.+)"
            );

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if(line.contains("Not 24/7")) {
                    continue;
                }
                if(line.contains("Not 24/7")) {
                    continue;
                }
                if (line.startsWith("#EXTINF")) {
                    Matcher matcher = extInfPattern.matcher(line);
                    if (matcher.find()) {
                        tvgName = matcher.group(1);
                        tvgLogo = matcher.group(2);
                        groupTitle = matcher.group(3);
                        title = matcher.group(4);
                    }
                } else if (line.startsWith("http") || line.startsWith("https")) {
                    url = line;
                    if (tvgName == null) {
                        tvgName = "Unknown";  // Assign default if not present
                    }
                    if (tvgLogo == null) {
                        tvgLogo = "https://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/card.jpg";  // Default logo
                    }

                    if ( url != null && groupTitle != null) {
                        // Efficiently build and add movies to their respective groups
                        Movie movie = buildMovieInfo(title, groupTitle, tvgName, url, tvgLogo);
                        if(groupTitle != null && filter.contains(groupTitle)){
                            Set<Movie> movies = allMovies.get(groupTitle);
                            if(movieIds.contains(movie.getVideoUrl())){
                               // continue;
                            }

                            if(movies == null){
                                movies = new HashSet<>();
                                movies.add(movie);
                                allMovies.put(groupTitle, movies);
                                movieIds.add(movie.getVideoUrl());
                            }else {
                                allMovies.get(groupTitle).add(movie);
                                movieIds.add(movie.getVideoUrl());
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allMovies;
    }

    private static Movie buildMovieInfo(String title, String groupTitle, String studio, String videoUrl, String cardImageUrl) {
        Movie movie = new Movie();
        movie.setId(count++);
        movie.setTitle(title);
        movie.setDescription(groupTitle);  // Group title as description
        movie.setStudio(studio);
        movie.setCardImageUrl(cardImageUrl);
        movie.setVideoUrl(videoUrl);
        return movie;
    }
}
