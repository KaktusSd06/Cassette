package com.example.cassette.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cassette.MainActivity;
import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.R;
import com.example.cassette.infoFilm;
import com.example.cassette.main;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapterSavedFilmListView extends BaseAdapter {
    Context context;
    boolean isStart = true;

    public MyAdapterSavedFilmListView(Context context, ArrayList<SavedMovie> savedMovieArrayList) {
        isStart = true;
        this.context = context;
        this.savedMovieArrayList = savedMovieArrayList;
        lInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    LayoutInflater lInflater;
    ArrayList<SavedMovie> savedMovieArrayList;

    @Override
    public int getCount() {
        return savedMovieArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return savedMovieArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_savedfilm_item, parent, false);
        }
        isStart = true;

        SavedMovie savedFilm = getSavedFilm(position);

        String filmTitleResult = savedFilm.getMovie().getTitle();

        if (filmTitleResult.length() > 20) {
            filmTitleResult = filmTitleResult.substring(0, 18) + "...";
        }

        ((TextView) view.findViewById(R.id.TV_tilte)).setText(filmTitleResult);
        ((TextView) view.findViewById(R.id.TV_IMDb)).setText("IMDb:" + String.valueOf(savedFilm.getMovie().getImdb()));
        ((TextView) view.findViewById(R.id.TV_time_)).setText((savedFilm.getMovie().GetStringDuraction() + " хв"));


        String[] genres = savedFilm.getMovie().getGenres();
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

        ((TextView) view.findViewById(R.id.TV_genres)).setText(genresString);

        String imageUrl = savedFilm.getMovie().getImgSrc();

        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.icon)
                .into(((ImageView) view.findViewById(R.id.imageView5)));

        ((ImageView) view.findViewById(R.id.imageView5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = getSavedFilm(position).getMovie();
                    InfoFilm.getInstance().setMovie(movie);
                    Intent intent = new Intent(main.activity, infoFilm.class);
                    main.activity.startActivity(intent);
            }
        });

        if(savedFilm.isWatched()){
            ((CheckBox) view.findViewById(R.id.CB_isWatched)).setChecked(true);
            isStart=false;
        }
        else{
            ((CheckBox) view.findViewById(R.id.CB_isWatched)).setChecked(false);
            isStart=false;
        }

        ((CheckBox) view.findViewById(R.id.CB_isWatched)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String currentUserId = currentUser.getUid();

                if(isChecked && !isStart){
                        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                                .child("users")
                                .child(currentUserId)
                                .child("savedfilm").child(getSavedFilm(position).getKeyFirebase());

                        currentUserRef.child("isWatched").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "Відмічено як переглянутий", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(context, "Помилка", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                }
                else if (!isChecked && !isStart){
                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(currentUserId)
                            .child("savedfilm").child(getSavedFilm(position).getKeyFirebase());

                    currentUserRef.child("isWatched").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Відмічено як непереглянутий", Toast.LENGTH_SHORT).show();
                            }
                            else{
                               Toast.makeText(context, "Помилка", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }

    SavedMovie getSavedFilm(int position) {
        return ((SavedMovie) getItem(position));
    }
}
