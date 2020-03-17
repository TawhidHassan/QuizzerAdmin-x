package com.example.quizzeradminx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<QuestionModel>questionModelList;

    public QuestionAdapter(List<QuestionModel> questionModelList) {
        this.questionModelList = questionModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String questionx=questionModelList.get(position).getQuestion();
        String answerx=questionModelList.get(position).getAnswer();

        holder.setData(questionx,answerx,position);
    }

    @Override
    public int getItemCount() {
        return questionModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView question;
        private TextView answer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question=itemView.findViewById(R.id.questionId);
            answer=itemView.findViewById(R.id.answerId);
        }

        private void setData(String questionX,String answerX,int position)
        {
            this.question.setText((position+1)+" "+questionX);
            this.answer.setText("Ans:"+" "+answerX);
        }
    }
}
