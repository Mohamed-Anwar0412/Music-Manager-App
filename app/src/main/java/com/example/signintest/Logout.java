package com.example.signintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Logout extends AppCompatActivity {
    FirebaseAuth auth;
    Button button ;
    TextView textView;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        auth = FirebaseAuth.getInstance();
        button =findViewById(R.id.Logout);
        textView =findViewById(R.id.textView);
        user=auth.getCurrentUser();
                if(user==null)
                {
                    Intent intent =new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    textView.setText(user.getEmail());
                }
                button.setOnClickListener(v -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent =new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                });
    }
}