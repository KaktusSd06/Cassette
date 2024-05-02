package com.example.cassette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.adapters.InfoFilm;
import com.example.cassette.adapters.InfoSavedFilm;
import com.example.cassette.adapters.MyAdapterCommentsListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class infoFilm extends AppCompatActivity {

    DatabaseReference database;
    ArrayList<SavedMovie> savedMovie;
    double userRating;
    public interface LoadSavedFilmsCallback {
        void onSavedFilmsLoaded(ArrayList<SavedMovie> savedMovies);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_film);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        savedMovie = new ArrayList<>();
        Movie movie = InfoFilm.getInstance().getMovies();
        database = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("savedfilm");
        loadSavedFilms(new LoadSavedFilmsCallback() {
            @Override
            public void onSavedFilmsLoaded(ArrayList<SavedMovie> savedMovies) {
                savedMovie = savedMovies;


                setInfo(savedMovie);

                ((Button) findViewById(R.id.showComments)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowComments showComments = new ShowComments();
                        showComments.show(getSupportFragmentManager(), "dialog_tag");
                    }
                });

                ((Button) findViewById(R.id.btnAddComent)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TextView textInputLayout = findViewById(R.id.comment);
                        try {
                            EditText editText = findViewById(R.id.commentText);
                            String com = editText.getText().toString();
                            if(!movie.getFirebaseKey().equals("MyFilm")) {
                                if (!com.isEmpty()) {
                                    Movie movie = InfoFilm.getInstance().getMovies();
                                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    String currentUserId = currentUser.getUid();
                                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                            .child("films")
                                            .child(movie.getFirebaseKey()).child("comments").push();

                                    currentUserRef.child("email").setValue(currentUser.getEmail());
                                    currentUserRef.child("comment").setValue(com);

                                    Toast.makeText(getApplicationContext(), "Коментар опубліковано", Toast.LENGTH_SHORT).show();
                                    editText.clearFocus();
                                    editText.setText("");
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Введіть коментар", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Коментарі для власних фільмів недоступні", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e){

                        }
                    }
                });

                ((CheckBox) findViewById(R.id.infoFilm_isSaved)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Movie movie = InfoFilm.getInstance().getMovies();
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        String currentUserId = currentUser.getUid();
                        if(isChecked && !isMovieInList(movie, savedMovies)){
                            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(currentUserId)
                                    .child("savedfilm").push();

                            currentUserRef.child("about").setValue(movie.getAbout() != null ? movie.getAbout() : "Немає інформації");
                            currentUserRef.child("imdb").setValue(movie.getImdb() != 0 ? movie.getImdb() : 0.0);
                            currentUserRef.child("country").setValue(movie.getCountry() != null ? movie.getCountry() : "Немає інформації");
                            currentUserRef.child("director").setValue(movie.getDirector() != null ? movie.getDirector() : "Немає інформації");
                            currentUserRef.child("duration").setValue(movie.getDuration() != 0 ? movie.getDuration() : 0);
                            currentUserRef.child("filmCompany").setValue(movie.getFilmCompany() != null ? movie.getFilmCompany() : "Немає інформації");
                            currentUserRef.child("imgSrc").setValue(movie.getImdb() != 0 ? movie.getImgSrc() : "Немає інформації");
                            currentUserRef.child("title").setValue(movie.getTitle() != null ? movie.getTitle() : "Немає інформації");
                            currentUserRef.child("type").setValue(movie.getType() != null ? movie.getType() : "Немає інформації");
                            currentUserRef.child("cast").setValue(movie.getCast() != null ? movie.getCastString() : "Немає інформації");
                            currentUserRef.child("genres").setValue(movie.getGenres() != null ? movie.getGenresString() : "Немає інформації");
                            currentUserRef.child("ageRating").setValue(movie.getAgeRating() != 0 ? movie.getAgeRating() : 0);
                            currentUserRef.child("year").setValue(movie.getYear() != 0 ? movie.getYear() : 0);
                            currentUserRef.child("isWatched").setValue(false);
                            currentUserRef.child("rating").setValue(0.0);
                            currentUserRef.child("movieKey").setValue(movie.getFirebaseKey());

                            savedMovies.add(new SavedMovie(movie, false, 0.0, currentUserRef.getKey()));
                            Toast.makeText(getApplicationContext(), "Фільм збережено", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SavedMovie savedToRemove = getSaved(movie, savedMovies);

                            if (savedToRemove != null) {
                                DatabaseReference currentUserRefToRemove = FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(currentUserId)
                                        .child("savedfilm")
                                        .child(savedToRemove.getKeyFirebase());
                                currentUserRefToRemove.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Фільм видалено", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Помилка під час видалення", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setOnCheckedChangeListener(null);
                            ((android.widget.RatingBar) findViewById(R.id.ratingBar)).setOnRatingBarChangeListener(null);
                            ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setChecked(false);
                            ((android.widget.RatingBar) findViewById(R.id.ratingBar)).setRating(0);

                        }
                    }
                });

                ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Movie movie = InfoFilm.getInstance().getMovies();
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        String currentUserId = currentUser.getUid();

                        if(isChecked){
                            if(isMovieInList(movie, savedMovies)){
                                DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(currentUserId)
                                        .child("savedfilm").child(getSaved(movie, savedMovies).getKeyFirebase());

                                currentUserRef.child("isWatched").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Відмічено як переглянутий", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Помилка", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                                getSaved(movie, savedMovies).setWatched(true);
                            }
                            else{
                                ((CheckBox) findViewById(R.id.infoFilm_isSaved)).setChecked(true);
                                ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setChecked(true);
                            }
                        }
                        else{
                            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(currentUserId)
                                    .child("savedfilm").child(getSaved(movie, savedMovies).getKeyFirebase());

                            currentUserRef.child("isWatched").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Відмічено як непереглянутий", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Помилка", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                            getSaved(movie, savedMovies).setWatched(false);
                        }
                    }
                });

                ((android.widget.RatingBar) findViewById(R.id.ratingBar)).setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        Movie movie = InfoFilm.getInstance().getMovies();
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        String currentUserId = currentUser.getUid();

                        if(isMovieInList(movie, savedMovies)){
                            if(!((CheckBox) findViewById(R.id.infoFilm_isWatched)).isChecked()) ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setChecked(true);
                            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(currentUserId)
                                    .child("savedfilm").child(getSaved(movie, savedMovies).getKeyFirebase());

                            currentUserRef.child("rating").setValue(rating);
                            if(userRating == 0){
                                userRating = rating;
                            }
                            else{
                                userRating = (userRating + rating)/2;
                            }
                            currentUserRef.child("userRating").setValue(userRating);
                            String formattedRating = String.format("%.1f", userRating);
                            ((TextView) findViewById(R.id.textView23)).setText(formattedRating);

                            getSaved(movie, savedMovies).setRating(rating);


                        }
                        else{
                            ((CheckBox) findViewById(R.id.infoFilm_isSaved)).setChecked(true);
                            ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setChecked(true);

                            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(currentUserId)
                                    .child("savedfilm").child(getSaved(movie, savedMovies).getKeyFirebase());

                            if(userRating == 0){
                                userRating = rating;
                            }
                            else{
                                userRating = (userRating + rating)/2;
                            }
                            currentUserRef.child("userRating").setValue(userRating);
                            String formattedRating = String.format("%.1f", userRating);
                            ((TextView) findViewById(R.id.textView23)).setText(formattedRating);
                        }
                    }
                });
                return;
            }
        });
        setInfo(savedMovie);

        Button addToTags = findViewById(R.id.infoFilm_tags_btn);
        addToTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMovieInList(InfoFilm.getInstance().getMovies(), savedMovie)) ((CheckBox) findViewById(R.id.infoFilm_isSaved)).setChecked(true);
                InfoSavedFilm.getInstance().setMovie(getSaved(InfoFilm.getInstance().getMovies(), savedMovie));
                Intent intent = new Intent(infoFilm.this, AddToTags.class);
                startActivity(intent);
            }
        });
    }
    private void loadSavedFilms(LoadSavedFilmsCallback callback) {
        ArrayList<SavedMovie> savedFromBd = new ArrayList<>();
        Query saved = database.orderByKey();
        saved.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            String key = dataSnapshot.getKey();
                            double imdb;
                            try{
                                imdb = dataSnapshot.child("imdb").getValue() != null ? (double) dataSnapshot.child("imdb").getValue() : 0;
                            }
                            catch (Exception ex){
                                imdb = 0;
                            }

                            String about = dataSnapshot.child("about").getValue() != null ? dataSnapshot.child("about").getValue().toString() : null;
                            String country = dataSnapshot.child("country").getValue() != null ? dataSnapshot.child("country").getValue().toString() : null;
                            String director = dataSnapshot.child("director").getValue() != null ? dataSnapshot.child("director").getValue().toString() : null;
                            String filmCompany = dataSnapshot.child("filmCompany").getValue() != null ? dataSnapshot.child("filmCompany").getValue().toString() : null;
                            String imgSrc = dataSnapshot.child("imgSrc").getValue() != null ? dataSnapshot.child("imgSrc").getValue().toString() : null;
                            String title = dataSnapshot.child("title").getValue() != null ? dataSnapshot.child("title").getValue().toString() : null;
                            String type = dataSnapshot.child("type").getValue() != null ? dataSnapshot.child("type").getValue().toString() : null;
                            String cast = dataSnapshot.child("cast").getValue() != null ? dataSnapshot.child("cast").getValue().toString() : null;
                            String genres = dataSnapshot.child("genres").getValue() != null ? dataSnapshot.child("genres").getValue().toString() : null;
                            String bdKey = dataSnapshot.getKey();

                            int duration = dataSnapshot.child("duration").getValue() != null ? ((Long) dataSnapshot.child("duration").getValue()).intValue() : 0;
                            int ageRating = dataSnapshot.child("ageRating").getValue() != null ? ((Long) dataSnapshot.child("ageRating").getValue()).intValue() : 0;
                            int year = dataSnapshot.child("year").getValue() != null ? ((Long) dataSnapshot.child("year").getValue()).intValue() : 0;

                            boolean isWatched = dataSnapshot.child("isWatched").getValue(Boolean.class);
                            double rating = dataSnapshot.child("rating").getValue(Double.class);

                            Movie movie = new Movie(about, ageRating, cast, country, director, duration, filmCompany, genres, imdb, imgSrc, title, type, year, bdKey);
                            SavedMovie newSaved = new SavedMovie(movie, isWatched, rating, key);

                            if(Objects.equals(InfoFilm.getInstance().getMovies().getAbout(), movie.getAbout())){
                                try{
                                    userRating = (double) dataSnapshot.child("userRating").getValue();
                                }
                                catch (Exception e){
                                    userRating = 0;
                                }
                            }


                            savedFromBd.add(newSaved);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (callback != null) {
                        callback.onSavedFilmsLoaded(savedFromBd);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Помилка підключення", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setInfo(ArrayList<SavedMovie> savedMovies){
        Movie movie = InfoFilm.getInstance().getMovies();
        if(movie==null) return;
        ((TextView) findViewById(R.id.infoFilm_title)).setText(movie.getTitle());
        ((TextView) findViewById(R.id.infoFilm_imdb)).setText("IMDb " + String.valueOf(movie.getImdb()));
        ((TextView) findViewById(R.id.infoFilm_year)).setText(String.valueOf(movie.getYear()));

        String company = movie.getFilmCompany();
        String comp = company;

        if(company.length() > 30) comp = company.substring(0, 27) + "...";

        ((TextView) findViewById(R.id.infoFilm_company)).setText(comp);
        ((TextView) findViewById(R.id.infoFilm_country)).setText(movie.getCountry());
        ((TextView) findViewById(R.id.infofilm_about)).setText(movie.getAbout());
        ((TextView) findViewById(R.id.infoFilm_casts)).setText(String.join(", ", movie.getCast()));

        String formattedRating = String.format("%.1f", userRating);
        ((TextView) findViewById(R.id.textView23)).setText(formattedRating);

        String[] genres = movie.getGenres();
        StringBuilder genresResult = new StringBuilder();
        int currentLength = 0;

        for (String genre : genres) {
            if (currentLength + genre.length() <= 30) {
                genresResult.append(genre).append(", ");
                currentLength += genre.length() + 2;
            } else {
                genresResult.append("\n").append(genre).append(", ");
                currentLength = genre.length() + 2;
            }
        }

        String genresString = genresResult.toString().trim();

        ((TextView) findViewById(R.id.infoFilm_genres)).setText(genresResult);
        ((TextView) findViewById(R.id.infoFilm_time)).setText(String.valueOf(movie.getDuration()) + " хв");
        String imageUrl = movie.getImgSrc();

        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.icon)
                .into((ImageView) findViewById(R.id.imageView4));

        if(isMovieInList(movie, savedMovies)){
            ((CheckBox) findViewById(R.id.infoFilm_isSaved)).setChecked(true);
            if(getSaved(movie, savedMovies).isWatched()){
                ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setChecked(true);
            }
            else{
                ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setChecked(false);
            }
            ((RatingBar) findViewById(R.id.ratingBar)).setRating((float) getSaved(movie, savedMovies).getRating());

        }
        else{
            ((RatingBar) findViewById(R.id.ratingBar)).setRating(0);
            ((CheckBox) findViewById(R.id.infoFilm_isSaved)).setChecked(false);
            ((CheckBox) findViewById(R.id.infoFilm_isWatched)).setChecked(false);
        }
    }

    public boolean isMovieInList(Movie movie, ArrayList<SavedMovie> savedMovies) {
        for (SavedMovie savedMovie : savedMovies) {
            if (savedMovie.getMovie().getTitle().equals(movie.getTitle()) && savedMovie.getMovie().getYear() == (movie.getYear()) && savedMovie.getMovie().getAbout().equals(movie.getAbout())) {
                return true;
            }
        }
        return false;
    }

    public SavedMovie getSaved(Movie movie, ArrayList<SavedMovie> savedMovies) {
        for (SavedMovie savedMovie : savedMovies) {
            if (savedMovie.getMovie().getTitle().equals(movie.getTitle()) && savedMovie.getMovie().getYear() == (movie.getYear()) && savedMovie.getMovie().getAbout().equals(movie.getAbout())) {
                return savedMovie;
            }
        }
        return null;
    }
}