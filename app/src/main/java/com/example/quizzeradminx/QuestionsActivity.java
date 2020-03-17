package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar toolbarx;
    RecyclerView recyclerView;
    Button addBtn,excelbtn;
    private QuestionAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        recyclerView=findViewById(R.id.questionsRecyclerViewId);
        addBtn = findViewById(R.id.addBtnId);
        excelbtn = findViewById(R.id.excelBtnId);
        toolbarx = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbarx);
        String categoryname=getIntent().getStringExtra("category");
        int setno=getIntent().getIntExtra("setNo",1);
        getSupportActionBar().setTitle(categoryname+"/set :"+(setno-1));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<QuestionModel>list=new ArrayList<>();
        list.add(new QuestionModel("sjbsus","what is capital of bangladesh","a","b","c","d","dhaka",setno));
        list.add(new QuestionModel("sjbsus","what is capital of bangladesh","a","b","c","d","dhaka",setno));
        list.add(new QuestionModel("sjbsus","what is capital of bangladesh","a","b","c","d","dhaka",setno));
        list.add(new QuestionModel("sjbsus","what is capital of bangladesh","a","b","c","d","dhaka",setno));
        list.add(new QuestionModel("sjbsus","what is capital of bangladesh","a","b","c","d","dhaka",setno));
        adapter=new QuestionAdapter(list);
        recyclerView.setAdapter(adapter);

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
