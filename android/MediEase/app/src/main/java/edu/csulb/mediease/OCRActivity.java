package edu.csulb.mediease;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class OCRActivity extends AppCompatActivity {

    public static final String IS_MEDICINE_PRESENT = "isMedicinePresent";
    public static final String IS_FREQUENCY_PRESENT = "isFrequencyPresent";
    public static final String MY_PREFS = "MyPrefs";
    public static final String MEDICINE_NAMES = "medicineNames";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;
    private static String output;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private TextView textViewOCR, textViewWhich;
    private Button btnDone, btnSave;
    private ArrayList<String> frequency, namesList;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        if (!preferences.contains(IS_MEDICINE_PRESENT))
            textViewWhich.setText("Capture Medicine Names");
        else textViewWhich.setText("Capture Frequency");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.imageView);
        textViewOCR = (TextView) findViewById(R.id.textViewOCR);
        textViewWhich = (TextView) findViewById(R.id.textViewWhich);
        btnDone = (Button) findViewById(R.id.buttonDone);
        btnSave = (Button) findViewById(R.id.buttonSave);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    finish();
                }
            }
            break;
        }
    }

    public void capture(View view) {
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".png", "/OCR/Pictures",
                Environment.getExternalStorageDirectory().getPath());
        CroperinoFileUtil.verifyStoragePermissions(OCRActivity.this);
        CroperinoFileUtil.setupDirectory(OCRActivity.this);
        Croperino.prepareChooser(OCRActivity.this, "Capture photo...",
                ContextCompat.getColor(OCRActivity.this, android.R.color.background_dark));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = Uri.fromFile(CroperinoFileUtil.getmFileTemp());
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setFixAspectRatio(false)
                            .start(this);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, OCRActivity.this);
                    Uri imageUri = Uri.fromFile(CroperinoFileUtil.getmFileTemp());
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setFixAspectRatio(false)
                            .start(this);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    imageView.setImageURI(resultUri);
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                resultUri);
                        if (imageBitmap != null) {
                            findViewById(R.id.buttonOCR).setEnabled(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void doOCR(View view) {

        new AsyncTask<Void, Void, OCROutput>() {

            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(OCRActivity.this, "Processing",
                        "Doing OCR...", true);
            }

            @Override
            protected OCROutput doInBackground(Void... params) {
                TessBaseAPI tessTwo = new TessBaseAPI();
                tessTwo.init(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/tesseract/", "eng");
                tessTwo.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD); //PSM_SPARSE_TEXT = 11
                tessTwo.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,
//                        "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ1234567890',.?;/-_ ");
                        "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ1234567890- ");
                tessTwo.setImage(imageBitmap);
                String recognizedText = tessTwo.getUTF8Text();
                int accuracy = tessTwo.meanConfidence();
                WriteFile.writeBitmap(tessTwo.getThresholdedImage());
                tessTwo.end();
//                recognizedText = recognizedText.replaceAll("(?m)^[ \t]*\r?\n", "");
                return new OCROutput(accuracy, recognizedText);
            }

            @Override
            protected void onPostExecute(OCROutput output) {
                progressDialog.dismiss();
                textViewOCR.setText(output.getText());
                OCRActivity.output = output.getText();
                btnSave.setEnabled(true);
            }
        }.execute();
    }

    private void processMedicineOutput(String output) {
        SharedPreferences preferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MEDICINE_NAMES, output);
        editor.apply();
    }

    private void processFreqOutput(String output) {
        SharedPreferences preferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        String names = preferences.getString(MEDICINE_NAMES, "");
        String tempnames[] = names.split("\n");
        namesList = new ArrayList<>(Arrays.asList(tempnames));
        namesList.removeAll(Arrays.asList(null, ""));

        String lines[] = output.split("\n"); //split every new line
        frequency = new ArrayList<>(Arrays.asList(lines));
        frequency.removeAll(Arrays.asList(null, ""));
    }

    public void done(View view) {
        System.out.println("medicines = " + namesList);
        System.out.println("frequency = " + frequency);

        SharedPreferences preferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(IS_FREQUENCY_PRESENT);
        editor.remove(IS_MEDICINE_PRESENT);
        editor.apply();

        MySQLiteHelper db = new MySQLiteHelper(OCRActivity.this);
        for (int i = 0; i < namesList.size(); i++) {
            Medicine medicine = new Medicine(namesList.get(i), frequency.get(i));
            db.insertData(medicine);
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void save(View view) {
        SharedPreferences preferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        if (preferences.contains(IS_MEDICINE_PRESENT)) { //2nd capture
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IS_FREQUENCY_PRESENT, true);
            editor.apply();
            processFreqOutput(output);
            btnDone.setEnabled(true);
        } else { //1st capture
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IS_MEDICINE_PRESENT, true);
            editor.apply();
            processMedicineOutput(output);
            textViewWhich.setText("Capture Frequency");
        }
    }
}
