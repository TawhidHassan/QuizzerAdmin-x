package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText email,password;
    Button loginButton;
    ProgressBar loginProgressBar;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email=findViewById(R.id.emailId);
        password=findViewById(R.id.passwordId);
        loginButton=findViewById(R.id.loginBtnId);
        loginProgressBar=findViewById(R.id.loginProgressBarId);


        final Intent categoryIntent=new Intent(getApplicationContext(),CategoryActivity.class);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            ///category intent
            startActivity(categoryIntent);
            finish();
            return;
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty())
                {
                    email.setError("required");
                    return;
                }else
                {
                    email.setError(null);
                } if(password.getText().toString().isEmpty())
                {
                    password.setError("required");
                    return;
                }else
                {
                    password.setError(null);
                }
                loginProgressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            ///category intent
                            startActivity(categoryIntent);
                            finish();
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_LONG).show();
                        }
                        loginProgressBar.setVisibility(View.INVISIBLE);
                    }

                });
            }
        });


    }
}
