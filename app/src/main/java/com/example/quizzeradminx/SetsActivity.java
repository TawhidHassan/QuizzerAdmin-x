package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;


public class SetsActivity extends AppCompatActivity {

    Toolbar toolbarx;
    GridView gridView;
    Dialog loadingDialog;
    GridAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);


        gridView = findViewById(R.id.setGrideViewId);

        toolbarx = findViewById(R.id.setToolbarId);
        setSupportActionBar(toolbarx);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("setTitle"));


        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_button));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


         adapter=new GridAdapter(getIntent().getIntExtra("sets", 0), getIntent().getStringExtra("setTitle"), new GridAdapter.GrideListener() {
            @Override
            public void addSet() {
                loadingDialog.show();
                FirebaseDatabase database=FirebaseDatabase.getInstance();
                database.getReference().child("Categories").child(getIntent().getStringExtra("key")).child("sets").setValue(getIntent().getIntExtra("sets", 0)+1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    adapter.setes=adapter.setes+1;
                                    adapter.notifyDataSetChanged();
                                }else
                                {
                                    Toast.makeText(SetsActivity.this,"faild action",Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                }

                                loadingDialog.dismiss();
                            }
                        });
            }
        });
        gridView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

