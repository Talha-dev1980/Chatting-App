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

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail, edtPass;
    private Button btnLogin;
    private TextView tvSignIn;
    private TextView txtForgetPass;
    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        edtEmail = (EditText) findViewById( R.id.edt_UserEmailLoginA );
        edtPass = (EditText) findViewById( R.id.edt_PasswrodLoginA );
        btnLogin = (Button) findViewById( R.id.btnLoginA );
        tvSignIn = (TextView) findViewById( R.id.tvSigninLoginA );
        txtForgetPass = (TextView) findViewById( R.id.tvSigninLoginA );
        txtForgetPass = (TextView) findViewById( R.id.tvForgetPasswordA );
        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference().child( "UsersData" );

        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser( edtEmail.getText().toString().trim(), edtPass.getText().toString().trim() );
            }
        } );
        tvSignIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( MainActivity.this, SignIn.class ) );
            }
        } );
        txtForgetPass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail( edtEmail.getText().toString().trim() )
                        .addOnCompleteListener( new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText( MainActivity.this, "Email Sent!", Toast.LENGTH_LONG ).show();
                                } else {
                                    Toast.makeText( MainActivity.this, "Email not Sent!", Toast.LENGTH_LONG ).show();
                                }
                            }
                        } );
            }
        } );
    }

    private void loginUser(String email, String pass) {
        mAuth.signInWithEmailAndPassword( email, pass )
                .addOnCompleteListener( this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                            startActivity( new Intent( MainActivity.this, Dashboard.class ) );
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText( MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT ).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                } );
    }
}