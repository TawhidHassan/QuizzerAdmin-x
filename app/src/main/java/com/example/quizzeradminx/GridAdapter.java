package com.example.quizzeradminx;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GridAdapter extends BaseAdapter {

    private int setes=0;
    private String category;


    public GridAdapter(int setes, String category) {
        this.setes = setes;
        this.category=category;
    }

    @Override
    public int getCount() {
        return setes;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view;
        if (convertView==null)
        {
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item,parent,false);
        }else
        {
            view=convertView;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Intent qusIntent=new Intent(parent.getContext(),MainActivity.class);
//                qusIntent.putExtra("category",category);
//                qusIntent.putExtra("setNo",position+1);
//
//                parent.getContext().startActivity(qusIntent);
            }
        });
        ((TextView)view.findViewById(R.id.setTextId)).setText(String.valueOf(position+1));
        return view;
    }
}
