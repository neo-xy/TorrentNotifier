package layout;


import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basicelixir.pawel.torrentnotifier.JustAddedAdapter;
import com.basicelixir.pawel.torrentnotifier.Movie;
import com.basicelixir.pawel.torrentnotifier.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class HomeTab extends Fragment implements View.OnClickListener, View.OnGenericMotionListener {

    Rect movieTipRect;
    ImageButton  informationBtn;
    TextView titleAdded, top5header, tipHeader, movieTip, tipInf;
    TextView textView1, textView2, textView3, textView4, textView5;
    ImageView addView1, addView2, addView3, addView4, addView5;
    ImageView linkView1, linkView2, linkView3, linkView4, linkView5;
    String imdb1, imdb2, imdb3, imdb4, imdb5;
    String TAG = "pawell";
    RecyclerView recyclerView;
    Realm realm;
    AlertDialog alertDialog;
    View view,view2;
    RealmResults<Movie> results;
    JustAddedAdapter justAddedAdapter;
    RealmChangeListener callback = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {

            justAddedAdapter.update(results);
        }
    };
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    public HomeTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_tab, container, false);
        findViews(view);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ubuntu.ttf");
        titleAdded.setTypeface(typeface);
        movieTip.setTypeface(typeface);
        textView1.setTypeface(typeface);
        textView2.setTypeface(typeface);
        textView3.setTypeface(typeface);
        textView4.setTypeface(typeface);
        textView5.setTypeface(typeface);

        informationBtn.setOnClickListener(this);

        realm = Realm.getDefaultInstance();
        updateAvailableTorrents();
        justAddedAdapter = new JustAddedAdapter(results, getActivity());

        recyclerView.setAdapter(justAddedAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        view2 = LayoutInflater.from(getContext())
                .inflate(R.layout.information_dialog_layout, null);
        alertDialog = new AlertDialog.Builder(getContext(), R.style.ert).create();
        alertDialog.setView(view2);
        tipInf = (TextView)view2.findViewById(R.id.inf_text);

        rootRef.child("top5").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                setTop5(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                setTop5(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        return view;
    }


    private void setTop5(DataSnapshot dataSnapshot) {
        switch (dataSnapshot.getKey()) {
            case "nr1":
                textView1.setText(dataSnapshot.getValue(String.class));
                break;
            case "nr2":
                textView2.setText(dataSnapshot.getValue(String.class));
                break;
            case "nr3":
                textView3.setText(dataSnapshot.getValue(String.class));
                break;
            case "nr4":
                textView4.setText(dataSnapshot.getValue(String.class));
                break;
            case "nr5":
                textView5.setText(dataSnapshot.getValue(String.class));
                break;
            case "imdb1":
                imdb1 = dataSnapshot.getValue(String.class);
                break;
            case "imdb2":
                imdb2 = dataSnapshot.getValue(String.class);
                break;
            case "imdb3":
                imdb3 = dataSnapshot.getValue(String.class);
                break;
            case "imdb4":
                imdb4 = dataSnapshot.getValue(String.class);
                break;
            case "imdb5":
                imdb5 = dataSnapshot.getValue(String.class);
                break;
            case "movieTipOfTheDay":
                movieTip.setText(dataSnapshot.getValue(String.class));
                break;
            case "tipInfo":
                tipInf.setText(dataSnapshot.getValue(String.class));
                break;
            default:

                break;
        }
    }

    public void updateAvailableTorrents() {
        results = realm.where(Movie.class)
                .notEqualTo("torrentFullName", "null")
                .equalTo("availableForDownload", true)
                .equalTo("activated", true)
                .findAll();
        if (results.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            titleAdded.setVisibility(View.VISIBLE);
        }
        results.addChangeListener(callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        results = realm.where(Movie.class)
                .notEqualTo("torrentFullName", "null")
                .equalTo("availableForDownload", true)
                .findAll();
        if (results.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        results.addChangeListener(callback);
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.removeAllChangeListeners();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.add1:
                addToRealm(imdb1, (String) textView1.getText());
                break;
            case R.id.add2:
                addToRealm(imdb2, (String) textView2.getText());
                break;
            case R.id.add3:
                addToRealm(imdb3, (String) textView3.getText());
                break;
            case R.id.add4:
                addToRealm(imdb4, (String) textView4.getText());
                break;
            case R.id.add5:
                addToRealm(imdb5, (String) textView5.getText());
                break;
            case R.id.link1:
                openLink(imdb1);
                break;
            case R.id.link2:
                openLink(imdb2);
                break;
            case R.id.link3:
                openLink(imdb3);
                break;
            case R.id.link4:
                openLink(imdb4);
                break;
            case R.id.link5:
                openLink(imdb5);
                break;
            case R.id.delete_button:
                break;

            case R.id.information_IB:
                hendleInformationClick();
                break;


        }

    }

    private void hendleInformationClick() {

        Rect homeTabRect = new Rect();
        view.getGlobalVisibleRect(homeTabRect);

        movieTipRect = new Rect();
        movieTip.getGlobalVisibleRect(movieTipRect);

        Rect informationDialogRect = new Rect();

//
//        View view2 = LayoutInflater.from(getContext())
//                .inflate(R.layout.information_dialog_layout, null);
//        AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.ert).create();
//        alertDialog.setView(view2);

        alertDialog.show();


        int px = (getResources().getDisplayMetrics().widthPixels);
        double g = px / 1.5;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = (int)( px / 1.5);
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp.y=homeTabRect.bottom-movieTipRect.top;
        alertDialog.getWindow().setAttributes(lp);

    }


    private void openLink(String imdbLink) {
        imdbLink = "http://www." + imdbLink;
        try {
            Intent browserIn = new Intent(Intent.ACTION_VIEW, Uri.parse(imdbLink));
            startActivity(browserIn);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Not Available at the moment", Toast.LENGTH_LONG).show();
        }

    }

    private void addToRealm(final String url, final String title) {
        Realm realm2 = Realm.getDefaultInstance();
        realm2.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Movie movie = new Movie();
                movie.setTitle(title);
                movie.setMovieURL(url);
                movie.setAvailableForDownload(false);
                movie.setActivated(true);
                realm.copyToRealm(movie);
            }
        }, new Realm.Transaction.OnSuccess() {

            @Override
            public void onSuccess() {
                //TODO find fragmnets with tags
                int numberOfFragments = getActivity().getSupportFragmentManager().getFragments().size();
                MyListTab myListTab = (MyListTab) getActivity().getSupportFragmentManager().getFragments().get(numberOfFragments - 1);

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
        realm2.close();
    }


    private void findViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.rec_new_torrents);

        titleAdded = (TextView) view.findViewById(R.id.title_added);
        textView1 = (TextView) view.findViewById(R.id.nr1);
        textView2 = (TextView) view.findViewById(R.id.nr2);
        textView3 = (TextView) view.findViewById(R.id.nr3);
        textView4 = (TextView) view.findViewById(R.id.nr4);
        textView5 = (TextView) view.findViewById(R.id.nr5);

        top5header = (TextView) view.findViewById(R.id.top_5_header);
        tipHeader = (TextView) view.findViewById(R.id.kill_time_header_TV);
        movieTip = (TextView) view.findViewById(R.id.movie_tip_TV);
        informationBtn = (ImageButton) view.findViewById(R.id.information_IB);


        linkView1 = (ImageView) view.findViewById(R.id.link1);
        linkView2 = (ImageView) view.findViewById(R.id.link2);
        linkView3 = (ImageView) view.findViewById(R.id.link3);
        linkView4 = (ImageView) view.findViewById(R.id.link4);
        linkView5 = (ImageView) view.findViewById(R.id.link5);

        addView1 = (ImageView) view.findViewById(R.id.add1);
        addView2 = (ImageView) view.findViewById(R.id.add2);
        addView3 = (ImageView) view.findViewById(R.id.add3);
        addView4 = (ImageView) view.findViewById(R.id.add4);
        addView5 = (ImageView) view.findViewById(R.id.add5);

        linkView1.setOnClickListener(this);
        linkView2.setOnClickListener(this);
        linkView3.setOnClickListener(this);
        linkView4.setOnClickListener(this);
        linkView5.setOnClickListener(this);

        addView1.setOnClickListener(this);
        addView2.setOnClickListener(this);
        addView3.setOnClickListener(this);
        addView4.setOnClickListener(this);
        addView5.setOnClickListener(this);

    }

    @Override
    public boolean onGenericMotion(View v, MotionEvent event) {

        Log.i(TAG, "onGenericMotion: " + event.getAxisValue(MotionEvent.AXIS_Y));
        return false;
    }
}