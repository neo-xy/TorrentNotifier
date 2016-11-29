package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.basicelixir.pawel.torrentnotifier.Movie;
import com.basicelixir.pawel.torrentnotifier.MyListAdapter;
import com.basicelixir.pawel.torrentnotifier.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyListTab extends Fragment implements View.OnClickListener {
    String TAG = "pawell";

    private Button deleteBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private String currentUser;
    private Movie movie;
    private ArrayList<Movie> movieList;
    private RecyclerView movieListRecyclerView;
    private MyListAdapter myListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_list_tab, container, false);
        movieListRecyclerView = (RecyclerView) view.findViewById(R.id.my_rec);

        movieList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUser = firebaseAuth.getCurrentUser().getUid();
            getMovieList();
        }

        deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(this);
        deleteBtn.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

        return view;
    }

    private void getMovieList() {
        movieList.removeAll(movieList);
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseDatabase.getReference().child("users").child(currentUser).child("movieList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    movieList.clear();

                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        movie = new Movie();
                        movie.setTitle(d.getKey().toString());
                        for (DataSnapshot d2 : d.getChildren()) {
                            if (d2.getKey().equals("url")) {
                                movie.setMovieURL(d2.getValue().toString());
                            } else if (d2.getKey().equals("available")) {
                                movie.setAvailableForDownload(d2.getValue(Boolean.class));
                            }
                        }
                        movieList.add(movie);
                    }
                    myListAdapter = new MyListAdapter(movieList, getContext(), deleteBtn);
                    movieListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    movieListRecyclerView.setAdapter(myListAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void add() {
        myListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        //TODO si new ArrayLIst needed?
        ArrayList<String> itemToDelete = myListAdapter.getItemsToDelete();
        if (itemToDelete.size() > 0) {
            deleteFromList(itemToDelete);
        }
    }

    private void deleteFromList(ArrayList<String> itemToDelete) {

        for (String title : itemToDelete) {
            firebaseDatabase.getReference().child("users").child(currentUser).child("movieList").child(title).removeValue();
        }
    }

}
