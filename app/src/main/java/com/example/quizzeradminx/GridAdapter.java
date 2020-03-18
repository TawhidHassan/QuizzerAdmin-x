package com.example.quizzeradminx;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GridAdapter extends BaseAdapter {

    public int setes=0;
    private String category;
    private GrideListener listener;


    public GridAdapter(int setes, String category,GrideListener listener) {
        this.setes = setes;
        this.category=category;
        this.listener=listener;
    }



    @Override
    public int getCount() {
        return setes+1;
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
        if (position==0)
        {
            ((TextView)view.findViewById(R.id.setTextId)).setText("+");
        }else
        {
            ((TextView)view.findViewById(R.id.setTextId)).setText(String.valueOf(position));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position==0)
                {
                    //add set code
                    listener.addSet();
                }else {
                Intent qusIntent=new Intent(parent.getContext(),QuestionsActivity.class);
                qusIntent.putExtra("category",category);
                qusIntent.putExtra("setNo",position);
                parent.getContext().startActivity(qusIntent);
                }
            }
        });
        return view;
    }

    public interface GrideListener{
        public void addSet();
    }
}
