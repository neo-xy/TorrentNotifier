package layout;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.basicelixir.pawel.torrentnotifier.Movie;
import com.basicelixir.pawel.torrentnotifier.MyListAdapter;
import com.basicelixir.pawel.torrentnotifier.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyListTab extends Fragment implements View.OnClickListener {
    String TAG = "pawell";
    MyListAdapter myListAdapter;
    RealmResults<Movie> results;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_list_tab, container, false);

        Button button = (Button) view.findViewById(R.id.deleteBtn);
        button.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_rec);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Movie> results = realm.where(Movie.class).findAll();

        myListAdapter = new MyListAdapter(results, getContext(), button);
        recyclerView.setAdapter(myListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void add() {
        myListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        //TODO si new ArrayLIst needed?
        ArrayList<Integer> itemToDelete = myListAdapter.getItemsToDelete();
        deleteFromList(itemToDelete);
    }

    private void deleteFromList(final ArrayList<Integer> itemToDelete) {

        Realm realm = Realm.getDefaultInstance();
        results = realm.where(Movie.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < itemToDelete.size(); i++) {
                    //TODO is-1 necesarry?
                    Movie movie = results.get(itemToDelete.get(i));
                    movie.deleteFromRealm();
                }
            }
        });

        RealmResults<Movie> newResults = realm.where(Movie.class).findAll();
        realm.close();
        myListAdapter.update(newResults);
    }
}
