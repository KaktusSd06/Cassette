package com.example.cassette.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassette.MyClass.SavedMovie;
import com.example.cassette.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

    public class MyListAdapter extends BaseAdapter {

        Context context;
        LayoutInflater lInflater;
        ArrayList<SavedMovie> arraListSavedMovie;
        public void setFilteredList(ArrayList<SavedMovie> filteredList){
            arraListSavedMovie = filteredList;
            notifyDataSetChanged();
        }


        public MyListAdapter(Context context, ArrayList<SavedMovie> productsList) {
            this.context = context;
            this.arraListSavedMovie = productsList;
            lInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arraListSavedMovie.size();
        }

        @Override
        public Object getItem(int position) {
            return arraListSavedMovie.get(position);
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


            SavedMovie movie = arraListSavedMovie.get(position);

            ((TextView) view.findViewById(R.id.TV_time_)).setText(movie.getMovie().GetStringDuraction() + " хв");
            ((TextView) view.findViewById(R.id.TV_tilte)).setText(movie.getMovie().getTitle());
            ((TextView) view.findViewById(R.id.TV_Imdb)).setText("IMDb " + String.valueOf(movie.getMovie().getImdb()));

            String[] genres = movie.getMovie().getGenres();
            StringBuilder genresResult = new StringBuilder();
            String genresString = genresResult.toString().trim();
            ((TextView) view.findViewById(R.id.TV_genres)).setText(genresString);
            String imageUrl = movie.getMovie().getImgSrc();
            Picasso.get()
                    .load(imageUrl)
                    .error(R.drawable.icon)
                    .into((ImageView) view.findViewById(R.id.imageView5));




            return view;
        }

        SavedMovie getProduct(int position) {
            return ((SavedMovie) getItem(position));
        }

    }
