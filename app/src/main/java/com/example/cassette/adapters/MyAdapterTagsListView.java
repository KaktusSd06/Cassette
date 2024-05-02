package com.example.cassette.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cassette.MyClass.Collection;
import com.example.cassette.tags;
import com.example.cassette.R;
import com.example.cassette.databinding.FragmentTagsBinding;
import com.example.cassette.tagItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapterTagsListView extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<Collection> tags;
    FragmentTagsBinding bindingTags;
    Fragment fr;


    public MyAdapterTagsListView(Context context, ArrayList<Collection> savedMovieArrayList, FragmentTagsBinding bindingTags, Fragment fr) {
        this.context = context;
        this.tags = savedMovieArrayList;
        this.bindingTags = bindingTags;
        lInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fr = fr;
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
            view = lInflater.inflate(R.layout.list_collection_item, parent, false);
        }

        Collection tag = getTag(position);

        ((TextView) view.findViewById(R.id.collection_title)).setText(tag.getTitle());
        ((TextView) view.findViewById(R.id.collection_size)).setText("Кількість фільмів: " + String.valueOf(tag.getSavedMoviesInTag().size()));

        if(tag.getSavedMoviesInTag().size() > 0){
            String imageUrl = tag.getSavedMoviesInTag().get(0).getMovie().getImgSrc();

            Picasso.get()
                    .load(imageUrl)
                    .error(R.drawable.icon)
                    .into(((ImageView) view.findViewById(R.id.collection_img)));
        }
        else{
            ((ImageView) view.findViewById(R.id.collection_img)).setImageResource(R.drawable.logo_icon);
        }

        ((ImageView) view.findViewById(R.id.collection_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof Activity) {
                    TagInfo.getInstance().setCollection(getTag(position));
                    Intent intent = new Intent(context, tagItem.class);
                    context.startActivity(intent);
                } else {

                    Toast.makeText(context, "Context is not an instance of Activity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.removeTag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Видалити тег?")
                        .setPositiveButton("Так", (dialog, which) -> {
                            try {

                                Collection tagToRemove = getTag(position);
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                String currentUserId = currentUser.getUid();

                                if (tagToRemove != null) {
                                    DatabaseReference currentUserRefToRemove = FirebaseDatabase.getInstance().getReference()
                                            .child("users")
                                            .child(currentUserId)
                                            .child("tags")
                                            .child(tagToRemove.getKeyFirebase());

                                    currentUserRefToRemove.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                notifyDataSetChanged();
                                                if (getCount() < 1) {
                                                    bindingTags.textView2.setVisibility(View.VISIBLE);
                                                }
                                                Toast.makeText(context, "Тег видалено", Toast.LENGTH_SHORT).show();

                                                ((tags)fr).loadMyTags(new tags.LoadTagsFrameCallback() {
                                                    @Override
                                                    public void onTagsFrameLoaded(ArrayList<Collection> tags) {

                                                    }
                                                });

                                            } else {
                                                Toast.makeText(context, "Помилка під час видалення", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            catch(Exception exception){}

                            notifyDataSetChanged();
                        }).setNegativeButton("Ні", (dialog, which) -> dialog.dismiss()).create().show();
            }
        });
        return view;
    }

    Collection getTag(int position) {
        return ((Collection) getItem(position));
    }
}
