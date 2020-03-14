package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryActivity extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar toolbarx;
    RecyclerView recyclerView;

    List<CategoryModel>categoryModelList;
    CategoryAdapter adapter;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    Dialog loadingDialog,addcategoryDialog;
    CircleImageView categoryImg;
    EditText categoryName;
    Button add;
    Uri image;
    String dwonloadUrl;

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
        setCategroyDialog();


        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        categoryModelList=new ArrayList<>();

        adapter=new CategoryAdapter(categoryModelList, new CategoryAdapter.DeleteListener() {
            @Override
            public void onDelete(final String key, final int position) {

                new AlertDialog.Builder(CategoryActivity.this,R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure to delete this category")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                myRef.child("categories").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            categoryModelList.remove(position);
                                            adapter.notifyDataSetChanged();
                                        }else
                                        {
                                            Toast.makeText(CategoryActivity.this,"faild to delete",Toast.LENGTH_LONG).show();

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

        loadingDialog.show();
        myRef.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    categoryModelList.add(new CategoryModel(dataSnapshot1.child("name").getValue().toString(),Integer.parseInt(dataSnapshot1.child("sets").getValue().toString()),dataSnapshot1.child("url").getValue().toString(),dataSnapshot1.getKey()));
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
            addcategoryDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setCategroyDialog()
    {
        addcategoryDialog=new Dialog(this);
        addcategoryDialog.setContentView(R.layout.add_category_dialog);
        addcategoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addcategoryDialog.setCancelable(true);
        categoryImg=addcategoryDialog.findViewById(R.id.categoryImgId);
        add=addcategoryDialog.findViewById(R.id.saveBtnId);
        categoryName=addcategoryDialog.findViewById(R.id.categoryNameId);

        categoryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,101);

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryName.getText().toString().isEmpty())
                {
                    categoryName.setError("!Required");
                    return;
                }
                if(image==null)
                {
                    Toast.makeText(getApplicationContext(),"Please select one image for category",Toast.LENGTH_LONG).show();
                    return;
                }
                addcategoryDialog.dismiss();
                //upload data
                uploadData();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101)
        {
            if (resultCode==RESULT_OK)
            {
                 image=data.getData();
                categoryImg.setImageURI(image);
            }
        }
    }

    //upload image
    private void uploadData()
    {
        loadingDialog.show();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        final StorageReference imageReferance=storageReference.child("categories").child(image.getLastPathSegment());

        UploadTask uploadTask = imageReferance.putFile(image);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReferance.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            dwonloadUrl=task.getResult().toString();
                            uploadCategoryName();
                        }else
                        {
                            loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Some thing is wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    // Handle failures
                    Toast.makeText(getApplicationContext(),"Some thing is wrong",Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
            }
        });

    }

    private void uploadCategoryName()
    {
        Map<String ,Object> map=new HashMap<>();
        map.put("name",categoryName.getText().toString());
        map.put("sets",0);
        map.put("url",dwonloadUrl);

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database.getReference().child("categories").child("category"+(categoryModelList.size()+ 1)).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    categoryModelList.add(new CategoryModel(categoryName.getText().toString(),0,dwonloadUrl,"category"+(categoryModelList.size()+ 1)));
                    adapter.notifyDataSetChanged();


                }else{
                    Toast.makeText(getApplicationContext(),"Some thing is wrong",Toast.LENGTH_LONG).show();
                }
                loadingDialog.dismiss();
            }
        });
    }


}
