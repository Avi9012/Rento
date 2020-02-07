package com.cell47.rento;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;

    private List<Commenthelp> list;

    public CommentAdapter(Context context, List<Commenthelp> list)
    {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.comments, viewGroup, false);

        //Toast.makeText(context, "In onCreateViewHolder class", Toast.LENGTH_SHORT).show();

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Commenthelp commenthelp = list.get(i);
        viewHolder.name.setText(commenthelp.getUserName());
        viewHolder.date.setText(commenthelp.getDate());
        viewHolder.time.setText(commenthelp.getTime());
        viewHolder.comment.setText(commenthelp.getCom());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, date, time, comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_name);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            comment = itemView.findViewById(R.id.txtcomment);
        }
    }
}
