package com.example.signintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent =new Intent(getApplicationContext(), Logout.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText userName = findViewById(R.id.userNameEditText);
        EditText password = findViewById(R.id.passwordEditText);
        Button button = findViewById(R.id.signInButton);
        mAuth=FirebaseAuth.getInstance();
        ProgressBar progressBar =findViewById(R.id.progress_bar);
        TextView textView =findViewById(R.id.regNow);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),Reg.class);
            startActivity(intent);
            finish();
        });
        button.setOnClickListener(v -> {
            String user = userName.getText().toString();
            String pass =password.getText().toString();
            if(user.isEmpty())
            {
                Toast.makeText(MainActivity.this,"Enter User",Toast.LENGTH_SHORT).show();
                return;
            }
            if(pass.isEmpty())
            {
                Toast.makeText(MainActivity.this,"Enter password",Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(user, pass)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Authentication sucssful.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(getApplicationContext(), Logout.class);
                                startActivity(intent);
                                finish();
                            } else {

                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


   });}
}