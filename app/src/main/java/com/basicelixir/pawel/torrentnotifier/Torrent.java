package com.basicelixir.pawel.torrentnotifier;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pawel on 14/05/2016.
 */
public class Torrent extends AsyncTask<Void, Void, ArrayList<NewTorrentMovies>> {


    private static final String ns = null;
    String TAG = "pawell";
    InputStream inputStream;
    ArrayList<NewTorrentMovies>urlList= new ArrayList<NewTorrentMovies>();
    int f =0;

    @Override
    protected ArrayList<NewTorrentMovies> doInBackground(Void... params) {

        try {
            URL url = new URL("http://ddlvalley.cool/category/movies/feed/");

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (httpURLConnection == null) {
                Log.i(TAG, "No Connection");
            }
            httpURLConnection.setRequestMethod("GET");
            // InputStream inputStream =httpURLConnection.getInputStream();
            inputStream = httpURLConnection.getInputStream();

            readStream(inputStream);
        } catch (Exception e) {
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return urlList;
    }

    public void readStream(InputStream inputStream) {
        XmlPullParser xmlpullParse = Xml.newPullParser();

        try {
            xmlpullParse.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            xmlpullParse.setInput(inputStream, null);
            xmlpullParse.nextTag();
            xmlpullParse.nextTag();
            readChannel(xmlpullParse);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readChannel(XmlPullParser xmlpullParse) throws IOException, XmlPullParserException {
        xmlpullParse.require(XmlPullParser.START_TAG, ns, "channel");

        while (xmlpullParse.next() != XmlPullParser.END_TAG) {
            if (xmlpullParse.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tag = xmlpullParse.getName();
            if (tag.equalsIgnoreCase("item")) {
                readItem(xmlpullParse);

            }else{
                skip(xmlpullParse);
            }
        }
    }

    private String readDescription(XmlPullParser xmlpullParse) throws XmlPullParserException, IOException {
xmlpullParse.nextToken();

        String CDATA = xmlpullParse.getText();
        int b = CDATA.indexOf("http://www.imdb.com/");
        int s = CDATA.indexOf(">iMDB");
        String imdbUrl="";
        if(b>-1&&s>-1){
            imdbUrl = CDATA.substring(b,s-1);
            if(imdbUrl.contains("www.")==true){
                imdbUrl = imdbUrl.replace("www.","");
            }

            int indexRef =imdbUrl.indexOf("?ref");
            if(indexRef!=-1){
              imdbUrl =  imdbUrl.substring(0,indexRef);
            }
        }
        xmlpullParse.nextTag();
        return imdbUrl;
    }

    private String readTitle(XmlPullParser xmlpullParse) throws IOException, XmlPullParserException {
        String title="";
        if (xmlpullParse.next() == XmlPullParser.TEXT) {
            String result = xmlpullParse.getText();

            title= result;
            xmlpullParse.nextTag();
        }
        return title;
    }

    private void skip(XmlPullParser xmlpullParse) throws XmlPullParserException, IOException {
        if (xmlpullParse.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (xmlpullParse.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private void readItem(XmlPullParser xmlPullparser) throws IOException, XmlPullParserException {
        String fullTitle="";
        String imdbUrl="";
        NewTorrentMovies newTorrentMovies = new NewTorrentMovies();
        while (xmlPullparser.next() != XmlPullParser.END_TAG) {


            if (xmlPullparser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tag = xmlPullparser.getName();
            if (tag.equalsIgnoreCase("title")) {
                fullTitle = readTitle(xmlPullparser);

                newTorrentMovies.setFulltorrentName(fullTitle);
            }else if(tag.equalsIgnoreCase("description")) {
               imdbUrl = readDescription(xmlPullparser);
                newTorrentMovies.setImdbUrl(imdbUrl);
                urlList.add(newTorrentMovies);
            } else {

                skip(xmlPullparser);
            }
        }
    }

    @Override
    protected void onPostExecute(ArrayList<NewTorrentMovies> strings) {
        super.onPostExecute(strings);
    }
}
