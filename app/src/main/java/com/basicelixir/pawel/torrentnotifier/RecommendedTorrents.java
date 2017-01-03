package com.basicelixir.pawel.torrentnotifier;

import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Pawel on 15/12/2016.
 */

public class RecommendedTorrents {


    String TAG = "pawell";

    XmlPullParserFactory factory;

    public RecommendedTorrents() {

        setUpRecommendedTorrents();


    }

    private void setUpRecommendedTorrents() {
        Log.i(TAG, "setUpRecommendedTorrents: ");

        URL recTorrentsURl = null;
        try {
            recTorrentsURl = new URL("https://rarbg.to/torrents.php");



        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection con = null;
        try {
            con = (HttpsURLConnection) recTorrentsURl.openConnection();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod("GET");



        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        InputStream is = null;

        try {
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder total= new StringBuilder();
            String line;

            while((line= br.readLine())!=null){
                total.append(line).append('\n');

            }
            Log.i(TAG, "setUpRecommendedTorrents:"+total);

            try {
                readIs(is);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void readIs(InputStream is) throws IOException, XmlPullParserException {


        XmlPullParser xpp = Xml.newPullParser();
        xpp.isEmptyElementTag();
        xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        xpp.setInput(is, null);
        int end = XmlPullParser.END_DOCUMENT;
        while(xpp.getEventType()!=XmlPullParser.END_DOCUMENT){
            if(xpp.getEventType()==XmlPullParser.START_TAG){
                switch (xpp.getName()){
                    case "div":
                        readDiv(xpp);
                        break;
                    default:
                        xpp.next();
                        break;

                }
            }
        }

    }

    private void readDiv(XmlPullParser xpp) throws IOException, XmlPullParserException {
        xpp.next();
        while(xpp.getName().equals("table")==false){
            xpp.next();
        }
        xpp.next();
        while (xpp.getName().equals("tr")==false){
            xpp.next();
        }
        xpp.next();
        while(xpp.getName().equals("td")==false){
            xpp.next();
        }
        while(xpp.getName().equals("table")==false){
            Log.i(TAG, "readDiv: "+xpp.getName());
        }


    }


    private void skip(XmlPullParser parse) throws XmlPullParserException, IOException {
        if (parse.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parse.next()) {
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
