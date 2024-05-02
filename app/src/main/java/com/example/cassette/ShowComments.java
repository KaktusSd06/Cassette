package com.example.cassette;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cassette.MyClass.Movie;
import com.example.cassette.adapters.InfoFilm;
import com.example.cassette.adapters.MyAdapterCommentsListView;
import com.example.cassette.databinding.FragmentCreateTagBinding;
import com.example.cassette.databinding.FragmentShowCommentsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowComments extends DialogFragment {

    String[] censor = {"херня", "хуйня", "піздєц", "блять",  "сука", "нахуй", "кончений", "йобнутий", "піздатий", "ахуєнний", "нев'єбєнний"};
    FragmentShowCommentsBinding binding;
    DatabaseReference commentDataBase;

    public ShowComments() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Movie movie = InfoFilm.getInstance().getMovies();
        commentDataBase = FirebaseDatabase.getInstance().getReference("films").child(movie.getFirebaseKey());

        loadComments();

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void loadComments(){
        ArrayList<String> comments = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        try {
            commentDataBase = commentDataBase.child("comments");
            commentDataBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            try {
                                String userEmail = data.child("email").getValue().toString();
                                String comment = data.child("comment").getValue().toString();

                                comments.add(censorComment(comment));
                                users.add(userEmail);
                            }
                            catch (Exception exception){
                                binding.textView24.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    if (users.isEmpty()) {
                        binding.textView24.setVisibility(View.VISIBLE);
                    } else {
                        try {
                            binding.textView24.setVisibility(View.INVISIBLE);
                            MyAdapterCommentsListView adapter = new MyAdapterCommentsListView(getContext(), comments, users);
                            binding.listViewComments.setAdapter(adapter);
                        }catch (Exception ex){}
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception ex){
            binding.textView24.setVisibility(View.VISIBLE);
        }
    }

    private String censorComment(String comment) {
        for (String word : censor) {
            comment = comment.replaceAll("\\b" + word + "\\b", "***");
        }
        return comment;
    }
}