package com.basicelixir.pawel.torrentnotifier;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class JustAddedAdapter extends RecyclerView.Adapter<JustAddedAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    String TAG = "pawell";
    private ArrayList<String> titles;
    private RecyclerView rc;

    public JustAddedAdapter(ArrayList<String> titles, Context context, RecyclerView rc) {
        layoutInflater = LayoutInflater.from(context);
        this.titles = titles;
        this.rc = rc;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_just_added, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(titles.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

   private void update() {
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageButton imageButton;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.just_added_textView);
            imageButton = (ImageButton) itemView.findViewById(R.id.delete_button);
            imageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            titles.remove(getAdapterPosition());
            update();
            if (titles.size() < 1) {
                rc.setVisibility(View.GONE);
            }
        }

    }
}
