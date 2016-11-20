package com.basicelixir.pawel.torrentnotifier;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import layout.HomeTab;

/**
 * Created by Pawel on 15/06/2016.
 */

public class JustAddedAdapter extends RecyclerView.Adapter<JustAddedAdapter.MyViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    RealmResults<Movie> results;
    String TAG = "pawell";

    public JustAddedAdapter(RealmResults results, Context context) {
        update(results);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_just_added, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(results.get(position).getTorrentFullName());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void update(RealmResults<Movie> results) {

        this.results = results;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageButton imageButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.just_added_textView);
            imageButton = (ImageButton) itemView.findViewById(R.id.delete_button);
            imageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "iiiiiiiiiiiiiiiii" + this.getAdapterPosition());
            final int position = this.getAdapterPosition();
            if (getAdapterPosition() > -1) {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Log.i(TAG, "execute: " + getAdapterPosition());
                        RealmResults<Movie> res = realm.where(Movie.class)
                                .notEqualTo("torrentFullName", "null")
                                .equalTo("availableForDownload", true)
                                .equalTo("activated", true)
                                .findAll();
                        Movie movie = res.get(getAdapterPosition());
                        movie.setActivated(false);

                        realm.copyToRealm(movie);

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        results = realm.where(Movie.class)
                                .notEqualTo("torrentFullName", "null")
                                .equalTo("availableForDownload", true)
                                .equalTo("activated", true)
                                .findAll();
                        update(results);
                        if (results.size() == 0) {
                            FragmentActivity a = (FragmentActivity) context;
                            HomeTab homeTab = (HomeTab) a.getSupportFragmentManager().getFragments().get(0);
                            homeTab.setTitleAddedToGone();
                        }
                    }
                });


            }
        }

    }
}
