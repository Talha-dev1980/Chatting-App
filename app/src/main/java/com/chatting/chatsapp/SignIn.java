package com.chatting.chatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPass;
    private TextView tvLogin;
    private Button btnSignIn;
    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );

        edtName = (EditText) findViewById( R.id.edt_userNameSI );
        edtEmail = (EditText) findViewById( R.id.edt_UserEmailSI );
        edtPass = (EditText) findViewById( R.id.edt_PasswrodSI );
        tvLogin = (TextView) findViewById( R.id.tvLoginSI );
        btnSignIn = (Button) findViewById( R.id.btnSignSI );

        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference().child( "UsersData" );
        btnSignIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser(edtName.getText().toString().trim(),edtPass.getText().toString().trim(),edtEmail.getText().toString().trim() );
            }
        } );
        tvLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( SignIn.this, MainActivity.class ) );
            }
        } );
    }
    private void signUpUser(String name, String password, String email) {
        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI( user, name, email );
                        } else {
                            Toast.makeText( SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT ).show();

                        }

                    }
                } );
    }
    private void updateUI(FirebaseUser user, String name, String email) {
        if (user != null) {
            Map map = new HashMap();
            map.put( "name", name );
            map.put( "email", email );
            map.put( "status", "Hey there Join Me!" );
            map.put( "img", "gs://fir-connectivity-79bf7.appspot.com/avatorUser.jpg" );
            dbReference.child( user.getUid().toString() ).setValue( map ).addOnCompleteListener( new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        startActivity( new Intent( SignIn.this, Dashboard.class ) );
                        finish();
                    } else {
                        Toast.makeText( SignIn.this, "Error in saving in database.",
                                Toast.LENGTH_SHORT ).show();
                    }
                }
            } );
        } else {
            Toast.makeText( SignIn.this, "Authentication failed.",
                    Toast.LENGTH_SHORT ).show();
        }
    }
}