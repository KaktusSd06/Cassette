package com.example.cassette;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cassette.MyClass.Collection;
import com.example.cassette.databinding.FragmentCreateTagBinding;
import com.example.cassette.databinding.FragmentFilmBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class createTag extends DialogFragment {

    FragmentCreateTagBinding binding;
    Fragment fr;

    public createTag( Fragment fr) {
        this.fr = fr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateTagBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.closeCreateTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.createTagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = String.valueOf(binding.tagTitleCreate.getText());
                if(!title.isEmpty()){
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();

                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(currentUserId)
                            .child("tags").push();

                    currentUserRef.child("title").setValue(title);
                    DatabaseReference saved =  currentUserRef.child("movies").push();

                    saved.child("TEST").setValue("TEST");

                    Toast.makeText(getContext(), "Тег успішно створено", Toast.LENGTH_SHORT).show();
                    if(fr!=null) {
                        ((tags) fr).loadMyTags(new tags.LoadTagsFrameCallback() {
                            @Override
                            public void onTagsFrameLoaded(ArrayList<Collection> tags) {

                            }
                        });
                    }
                }
                dismiss();
            }
        });
    }
}