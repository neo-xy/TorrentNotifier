package layout;


import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImdbTab extends Fragment implements View.OnClickListener {

    String TAG = "pawell";

    private WebView webView;
    public String movieURL;
    public String title;
    private String currentUser;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    public ImdbTab() {
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imdb_tab, container, false);

        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.myfab);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    floatingActionButton.setEnabled(true);
                    currentUser = firebaseAuth.getCurrentUser().getUid();
                } else {
                    floatingActionButton.setEnabled(false);
                }
            }
        };

        webView = (WebView) view.findViewById(R.id.webView);
        webView.loadUrl("http://www.imdb.com/");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (webView.getUrl().contains("title")) {
        //TODO title returns swedish title ex stjärnornas krig
        String webTitle = webView.getTitle();
        int s = webTitle.length();
        title = webTitle.substring(0, s - 7);
        movieURL = webView.getUrl();
        if (movieURL.contains("m.") == true) {
            movieURL = movieURL.replace("m.", "");
        }
//TODO contains title räcker inte till "news" inehåller "title" i url också

            movieURL = movieURL.replace("http://", "");
            if(title.contains(".")){
                Log.i(TAG, "onClick:contains . ");
               title = title.replace("."," ");
            }
            if(title.contains(",")){
                title =title.replace(","," ");
            }
            if(title.contains("-")){
               title= title.replace("-"," ");
            }
            Log.i(TAG, "onClick:t " +title);
            firebaseDatabase.getReference().child("users").child(currentUser).child("movieList").child(title).child("url").setValue(movieURL);
            firebaseDatabase.getReference().child("users").child(currentUser).child("movieList").child(title).child("available").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Movie Added to your List", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getContext(),"Not registered as Movie",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }
}
