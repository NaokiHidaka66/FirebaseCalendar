package com.example.chef_.firebasecalendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chef_.firebasecalendar.model.Schedule;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventAdding extends AppCompatActivity {

    private EditText contentText;
    private Button okButton;
    private Button cancelButton;
    private long datemills;
    private DatabaseReference mDatabase;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        contentText = findViewById(R.id.contenttext);
        okButton = findViewById(R.id.okbutton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contentText.getText().length() > 0){
                    addfirebase(datemills, contentText.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("date", datemills);
                    int requestCode = 0;
                    setResult(requestCode, intent);
                    finish();
                } else {
                    showDialog();
                }
            }
        });

        cancelButton = findViewById(R.id.cancelbutton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                int requestCode = 1;
                setResult(requestCode, intent);
                finish();
            }
        });

        Intent intent = getIntent();
        datemills = intent.getLongExtra("date", 0);


    }

    private void addfirebase(long date, String content){
        Schedule schedule = new Schedule(date, content);
        mDatabase.child("schedule").push().setValue(schedule);
    }

    private void showDialog(){
        builder = new AlertDialog.Builder(this);
        builder.setTitle("タイトルを入力してください");
        builder.setPositiveButton("確認",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
