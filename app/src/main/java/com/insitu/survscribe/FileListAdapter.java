package com.insitu.survscribe;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.survscribe.R;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {
    private Context context;
    private List<Pair<String, String>> fileDetails; // Pair of file name and test type

    public FileListAdapter(Context context, List<Pair<String, String>> fileDetails) {
        this.context = context;
        this.fileDetails = fileDetails;
    }

    public void setFileDetails(List<Pair<String, String>> fileDetails) {
        this.fileDetails = fileDetails;
        notifyDataSetChanged(); // Tell the adapter the data has changed
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_file_layout, parent, false); // Use the layout you created
        return new FileViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        Pair<String, String> fileDetail = fileDetails.get(position);
        String filename = fileDetail.first;
        String testType = fileDetail.second;
        holder.fileNameTextView.setText(filename);
        holder.testTypeTextView.setText(testType);
        holder.itemView.setOnClickListener(v -> {
            if (testType.equalsIgnoreCase("Dynamic Cone Penetration Test")) {
                openFileUtil.startDCPTestLayoutActivity(context, filename);
            } else if (testType.equalsIgnoreCase("Field Density Test")) {
                openFileUtil.startFDTestLayoutActivity(context, filename);
            } else if (testType.equalsIgnoreCase("Standard Penetration Test")) {
                openFileUtil.startSPTestLayoutActivity(context, filename);
            }
            else {
                // Handle the case of unknown test type
            }
        });
        holder.itemView.setTag(holder);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
                FileViewHolder holder = (FileViewHolder) view.getTag();
                holder.showDeleteConfirmationDialog(position, fileDetails, view.getContext());
                return true; // Indicates that we've handled the long click
            }
        });
    }

    private boolean deleteSPTestAndImages(Context context, String filename) {
        try {
            // 1. Load the SPTest data
            SPTest spTest = loadSPTestData(context, filename);

            // 2. Delete images
            for (SP_Data dataItem : spTest.getSPDataList()) {
                String imagePath = dataItem.getImagePath();
                if (imagePath != null) {
                    File imageFile = new File(imagePath);
                    if (!imageFile.delete()) {
                        Log.e("FileListAdapter", "Error deleting image: " + imagePath);
                    }
                }
            }

            // 3. Delete the main SPTest file
            File file = new File(context.getFilesDir(), filename);
            return file.delete();
        } catch (Exception e) {
            Log.e("FileListAdapter", "Error deleting SPTest or images", e);
            return false;
        }
    }

    private SPTest loadSPTestData(Context context, String filename) throws IOException, ClassNotFoundException, FileNotFoundException {
        File file = new File(context.getFilesDir(), filename);
        FileInputStream fileIn = new FileInputStream(file);

        XStream xstream = new XStream();
        XStream.setupDefaultSecurity(xstream); // Allow some potentially less secure operations
        xstream.allowTypes(new Class[] {SPTest.class, SiteInformation.class, SP_Data.class});
        xstream.alias("SiteInformation", SiteInformation.class);
        xstream.alias("SP_Data", SP_Data.class);
        xstream.alias("SPTest", SPTest.class);
        SPTest spTest = (SPTest) xstream.fromXML(fileIn);
        fileIn.close();
        return spTest;
    }

    @Override
    public int getItemCount() {
        return fileDetails != null ? fileDetails.size() : 0;
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;
        TextView testTypeTextView;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.file_name);
            testTypeTextView = itemView.findViewById(R.id.file_test_type);


        }

        private void showDeleteConfirmationDialog(int position, List<Pair<String, String>> fileDetails, Context context) {
            Pair<String, String> fileDetail = fileDetails.get(position);
            String filename = fileDetail.first;

            new AlertDialog.Builder(context)
                    .setTitle("Delete File")
                    .setMessage("Are you sure you want to delete " + filename + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if(Objects.equals(fileDetail.second, "Standard Penetration Test")){
                            deleteSPTestAndImages(context, filename);
                        }
                        deleteFile(filename);
                        fileDetails.remove(position);
                        notifyItemRemoved(position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void deleteFile(String filename) {
            File file = new File(context.getFilesDir(), filename);
            if (file.delete()) {
                Toast.makeText(context, "Successfully deleted: " + filename, Toast.LENGTH_SHORT).show();
            } else {
                // Handle file deletion error (e.g., show an error message)
            }
        }
    }
}
