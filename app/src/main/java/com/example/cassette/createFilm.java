package com.example.cassette;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.adapters.InfoFilm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class createFilm extends AppCompatActivity {
    EditText tpTitle, tpCompany, tpCountry, tpDuration, tpIMDb, tpYear, tpAbout, tpCast, tpGenres;

    private boolean isEditTextFilled(EditText editText) {
        String text = editText.getText().toString().trim();
        return !text.isEmpty();
    }

    public boolean isCorectInfo(){

        return (isEditTextFilled(tpTitle) &&
                isEditTextFilled(tpCompany) &&
                isEditTextFilled(tpCountry) &&
                isEditTextFilled(tpDuration) &&
                isEditTextFilled(tpIMDb) &&
                isEditTextFilled(tpYear) &&
                isEditTextFilled(tpAbout) &&
                isEditTextFilled(tpCast) &&
                isEditTextFilled(tpGenres));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_film);
        tpTitle = findViewById(R.id.createfilmTitle);
        tpCompany = findViewById(R.id.createfilmCompany);
        tpCountry = findViewById(R.id.createfilmCountry);
        tpDuration = findViewById(R.id.createfilmDuration);
        tpIMDb = findViewById(R.id.createfilmImdb);
        tpYear = findViewById(R.id.createfilmYear);
        tpAbout = findViewById(R.id.createfilmAbout);
        tpCast = findViewById(R.id.createfilmCast);
        tpGenres = findViewById(R.id.createfilmGenres);


        ((android.widget.ImageButton) findViewById(R.id.closeCreateFilm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((Button) findViewById(R.id.createFilmAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title, company, country, about, cast, genres, img;
                int year, duration;
                double imdb;

                if(isCorectInfo()){
                    try {
                        title = tpTitle.getText().toString().trim();
                        company = tpCompany.getText().toString().trim();
                        country = tpCountry.getText().toString().trim();
                        about = tpAbout.getText().toString().trim();
                        cast = tpCast.getText().toString().trim();
                        year = Integer.parseInt(tpYear.getText().toString());
                        duration = Integer.parseInt(tpDuration.getText().toString());
                        try{
                            imdb = Integer.parseInt(tpIMDb.getText().toString());
                        }
                        catch (Exception exception){
                            imdb = 0;
                        }

                        genres = tpGenres.getText().toString().trim();


                        Movie movie = new Movie(about, 0, cast, country, "director", duration, company, genres, imdb, "img", title, "фільм", year);
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        String currentUserId = currentUser.getUid();

                        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                .child("users")
                                .child(currentUserId)
                                .child("savedfilm").push();

                        currentUserRef.child("about").setValue(movie.getAbout() != null ? movie.getAbout() : "Немає інформації");
                        currentUserRef.child("imdb").setValue(imdb);
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

                        Toast.makeText(getApplicationContext(), "Фільм збережено", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Помилка додавання", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "Додайте повну інофрмацію про фільм", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}