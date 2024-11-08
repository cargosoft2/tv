package com.app.palestineapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class MovieList {

    private static List<Movie> list;
    private static long count = 0;

    public static List<Movie> getList() {
        return list;
    }

    public static List<Movie> parseM3U(InputStream inputStream) {

        list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String title = null;
            String tvgId = null;
            String tvglogo = null;
            String tvgname = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#EXTINF:")) {
                    // Extract tvg-id and tvg-logo using regex
                    tvgname = line.replaceAll(".*tvg-name=\"([^\"]+)\".*", "$1");
                    tvgId = line.replaceAll(".*tvg-id=\"([^\"]+)\".*", "$1");
                    tvglogo = line.replaceAll(".*tvg-logo=\"([^\"]+)\".*", "$1");
                    title = line.replaceAll(".*group-title=\"([^\"]+)\".*", "$1");

                    if(title != null && title.contains("#EXTINF") && tvgId != null ) {
                        title = tvgId;
                    }

                } else if (line.startsWith("http") || line.startsWith("rtsp") || line.startsWith("rtmp")) {
                    // The URL for the movie
                    if(tvglogo == null || (tvglogo != null && tvglogo.contains("#EXTINF") )) {
                        tvglogo = "https://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review/card.jpg";
                    }
                    list.add(buildMovieInfo(
                            tvgname,
                            tvgId,
                            title,
                            line.trim(),
                            tvglogo
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    private static Movie buildMovieInfo(
            String title,
            String description,
            String studio,
            String videoUrl,
            String cardImageUrl) {
        Movie movie = new Movie();
        movie.setId(count++);
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setStudio(studio);
        movie.setCardImageUrl(cardImageUrl);
        movie.setVideoUrl(videoUrl);
        return movie;
    }
}