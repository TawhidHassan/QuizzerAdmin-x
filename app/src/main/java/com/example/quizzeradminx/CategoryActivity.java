package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar toolbarx;
    RecyclerView recyclerView;

    List<CategoryModel>categoryModelList;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    Dialog loadingDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        recyclerView=findViewById(R.id.categoryRecyclerViewId);
        toolbarx = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbarx);
        getSupportActionBar().setTitle("Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_button));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        categoryModelList=new ArrayList<>();

        final CategoryAdapter adapter=new CategoryAdapter(categoryModelList);
        recyclerView.setAdapter(adapter);

        loadingDialog.show();
        myRef.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    categoryModelList.add(dataSnapshot1.getValue(CategoryModel.class));
                    //why we use CatehoryModel.class
                    //because .>>>amara category model a direct firebase ar cildren name gyula use korsi tai aikhana just category model call korlai hoba.
                    //ar jodi firebase ar direct name use na kortam tahola amadar alada alada vaba model diya data nita hoto

                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CategoryActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if (item.getItemId() == R.id.add)
        {
            ///diloag show
            Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }


}
