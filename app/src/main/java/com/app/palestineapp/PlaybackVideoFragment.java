package com.app.palestineapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.leanback.app.VideoSupportFragment;
import androidx.leanback.app.VideoSupportFragmentGlueHost;
import androidx.leanback.media.MediaPlayerAdapter;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.PlaybackControlsRow;

import java.util.Objects;


/**
 * Handles video playback with media controls.
 */
public class PlaybackVideoFragment extends VideoSupportFragment {

    private static final Object YOUTUBE_API_KEY = "AIzaSyCmuyx5l-AO572FIsPjHqTNhJ7XQOK7UNg";
    private PlaybackTransportControlGlue<MediaPlayerAdapter> mTransportControlGlue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Movie movie =
                (Movie) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);

        // Request audio focus to ensure smooth playback
        requestAudioFocus();

        // Set up glue host for video controls
        VideoSupportFragmentGlueHost glueHost =
                new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);

        String videoUrl = movie.getVideoUrl();
        if (isYouTubeUrl(videoUrl)) {

        } else {
            // Handle regular video playback (e.g., from a server or local file)
            initializeMediaPlayerAdapter(videoUrl, glueHost, movie);
        }

    }

    private boolean isYouTubeUrl(String url) {
        return url != null && url.contains("youtube.com") || url.contains("youtu.be");
    }



    private String extractYouTubeVideoId(String url) {
        // Extracts the video ID from a YouTube URL
        String videoId = null;
        if (url.contains("v=")) {
            videoId = url.split("v=")[1];
            if (videoId.contains("&")) {
                videoId = videoId.split("&")[0]; // In case the URL has additional parameters
            }
        }
        return videoId;
    }

    private void initializeMediaPlayerAdapter(String videoUrl, VideoSupportFragmentGlueHost glueHost, Movie movie) {
        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getActivity());
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);

        mTransportControlGlue = new PlaybackTransportControlGlue<>(getActivity(), playerAdapter);
        mTransportControlGlue.setHost(glueHost);
        mTransportControlGlue.setTitle(movie.getTitle());
        mTransportControlGlue.setSubtitle(movie.getDescription());
        mTransportControlGlue.playWhenPrepared();

       // Set the data source (video URL)
        playerAdapter.setDataSource(Uri.parse(videoUrl));

        // Optional: Set up Equalizer for better sound quality
        setupEqualizer(playerAdapter);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }
    }

    /**
     * Request audio focus to ensure uninterrupted playback
     */
    private void requestAudioFocus() {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(
                new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        // Handle audio focus change (e.g., pause or resume playback)
                        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                            // Pause or stop playback when focus is lost
                            if (mTransportControlGlue != null) {
                                mTransportControlGlue.pause();
                            }
                        }
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("Audio", "Audio focus granted.");
        } else {
            Log.d("Audio", "Failed to gain audio focus.");
        }
    }

    /**
     * Set up equalizer for better sound quality.
     * You can tweak the band levels according to your preferences.
     */
    private void setupEqualizer(MediaPlayerAdapter playerAdapter) {
        // Get the MediaPlayer instance from the adapter
        MediaPlayer mediaPlayer = playerAdapter.getMediaPlayer();
        if (mediaPlayer != null) {
            // Create the equalizer and attach it to the media player
            Equalizer equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
            equalizer.setEnabled(true);

            // Adjust equalizer settings (You can modify these levels as needed)
            for (short i = 0; i < equalizer.getNumberOfBands(); i++) {
                equalizer.setBandLevel(i, (short) 1000); // Adjust values to your preference
            }

            // Optionally, adjust the equalizer presets (if needed)
            equalizer.usePreset((short) 0); // Use preset 0 (Flat preset) or any other preset
            setMaxVolume();
        }

    }

    /**
     * Check if Bluetooth is enabled and log the audio routing.
     */
    private void checkAudioOutput() {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isBluetoothA2dpOn()) {
            // Audio is routed to Bluetooth
            Log.d("Audio", "Audio routed to Bluetooth.");
        } else {
            // Audio is routed to device speaker or headphones
            Log.d("Audio", "Audio routed to device speaker.");
        }
    }

    /**
     * Optional: Sync device volume to max when playback starts (if necessary)
     */
    private void setMaxVolume() {
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }
}
