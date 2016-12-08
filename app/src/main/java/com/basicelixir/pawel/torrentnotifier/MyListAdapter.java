package com.basicelixir.pawel.torrentnotifier;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyHolder> {
    static String TAG = "pawell";
    private ArrayList<Movie> movieList;
    private LayoutInflater layoutInflater;
    private Context context;
    private Button deleteBTn;
    private ArrayList<String> itemToErase;

    public MyListAdapter(ArrayList<Movie> movieList, Context context, Button deleteBtn) {

        this.deleteBTn = deleteBtn;
        this.movieList = movieList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        itemToErase = new ArrayList<>();
        if (movieList.size() < 1) {
            deleteBTn.setVisibility(View.GONE);
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_layout, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.textView.setText(movieList.get(position).getTitle());
        holder.checkBox.setChecked(false);
       if(movieList.get(position).isAvailableForDownload()){
           holder.availableTv.setText("AVAILABLE");
           holder.availableTv.setTextColor(context.getResources().getColor(R.color.colorAccent));
       }else{
           holder.availableTv.setText("NOT AVAILABLE");
       }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public ArrayList<String> getItemsToDelete() {

        movieList.clear();
        notifyDataSetChanged();

        return itemToErase;
    }


    public class MyHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        TextView textView, availableTv;
        CheckBox checkBox;
        ImageButton imageButton;

        public MyHolder(View itemView) {
            super(itemView);

            availableTv = (TextView)itemView.findViewById(R.id.availableTv);
            textView = (TextView) itemView.findViewById(R.id.textView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(this);
            checkBox.setOnCheckedChangeListener(this);

        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            int position = getAdapterPosition();

            if (isChecked) {
                itemToErase.add(movieList.get(position).getTitle());
            } else if (isChecked == false) {
                itemToErase.remove(movieList.get(position).getTitle());
            }

            if (itemToErase.size() > 0) {
                deleteBTn.setVisibility(View.VISIBLE);
            } else {
                deleteBTn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {

            String url = "http://www." + movieList.get(getAdapterPosition()).getMovieURL();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);

        }
    }
}
