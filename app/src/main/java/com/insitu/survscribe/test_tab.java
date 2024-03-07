package com.insitu.survscribe;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.survscribe.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link test_tab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class test_tab extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public test_tab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment test_tab2.
     */
    // TODO: Rename and change types and number of parameters
    public static test_tab newInstance(String param1, String param2) {
        test_tab fragment = new test_tab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private ProgressBar loadingSpinner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ren_fragment_test_tab, container, false);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new test_tab.SetTextTask());

        return view;
    }

    private class SetTextTask implements Runnable {

        @Override
        public void run() {
            // Simulate background tasks
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Update UI on the main thread
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateText();

                    // Hide loading spinner after the background task is completed
                    loadingSpinner.setVisibility(View.GONE);
                }
            });
        }
    }

    private void updateText() {
        View view = getView(); // Get the root view

        if (view == null) return;

        ConstraintLayout spt_menu = view.findViewById(R.id.spt_menu);
        ConstraintLayout dcp_menu = view.findViewById(R.id.dcp_menu);
        ConstraintLayout fd_menu = view.findViewById(R.id.fd_menu);

        TextView sptLabel = spt_menu.findViewById(R.id.menu_label);
        TextView dcpLabel = dcp_menu.findViewById(R.id.menu_label);
        TextView fdLabel = fd_menu.findViewById(R.id.menu_label);

        TextView sptTitle = spt_menu.findViewById(R.id.menu_title);
        TextView dcpTitle = dcp_menu.findViewById(R.id.menu_title);
        TextView fdTitle = fd_menu.findViewById(R.id.menu_title);

        TextView sptSubTitle = spt_menu.findViewById(R.id.menu_subtitle);
        TextView dcpSubTitle = dcp_menu.findViewById(R.id.menu_subtitle);
        TextView fdSubTitle = fd_menu.findViewById(R.id.menu_subtitle);

        ConstraintLayout sptButton = spt_menu.findViewById(R.id.button);
        ConstraintLayout dcpButton = dcp_menu.findViewById(R.id.button);
        ConstraintLayout fdButton = fd_menu.findViewById(R.id.button);

        dcpLabel.setText("ASTM D6951");
        fdLabel.setText("ASTM D1556");

        dcpTitle.setText("Dynamic Cone");
        fdTitle.setText("Field Density Test");

        sptSubTitle.setText("Penetration Test");
        dcpSubTitle.setText("Penetration Test");
        fdSubTitle.setText("Sand-Cone Method");

        Typeface customTypeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_thin);
        fdSubTitle.setTypeface(customTypeface);

        // Set click listeners for the buttons
        sptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSptTestLayout();
            }
        });

        dcpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDcpTestLayout();
            }
        });

        fdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFdTestLayout();
            }
        });
    }

    // Methods to open the respective activities
    private void openSptTestLayout() {
        Intent intent = new Intent(requireContext(), sp_test_layout.class);
        startActivity(intent);
    }

    private void openDcpTestLayout() {
        Intent intent = new Intent(requireContext(), dcp_test_layout.class);
        startActivity(intent);
    }

    private void openFdTestLayout() {
        Intent intent = new Intent(requireContext(), fd_test_layout.class);
        startActivity(intent);
    }
}