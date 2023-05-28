package com.example.musicmanagerapp;

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

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Reg extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        EditText userName = findViewById(R.id.userReg);
        EditText password = findViewById(R.id.passreg);
        Button button = findViewById(R.id.Regbutton);
        mAuth=FirebaseAuth.getInstance();
        ProgressBar progressBar=findViewById(R.id.progress_bar);
        TextView textView =findViewById(R.id.loginNow);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        });

        button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String userreg = userName.getText().toString();
            String passreg =password.getText().toString();


            if(userreg.isEmpty())
            {
                Toast.makeText(Reg.this,"Enter User",Toast.LENGTH_SHORT).show();
                return;
            }
            if(passreg.isEmpty())
            {
                Toast.makeText(Reg.this,"Enter password",Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(userreg, passreg)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {

                                Toast.makeText(Reg.this, "Authentication created.",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Reg.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        });
    }
}