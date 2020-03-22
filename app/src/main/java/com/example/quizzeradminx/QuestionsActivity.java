package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        toolbarx = findViewById(R.id.toolbarId);
        recyclerView=findViewById(R.id.questionsRecyclerViewId);
        addBtn = findViewById(R.id.addBtnId);
        excelbtn = findViewById(R.id.excelBtnId);


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
        adapter=new QuestionAdapter(list, categoryname, new QuestionAdapter.DeleteListenerQs() {
            @Override
            public void onLongClick(final int position, final String id) {
                new AlertDialog.Builder(QuestionsActivity.this,R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Delete Question")
                        .setMessage("Are you sure to delete this question")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                myRef.child("SETES").child(categoryname).child("questions").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            list.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            Toast.makeText(QuestionsActivity.this,"question is delete",Toast.LENGTH_LONG).show();
                                        }else
                                        {
                                            Toast.makeText(QuestionsActivity.this,"faild to delete",Toast.LENGTH_LONG).show();
                                            loadingDialog.dismiss();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
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
        excelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(QuestionsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectFile();
                }else {
                    ActivityCompat.requestPermissions(QuestionsActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==101)
        {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                selectFile();
            }else
            {
                Toast.makeText(getApplicationContext(),"please grant permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectFile()
    {

        String[] mimeTypes =
                {"image/*","application/pdf","application/msword","application/vnd.ms-powerpoint","application/vnd.ms-excel","text/plain"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"select file"),102);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCode  && resultCode == RESULT_OK && null != data)
            {
                String FileName = data.getData().getLastPathSegment();
                String FilePath = data.getData().getPath();
                if(FilePath.endsWith(".xlsx"))
                {
                    Toast.makeText(getApplicationContext(),"file was selected",Toast.LENGTH_LONG).show();
                }else
                {
                    Toast.makeText(getApplicationContext(),"file is not match",Toast.LENGTH_LONG).show();

                }

            }
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
