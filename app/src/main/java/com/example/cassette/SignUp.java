package com.example.cassette;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText eMail, password, passwordRep;
    Button signUpEmail, signUpGoogle;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient client;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        TextView logIn = findViewById(R.id.logIn_SignUp_SignUp);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        signUpEmail = findViewById(R.id.SignUp_SignUp);
        signUpGoogle = findViewById(R.id.google_SignUp);

        eMail = findViewById(R.id.email_SignUp);
        password = findViewById(R.id.password_SignUp);
        passwordRep = findViewById(R.id.password2_SignUp);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseAuth finalFirebaseAuth = firebaseAuth;
        signUpEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = eMail.getText().toString();
                String passwordStr = password.getText().toString();
                String passwordRepStr = passwordRep.getText().toString();

                if (TextUtils.isEmpty(emailStr))
                    eMail.setError("Enter e-mail");
                if (TextUtils.isEmpty(passwordStr))
                    password.setError("Enter password");
                if (TextUtils.isEmpty(passwordRepStr))
                    passwordRep.setError("Repeat the password");
                if (passwordStr.length() < 6)
                    password.setError("Password must be >= 6 character");
                if (passwordStr.compareTo(passwordRepStr) != 0)
                    passwordRep.setError("Not the same");
                if(!TextUtils.isEmpty(emailStr) && !TextUtils.isEmpty(passwordStr) && !TextUtils.isEmpty(passwordRepStr)) {

                    finalFirebaseAuth.createUserWithEmailAndPassword(emailStr, passwordRepStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Користувач успішно створений", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUp.this, main.class);

                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                if (currentUser != null) {
                                    String userId = currentUser.getUid();
                                    DatabaseReference usersRef = database.child("users").child(userId);

                                    usersRef.child("ID").setValue(currentUser.getUid());
                                    usersRef.child("Email").setValue(currentUser.getEmail());
                                    usersRef.child("Password").setValue(passwordRepStr);
                                    usersRef.child("savedfilm").child("TEST").setValue("TEST");
                                    usersRef.child("tags").child("TEST").setValue("TEST");

                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Помилка реєстрації", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Помилка реєстрації", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        client = GoogleSignIn.getClient(this, options);

        signUpGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = client.getSignInIntent();
                startActivityForResult(i, 1234);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Користувач успішно створений", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, main.class);

                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                DatabaseReference usersRef = database.child("users").child(userId);

                                usersRef.child("ID").setValue(currentUser.getUid());
                                usersRef.child("Email").setValue(currentUser.getEmail());
                                usersRef.child("Password").setValue("UseGoogle");
                                usersRef.child("savedfilm").child("TEST").setValue("TEST");
                                usersRef.child("tags").child("TEST").setValue("TEST");

                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Помилка реєстрації", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Помилка реєстрації", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
