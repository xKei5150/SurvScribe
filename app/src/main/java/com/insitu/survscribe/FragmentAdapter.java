package com.insitu.survscribe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity; // Correct import
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle; // Correct import
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragments;

    // Updated constructor for ViewPager2
    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity); // Pass the FragmentActivity
        this.fragments = fragments;
    }

    // Handle the creation of your fragments correctly
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position % fragments.size()); // Handle infinite looping
    }

    // Reflect the actual number of fragments
    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
