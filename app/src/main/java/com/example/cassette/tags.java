package com.example.cassette;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassette.MyClass.Collection;
import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.adapters.MyAdapterAddToTagsListView;
import com.example.cassette.adapters.MyAdapterTagsListView;
import com.example.cassette.databinding.FragmentFilmBinding;
import com.example.cassette.databinding.FragmentTagsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class tags extends Fragment {
    FragmentTagsBinding binding;
    DatabaseReference database;
    ArrayList<Collection> tags;
    public Fragment fr;

    public tags() {
        fr = this;
    }


    public interface LoadTagsFrameCallback {
        void onTagsFrameLoaded(ArrayList<Collection> tags);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTagsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void hideFabButton() {
        binding.fabAddTag.animate().translationY(binding.fabAddTag.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showFabButton() {
        binding.fabAddTag.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        hideLoadingAnimation();

        tags = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tags");


        binding.fabAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTag createNewTag = new createTag(fr);
                createNewTag.show(getParentFragmentManager(), "dialog_tag");
            }
        });

        loadMyTags(new LoadTagsFrameCallback() {
            @Override
            public void onTagsFrameLoaded(ArrayList<Collection> tagsBd) {
                tags = tagsBd;
            }
        });

        binding.listViewTags.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(totalItemCount > visibleItemCount) {
                    if (view.getId() == binding.listViewTags.getId()) {
                        if (firstVisibleItem + visibleItemCount < totalItemCount) {
                            showFabButton();
                        } else if (firstVisibleItem + visibleItemCount == totalItemCount) {
                            hideFabButton();
                        }
                    }
                }
            }
        });
    }

    private void hideLoadingAnimation() {
        binding.progressBarTags.setVisibility(View.GONE);
    }

    private void showLoadingAnimation() {
        binding.progressBarTags.setVisibility(View.VISIBLE);
    }

    public void loadMyTags(tags.LoadTagsFrameCallback callback) {
            showLoadingAnimation();

            ArrayList<Collection> tagsFromBD = new ArrayList<>();

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                            } catch (Exception ex) {}
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
                            binding.textView2.setVisibility(View.VISIBLE);
                        } else {
                            binding.textView2.setVisibility(View.INVISIBLE);
                            MyAdapterTagsListView adapter = new MyAdapterTagsListView(getContext(), tagsFromBD, binding, fr);
                            binding.listViewTags.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
}