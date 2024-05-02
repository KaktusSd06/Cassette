package com.example.cassette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.adapters.MyAdapterSearch;
import com.example.cassette.adapters.SavedListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                if(currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, main.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(MainActivity.this, LogIn.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);
    }
}