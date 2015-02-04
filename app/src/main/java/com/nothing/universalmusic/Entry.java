package com.nothing.universalmusic;

/**
 * Created by andy on 4/02/15.
 */
public class Entry {
    public static String ARTIST = "artist";
    public static String ALBUM = "album";
    public static String TRACK = "track";
    public static String IMAGE = "imageFileName";
    public static String MP3 = "sampleFileName";

    public final String artist;
    public final String album;
    public final String track;
    public final String imageUrl;
    public final String mp3Url;

    public Entry(String artist, String track, String album, String imageUrl, String mp3Url) {
        this.artist = artist;
        this.track = track;
        this.album = album;
        this.imageUrl = imageUrl;
        this.mp3Url = mp3Url;
    }

    public String getImageUrl()
    {
        return "https://umusic-image.s3.amazonaws.com/" + imageUrl;
    }
    public String getMp3Url()
    {
        return "https://umusic-sample.s3.amazonaws.com/" + mp3Url;
    }

    @Override
    public String toString() {
        return artist + "\t\t" + track + "\t\t" +album + "\t\t" +getImageUrl() + "\t\t" +getMp3Url();
    }
}