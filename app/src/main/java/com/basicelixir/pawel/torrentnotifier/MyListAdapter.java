package com.basicelixir.pawel.torrentnotifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.RealmResults;

/**
 * Created by Pawel on 25/05/2016.
 */
public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyHolder> {
    static String TAG = "pawell";

    RealmResults<Movie> results;
    LayoutInflater layoutInflater;
     ArrayList<Integer> itemToErase;
    Button deleteBTn;
    Context context;
    HashMap <Integer, Integer> rt = new HashMap<>();


    public MyListAdapter(RealmResults<Movie> results, Context context,Button button) {
        deleteBTn = button;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        itemToErase = new ArrayList<>();
        this.results = results;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_layout, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
if(results.get(position).isActivated()==false){
    holder.textView.setTextColor(Color.GREEN);
}
        holder.textView.setText(results.get(position).getTitle());
        holder.checkBox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return results.size();

    }

    public void update(RealmResults newResults) {
        results = newResults;
        itemToErase.clear();
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getItemsToDelete() {

        for(Integer t :rt.values()){
            Log.i(TAG, "getItemsToDelete: "+t);
            itemToErase.add(t);
        }
        return itemToErase;
    }


    class MyHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

        TextView textView;
        CheckBox checkBox;
        ImageButton imageButton;

        public MyHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            imageButton =(ImageButton)itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(this);
            checkBox.setOnCheckedChangeListener(this);
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {
                int position = getAdapterPosition();
                rt.put(position,position);
            }else if(isChecked==false){
                rt.remove(getAdapterPosition());
            }

            if(rt.size()>0){
                deleteBTn.setVisibility(View.VISIBLE);
            }else{
                deleteBTn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
           String url = "http://www."+results.get(getAdapterPosition()).getMovieURL();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }
}
