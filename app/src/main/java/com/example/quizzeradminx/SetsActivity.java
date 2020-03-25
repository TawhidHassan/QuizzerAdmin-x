package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SetsActivity extends AppCompatActivity {

    Toolbar toolbarx;
    GridView gridView;
    Dialog loadingDialog;
    GridAdapter adapter;
    String categoryName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);


        gridView = findViewById(R.id.setGrideViewId);
        categoryName=getIntent().getStringExtra("setTitle");
        toolbarx = findViewById(R.id.setToolbarId);
        setSupportActionBar(toolbarx);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoryName);


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
                database.getReference().child("categories").child(getIntent().getStringExtra("key")).child("sets").setValue(getIntent().getIntExtra("sets", 0)+1)
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

             @Override
             public void onLongClick(final int setNo) {
                 new AlertDialog.Builder(SetsActivity.this,R.style.Theme_AppCompat_Light_Dialog)
                         .setTitle("Delete Question")
                         .setMessage("Are you sure to delete this Set")
                         .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 loadingDialog.show();
                                 FirebaseDatabase.getInstance().getReference()
                                         .child("SETES").child(categoryName).child("questions").orderByChild("setNo").equalTo(setNo)
                                         .addListenerForSingleValueEvent(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                 for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                                 {
                                                     String id=dataSnapshot1.getKey();
                                                     FirebaseDatabase.getInstance().getReference()
                                                             .child("SETES").child(categoryName).child("questions").child(id).removeValue();

                                                 }
                                                 adapter.setes--;
                                                 loadingDialog.dismiss();
                                                 adapter.notifyDataSetChanged();
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {
                                                 loadingDialog.dismiss();
                                                 Toast.makeText(getApplicationContext(),"something worng",Toast.LENGTH_LONG).show();

                                             }
                                         });
                             }
                         })
                         .setNegativeButton("No",null)
                         .setIcon(android.R.drawable.ic_dialog_alert)
                         .show();
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

