package com.example.cassette;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cassette.MyClass.Collection;
import com.example.cassette.adapters.MyAdapterSavedFilmListView;
import com.example.cassette.adapters.TagInfo;

public class tagItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_item);

        ((ImageButton) findViewById(R.id.closeAddToTagItem)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Collection collection = TagInfo.getInstance().getCollection();

        ((TextView) findViewById(R.id.textView_titleTag)).setText(collection.getTitle());

        MyAdapterSavedFilmListView adaper = new MyAdapterSavedFilmListView(getApplicationContext(), collection.getSavedMoviesInTag());
        ((ListView) findViewById(R.id.listView_TagsItem)).setAdapter(adaper);
    }
}