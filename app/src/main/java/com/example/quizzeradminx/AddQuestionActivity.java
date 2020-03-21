package com.example.quizzeradminx;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddQuestionActivity extends AppCompatActivity {

    EditText question;
    RadioGroup options;
    LinearLayout answers;
    Button uploadBtn;

    Dialog loadingDialog;

    private String categoryName;
    int setNo;
    int position;
    String id;

    private QuestionModel questionModel;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_button));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        question=findViewById(R.id.questionId);
        options=findViewById(R.id.optionsRadioid);
        answers=findViewById(R.id.answerEditTextId);
        uploadBtn=findViewById(R.id.uploadBtnId);


        categoryName=getIntent().getStringExtra("categoryName");
        setNo=getIntent().getIntExtra("setNo",-1);
        position=getIntent().getIntExtra("position",-1);
        if (setNo==-1)
        {
            finish();
            return;
        }

        if (position!=-1)
        {
            questionModel=QuestionsActivity.list.get(position);
            setData();
        }

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"please enter Question",Toast.LENGTH_LONG).show();
                    question.setError("required");
                    return;

                }
                for (int i = 0; i < answers.getChildCount(); i++) {
                    EditText answer= (EditText) answers.getChildAt(i);
                    if (answer.getText().toString().isEmpty())
                    {
                        answer.setError("Required");
                        return;
                    }
                }

                upload();

            }
        });
    }

    private void upload() {
        int correct=-1;
        for (int i = 0; i < options.getChildCount(); i++)
        {
            RadioButton radioButton=(RadioButton)options.getChildAt(i);
            if (radioButton.isChecked())
            {
                correct=i;
                break;
            }
        }
        if (correct==-1)
        {
            Toast.makeText(getApplicationContext(),"please mark correct option",Toast.LENGTH_LONG).show();
            return;
        }
        final HashMap<String ,Object>map=new HashMap<>();
        map.put("correctAns",((EditText)answers.getChildAt(correct)).getText().toString());
        map.put("optionA",((EditText)answers.getChildAt(0)).getText().toString());
        map.put("optionB",((EditText)answers.getChildAt(1)).getText().toString());
        map.put("optionC",((EditText)answers.getChildAt(2)).getText().toString());
        map.put("optionD",((EditText)answers.getChildAt(3)).getText().toString());
        map.put("question",question.getText().toString());
        map.put("setNo",setNo);

        if (position!=-1)
        {
            id=questionModel.getId();
        }else
        {
            id=UUID.randomUUID().toString();
        }


        loadingDialog.show();
        FirebaseDatabase.getInstance().getReference()
                .child("SETES").child(categoryName)
                .child("questions").child(id)
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    QuestionModel questionModel=new QuestionModel(id,map.get("question").toString(),
                            map.get("optionA").toString(),
                            map.get("optionB").toString(),
                            map.get("optionC").toString(),
                            map.get("optionD").toString(),
                            map.get("correctAns").toString(),
                            setNo);

                    if (position!=-1)
                    {
                        QuestionsActivity.list.set(position,questionModel);
                    }else {
                        QuestionsActivity.list.add(questionModel);
                    }
                    finish();
                }else
                {
                    Toast.makeText(getApplicationContext(),"something is wrong",Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
                loadingDialog.dismiss();
            }
        });

    }


    //when edit the question auto maticaly set the data on editText filed
    private void setData()
    {
        question.setText(questionModel.getQuestion());
        ((EditText)answers.getChildAt(0)).setText(questionModel.getA());
        ((EditText)answers.getChildAt(1)).setText(questionModel.getB());
        ((EditText)answers.getChildAt(2)).setText(questionModel.getC());
        ((EditText)answers.getChildAt(3)).setText(questionModel.getD());

        for (int i=0;i<answers.getChildCount();i++)
        {
            if (((EditText)answers.getChildAt(i)).getText().equals(questionModel.getAnswer()))
            {
                RadioButton radioButton= (RadioButton) options.getChildAt(i);
                radioButton.setChecked(true);
                break;
            }
        }

    }
}
