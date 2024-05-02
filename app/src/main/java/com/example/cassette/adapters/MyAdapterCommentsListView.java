package com.example.cassette.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cassette.R;

import java.util.ArrayList;

public class MyAdapterCommentsListView extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<String> comments;
    ArrayList<String> users;
    int pos = 0;

    public MyAdapterCommentsListView(Context context, ArrayList<String> comments, ArrayList<String> users) {
        this.context = context;
        this.comments = comments;
        this.users = users;
        pos = 0;
        lInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    public Object getUserEmail(int position){
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_comment_item, parent, false);
        }
        Log.v("TAG" , String.valueOf(position));

            ((TextView) view.findViewById(R.id.userEmail)).setText(getUserEmail(position).toString());

            ((TextView) view.findViewById(R.id.comment)).setText(getItem(position).toString());

        return view;
    }
}
