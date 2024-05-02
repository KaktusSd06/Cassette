package com.example.cassette;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cassette.MyClass.*;
import com.example.cassette.MyClass.Movie;
import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.adapters.MyAdapterSavedFilmListView;
import com.example.cassette.databinding.FragmentFilmBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import android.view.animation.AccelerateInterpolator;

public class film extends Fragment {
    FragmentFilmBinding binding;
    ArrayList<SavedMovie> fullList;
    DatabaseReference database;
    ArrayList<SavedMovie> searchResult;
    ArrayList<SavedMovie> genresResult;
    ArrayList<SavedMovie> sortResult;
    ListView listviewSavedFilm;
    String genresFilter = "Жанр";

    public film() {

    }

    public interface LoadSavedFilmsCallback {
        void savedFilmsLoaded(ArrayList<SavedMovie> savedMovies);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadSaved() {
        showLoadingAnimation();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            String key = dataSnapshot.getKey();

                            String about = dataSnapshot.child("about").getValue() != null ? dataSnapshot.child("about").getValue().toString() : null;
                            String country = dataSnapshot.child("country").getValue() != null ? dataSnapshot.child("country").getValue().toString() : null;
                            String director = dataSnapshot.child("director").getValue() != null ? dataSnapshot.child("director").getValue().toString() : null;
                            String filmCompany = dataSnapshot.child("filmCompany").getValue() != null ? dataSnapshot.child("filmCompany").getValue().toString() : null;
                            String imgSrc = dataSnapshot.child("imgSrc").getValue() != null ? dataSnapshot.child("imgSrc").getValue().toString() : null;
                            String title = dataSnapshot.child("title").getValue() != null ? dataSnapshot.child("title").getValue().toString() : null;
                            String type = dataSnapshot.child("type").getValue() != null ? dataSnapshot.child("type").getValue().toString() : null;
                            String cast = dataSnapshot.child("cast").getValue() != null ? dataSnapshot.child("cast").getValue().toString() : null;
                            String genres = dataSnapshot.child("genres").getValue() != null ? dataSnapshot.child("genres").getValue().toString() : null;
                            String bdKey = "MyFilm";
                            try {
                                 bdKey = dataSnapshot.child("movieKey").getValue().toString();
                            }catch (Exception ex){
                                 bdKey = "MyFilm";
                            }

                            int duration = dataSnapshot.child("duration").getValue() != null ? ((Long) dataSnapshot.child("duration").getValue()).intValue() : 0;
                            int ageRating = dataSnapshot.child("ageRating").getValue() != null ? ((Long) dataSnapshot.child("ageRating").getValue()).intValue() : 0;
                            int year = dataSnapshot.child("year").getValue() != null ? ((Long) dataSnapshot.child("year").getValue()).intValue() : 0;
                            double imdb;
                            try{
                                imdb = dataSnapshot.child("imdb").getValue() != null ? (double) dataSnapshot.child("imdb").getValue() : 0;
                            }
                            catch (Exception ex){
                                imdb = 0;
                            }

                            boolean isWatched = dataSnapshot.child("isWatched").getValue(Boolean.class);
                            double rating = dataSnapshot.child("rating").getValue(Double.class);


                            Movie movie = new Movie(about, ageRating, cast, country, director, duration, filmCompany, genres, imdb, imgSrc, title, type, year, bdKey);
                            SavedMovie newSaved = new SavedMovie(movie, isWatched, rating, key);

                            fullList.add(newSaved);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    hideLoadingAnimation();
                    if(fullList.isEmpty()){
                        binding.textView2.setVisibility(View.VISIBLE);
                    }
                    else{
                        Collections.reverse(fullList);
                        MyAdapterSavedFilmListView adapter = new MyAdapterSavedFilmListView(getContext(), fullList);
                        binding.listViewSavedFilm.setAdapter(adapter);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoadingAnimation();
                Toast.makeText(getContext(), "Помилка підключення", Toast.LENGTH_SHORT).show();
            }


        });

