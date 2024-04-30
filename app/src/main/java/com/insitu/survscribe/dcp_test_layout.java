package com.insitu.survscribe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.survscribe.R;
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
import com.squareup.picasso.Picasso;
import com.thoughtworks.xstream.XStream;

public class dcp_test_layout extends AppCompatActivity implements RowDataClickListener {
    private static final int CREATE_PDF_REQUEST_CODE = 2;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private View soilImageView;
    RecyclerView testDataTable;
    DCP_Adapter adapter;
    List<DCP_RowData> DCPRowDataList;
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

    private boolean dataChanged = false;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_test_layout);

        teamMembers = new ArrayList<>();

        ConstraintLayout siteInfoLayout = findViewById(R.id.soilTest_siteInfo);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        processSelectedImage(selectedImageUri);
                        previewImage(soilImageView);
                    }
                });

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Dynamic Cone Penetration Test");

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
        siteInfo = new SiteInformation("",
                toolbarTitle.getText().toString(),
                "", "", "", "", "", "", "", teamMembers, "", "");


        testDataTable = findViewById(R.id.testDataTable);
        testDataTable.setLayoutManager(new LinearLayoutManager(this));

        DCPRowDataList = prepareData();
        adapter = new DCP_Adapter(DCPRowDataList, this);
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
    private void loadXmlData() {
        // Get the filename from the Intent
        String filename = getIntent().getStringExtra("filename");
        if (filename != null) {
            try {
                File file = new File(getFilesDir(), filename);
                FileInputStream fileIn = new FileInputStream(file);

                XStream xstream = new XStream();
                XStream.setupDefaultSecurity(xstream); // Allow some potentially less secure operations
                xstream.allowTypes(new Class[] {DCPTest.class, SiteInformation.class, DCP_RowData.class}); // Add all classes that need to be deserialized
                xstream.alias("DCPTest", DCPTest.class);
                xstream.alias("SiteInformation", SiteInformation.class);
                xstream.alias("RowData", DCP_RowData.class);
                xstream.alias("DCPTest", DCPTest.class);

                DCPTest dcpTest = (DCPTest) xstream.fromXML(fileIn);

                // Update your UI elements using the data from dcpTest
                runOnUiThread(() -> updateUiFromDcpTest(dcpTest));


                fileIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUiFromDcpTest(DCPTest dcpTest) {
        siteInfo = dcpTest.getSiteInfo();
        List<DCP_RowData> DCPRowDataList = dcpTest.getRowDataList();

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
        this.DCPRowDataList.clear();
        this.DCPRowDataList.addAll(DCPRowDataList);
        adapter.notifyDataSetChanged();
    }


    private List<DCP_RowData> prepareData() {
        List<DCP_RowData> DCPRowDataList = new ArrayList<>();

        DCPRowDataList.add(new DCP_RowData(true, "NO. OF BLOWS", "DEPTH OF PENETRATION\n(MM)", "DIFFERENCE OF\nPENETRATION BETWEEN\nREADING"));


        // Add blank data rows
        int numBlankRows = 13;
        for (int i = 0; i < numBlankRows; i++) {
            DCPRowDataList.add(new DCP_RowData(false, "", "", ""));
        }

        return DCPRowDataList;
    }

    private void showAddDataDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.form_dcp_data_layout, null);

        EditText blowsInput = dialogView.findViewById(R.id.blowsInput);
        EditText penetrationInput = dialogView.findViewById(R.id.penetrationInput);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(v -> {
            String blows = blowsInput.getText().toString();
            String penetration = penetrationInput.getText().toString();

            if (blows.isEmpty() || penetration.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // Find the next blank row and update it
            boolean dataAdded = updateNextBlankRow(blows, penetration);

            if (!dataAdded) {
                int newRowIndex = DCPRowDataList.size();  // Get index of the newly added row
                DCPRowDataList.add(new DCP_RowData(false, blows, penetration, ""));
                calculateDifference();
                adapter.notifyItemInserted(newRowIndex);
                dataChanged = true;
            }

            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private boolean updateNextBlankRow(String blows, String penetration) {
        for (int i = 1; i < DCPRowDataList.size(); i++) {
            DCP_RowData row = DCPRowDataList.get(i);
            if (row.isHeader) continue;

            if (row.getBlowsValue().isEmpty() && row.getPenetrationValue().isEmpty()) {
                row.setBlowsValue(blows);
                row.setPenetrationValue(penetration);
                calculateDifference(); // Calculate difference
                adapter.notifyItemChanged(i); // Update specific row
                dataChanged = true;
                return true;
            }
        }
        return false; // No blank row found

    }

    private void calculateDifference() {

        for (int i = 1; i < DCPRowDataList.size(); i++) {
            DCP_RowData currentRow = DCPRowDataList.get(i);
                DCP_RowData previousRow = DCPRowDataList.get(i - 1);
            if(i == 1) {
                currentRow.setDifferenceValue(String.valueOf(Double.parseDouble(currentRow.getPenetrationValue())));
            } else {
                try {
                    double currentPenetration = Double.parseDouble(currentRow.getPenetrationValue());
                    double previousPenetration = Double.parseDouble(previousRow.getPenetrationValue());
                    double difference = currentPenetration - previousPenetration;
                    currentRow.setDifferenceValue(String.valueOf(difference));
                }catch (Exception e) {
                    currentRow.setDifferenceValue("");
                }

            }
            adapter.notifyItemChanged(i);
        }
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
            teamMembers.clear();
            teamMembers.addAll(tempTeamMembers);
            dataChanged = true;
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
    private void showEditDataDialog(int position, DCP_RowData DCPRowData) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.form_dcp_data_layout, null);

        EditText blowsInput = dialogView.findViewById(R.id.blowsInput);
        EditText penetrationInput = dialogView.findViewById(R.id.penetrationInput);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);


        blowsInput.setText(DCPRowData.getBlowsValue());
        penetrationInput.setText(DCPRowData.getPenetrationValue());

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        saveButton.setOnClickListener(v -> {
            String blows = blowsInput.getText().toString();
            String penetration = penetrationInput.getText().toString();

            // Update rowData in the list
            adapter.updateRow(position, blows, penetration);

            calculateDifference();

            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
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
                filename += ".xml";
                File file = new File(getFilesDir(), filename);
                if (file.exists()) {
                    showValidationErrorToast("A file with this name already exists.");
                    return;
                }
                saveDataToXml(filename);
                dialog.dismiss();
                super.onBackPressed();
            }

        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
    private void saveDataToXml(String filename) {
        try {
            siteInfo = new SiteInformation(
                    siteInfo.getSoilImage(),
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
            DCPTest wrapper = new DCPTest();
            wrapper.setSiteInfo(siteInfo);
            wrapper.setRowDataList(DCPRowDataList);

            XStream xstream = new XStream();
            xstream.alias("DCPTest", DCPTest.class);

            File file = new File(getFilesDir(), filename);
            FileOutputStream fileOut = new FileOutputStream(file);

            xstream.toXML(wrapper, fileOut);

            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showValidationErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed(View view) {
        if (dataChanged) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRowDataClick(int position, Object rowData) {
        showEditDataDialog(position, (DCP_RowData) rowData);
    }

    private void savePdfToUri(Uri pdfUri) {
        try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(pdfUri, "w");
             FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor())) {

            Document document = new Document();
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            LineSeparator separator = new LineSeparator();
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph title = new Paragraph("DYNAMIC CONE PENETRATION TEST", boldFont);
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
            document.add(createTestTable(this.DCPRowDataList));
            Paragraph pict = new Paragraph("Picture: ", boldFont);
            pict.setSpacingAfter(12);
            if (this.siteInfo.getSoilImage() != null) {
                document.add(pict);
                Image image = Image.getInstance(this.siteInfo.getSoilImage());
                image.scaleToFit(300, 200);
                image.setAlignment(Element.ALIGN_CENTER);
                document.add(image);
            }
            document.close();
            fileOutputStream.close();

            // Show a toast message to indicate successful file creation
            Toast.makeText(this, "Test report saved to Downloads.", Toast.LENGTH_SHORT).show();
        } catch (IOException | DocumentException e) {
            Log.e("PDF Saving", "Error saving PDF", e);
            Toast.makeText(this, "Unable to save PDF file. Try to save the file first.", Toast.LENGTH_SHORT).show();
        }
    }


    private PdfPTable createTestTable(List<DCP_RowData> dcpDataList) throws BadElementException, IOException {
        PdfPTable testCell = new PdfPTable(3);
        testCell.setWidthPercentage(100);
        testCell.setHeaderRows(1);
        testCell.getDefaultCell().setPaddingBottom(3);
        testCell.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        testCell.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        testCell.getDefaultCell().setBorder(Rectangle.BOX);
        // Add data rows based on SPDataList
        for (int i = 0; i < dcpDataList.size(); i++) {
            DCP_RowData dcpData = dcpDataList.get(i);
            testCell.addCell(dcpData.getBlowsValue());
            testCell.addCell(dcpData.getPenetrationValue());
            testCell.addCell(dcpData.getDifferenceValue());
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

    private PdfPCell pdfBold(String string) {
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
        return currentDate + "_" + currentTime + "_DCP.pdf";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_PDF_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri pdfUri = null;
            if (data != null) {
                pdfUri = data.getData();
                savePdfToUri(pdfUri);  // Implement this function
            }
        }
    }

    public void onSoilImagePressed(View view){
        showSoilImage();
    }

    private void showSoilImage() {
        // Inflate the dialog layout
        soilImageView = LayoutInflater.from(this).inflate(R.layout.form_soil_image, null);
        ImageView imageView = soilImageView.findViewById(R.id.imagePreview);
        Button selectImageButton = soilImageView.findViewById(R.id.selectImageButton);

        previewImage(soilImageView);
        selectImageButton.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);

        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(soilImageView)
                .setTitle("Select Soil Image")
                .create();
        dialog.show();
    }

    private void processSelectedImage(Uri selectedImageUri) {
        try {
            File tempImageFile = File.createTempFile("temp_image_", null, getCacheDir());
            String tempImagePath = tempImageFile.getAbsolutePath();

            // Copy and get the internal storage path
            String internalImagePath = copyTempImageToInternalStorage(selectedImageUri, tempImagePath);

            siteInfo.setSoilImage(internalImagePath); // Use internal path
            previewImage(soilImageView);
            dataChanged = true;
        } catch (IOException e) {
            showValidationErrorToast("Error copying or uploading image: " + e);
        }
    }

    private String copyTempImageToInternalStorage(Uri imageUri, String tempImagePath) throws IOException {
        // 1. Get access to internal storage
        File directory = getFilesDir();
        // 2. Generate a unique filename
        String fileName = UUID.randomUUID().toString() + ".jpg"; // Adjust extension as needed
        File file = new File(directory, fileName);

        // 3. Copy the image from Uri to internal storage (Corrected)
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();

        // 4. Delete the temporary file (Check result)
        boolean deleted = new File(tempImagePath).delete();
        if (!deleted) {
            Log.w("ImageHandling", "Failed to delete temp file: " + tempImagePath);
        }

        // 5. Return the internal storage path
        return file.getAbsolutePath();
    }

    private void previewImage(View soilImageView) {
        ImageView imageView = soilImageView.findViewById(R.id.imagePreview);
        Picasso.get()
                .load(new File(siteInfo.getSoilImage()))
                .placeholder(R.drawable.file_not_found)
                .into(imageView);
    }

}