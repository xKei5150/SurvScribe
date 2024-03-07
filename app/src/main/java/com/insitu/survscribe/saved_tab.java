package com.insitu.survscribe;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.survscribe.R;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


public class saved_tab extends Fragment {

    private RecyclerView savedFilesRecyclerView;
    private FileListAdapter fileListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_saved_tab, container, false);

        savedFilesRecyclerView = view.findViewById(R.id.list_files);
        savedFilesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        fileListAdapter = new FileListAdapter(requireContext(), new ArrayList<>());
        savedFilesRecyclerView.setAdapter(fileListAdapter);

        listSavedFiles();

        return view;
    }

    private void listSavedFiles() {
        File filesDir = requireContext().getFilesDir();
        File[] files = filesDir.listFiles();

        List<Pair<String, String>> fileDetails = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".xml")) {
                    Pair<String, Class<?>> typeInfo = extractTestType(file);
                    String filename = typeInfo.first;
                    Class<?> testClass = typeInfo.second;

                    if (testClass != null) {
                        String testType = (testClass == DCPTest.class) ? "Dynamic Cone Penetration Test" :
                                (testClass == FDTest.class) ? "Field Density Test" :
                                        (testClass == SPTest.class) ? "Standard Penetration Test" :
                                "Error";
                        fileDetails.add(new Pair<>(filename, testType));
                    } else {
                        fileDetails.add(new Pair<>(filename, "Unknown Test Type"));
                    }
                } else {
                    Log.d("SavedFiles", "Skipping non-XML file: " + file.getName());
                }
            }
        }

        fileListAdapter.setFileDetails(fileDetails);
    }

    private Pair<String, Class<?>> extractTestType(File file) {
        try {
            XStream xstream = new XStream();
            XStream.setupDefaultSecurity(xstream);

            xstream.allowTypes(new Class[] {SiteInformation.class,
                    FDTest.class,
                    FD_Data.class,
                    DCPTest.class,
                    DCP_RowData.class,
                    SPTest.class,
                    SP_Data.class
            });

            xstream.alias("SiteInformation", SiteInformation.class);
            xstream.alias("FDTest", FDTest.class);
            xstream.alias("DCPTest", DCPTest.class);
            xstream.alias("SPTest", SPTest.class);

            FileInputStream fileIn = new FileInputStream(file);
            Object loadedObject = xstream.fromXML(fileIn);
            fileIn.close();

            if (loadedObject instanceof DCPTest) {
                DCPTest dcpTest = (DCPTest) loadedObject;
                String testType = dcpTest.getSiteInfo().getTestType();
                return new Pair<>(file.getName(), DCPTest.class);

            } else if (loadedObject instanceof FDTest) {
                FDTest fdTest = (FDTest) loadedObject;
                String testType = fdTest.getSiteInfo().getTestType();
                return new Pair<>(file.getName(), FDTest.class);
            } else if (loadedObject instanceof SPTest) {
                SPTest spTest = (SPTest) loadedObject;
                String testType = spTest.getSiteInfo().getTestType();
                return new Pair<>(file.getName(), SPTest.class);

            } else {
                Log.e("FileListAdapter", "Unexpected object type in XML: " + file.getName());
                return new Pair<>(file.getName(), null);
            }

        } catch (Exception e) {
            Log.e("FileListAdapter", "Error parsing XML: " + file.getName(), e);
            return new Pair<>(file.getName(), null);
        }
    }


    // Helper for loading the full test data (Implement later)
    private void loadTestData(File file, Class<?> testClass) {
        // ... Add logic here to load and process DCPTest or FDTest data ...
    }
}