        binding.fabCreateFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), createFilm.class);
                startActivity(intent);
            }
        });
    }

    private void hideLoadingAnimation() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showLoadingAnimation() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dropdownMenu();

        database = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("savedfilm");
        loadSaved();

        searchSettings();

        binding.listViewSavedFilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            }
        });

        binding.listViewSavedFilm.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(totalItemCount > visibleItemCount) {
                    if (view.getId() == binding.listViewSavedFilm.getId()) {
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

    private void hideFabButton() {
        binding.fabCreateFilm.animate().translationY(binding.fabCreateFilm.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showFabButton() {
        binding.fabCreateFilm.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFilmBinding.inflate(inflater, container, false);

        fullList = new ArrayList<>();
        genresResult = new ArrayList<>();
        searchResult = new ArrayList<>();

        return binding.getRoot();
    }

    private void dropdownMenu(){
        String[] sortBy = {"Впорядкувати","Назва", "Тривалість", "Рік", "IMDB"};
        String[] orderBy = {"sort", "title", "duration", "year", "IMDB"};
        ArrayAdapter adapterSortBy = new ArrayAdapter(getContext(), R.layout.select_dropdown, sortBy);
        adapterSortBy.setDropDownViewResource(R.layout.dropdown);
        binding.spinnerSortBySaved.setAdapter(adapterSortBy);

        binding.spinnerSortBySaved.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                MyAdapterSavedFilmListView adapter;
                if (position == 0) {
                    if(!genresResult.isEmpty()){
                        adapter = new MyAdapterSavedFilmListView(getContext(), genresResult);
                    }
                    else if (!searchResult.isEmpty()) {
                        adapter = new MyAdapterSavedFilmListView(getContext(), searchResult);
                    } else {
                        adapter = new MyAdapterSavedFilmListView(getContext(), fullList);
                    }
                    binding.listViewSavedFilm.setAdapter(adapter);
                }
                else{
                    if(!genresResult.isEmpty()){
                        sortBy(genresResult, position);
                    }
                    else if (!searchResult.isEmpty()) {
                        sortBy(searchResult, position);
                    }
                    else {
                        sortBy(fullList, position);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] genres = {"Жанр", "Драма", "Комедія", "Жахи", "Фантастика", "Бойовик", "Пригоди", "Містика", "Детектив", "Трилер", "Мелодрама", "Фентезі", "Мультфільм", "Спорт", "Документальний", "Історичний", "Воєнний", "Екшн", "Кримінал", "Романтика", "Сімейний", "Дорожній", "Науково-популярний", "Біографічний", "Анімація", "Мюзикл", "Нуар"};
        ArrayAdapter adapterGenres = new ArrayAdapter(getContext(), R.layout.select_dropdown, genres);
        adapterGenres.setDropDownViewResource(R.layout.dropdown);
        binding.spinnerGenresSaved.setAdapter(adapterGenres);

        binding.spinnerGenresSaved.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genresFilter = genres[position];
                binding.spinnerSortBySaved.setSelection(0);

                if(genresFilter.equals("Жанр")){
                    if (searchResult.isEmpty()) {
                        MyAdapterSavedFilmListView adapter = new MyAdapterSavedFilmListView(getContext(), fullList);
                        binding.listViewSavedFilm.setAdapter(adapter);
                    } else {
                        MyAdapterSavedFilmListView adapter = new MyAdapterSavedFilmListView(getContext(), searchResult);
                        binding.listViewSavedFilm.setAdapter(adapter);
                    }
                }
                else {
                    if (searchResult.isEmpty()) {
                        searchByGenres(fullList, genresFilter);
                    } else {
                        searchByGenres(searchResult, genresFilter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void searchByGenres(ArrayList<SavedMovie> list, String genresFilter){
        genresResult.clear();
        for (SavedMovie movie : list) {
            if(movie.getMovie().getGenresString().toLowerCase().contains(genresFilter.toLowerCase())){
                genresResult.add(movie);
            }
        }

        if(genresResult.isEmpty()){
            Toast.makeText(getContext(), "Відсутні фільми за вибраним жанром", Toast.LENGTH_SHORT).show();
            binding.spinnerGenresSaved.setSelection(0);
        }
        else{
            MyAdapterSavedFilmListView adapter = new MyAdapterSavedFilmListView(getContext(), genresResult);
            binding.listViewSavedFilm.setAdapter(adapter);
        }
    }

    private void sortBy(ArrayList<SavedMovie> list, Integer parameter) {
        ArrayList<SavedMovie> sorted = new ArrayList<>(list);
        switch (parameter) {
            case 1:
                sorted.sort(Comparator.comparing(movie -> movie.getMovie().getTitle()));
                break;
            case 2:
                sorted.sort(Comparator.comparingInt(movie -> movie.getMovie().getDuration()));
                break;
            case 3:
                sorted.sort(Comparator.comparingInt(movie -> movie.getMovie().getYear()));
                break;
            case 4:
                sorted.sort(Comparator.comparingDouble(movie -> movie.getMovie().getImdb()));
                break;
            default:
                break;
        }
        MyAdapterSavedFilmListView adapter = new MyAdapterSavedFilmListView(getContext(), sorted);
        binding.listViewSavedFilm.setAdapter(adapter);
    }

    private void searchSettings(){
        binding.searchViewFilmSaved.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResult.clear();
                genresResult.clear();
                binding.spinnerGenresSaved.setSelection(0);
                binding.spinnerSortBySaved.setSelection(0);

                int year = 0;
                try{
                    year = Integer.parseInt(query);
                }
                catch (Exception e){
                    year = 0;
                }

                for (SavedMovie movie : fullList) {

                    if((year != 0 && year == movie.getMovie().getYear()) || movie.getMovie().getTitle().toLowerCase().contains(query.toLowerCase())){
                        searchResult.add(movie);
                    }
                }

                if(searchResult.isEmpty()){
                    Toast.makeText(getContext(), "Жодних результатів за запитом", Toast.LENGTH_SHORT).show();
                }
                else{
                    MyAdapterSavedFilmListView adapter = new MyAdapterSavedFilmListView(getContext(), searchResult);
                    binding.listViewSavedFilm.setAdapter(adapter);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (Objects.equals(newText, "")){
                    searchResult.clear();
                    MyAdapterSavedFilmListView adapter = new MyAdapterSavedFilmListView(getContext(), fullList);
                    binding.listViewSavedFilm.setAdapter(adapter);

                    binding.spinnerGenresSaved.setSelection(0);
                    binding.spinnerSortBySaved.setSelection(0);
                }
                return true;
            }
        });


        binding.searchViewFilmSaved.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
    }
}