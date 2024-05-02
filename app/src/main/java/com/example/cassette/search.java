    package com.example.cassette;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.os.Bundle;

    import androidx.annotation.NonNull;
    import androidx.browser.trusted.splashscreens.SplashScreenParamKey;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.GridLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.SearchView;
    import android.widget.Toast;

    import com.example.cassette.MyClass.Movie;
    import com.example.cassette.adapters.InfoFilm;
    import com.example.cassette.adapters.MyAdapterSearch;
    import com.example.cassette.adapters.SelectListener;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.example.cassette.databinding.FragmentSearchBinding;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.lang.reflect.Array;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.Objects;


    public class search extends Fragment implements SelectListener {

        public search(){
        }
        DatabaseReference database;
        RecyclerView recyclerView;
        MyAdapterSearch myAdapter;
        ArrayList<Movie> fullList;
        ArrayList<Movie> searchResult;
        ArrayList<Movie> genresResult;
        RecyclerView.LayoutManager  layoutManager;
        private boolean isLoading = false;
        FragmentSearchBinding binding;
        int orderPosition = 0;
        String genresFilter = "Жанр";


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            recyclerView = binding.recycleViewSearch;
            layoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(layoutManager);

            loadFilmList();
            searchSetings();
            dropdownMenu();
        }

        private void sortBy(ArrayList<Movie> list, Integer parameter){
            ArrayList<Movie> sorted = new ArrayList<>(list);
            switch (parameter) {
                case 1: {
                    sorted.sort(Comparator.comparing(Movie::getTitle));
                }
                break;
                case 2: {
                    sorted.sort(Comparator.comparingInt(Movie::getDuration));
                }
                break;
                case 3: {
                    sorted.sort(Comparator.comparingInt(Movie::getYear));
                }
                break;
                case 4: {
                    sorted.sort(Comparator.comparingDouble(Movie::getImdb));
                }
                break;
                default:
                    break;
            }
            myAdapter = new MyAdapterSearch(getContext(), sorted, search.this::onItemClick);
            recyclerView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();

        }

        private void dropdownMenu(){
            String[] sortBy = {"Впорядкувати","Назва", "Тривалість", "Рік", "IMDB"};
            String[] orderBy = {"sort", "title", "duration", "year", "IMDB"};
            ArrayAdapter adapterSortBy = new ArrayAdapter(getContext(), R.layout.select_dropdown, sortBy);
            adapterSortBy.setDropDownViewResource(R.layout.dropdown);
            binding.spinnerSortBy.setAdapter(adapterSortBy);

            binding.spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String selectedOption = orderBy[position];
                    if (position == 0) {
                        if(!genresResult.isEmpty()){
                            myAdapter = new MyAdapterSearch(getContext(), genresResult, search.this::onItemClick);
                        }
                        else if (!searchResult.isEmpty()) {
                            myAdapter = new MyAdapterSearch(getContext(), searchResult, search.this::onItemClick);
                        } else {
                            myAdapter = new MyAdapterSearch(getContext(), fullList, search.this::onItemClick);
                        }
                        recyclerView.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
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
            binding.spinnerGenres.setAdapter(adapterGenres);

            binding.spinnerGenres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    genresFilter = genres[position];
                    binding.spinnerSortBy.setSelection(0);

                    if(genresFilter.equals("Жанр")){
                        if (searchResult.isEmpty()) {
                            myAdapter = new MyAdapterSearch(getContext(), fullList, search.this::onItemClick);
                            recyclerView.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            myAdapter = new MyAdapterSearch(getContext(), searchResult, search.this::onItemClick);
                            recyclerView.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged();
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

        private void searchByGenres(ArrayList<Movie> list, String genresFilter){
            genresResult.clear();
            for (Movie movie : list) {
                if(movie.getGenresString().toLowerCase().contains(genresFilter.toLowerCase())){
                    genresResult.add(movie);
                }
            }

            if(genresResult.isEmpty()){
                Toast.makeText(getContext(), "Відсутні фільми за вибраним жанром", Toast.LENGTH_SHORT).show();
                binding.spinnerGenres.setSelection(0);
            }
            else{
                myAdapter = new MyAdapterSearch(getContext(), genresResult, search.this::onItemClick);
                recyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            }
        }

        private void searchSetings(){
            binding.searchViewFilm.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchResult.clear();
                    genresResult.clear();
                    binding.spinnerGenres.setSelection(0);
                    binding.spinnerSortBy.setSelection(0);

                    int year = 0;
                    try{
                        year = Integer.parseInt(query);
                    }
                    catch (Exception e){
                        year = 0;
                    }

                    for (Movie movie : fullList) {

                        if((year != 0 && year == movie.getYear()) || movie.getTitle().toLowerCase().contains(query.toLowerCase())){
                            searchResult.add(movie);
                        }
                    }

                    if(searchResult.isEmpty()){
                        Toast.makeText(getContext(), "Жодних результатів за запитом", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        myAdapter = new MyAdapterSearch(getContext(), searchResult, search.this::onItemClick);
                        recyclerView.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (Objects.equals(newText, "")){
                        searchResult.clear();
                        myAdapter = new MyAdapterSearch(getContext(), fullList, search.this::onItemClick);
                        recyclerView.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();

                        binding.spinnerGenres.setSelection(0);
                        binding.spinnerSortBy.setSelection(0);
                    }
                    return true;
                }
            });


            binding.searchViewFilm.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    return true;
                }
            });
        }

        private void loadFilmList() {
            isLoading = true;
            showLoadingAnimation();

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ArrayList<Movie> newList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            try {
                                double imdb = dataSnapshot.child("imdb").getValue() != null ? (double) dataSnapshot.child("imdb").getValue() : 0;

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

                                Movie movie = new Movie(about, ageRating, cast, country, director, duration, filmCompany, genres, imdb,  imgSrc, title, type, year, bdKey);
                                if(genresFilter.equals("Жанр")) {
                                    newList.add(movie);
                                }
                                else{
                                    if(Arrays.asList(movie.getGenres()).contains(genresFilter)){
                                        newList.add(movie);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (newList.size() > 0) {
                            fullList.clear();
                            fullList.addAll(newList);
                            myAdapter = new MyAdapterSearch(getContext(), fullList, search.this::onItemClick);
                            recyclerView.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(getContext(), "За запитом відсутні результати", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                    }
                    isLoading = false;
                    hideLoadingAnimation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    isLoading = false;
                    hideLoadingAnimation();
                    Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
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
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            binding = FragmentSearchBinding.inflate(inflater, container, false);

            database = FirebaseDatabase.getInstance().getReference("films");
            fullList = new ArrayList<>();
            genresResult = new ArrayList<>();
            searchResult = new ArrayList<>();

            return binding.getRoot();
        }

        @Override
        public void onItemClick(Movie movie) {
            InfoFilm.getInstance().setMovie(movie);

            Intent intent = new Intent(requireContext(), infoFilm.class);
            startActivity(intent);
        }
    }