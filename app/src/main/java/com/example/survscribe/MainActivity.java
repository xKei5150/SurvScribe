package com.example.survscribe;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingSpinner = findViewById(R.id.loadingSpinner);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new SetTextTask());
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
            runOnUiThread(new Runnable() {
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
        ConstraintLayout spt_menu = findViewById(R.id.spt_menu);
        ConstraintLayout dcp_menu = findViewById(R.id.dcp_menu);
        ConstraintLayout fd_menu = findViewById(R.id.fd_menu);

        TextView sptLabel = spt_menu.findViewById(R.id.menu_label);
        TextView dcpLabel = dcp_menu.findViewById(R.id.menu_label);
        TextView fdLabel = fd_menu.findViewById(R.id.menu_label);

        TextView sptTitle = spt_menu.findViewById(R.id.menu_title);
        TextView dcpTitle = dcp_menu.findViewById(R.id.menu_title);
        TextView fdTitle = fd_menu.findViewById(R.id.menu_title);

        TextView sptSubTitle = spt_menu.findViewById(R.id.menu_subtitle);
        TextView dcpSubTitle = dcp_menu.findViewById(R.id.menu_subtitle);
        TextView fdSubTitle = fd_menu.findViewById(R.id.menu_subtitle);

        Button sptButton = spt_menu.findViewById(R.id.button);
        Button dcpButton = dcp_menu.findViewById(R.id.button);
        Button fdButton = fd_menu.findViewById(R.id.button);

        dcpLabel.setText("ASTM D6951");
        fdLabel.setText("ASTM D1556");

        dcpTitle.setText("Dynamic Cone");
        fdTitle.setText("Field Density Test");

        sptSubTitle.setText("Penetration Test");
        dcpSubTitle.setText("Penetration Test");
        fdSubTitle.setText("Sand-Cone Method");

        Typeface customTypeface = ResourcesCompat.getFont(this, R.font.montserrat_thin);
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
        Intent intent = new Intent(this, st_test_layout.class);
        startActivity(intent);
    }

    private void openDcpTestLayout() {
        Intent intent = new Intent(this, dcp_test_layout.class);
        startActivity(intent);
    }

    private void openFdTestLayout() {
        Intent intent = new Intent(this, fd_test_layout.class);
        startActivity(intent);
    }
}
