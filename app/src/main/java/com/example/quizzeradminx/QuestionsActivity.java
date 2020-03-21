package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar toolbarx;
    RecyclerView recyclerView;
    Button addBtn,excelbtn;
    private QuestionAdapter adapter;
    public static List<QuestionModel>list;

    Dialog loadingDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        recyclerView=findViewById(R.id.questionsRecyclerViewId);
        addBtn = findViewById(R.id.addBtnId);
        excelbtn = findViewById(R.id.excelBtnId);
        toolbarx = findViewById(R.id.toolbarId);

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_button));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        setSupportActionBar(toolbarx);
        final String categoryname=getIntent().getStringExtra("category");
        final int setno=getIntent().getIntExtra("setNo",1);
        getSupportActionBar().setTitle(categoryname+"/set :"+setno);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list=new ArrayList<>();
        adapter=new QuestionAdapter(list,categoryname);
        recyclerView.setAdapter(adapter);
        //get all question from firebase
        getdata(categoryname,setno);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addQuestionIntent=new Intent(getApplicationContext(),AddQuestionActivity.class);
                addQuestionIntent.putExtra("categoryName",categoryname);
                addQuestionIntent.putExtra("setNo",setno);
                startActivity(addQuestionIntent);
            }
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getdata(String categoryname, final int setno)
    {

        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference()
                .child("SETES").child(categoryname).child("questions").orderByChild("setNo").equalTo(setno)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {
                            String id=dataSnapshot1.getKey();
                            String question=dataSnapshot1.child("question").getValue().toString();
                            String a=dataSnapshot1.child("optionA").getValue().toString();
                            String b=dataSnapshot1.child("optionB").getValue().toString();
                            String c=dataSnapshot1.child("optionC").getValue().toString();
                            String d=dataSnapshot1.child("optionD").getValue().toString();
                            String correctAns=dataSnapshot1.child("correctAns").getValue().toString();

                            list.add(new QuestionModel(id,question,a,b,c,d,correctAns,setno));
                        }
                        loadingDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"something worng",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    //when data upload in server refresh the recyclerview
    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }
}
