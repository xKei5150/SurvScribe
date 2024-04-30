package com.insitu.survscribe;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.survscribe.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class sp_test_layout extends AppCompatActivity implements RowDataClickListener {

    private static final int CREATE_PDF_REQUEST_CODE = 2;
    public int clickedImagePosition;
    RecyclerView testDataTable;
    SP_Adapter adapter;
    List<SP_Data> SPDataList;
    private ImageButton soilImage;
    private List<String> teamMembers;
    private EditText dateConductedEditText;
    private EditText timeEditText;
    private EditText companyName;
    private EditText projectName;
    private EditText ownerLessor;
    private EditText teamLeader;
    private EditText location;
    private EditText weather;
    private EditText materialClassification;
    private SiteInformation siteInfo;
    private DateTimePicker dateTimePicker;

    private TextView toolbarTitle;
    private static final int PICK_IMAGE_REQUEST = 1;
    private File tempImageDirectory;


    private boolean dataChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_test_layout);

        teamMembers = new ArrayList<>();

        ConstraintLayout siteInfoLayout = findViewById(R.id.soilTest_siteInfo);
        MaterialToolbar toolbarLayout = findViewById(R.id.test_toolbar);
        soilImage = toolbarLayout.findViewById(R.id.soilImageButton);
        soilImage.setVisibility(View.GONE);

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Standard Penetration Test");

        companyName = siteInfoLayout.findViewById(R.id.companyNameInput);
        dateConductedEditText = siteInfoLayout.findViewById(R.id.dateConductedInput);
        projectName = siteInfoLayout.findViewById(R.id.projectNameInput);
        timeEditText = siteInfoLayout.findViewById(R.id.timeInput);
        ownerLessor = siteInfoLayout.findViewById(R.id.ownerInput);
        teamLeader = siteInfoLayout.findViewById(R.id.teamLeaderInput);
        EditText teamMember = siteInfoLayout.findViewById(R.id.teamMemberInput);
        location = siteInfoLayout.findViewById(R.id.locationInput);
        weather = siteInfoLayout.findViewById(R.id.weatherInput);
        materialClassification = siteInfoLayout.findViewById(R.id.materialClassificationInput);

        dateTimePicker = new DateTimePicker(this, dateConductedEditText, timeEditText);

        ConstraintLayout add_data_button = findViewById(R.id.add_data_button);
        add_data_button.setOnClickListener(view -> {
            showAddDataDialog();
        });

        teamMember.setOnClickListener(view -> {
            showManageMembersDialog();
        });
        try {
            tempImageDirectory = createTempImageDirectory(this);
        } catch (IOException e) {
            showValidationErrorToast("Error creating temporary image directory" + e);
        }

        siteInfo = new SiteInformation("",
                toolbarTitle.getText().toString(),
                "", "", "", "", "", "", "", teamMembers, "", "");


        testDataTable = findViewById(R.id.testDataTable);
        testDataTable.setLayoutManager(new LinearLayoutManager(this));

        SPDataList = prepareData();
        adapter = new SP_Adapter(SPDataList, this);
        loadXmlData();
        testDataTable.setAdapter(adapter);

        addChangeListenerToEditText(companyName);
        addChangeListenerToEditText(dateConductedEditText);
        addChangeListenerToEditText(projectName);
        addChangeListenerToEditText(timeEditText);
        addChangeListenerToEditText(ownerLessor);
        addChangeListenerToEditText(teamLeader);
        addChangeListenerToEditText(location);
        addChangeListenerToEditText(weather);
        addChangeListenerToEditText(materialClassification);
    }

    private void updateUi(SPTest spTest) {
        SiteInformation siteInfo = spTest.getSiteInfo();
        List<SP_Data> SPDataList = spTest.getSPDataList();

        toolbarTitle.setText(siteInfo.getTestType());
        companyName.setText(siteInfo.getCompanyName());
        dateConductedEditText.setText(siteInfo.getDateConducted());
        projectName.setText(siteInfo.getProjectName());
        timeEditText.setText(siteInfo.getTime());
        ownerLessor.setText(siteInfo.getOwnerLessor());
        teamLeader.setText(siteInfo.getTeamLeader());
        teamMembers = siteInfo.getTeamMembers();
        location.setText(siteInfo.getLocation());
        weather.setText(siteInfo.getWeather());
        materialClassification.setText(siteInfo.getMaterialClassification());

        this.siteInfo = siteInfo;

        this.SPDataList.clear(); // Clear existing data
        this.SPDataList.addAll(SPDataList); // Add new data
        adapter.notifyDataSetChanged(); // Notify adapter of changes
    }


    private List<SP_Data> prepareData() {
        List<SP_Data> SPDataList = new ArrayList<>();

        SPDataList.add(new SP_Data(true, "DEPTH OF\nREFUSAL", "N-VALUES\nOR\nN-BLOWS", "SOIL SAMPLE"));

        // Add blank data rows
        int numBlankRows = 9;
        for (int i = 0; i < numBlankRows; i++) {
            SPDataList.add(new SP_Data(false, "", "", ""));
        }

        return SPDataList;
    }


    private void addChangeListenerToEditText(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void showManageMembersDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.form_site_members, null);
        List<String> tempTeamMembers = new ArrayList<>(teamMembers);
        // Find views
        RecyclerView membersRecyclerView = dialogView.findViewById(R.id.membersRecyclerView);
        TextView noMembersMessage = dialogView.findViewById(R.id.noMembersMessage);
        EditText newMemberInput = dialogView.findViewById(R.id.newMemberInput);
        Button addMemberButton = dialogView.findViewById(R.id.addMemberButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Setup RecyclerView
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MembersAdapter adapter = new MembersAdapter(tempTeamMembers);
        membersRecyclerView.setAdapter(adapter);


        // Handle Button Clicks
        addMemberButton.setOnClickListener(v -> {
            String newMember = newMemberInput.getText().toString();
            if (!newMember.isEmpty()) {
                tempTeamMembers.add(newMember);
                adapter.notifyDataSetChanged();
                newMemberInput.setText("");
                noMembersMessage.setVisibility(View.GONE);
            }
        });


        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(v -> {
            teamMembers.clear(); // Clear the original list
            teamMembers.addAll(tempTeamMembers); // Copy changes
            dataChanged = true;
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void showAddDataDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.form_sp_data_layout, null);

        EditText depthOfRefusalInput = dialogView.findViewById(R.id.depthOfRefusalInput);
        EditText nValuesInput = dialogView.findViewById(R.id.nValuesInput);
        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {

                    String depthOfRefusal = depthOfRefusalInput.getText().toString();
                    String nValues = nValuesInput.getText().toString();

                    boolean dataAdded = updateNextBlankRow(depthOfRefusal, nValues);

                    if (!dataAdded) {
                        int newRowIndex = SPDataList.size();  // Get index of the newly added row
                        SPDataList.add(new SP_Data(false, depthOfRefusal, nValues, ""));
                        adapter.notifyItemInserted(newRowIndex);
                        dataChanged = true;
                    }
                    adapter.notifyDataSetChanged();

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showEditDataDialog(int position, SP_Data spData) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.form_sp_data_layout, null);

        EditText depthOfRefusalInput = dialogView.findViewById(R.id.depthOfRefusalInput);
        EditText nValuesInput = dialogView.findViewById(R.id.nValuesInput);
        // Pre-fill
        depthOfRefusalInput.setText(spData.getDepthOfRefusal());
        nValuesInput.setText(spData.getnValues());

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String depthOfRefusal = depthOfRefusalInput.getText().toString();
                    String nValues = nValuesInput.getText().toString();

                    dataChanged = true;
                    adapter.updateRow(position, depthOfRefusal, nValues);
                    adapter.notifyItemChanged(position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }



    private void showSaveFileDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.save_file_dialog, null);

        EditText filenameInput = dialogView.findViewById(R.id.filenameInput);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(v -> {
            String filename = filenameInput.getText().toString();
            if (!filename.isEmpty()) {
                filename += ".xml"; // Add the .xml extension
                File file = new File(getFilesDir(), filename);
                if (file.exists()) {
                    showValidationErrorToast("A file with this name already exists.");
                    return;
                }
                saveDataToXml(filename);
                dialog.dismiss();
                super.onBackPressed();
            }
            // Else: You might want to show an error if the filename is empty
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void showValidationErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean updateNextBlankRow(String depthOfRefusal, String nValues) {
        for (int i = 1; i < SPDataList.size(); i++) {
            SP_Data row = SPDataList.get(i);
            if (row.isHeader) continue;

            if (row.getDepthOfRefusal().isEmpty() && row.getnValues().isEmpty()) {
                row.setDepthOfRefusal(depthOfRefusal);
                row.setnValues(nValues);
                adapter.notifyItemChanged(i); // Update specific row
                dataChanged = true;
                return true;
            }
        }
        return false; // No blank row found
    }

    public void onSavePressed(View view) {
        String filename = getIntent().getStringExtra("filename");
        if(filename == null)
            showSaveFileDialog();
        else {
            saveDataToXml(filename);
            super.onBackPressed();
        }
    }

    private void showUnsavedChangesDialog() {
        String filename = getIntent().getStringExtra("filename");
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Unsaved Changes")
                .setMessage("Do you want to save your changes?")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Handle saving logic
                    if (filename != null) {
                        saveDataToXml(filename);
                        super.onBackPressed();
                    } else {
                        showSaveFileDialog();
                    }
                })
                .setNegativeButton("Discard", (dialog, which) -> {
                    dataChanged = false;  // Reset the flag
                    super.onBackPressed(); // Finish the activity
                })
                .setNeutralButton("Cancel", (dialog, which) -> {

                })
                .show();
    }

    private void saveDataToXml(String filename) {
        try {
            siteInfo = new SiteInformation("",
                    toolbarTitle.getText().toString(),
                    companyName.getText().toString(),
                    dateConductedEditText.getText().toString(),
                    projectName.getText().toString(),
                    timeEditText.getText().toString(),
                    ownerLessor.getText().toString(),
                    teamLeader.getText().toString(),
                    location.getText().toString(),
                    teamMembers,
                    weather.getText().toString(),
                    materialClassification.getText().toString());
            SPTest wrapper = new SPTest();
            wrapper.setSiteInfo(siteInfo);
            for (SP_Data dataItem : SPDataList) {
                String imagePath = dataItem.getImagePath();
                if (imagePath != null && imagePath.startsWith(tempImageDirectory.getAbsolutePath())) {
                    String oldImagePath = dataItem.getImagePath();

                    File savedImage = copyImageToInternalStorage(Uri.fromFile(new File(imagePath)));
                    if (oldImagePath != null) {
                        File oldImage = new File(oldImagePath);
                        if (oldImage.exists()) {
                            oldImage.delete();
                        }
                    }
                    dataItem.setImagePath(savedImage.getAbsolutePath());
                }
            }
            wrapper.setSPDataList(SPDataList);

            XStream xstream = new XStream();
            xstream.alias("SPTest", SPTest.class);

            File file = new File(getFilesDir(), filename);
            FileOutputStream fileOut = new FileOutputStream(file);

            xstream.toXML(wrapper, fileOut);

            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadXmlData() {
        // Get the filename from the Intent
        String filename = getIntent().getStringExtra("filename");
        if (filename != null) {
            try {
                File file = new File(getFilesDir(), filename);
                FileInputStream fileIn = new FileInputStream(file);

                XStream xstream = new XStream();
                XStream.setupDefaultSecurity(xstream); // Allow some potentially less secure operations
                xstream.allowTypes(new Class[] {SPTest.class, SiteInformation.class, SP_Data.class}); // Add all classes that need to be deserialized
                xstream.alias("SiteInformation", SiteInformation.class);
                xstream.alias("SP_Data", SP_Data.class);
                xstream.alias("SPTest", SPTest.class);

                SPTest spTest = (SPTest) xstream.fromXML(fileIn);

                runOnUiThread(() -> updateUi(spTest));

                fileIn.close();
            } catch (Exception e) {
                // Handle exceptions (file not found, parsing errors)
                e.printStackTrace();
                // Show a message to the user indicating the error
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            processSelectedImage(selectedImageUri, clickedImagePosition); // We'll create this method
        }
        if (requestCode == CREATE_PDF_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri pdfUri = null;
            if (data != null) {
                pdfUri = data.getData();
                savePdfToUri(pdfUri);  // Implement this function 
            }
        }
    }



    private void processSelectedImage(Uri selectedImageUri, int position) {
        try {
            File tempImageFile = File.createTempFile("temp_image_", null, tempImageDirectory);
            String tempImagePath = tempImageFile.getAbsolutePath();
            copyTempImageToInternalStorage(selectedImageUri, tempImagePath);
//            File copiedImageFile = copyImageToInternalStorage(selectedImageUri);
//            String imagePath = copiedImageFile.getAbsolutePath();

            SP_Data spData = SPDataList.get(position);

            spData.setImagePath(tempImagePath);
            dataChanged = true;
            adapter.notifyItemChanged(position);
        } catch (IOException e) {
            showValidationErrorToast("Error copying or uploading image: " + e);
        }
    }

    private File createTempImageDirectory(Context context) throws IOException {
        File tempDir = new File(context.getCacheDir(), "temp_images");

        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) { // Create the directory if it doesn't exist
                throw new IOException("Failed to create temp image directory");
            }
        }
        return tempDir;
    }

    private File copyImageToInternalStorage(Uri imageUri) throws IOException {
        String filename = "soil_image_" + UUID.randomUUID().toString();
        String mimeType = getContentResolver().getType(imageUri);
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

        if (extension != null) {
            filename += "." + extension;
        }

        File imageFile = new File(getFilesDir(), filename);

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = getContentResolver().openInputStream(imageUri);
            outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            // Ensure streams are closed
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        }

        return imageFile;
    }
    private File copyTempImageToInternalStorage(Uri imageUri, String destinationPath) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = getContentResolver().openInputStream(imageUri);
            outputStream = new FileOutputStream(destinationPath); // Use provided destination
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        }

        return new File(destinationPath);
    }


    public void onBackPressed(View view) {
        if (dataChanged) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRowDataClick(int position, Object spData) {
        showEditDataDialog(position, (SP_Data) spData);
    }



    private void savePdfToUri(Uri pdfUri) {
        try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(pdfUri, "w");
             FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor())) {

            Document document = new Document();
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            LineSeparator separator = new LineSeparator();
//            separator.setPercentage(95);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph title = new Paragraph("STANDARD PENETRATION TEST", boldFont);
            title.setSpacingAfter(8);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph siteInfo = new Paragraph("SITE INFORMATION FORM", boldFont);
            siteInfo.setSpacingAfter(8);

            document.add(separator);
            document.add(siteInfo);
            document.add(createSiteTable(this.siteInfo));

            document.add(Chunk.NEWLINE);
            Paragraph test = new Paragraph("TEST DATA TABLE", boldFont);
            test.setSpacingAfter(12);
            separator.setOffset(5);
            document.add(test);
            document.add(separator);
            document.add(createTestTable(this.SPDataList));

            document.close();
            fileOutputStream.close();

            // Show a toast message to indicate successful file creation
            Toast.makeText(this, "Test report saved to Downloads.", Toast.LENGTH_SHORT).show();
        } catch (IOException | DocumentException e) {
            Log.e("PDF Saving", "Error saving PDF", e);
            Toast.makeText(this, "Unable to save PDF file. Try to save the file first.", Toast.LENGTH_SHORT).show();
        }
    }


    private PdfPTable createTestTable(List<SP_Data> spDataList) throws BadElementException, IOException {
        PdfPTable testCell = new PdfPTable(3);
        testCell.setWidthPercentage(100);
        testCell.setHeaderRows(1);
        testCell.getDefaultCell().setPaddingBottom(3);
        testCell.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        testCell.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        testCell.getDefaultCell().setBorder(Rectangle.BOX);
        testCell.addCell("Depth of Refusal");
        testCell.addCell("N-Values or N-Blows");
        testCell.addCell("Soil Sample");

        // Add data rows based on SPDataList
        for (int i = 1; i < spDataList.size(); i++) {
            SP_Data spData = spDataList.get(i);
            testCell.addCell(spData.getDepthOfRefusal());
            testCell.addCell(spData.getnValues());
            try {
                Image image = Image.getInstance(spData.getImagePath());
                image.scaleToFit(250, 250);
                testCell.addCell(image);
            } catch (IOException | BadElementException e) {
                testCell.addCell("");
            }
        }

        return testCell;
    }

    private PdfPTable createSiteTable(SiteInformation siteInfo) throws BadElementException, IOException {
        PdfPTable siteData = new PdfPTable(2);
        siteData.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        siteData.setWidthPercentage(100);
        siteData.getDefaultCell().setPaddingBottom(5);
        siteData.setHorizontalAlignment(Element.ALIGN_CENTER);
        List<String> teamMembers = siteInfo.getTeamMembers();
        siteData.addCell(pdfBold("COMPANY NAME:"));
        siteData.addCell(pdfBold("DATE CONDUCTED:"));
        siteData.addCell(siteInfo.getCompanyName());
        siteData.addCell(siteInfo.getDateConducted());
        siteData.addCell(pdfBold("PROJECT NAME:"));
        siteData.addCell(pdfBold("TIME:"));
        siteData.addCell(siteInfo.getProjectName());
        siteData.addCell(siteInfo.getTime());
        siteData.addCell(pdfBold("OWNER/LESSOR:"));
        siteData.addCell(pdfBold("TEAM LEADER:"));
        siteData.addCell(siteInfo.getOwnerLessor());
        siteData.addCell(siteInfo.getTeamLeader());
        siteData.addCell(pdfBold("LOCATION:"));
        siteData.addCell(pdfBold("TEAM MEMBERS:"));
        siteData.addCell(siteInfo.getLocation());

        for(int i = 0; i < teamMembers.size(); i++){
            if(i == 0)
                siteData.addCell(teamMembers.get(i));
            else {
                siteData.addCell("");
                siteData.addCell(teamMembers.get(i));
            }

        }
        siteData.addCell(pdfBold("WEATHER:"));
        siteData.addCell(pdfBold("MATERIAL CLASSIFICATION:"));
        siteData.addCell(siteInfo.getWeather());
        siteData.addCell(siteInfo.getMaterialClassification());

        return siteData;
    }

    private PdfPCell  pdfBold(String string) {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(string, boldFont));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
    public void onPdfPressed(View view) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, generatePDFFilename());
        startActivityForResult(intent, CREATE_PDF_REQUEST_CODE);
    }
    private String generatePDFFilename() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());
        return currentDate + "_" + currentTime + "_SP.pdf";
    }


}
