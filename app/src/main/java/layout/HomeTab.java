package layout;


import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basicelixir.pawel.torrentnotifier.ChatDialog;
import com.basicelixir.pawel.torrentnotifier.JustAddedAdapter;
import com.basicelixir.pawel.torrentnotifier.MainActivity;
import com.basicelixir.pawel.torrentnotifier.NottifiactionService;
import com.basicelixir.pawel.torrentnotifier.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;

public class HomeTab extends Fragment implements View.OnClickListener {


    private View view;
    private ImageButton btnTip;
    private TextView tvTipTitle;
    private TextView textView1, textView2, textView3, textView4, textView5;
    private ImageView addView1, addView2, addView3, addView4, addView5;
    private ImageView linkView1, linkView2, linkView3, linkView4, linkView5;
    TextView newMessageIcon;

    private String imdb1, imdb2, imdb3, imdb4, imdb5, tipLink;
    private String TAG = "pawell";

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ImageButton chatBtn;
    public int nrItem;


    public HomeTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_tab, container, false);


        if (savedInstanceState == null) {
        } else {
        }
        firebaseAuth = FirebaseAuth.getInstance();

        btnTip = (ImageButton) view.findViewById(R.id.ib_tip_link);
        tvTipTitle = (TextView) view.findViewById(R.id.tv_tip_title);
        newMessageIcon = (TextView) view.findViewById(R.id.btn_chat_notis2);


        findViews(view);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ubuntu.ttf");

        textView1.setTypeface(typeface);
        textView2.setTypeface(typeface);
        textView3.setTypeface(typeface);
        textView4.setTypeface(typeface);
        textView5.setTypeface(typeface);

        btnTip.setOnClickListener(this);

        databaseReference.child("top5").addChildEventListener(new ChildEventListener() {

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
        databaseReference.child("tip").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
                    case "link":
                        tipLink = dataSnapshot.getValue().toString();
                        break;
                    case "title":
                        tvTipTitle.setText(dataSnapshot.getValue().toString());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

        if (firebaseAuth.getCurrentUser() != null) {

            checkForMessages(firebaseAuth.getCurrentUser().getUid(), databaseReference);
        }

        return view;
    }

    private void checkForMessages(String uid, DatabaseReference databaseReference) {

        databaseReference.child("users").child(uid).child("question").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: zzzzzzz");
                if (dataSnapshot.getValue() != null) {
                    setMessageIcon();
                  nrItem=1;

                }
                if (dataSnapshot.getValue() == null) {
                    newMessageIcon.setVisibility(View.GONE);
                    nrItem=0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (firebaseAuth.getCurrentUser() != null) {
            int id = v.getId();
            switch (id) {
                case R.id.add1:
                    addMovieToDatabase(imdb1, textView1.getText().toString());
                    break;
                case R.id.add2:
                    addMovieToDatabase(imdb1, textView2.getText().toString());
                    break;
                case R.id.add3:
                    addMovieToDatabase(imdb1, textView3.getText().toString());
                    break;
                case R.id.add4:
                    addMovieToDatabase(imdb1, textView4.getText().toString());
                    break;
                case R.id.add5:
                    addMovieToDatabase(imdb1, textView5.getText().toString());
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
                case R.id.ib_tip_link:
                    openLink(tipLink);
                    break;

                default:

                    break;
            }

        } else {
            Toast.makeText(getContext(), "Log In to Usa this Fucnction", LENGTH_SHORT).show();
        }
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

    private void addMovieToDatabase(String imdbUrl, String title) {
        databaseReference.child("users").child(((MainActivity) getActivity()).getCurrentUser()).child("movieList").child(title).child("url").setValue(imdbUrl);
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("movieList").child(title).child("available").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Movie added to Your List", LENGTH_SHORT).show();
            }
        });
    }

    private void findViews(View view) {

        textView1 = (TextView) view.findViewById(R.id.nr1);
        textView2 = (TextView) view.findViewById(R.id.nr2);
        textView3 = (TextView) view.findViewById(R.id.nr3);
        textView4 = (TextView) view.findViewById(R.id.nr4);
        textView5 = (TextView) view.findViewById(R.id.nr5);

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


        chatBtn = (ImageButton) view.findViewById(R.id.ibtnchat);
        chatBtn.setOnClickListener((View.OnClickListener) getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void updateAvailableTorrents(ArrayList<String> torrent) {
        Log.i(TAG, "updateAvailableTorrents: " + torrent.size());
        RecyclerView rc = (RecyclerView) view.findViewById(R.id.rec_new_torrents);

        JustAddedAdapter justAddedAdapter = new JustAddedAdapter(torrent, getContext(), rc);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(justAddedAdapter);
        rc.setVisibility(View.VISIBLE);
    }

    public void setMessageIcon() {
        newMessageIcon.setVisibility(View.VISIBLE);
        newMessageIcon.setText("1");

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity().getIntent().getExtras() != null) {
            if (getActivity().getIntent().getExtras().getStringArrayList(NottifiactionService.NEW_FILMS) != null) {
                updateAvailableTorrents(getActivity().getIntent().getExtras().getStringArrayList(NottifiactionService.NEW_FILMS));
            }
        }
    }
}