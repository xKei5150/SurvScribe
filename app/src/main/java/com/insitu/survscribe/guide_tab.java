package com.insitu.survscribe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.survscribe.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class guide_tab extends Fragment {

    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_tab, container, false);

        // Create your fragment instances
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new spt_guide());
        fragments.add(new dcp_guide());
        fragments.add(new fd_guide());
        // Find the ViewPager within the inflated view
        viewPager = view.findViewById(R.id.viewPager);
        WormDotsIndicator dotsIndicator = view.findViewById(R.id.worm_dots_indicator);

        // Get the FragmentActivity
        FragmentAdapter adapter = new FragmentAdapter(requireActivity(), fragments);
        viewPager.setAdapter(adapter);
        dotsIndicator.attachTo(viewPager);
        return view;
    }
}
