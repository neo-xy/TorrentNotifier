package layout;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.basicelixir.pawel.torrentnotifier.Movie;
import com.basicelixir.pawel.torrentnotifier.R;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImdbTab extends Fragment implements View.OnClickListener {

    WebView webView;
    String TAG = "pawell";
    String movieURL;

    public ImdbTab() {
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.imdb_tab, container, false);

        webView = (WebView) view.findViewById(R.id.webView);
        webView.loadUrl("http://www.imdb.com/");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        // webView.getSettings().setJavaScriptEnabled(true);
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.myfab);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        //TODO title returns swedish title ex stjärnornas krig
        String webTitle = webView.getTitle();
        int s = webTitle.length();
        final String title = webTitle.substring(0, s - 7);
         movieURL = webView.getUrl();
        if(movieURL.contains("m.")==true){
            movieURL = movieURL.replace("m.","");
        }
//TODO contains title räcker inte till "news" inehåller "title" i url också
        if (movieURL.contains("title")) {
            Log.i(TAG, "omovieurl: "+ movieURL);
            movieURL =movieURL.replace("http://","");

            Log.i(TAG, "omovieurl: "+ movieURL);
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Movie movie = new Movie();
                    movie.setMovieURL(movieURL);
                    movie.setTitle(title);
                    movie.setAvailableForDownload(false);
                    movie.setActivated(true);
                    realm.copyToRealm(movie);

                }
            }, new Realm.Transaction.OnSuccess() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, "onSuccess: "+getActivity().getSupportFragmentManager().getFragments().size());

                    MyListTab myListTab = (MyListTab) getActivity().getSupportFragmentManager().getFragments().get(2);

                    myListTab.add();
                    Toast.makeText(getContext(), "Movie Added", Toast.LENGTH_LONG).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.i(TAG, "onError: " + error);
                    //TODO var mer exact vilken error innan Toast
                    Toast.makeText(getContext(), "Movie allready in Your List", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.i(TAG, "else");
            Toast.makeText(getContext(), "No Movie choosed", Toast.LENGTH_LONG).show();
        }

    }
}
