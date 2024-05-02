package com.example.cassette;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cassette.databinding.FragmentHomeBinding;
import com.example.cassette.databinding.MainBinding;
import com.google.android.material.tabs.TabLayout;

public class home extends Fragment {

    FragmentHomeBinding binding;
    Fragment fragmentFiml, fragmentTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        fragmentFiml = new film();
        fragmentTag = new tags();
        replaceFragment(fragmentFiml);

        binding.filmTags.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 0) {
                    replaceFragment(fragmentFiml);
                } else if (position == 1) {
                    replaceFragment(fragmentTag);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void replaceFragment(Fragment fragment1) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameFilmTags, fragment1);
        fragmentTransaction.commit();

//        fragmentTransaction.remove(fragment);
//        fragment = fragment1;
//        if(fragment.getClass().equals(tags.class))
//        {
//
//            fragment = fragment1;
//        }
//        else fragment = fragment1;
    }
}
