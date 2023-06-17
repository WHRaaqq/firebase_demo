package com.example.firebasedemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;


public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.CustomViewHolder>{
    private Context context;
    private List<Post> items;
    private String uid;

    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

    private OnItemClickEventListener mItemClickListener;

    public interface OnItemClickEventListener {
        void onItemClick(View a_view, int a_position);
    }

    public ListViewAdapter(List<Post> items, String uid, Context context) {
        this.context = context;
        this.items = items;
        this.uid = uid;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final Post item = items.get(position);
        String str_date = sdf.format(item.date);
        holder.date.setText(str_date);
        holder.content.setText(item.content);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void setOnItemClickListener(OnItemClickEventListener a_listener) {
        mItemClickListener = a_listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView date, content;

        public CustomViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.item_tv_date);
            content = itemView.findViewById(R.id.item_tv_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        Post post = items.get(pos);
                        Intent it = new Intent(context, PostActivity.class);
                        post.addToIntent(it);
                        context.startActivity(it);
                        //((Activity)context).finish();
                        //Toast.makeText(context, "view click :"+pos, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
