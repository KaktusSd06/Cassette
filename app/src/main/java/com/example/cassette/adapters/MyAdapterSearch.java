package com.example.cassette.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cassette.MyClass.Movie;
import com.example.cassette.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapterSearch extends RecyclerView.Adapter<MyAdapterSearch.MyViewHolder>{

    Context context;
    ArrayList<Movie> movieArrayList;
    SelectListener selectListener;

    public MyAdapterSearch(Context context, ArrayList<Movie> movieArrayList, SelectListener selectListener)  {
        this.context = context;
        this.movieArrayList = movieArrayList;
        this.selectListener = selectListener;
    }

    Movie getProduct(int position) {
        return ((Movie) getItem(position));
    }


    public Movie getItem(int position) {
        return movieArrayList.get(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_film_item, parent, false);
        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Movie movie = movieArrayList.get(position);

        String FilmTitileResult;
        String FilmTitile = movie.getTitle();

        if (FilmTitile.length() > 14) {
            FilmTitileResult = FilmTitile.substring(0, 14) + "...";
        }
        else{
            FilmTitileResult = FilmTitile;
        }
        holder.textViewTitle.setText(FilmTitileResult);
        holder.textViewIMDb.setText("IMDb " + String.valueOf(movie.getImdb()));
        holder.textViewTime.setText(movie.GetStringDuraction() + " хв");
        String imageUrl = movie.getImgSrc();

        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.icon) // Replace with your default image resource
                .into(holder.imgView);

        //Picasso.get().load(imageUrl).into(holder.imgView);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.onItemClick(movieArrayList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgView;
        TextView textViewTime, textViewTitle, textViewIMDb;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            textViewTime = itemView.findViewById(R.id.TV_time);
            textViewTitle = itemView.findViewById(R.id.textView3);
            imgView = itemView.findViewById(R.id.imageView3);
            textViewIMDb = itemView.findViewById(R.id.TV_Imdb);
        }
    }
}