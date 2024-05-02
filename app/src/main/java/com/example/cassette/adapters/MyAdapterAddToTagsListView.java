package com.example.cassette.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cassette.MyClass.Collection;
import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapterAddToTagsListView extends BaseAdapter {
    Context context;
    boolean isStart = true;
    LayoutInflater lInflater;
    ArrayList<Collection> tags;

    public MyAdapterAddToTagsListView(Context context, ArrayList<Collection> savedMovieArrayList) {
        isStart = true;
        this.context = context;
        this.tags = savedMovieArrayList;
        lInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_add_tags_item, parent, false);
        }

        Collection tag = getTag(position);

        ((TextView) view.findViewById(R.id.tags_title)).setText(tag.getTitle());
        ((TextView) view.findViewById(R.id.tags_size)).setText("Кількість фільмів: " + String.valueOf(tag.getSavedMoviesInTag().size()));

        if (tag.getSavedMoviesInTag().size() > 0) {
            String imageUrl = tag.getSavedMoviesInTag().get(0).getMovie().getImgSrc();

            Picasso.get()
                    .load(imageUrl)
                    .error(R.drawable.icon)
                    .into(((ImageView) view.findViewById(R.id.imageView8)));
        } else {
            ((ImageView) view.findViewById(R.id.imageView8)).setImageResource(R.drawable.logo_icon);
        }

        if(isMovieInTag(InfoSavedFilm.getInstance().getMovies(), position)){
            ((CheckBox) view.findViewById(R.id.checkBox_add_to_tags)).setChecked(true);
            isStart = false;
        }
        else{
            ((CheckBox) view.findViewById(R.id.checkBox_add_to_tags)).setChecked(false);
            isStart = false;
        }

        ((CheckBox) view.findViewById(R.id.checkBox_add_to_tags)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    SavedMovie saved = InfoSavedFilm.getInstance().getMovies();
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();
                    if (isChecked && !isStart) {
                        tags.get(position).addMovie(InfoSavedFilm.getInstance().getMovies());


                        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                .child("users")
                                .child(currentUserId)
                                .child("tags").child(getTag(position).getKeyFirebase()).child("movies").child(InfoSavedFilm.getInstance().getMovies().getKeyFirebase());

                        currentUserRef.child("about").setValue(saved.getMovie().getAbout() != null ? saved.getMovie().getAbout() : "Немає інформації");
                        currentUserRef.child("imdb").setValue(saved.getMovie().getImdb() != 0 ? saved.getMovie().getImdb() : 0.0);
                        currentUserRef.child("country").setValue(saved.getMovie().getCountry() != null ? saved.getMovie().getCountry() : "Немає інформації");
                        currentUserRef.child("director").setValue(saved.getMovie().getDirector() != null ? saved.getMovie().getDirector() : "Немає інформації");
                        currentUserRef.child("duration").setValue(saved.getMovie().getDuration() != 0 ? saved.getMovie().getDuration() : 0);
                        currentUserRef.child("filmCompany").setValue(saved.getMovie().getFilmCompany() != null ? saved.getMovie().getFilmCompany() : "Немає інформації");
                        currentUserRef.child("imgSrc").setValue(saved.getMovie().getImdb() != 0 ? saved.getMovie().getImgSrc() : "Немає інформації");
                        currentUserRef.child("title").setValue(saved.getMovie().getTitle() != null ? saved.getMovie().getTitle() : "Немає інформації");
                        currentUserRef.child("type").setValue(saved.getMovie().getType() != null ? saved.getMovie().getType() : "Немає інформації");
                        currentUserRef.child("cast").setValue(saved.getMovie().getCast() != null ? saved.getMovie().getCastString() : "Немає інформації");
                        currentUserRef.child("genres").setValue(saved.getMovie().getGenres() != null ? saved.getMovie().getGenresString() : "Немає інформації");
                        currentUserRef.child("ageRating").setValue(saved.getMovie().getAgeRating() != 0 ? saved.getMovie().getAgeRating() : 0);
                        currentUserRef.child("year").setValue(saved.getMovie().getYear() != 0 ? saved.getMovie().getYear() : 0);
                        currentUserRef.child("isWatched").setValue(saved.isWatched());
                        currentUserRef.child("rating").setValue(saved.getRating());
                        currentUserRef.child("movieKey").setValue(saved.getMovie().getFirebaseKey());

                        Toast.makeText(context, "Фільм додано в колекцію", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else if (!isStart) {
                        SavedMovie savedToRemove = InfoSavedFilm.getInstance().getMovies();

                        if (savedToRemove != null) {
                            DatabaseReference currentUserRefToRemove = FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(currentUserId)
                                    .child("tags")
                                    .child(getTag(position).getKeyFirebase())
                                    .child("movies")
                                    .child(savedToRemove.getKeyFirebase());
                            currentUserRefToRemove.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Фільм видалено з тегу", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, "Помилка під час видалення", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
                catch (Exception exception){}

            }
        });

        return view;
    }

    public Boolean isMovieInTag(SavedMovie savedMovie, int position) {
        return isMovieInSaved(savedMovie.getMovie(), tags.get(position).getSavedMoviesInTag());
    }

    public boolean isMovieInSaved(Movie movie, ArrayList<SavedMovie> savedMovies){
        for(SavedMovie savedMovie : savedMovies){
            if(savedMovie.getMovie().getTitle().equals(movie.getTitle()) && savedMovie.getMovie().getYear() == (movie.getYear()) && savedMovie.getMovie().getAbout().equals(movie.getAbout())){
                return true;
            }
        }
        return false;
    }
    Collection getTag(int position) {
        return ((Collection) getItem(position));
    }
}
