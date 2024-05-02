package com.example.cassette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassette.MyClass.Collection;
import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.adapters.InfoFilm;
import com.example.cassette.adapters.MyAdapterAddToTagsListView;
import com.example.cassette.adapters.MyAdapterSavedFilmListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AddToTags extends AppCompatActivity {

    DatabaseReference database;
    ArrayList<Collection> tags;

    public interface LoadTagsCallback {
        void onTagsLoaded(ArrayList<Collection> tags);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_tags);

        tags = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tags");


        Movie movie = InfoFilm.getInstance().getMovies();

        ((ImageButton) findViewById(R.id.closeAddToTags)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((ImageButton) findViewById(R.id.createNewTag)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createTag createNewTag = new createTag(null);
                    createNewTag.show(getSupportFragmentManager(), "dialog_tag");
                }
                catch (Exception exception){}
            }
        });



        loadMyTags(new LoadTagsCallback() {
            @Override
            public void onTagsLoaded(ArrayList<Collection> tagsBD) {
                tags = tagsBD;

            }
        });
    }

    private void hideLoadingAnimation() {
        findViewById(R.id.progressBar2).setVisibility(View.GONE);
    }

    private void showLoadingAnimation() {
        findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
    }

    private void loadMyTags(AddToTags.LoadTagsCallback callback) {
        showLoadingAnimation();

        ArrayList<Collection> tagsFromBD = new ArrayList<>();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        tagsFromBD.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            try {
                                if (!Objects.equals(dataSnapshot.getKey(), "TEST")) {
                                    String tagKey = dataSnapshot.getKey();
                                    String tagTitle = (String) dataSnapshot.child("title").getValue();

                                    ArrayList<SavedMovie> savedMoviesInTag = new ArrayList<>();

                                    for (DataSnapshot data : dataSnapshot.child("movies").getChildren()) {
                                        String keySaved = data.getKey();
                                        if (!Objects.equals(data.child(keySaved).child("TEST").getValue(), "TEST")) {
                                            double imdb = data.child("imdb").getValue() != null ? (double) data.child("imdb").getValue() : 0;
                                            String about = data.child("about").getValue() != null ? data.child("about").getValue().toString() : null;
                                            String country = data.child("country").getValue() != null ? data.child("country").getValue().toString() : null;
                                            String director = data.child("director").getValue() != null ? data.child("director").getValue().toString() : null;
                                            String filmCompany = data.child("filmCompany").getValue() != null ? data.child("filmCompany").getValue().toString() : null;
                                            String imgSrc = data.child("imgSrc").getValue() != null ? data.child("imgSrc").getValue().toString() : null;
                                            String title = data.child("title").getValue() != null ? data.child("title").getValue().toString() : null;
                                            String type = data.child("type").getValue() != null ? data.child("type").getValue().toString() : null;
                                            String cast = data.child("cast").getValue() != null ? data.child("cast").getValue().toString() : null;
                                            String genres = data.child("genres").getValue() != null ? data.child("genres").getValue().toString() : null;
                                            //String bdKey = data.child("movieKey").getValue().toString();
                                            int duration = data.child("duration").getValue() != null ? ((Long) data.child("duration").getValue()).intValue() : 0;
                                            int ageRating = data.child("ageRating").getValue() != null ? ((Long) data.child("ageRating").getValue()).intValue() : 0;
                                            int year = data.child("year").getValue() != null ? ((Long) data.child("year").getValue()).intValue() : 0;

                                            Boolean isWatched = data.child("isWatched").getValue(Boolean.class);
                                            if (isWatched == null) {
                                                isWatched = false;
                                            }

                                            Double rating = data.child("rating").getValue(Double.class);
                                            if (rating == null) {
                                                rating = 0.0;
                                            }

                                            try {
                                                Movie movie = new Movie(about, ageRating, cast, country, director, duration, filmCompany, genres, imdb, imgSrc, title, type, year);
                                                SavedMovie newSaved = new SavedMovie(movie, isWatched, rating, keySaved);
                                                savedMoviesInTag.add(newSaved);
                                            } catch (Exception ex) {
                                                continue;
                                            }
                                        }
                                    }

                                    Collection newTag = new Collection(tagTitle, savedMoviesInTag, tagKey);
                                    tagsFromBD.add(newTag);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        hideLoadingAnimation();

                        if (tagsFromBD.size() < 1) {
                            ((TextView) findViewById(R.id.textView12)).setVisibility(View.VISIBLE);
                        } else {
                            MyAdapterAddToTagsListView adapter = new MyAdapterAddToTagsListView(getApplicationContext(), tagsFromBD);
                            ((ListView) findViewById(R.id.listView_addToTags)).setAdapter(adapter);

                        }
                    }
                }
                catch (Exception exception){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Помилка підключення", Toast.LENGTH_SHORT).show();
                hideLoadingAnimation();
            }
        });
    }

}