package com.nothing.universalmusic;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements ImageRowView.NewSongListener {

    private PlaceholderFragment fragment;
    private XmlPullParser parser;
    private MediaPlayer mediaPlayer;
    private ArrayList<ImageRowView> arrView;
    private ArrayList<FrameLayout> arrFrame;
    private Entry prevEntry;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = new PlaceholderFragment();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        }

        System.out.println("--- start ---");

        try {
            downloadUrl("http://www.getmusic.com.au/hackDay.xml", new ReceivesStream() {
                @Override
                public void setStream(InputStream stream) {
                    InputStream xmlStream = stream;
                    try {
                        parse( xmlStream );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ReceivesStream
    {
        public void setStream( InputStream stream );
    }

    private void downloadUrl(final String urlString, final ReceivesStream receivesStream ) throws IOException {

        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    System.out.println("urlString: " + urlString);
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    receivesStream.setStream( stream );

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }

    public void parse( InputStream in ) throws IOException {
        try
        {
            System.out.println("--- parse ---");

            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            List<Entry> list = readFeed( parser );

            System.out.println("~ list ~");
            System.out.println( list.size() );

//            for(Entry e : list ) System.out.println( e.toString() );

            displayTracks( list );

//            return readFeed(parser);
        }catch(Exception e){ e.printStackTrace(); } finally {

            in.close();
        }
    }

    private void displayTracks(final List<Entry> list)
    {
        arrView = new ArrayList<ImageRowView>();
        arrFrame = new ArrayList<FrameLayout>();

        final MainActivity activity = this;

        runOnUiThread( new Runnable() {
            @Override
            public void run() {
            int nImages = 3;
            for( int i = 0 ; i < 10 ; ++i )
            {
                final ImageRowView v = new ImageRowView( getApplicationContext() );
                v.idRow = i;



                for( int k = 0 ; k < nImages ; ++k ) v.loadImage(list.remove(0), activity );

                nImages ++;
                if(nImages == 10 ) nImages += 2;

                fragment.getView().addView( v );

                v.setNewSongListener( activity );

                arrView.add( v );

                FrameLayout f = new FrameLayout( getApplicationContext() );
                arrFrame.add( f );
                fragment.getView().addView( f );

            }



            }
        });
    }


    public void newSong( Entry entry , int rowId )
    {
        if( textView != null ) if (textView.getParent() != null) ((ViewGroup) textView.getParent()).removeAllViews();


        textView = new TextView( getApplicationContext() );

        textView.setText( "track: " + entry.track + "\nalbum: " + entry.album + "\nartist: " + entry.artist );

        textView.setMinimumHeight(10);
        textView.setTextSize(25);
//        int pad = 15;
//        textView.setPadding( pad,pad,pad,pad);
        textView.setPaddingRelative( 15 , 0,0,0);
        textView.setBackgroundColor( 0xFF000000 + (int)( 0x444444 * Math.random() ) );

        arrFrame.get( rowId ).addView( textView );


        // stop song.
        if( mediaPlayer != null )
        {
            if( prevEntry != null && entry.getMp3Url().equals( prevEntry.getMp3Url() ) )
            {
                if( mediaPlayer.isPlaying() )
                {
                    mediaPlayer.pause();
                    return;
                }
                else
                {
//                    mediaPlayer.reset();
                    mediaPlayer.start();
                    return;
                }

            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        prevEntry = entry;

        //load song.

        // start this song.

        System.out.println("Main newSong");



        Uri myUri = Uri.parse( entry.getMp3Url() );
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, myUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare(); //don't use prepareAsync for mp3 playback
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    setChildAlpha( 1f );
                    if( textView != null ) if (textView.getParent() != null) ((ViewGroup) textView.getParent()).removeAllViews();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChildAlpha( float alpha )
    {
        for( ImageRowView v : arrView )
        {
            v.setChildAlpha( alpha );
        }
    }

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

//        parser.require(XmlPullParser.START_TAG, ns, null);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
//            if (name.equals("entry")) {
                entries.add(readEntry(parser));
//            } else {
//                skip(parser);
//            }
        }
        return entries;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ViewGroup view;

        public PlaceholderFragment()
        {

        }

        public ViewGroup getView()
        {
            return view;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            view = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }


    }





    final String ns = null;

    // Parses the contents of an entry. If it encounters a artist, track, or album tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "entry");

        String artist = null;
        String track = null;
        String album = null;
        String imageUrl = null;
        String mp3Url = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(Entry.ARTIST))      artist = readSimple(parser,Entry.ARTIST);
            else if (name.equals(Entry.TRACK))  track = readSimple(parser, Entry.TRACK);
            else if (name.equals(Entry.ALBUM))  album = readSimple(parser, Entry.ALBUM);
            else if (name.equals(Entry.MP3))    mp3Url = readSimple(parser, Entry.MP3);
            else if (name.equals(Entry.IMAGE))  imageUrl = readSimple(parser, Entry.IMAGE);
            else skip(parser);

        }

        return new Entry(artist, track, album, imageUrl, mp3Url);
    }

    // Processes artist tags in the feed.
    private String readSimple(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag );
        String artist = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return artist;
    }

    // Processes album tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "album");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("album")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "album");
        return link;
    }

    // Processes track tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "track");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "track");
        return summary;
    }

    // For the tags artist and track, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}