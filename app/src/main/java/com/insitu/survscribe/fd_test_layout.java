package com.insitu.survscribe;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.survscribe.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fd_test_layout extends AppCompatActivity implements RowDataClickListener {
    private static final int CREATE_PDF_REQUEST_CODE = 2;

    private RecyclerView testDataTable;
    private static FD_Adapter adapter;
    List<FD_Data> FDDataList;
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
    private TextView remarks;
    private boolean dataChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_test_layout);

        teamMembers = new ArrayList<>();

        ConstraintLayout siteInfoLayout = findViewById(R.id.soilTest_siteInfo);

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Field Density Test");

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
        siteInfo = new SiteInformation(
                toolbarTitle.getText().toString(),
                "", "", "", "", "", "", "", teamMembers, "", "");


        dateTimePicker = new DateTimePicker(this, dateConductedEditText, timeEditText);

        ConstraintLayout add_data_button = findViewById(R.id.add_data_button);
        add_data_button.setOnClickListener(view -> {
            showAddDataDialog();
        });

        teamMember.setOnClickListener(view -> {
            showManageMembersDialog();
        });
        testDataTable = findViewById(R.id.testDataTable);
        testDataTable.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        FDDataList = getData();


        adapter = new FD_Adapter(FDDataList,this);
        loadXmlData();
        testDataTable.setAdapter(adapter);
        ConstraintLayout parentLayout = findViewById(R.id.test_activity);
        try {
            MaterialButton bottomButton = new MaterialButton(this);
            bottomButton.setText("Change Remarks");
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.topToBottom = R.id.soilTest_siteInfo;
            layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.bottomToTop = R.id.testDataTable;
            layoutParams.leftMargin = 38;
            int text_size = getResources().getDimensionPixelSize(R.dimen.remarks_button);
            bottomButton.setTextSize(text_size);
            bottomButton.setTextColor(Color.WHITE);
            bottomButton.setBackgroundColor(Color.parseColor("#1B1D24"));
            bottomButton.setPadding(2,2,2,2);
            bottomButton.setLayoutParams(layoutParams);
            bottomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRemarksClick();
                }
            });
            parentLayout.addView(bottomButton);
        }catch (Exception e) {
            Log.e("Tag", String.valueOf(e));
        }

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



    private void showAddDataDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.form_fd_data_layout, null);
        EditText timeStartedInput = dialogView.findViewById(R.id.timeStartedInput);
        EditText timeFinishedInput = dialogView.findViewById(R.id.timeFinishedInput);
        EditText soilTypesInput = dialogView.findViewById(R.id.soilTypesInput);
        EditText massDrySoilInput = dialogView.findViewById(R.id.massDrySoilInput);
        EditText initialMassInput = dialogView.findViewById(R.id.initialMassInput);
        EditText finalMassInput = dialogView.findViewById(R.id.finalMassInput);

        // Input Validation

        DateTimePicker dateTimePicker1 = new DateTimePicker(this, null, timeStartedInput);
        DateTimePicker dateTimePicker2 = new DateTimePicker(this, null, timeFinishedInput);

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {

                    String timeStarted = timeStartedInput.getText().toString();
                    String timeFinished = timeFinishedInput.getText().toString();
                    String soilTypes = soilTypesInput.getText().toString();
                    String massDrySoil = massDrySoilInput.getText().toString();
                    String initialMass = initialMassInput.getText().toString();
                    String finalMass = finalMassInput.getText().toString();
                    if (initialMass.isEmpty() || finalMass.isEmpty()) {
                        Toast.makeText(this, "Initial Mass and Finals Mass can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Double massOfSandFillTheHole = Double.parseDouble(initialMass) - Double.parseDouble(finalMass);
                    boolean dataAdded = updateNextBlankRow(timeStarted, timeFinished, soilTypes, massDrySoil, initialMass, finalMass);

                    if (!dataAdded) {
                        int newRowIndex = FDDataList.size();  // Get index of the newly added row
                        FDDataList.add(new FD_Data(timeStarted, timeFinished, soilTypes, massDrySoil, initialMass, finalMass, String.valueOf(massOfSandFillTheHole)));
                        adapter.notifyItemInserted(newRowIndex);
                        dataChanged = true;
                    }
                    adapter.notifyDataSetChanged();

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private boolean updateNextBlankRow(String timeStarted,
                                       String timeFinished,
                                       String soilTypes,
                                       String massOfDrySoilFromHole,
                                       String initialMassOfSandAndJar,
                                       String finalMassOfSandAndJar
                                       ) {
        for (int i = 1; i < FDDataList.size(); i++) {
            FD_Data row = FDDataList.get(i);
            boolean checkEmpty = row.getMassOfDrySoilFromHole().isEmpty() &&
                    row.getSoilTypes().isEmpty() &&
                    row.getInitialMassOfSandAndJar().isEmpty() &&
                    row.getFinalMassOfSandAndJar().isEmpty() &&
                    row.getTimeStarted().isEmpty() &&
                    row.getTimeFinished().isEmpty();
            if (checkEmpty) {
                row.setTimeStarted(timeStarted);
                row.setTimeFinished(timeFinished);
                row.setSoilTypes(soilTypes);
                row.setMassOfDrySoilFromHole(massOfDrySoilFromHole);
                row.setInitialMassOfSandAndJar(initialMassOfSandAndJar);
                row.setFinalMassOfSandAndJar(finalMassOfSandAndJar);
                Double massOfSandFillTheHole = Double.parseDouble(row.getInitialMassOfSandAndJar()) - Double.parseDouble(row.getFinalMassOfSandAndJar());
                row.setMassOfSandFillTheHole(String.valueOf(massOfSandFillTheHole));
                adapter.notifyItemChanged(i); // Update specific row
                dataChanged = true;
                return true;
            }
        }
        return false; // No blank row found

    }


    // Helper method to get your data (modify as needed)
    private List<FD_Data> getData() {
        List<FD_Data> dataList = new ArrayList<>();

        // Add sample FD_Data items:
        dataList.add(new FD_Data("\t\t\tTime Started", "\t\t\tTime Finished", "Soil Types/Soil Condition", "Mass of Dry Soil from Hole", "Initial Mass of Sand and Jar", "Final Mass of Sand and Jar", "Mass of Sand Fill the Hole"));
        int numBlankRows = 4;
        for (int i = 0; i < numBlankRows; i++) {
            dataList.add(new FD_Data("", "", "", "", "", "", ""));
        }
        return dataList;
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
                xstream.allowTypes(new Class[] {FDTest.class, SiteInformation.class, FD_Data.class}); // Add all classes that need to be deserialized
                xstream.alias("SiteInformation", SiteInformation.class);
                xstream.alias("FD_Data", FD_Data.class);
                xstream.alias("FDTest", FDTest.class);

                FDTest fdTest = (FDTest) xstream.fromXML(fileIn);

                // Update your UI elements using the data from dcpTest
                runOnUiThread(() -> updateUi(fdTest));


                fileIn.close();
            } catch (Exception e) {
                // Handle exceptions (file not found, parsing errors)
                e.printStackTrace();
                // Show a message to the user indicating the error
            }
        }
    }

    private void updateUi(FDTest fdTest) {
        SiteInformation siteInfo = fdTest.getSiteInfo();
        List<FD_Data> FDData = fdTest.getFdDataList();

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

        this.FDDataList.clear(); // Clear existing data
        this.FDDataList.addAll(FDData); // Add new data
        adapter.notifyDataSetChanged(); // Notify adapter of changes
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

    private void showEditDataDialog(int position, FD_Data fdData) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.form_fd_data_layout, null); // Reuse your FD data input layout

        EditText timeStartedInput = dialogView.findViewById(R.id.timeStartedInput);
        EditText timeFinishedInput = dialogView.findViewById(R.id.timeFinishedInput);
        EditText soilTypesInput = dialogView.findViewById(R.id.soilTypesInput);
        EditText massDrySoilInput = dialogView.findViewById(R.id.massDrySoilInput);
        EditText initialMassInput = dialogView.findViewById(R.id.initialMassInput);
        EditText finalMassInput = dialogView.findViewById(R.id.finalMassInput);

        // Pre-fill
        timeStartedInput.setText(fdData.getTimeStarted());
        timeFinishedInput.setText(fdData.getTimeFinished());
        soilTypesInput.setText(fdData.getSoilTypes());
        massDrySoilInput.setText(fdData.getMassOfDrySoilFromHole());
        initialMassInput.setText(fdData.getInitialMassOfSandAndJar());
        finalMassInput.setText(fdData.getFinalMassOfSandAndJar());

        DateTimePicker dateTimePicker1 = new DateTimePicker(this, null, timeStartedInput);
        DateTimePicker dateTimePicker2 = new DateTimePicker(this, null, timeFinishedInput);

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String timeStarted = timeStartedInput.getText().toString();
                    String timeFinished = timeFinishedInput.getText().toString();
                    String soilType = soilTypesInput.getText().toString();
                    String massDrySoil = massDrySoilInput.getText().toString();
                    String initialMass = initialMassInput.getText().toString();
                    String finalMass = finalMassInput.getText().toString();
                    dataChanged = true;
                    adapter.updateRow(position, timeStarted, timeFinished, soilType, massDrySoil, initialMass, finalMass);
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
    private void saveDataToXml(String filename) {
        try {
            siteInfo = new SiteInformation(
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
            FDTest wrapper = new FDTest();
            wrapper.setSiteInfo(siteInfo);
            wrapper.setFDDataList(FDDataList);

            XStream xstream = new XStream();
            xstream.alias("FDTest", FDTest.class);

            File file = new File(getFilesDir(), filename);
            FileOutputStream fileOut = new FileOutputStream(file);

            xstream.toXML(wrapper, fileOut);

            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed(View view) {
        if (dataChanged) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRowDataClick(int position, Object fdData) {
        showEditDataDialog(position, (FD_Data) fdData);
    }

    private void savePdfToUri(Uri pdfUri) {
        try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(pdfUri, "w");
             FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor())) {

            Document document = new Document();
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            LineSeparator separator = new LineSeparator();
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph title = new Paragraph("FIELD DENSITY TEST", boldFont);
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
            document.add(createTestTable(this.FDDataList));

            document.close();
            fileOutputStream.close();

            // Show a toast message to indicate successful file creation
            Toast.makeText(this, "Test report saved to Downloads.", Toast.LENGTH_SHORT).show();
        } catch (IOException | DocumentException e) {
            Log.e("PDF Saving", "Error saving PDF", e);
            Toast.makeText(this, "Unable to save PDF file. Try to save the file first.", Toast.LENGTH_SHORT).show();
        }
    }


    private PdfPTable createTestTable(List<FD_Data> fdDataList) throws BadElementException, IOException {
        PdfPTable testCell = new PdfPTable(fdDataList.size());
        testCell.setWidthPercentage(100);
        testCell.setHeaderRows(1);
        testCell.getDefaultCell().setPaddingBottom(3);
        testCell.getDefaultCell().setBorder(Rectangle.BOX);
        testCell.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        testCell.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        for (int i = 0; i < fdDataList.size(); i++) {

            if(i == 0)
                testCell.addCell("");
            else
                testCell.addCell(String.valueOf(i));
        }

        for (int i = 0; i < fdDataList.size(); i++) {
            FD_Data fdData = fdDataList.get(i);
            testCell.addCell(fdData.getTimeStarted());
        }
        for (int i = 0; i < fdDataList.size(); i++) {
            FD_Data fdData = fdDataList.get(i);
            testCell.addCell(fdData.getTimeFinished());
        }
        for (int i = 0; i < fdDataList.size(); i++) {
            FD_Data fdData = fdDataList.get(i);
            testCell.addCell(fdData.getSoilTypes());
        }

        for (int i = 0; i < fdDataList.size(); i++) {
            FD_Data fdData = fdDataList.get(i);
            testCell.addCell(fdData.getMassOfDrySoilFromHole());
        }
        for (int i = 0; i < fdDataList.size(); i++) {
            FD_Data fdData = fdDataList.get(i);
            testCell.addCell(fdData.getInitialMassOfSandAndJar());
        }
        for (int i = 0; i < fdDataList.size(); i++) {
            FD_Data fdData = fdDataList.get(i);
            testCell.addCell(fdData.getFinalMassOfSandAndJar());
        }
        for (int i = 0; i < fdDataList.size(); i++) {
            FD_Data fdData = fdDataList.get(i);
            testCell.addCell(fdData.getMassOfSandFillTheHole());
        }
        testCell.addCell("Remarks");
        FD_Data fdData = fdDataList.get(1);
        PdfPCell remarksCell = new PdfPCell(new Phrase(fdData.getRemarks()));
        remarksCell.setColspan(fdDataList.size() - 1);
        remarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        testCell.addCell(remarksCell);

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
        return currentDate + "_" + currentTime + "_FD.pdf";
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
    public void onRemarksClick() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.form_fd_remarks, null);
        EditText remarksInput = dialogView.findViewById(R.id.remarksEditText);

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String remarks = remarksInput.getText().toString();
                    adapter.updateRemarks(1, remarks);
                    adapter.notifyDataSetChanged();

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
