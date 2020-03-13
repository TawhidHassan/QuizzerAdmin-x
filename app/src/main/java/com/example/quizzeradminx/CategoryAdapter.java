package com.example.quizzeradminx;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {


    private List<CategoryModel>categoryModelList;
    private DeleteListener deleteListener;

    public CategoryAdapter(List<CategoryModel> categoryModelList,DeleteListener deleteListener) {
        this.categoryModelList = categoryModelList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {

        holder.setData(categoryModelList.get(position).getUrl(),categoryModelList.get(position).getName(),categoryModelList.get(position).getSets(),categoryModelList.get(position).getKey(),position);
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView circleImageView;
        TextView title;
        ImageButton deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.categoryImgeviewid);
            title=itemView.findViewById(R.id.titleId);
            deleteBtn=itemView.findViewById(R.id.deleteBtnId);
        }
        private void setData(String url, final String title, final int sets, final String key, final int position)
        {
            Glide.with(itemView.getContext()).load(url).into(circleImageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent=new Intent(itemView.getContext(),SetsActivity.class);
                    setIntent.putExtra("setTitle",title);
                    setIntent.putExtra("sets",sets);
                    itemView.getContext().startActivity(setIntent);
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDelete(key,position);
                }
            });


        }
    }

    public interface DeleteListener{
        public void onDelete(String key,int position);
    }
}
