package com.noteapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noteapp.Data.DatabaseHelper;
import com.noteapp.Model.CropingOption;
import com.noteapp.Model.NoteData;
import com.noteapp.Widgets.Constant;
import com.noteapp.Widgets.CropingOptionAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION_CODES.M;
import static com.noteapp.Widgets.Constant.darkenNoteColor;

public class NoteAddActivity extends AppCompatActivity {

    private TextInputEditText titleEditText, contentEditText;
    private TextInputLayout textTitie, textContent;
    private Button saveNote, takePhoto, chooseColor;
    private String lastUpdateDateString, creationDateString, lastTitle, lastContent, userId, noteId, getTitle, getContent, image;
    private int notePosition;
    private boolean isUpdate = false;
    View focusView = null;
    boolean cancel = false;
    private Uri picUri;
    private File pic;
    private final static int REQUEST_PERMISSION_REQ_CODE = 34;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private ImageView snap;
    private String encodedImageData;
    private DatabaseHelper databaseHelper;
    LinearLayout noteActionsLayout, bottom_bar;
    private NestedScrollView scrollView;
    RadioGroup colorPickerRadioGroup;
    private String noteColor;
    private Toolbar toolbar;
    private CardView main_card;
    private String oldColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseHelper = new DatabaseHelper(this);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        userId = prefs.getString("user_id", "");
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        getIntentData();
        init();

    }

    private void getIntentData() {
        Intent editionIntent = getIntent();
        noteId = editionIntent.getStringExtra("note_id");
        lastTitle = editionIntent.getStringExtra("title");
        lastContent = editionIntent.getStringExtra("content");
        creationDateString = editionIntent.getStringExtra("creationDate");
        oldColor = editionIntent.getStringExtra("color");
        image = editionIntent.getStringExtra("image");
        notePosition = editionIntent.getIntExtra("position", -1);
        noteColor = oldColor;
        if (noteId != null) {
            if (!noteId.equals("")) {
                isUpdate = true;
            } else {
                isUpdate = false;
            }
        }
    }

    private void init() {
        bindResources();
        saveNoteInDb();
        takePhoto();
        chooseColor();
    }


    private void bindResources() {
        scrollView = findViewById(R.id.scrollView);
        textTitie = findViewById(R.id.text_title);
        titleEditText = findViewById(R.id.title_edit_text);
        textContent = findViewById(R.id.text_content);
        contentEditText = findViewById(R.id.content_edit_text);
        saveNote = findViewById(R.id.save_note);
        takePhoto = findViewById(R.id.take_photo);
        chooseColor = findViewById(R.id.choose_color);
        snap = findViewById(R.id.snap);
        noteActionsLayout = findViewById(R.id.note_actions_layout);
        colorPickerRadioGroup = findViewById(R.id.color_picker_radio_group);
        main_card = findViewById(R.id.main_card);
        bottom_bar = findViewById(R.id.bottom_bar);


        // Set title and content if edit
        titleEditText.setText(lastTitle);
        contentEditText.setText(lastContent);

        if (image != null) {
            if (!image.equals("")) {
                snap.setVisibility(View.VISIBLE);
                snap.setImageBitmap(Constant.getBitmapFromBase64(image));
            } else {
                snap.setVisibility(View.GONE);
            }
        }


        // Get date
        if (creationDateString.isEmpty())
            creationDateString = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date());
        lastUpdateDateString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        scrollView.setBackgroundColor(Color.parseColor(oldColor));
        noteActionsLayout.setBackgroundColor(Color.parseColor(oldColor));
        main_card.setBackgroundColor(Color.parseColor(oldColor));
        toolbar.setBackgroundColor(Color.parseColor(oldColor));
        bottom_bar.setBackgroundColor(Color.parseColor(oldColor));
        getWindow().setStatusBarColor(darkenNoteColor(Color.parseColor(oldColor), 0.7f));

    }


    private void saveNoteInDb() {
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    hideKeyBoard();
                    saveNote();

                } else {
                    focusView.requestFocus();
                }
            }
        });
    }

    private void saveNote() {
        if (isUpdate) {
            //update
            if (!titleEditText.equals(lastTitle) || !contentEditText.equals(lastContent)) {
                //  databaseHelper.upDateNoti(titleEditText.getText().toString(),contentEditText.getText().toString(), noteId);

                encodedImageData = image;
                goToListNote(true);
                //
            } else {
                finish();

            }
        } else {
//            //insert


            //    databaseHelper.addNote(noteData);
            goToListNote(false);

        }

    }

    private void goToListNote(boolean updateSet) {

        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        NoteData noteData = new NoteData(userId
                , titleEditText.getText().toString()
                , contentEditText.getText().toString()
                , lastUpdateDateString
                , creationDateString
                , encodedImageData
                , noteColor);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("noteJSON", gson.toJson(noteData));
        resultIntent.putExtra("notePosition", notePosition);
        resultIntent.putExtra("update", updateSet);
        resultIntent.putExtra("noteId", noteId);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }


    private void takePhoto() {
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= M) {
                    CheckForPermission();
                } else {
                    selectImageOption();
                }
            }
        });
    }


    private static final int PERMISSION_REQUEST_CODE = 1;

    private void CheckForPermission() {
        if (checkPermission()) {
            selectImageOption();

        } else {
            ActivityCompat.requestPermissions(NoteAddActivity.this, new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageOption();

                }

                break;
        }
    }


    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(NoteAddActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {


                    if (Build.VERSION.SDK_INT > M) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {
//
                            ContentValues values = new ContentValues(1);
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                            mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            startActivityForResult(intent, CAMERA_CODE);

                        } else {
                            Toast.makeText(NoteAddActivity.this, "NO CAMERA", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                        mImageCaptureUri = Uri.fromFile(f);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(intent, CAMERA_CODE);
                    }

                } else if (items[item].equals("Choose from Gallery")) {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            mImageCaptureUri = data.getData();

            CropingIMG();

        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {


            CropingIMG();
        } else if (requestCode == CROPING_CODE) {

            try {
                if (outPutFile.exists()) {
                    Bitmap photo = decodeFile(outPutFile);
                    if (photo != null) {
                        encodedImageData = Constant.getEncoded64ImageStringFromBitmap(photo);
                        snap.setVisibility(View.VISIBLE);
                        snap.setImageBitmap(photo);
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Configuration overrideConfiguration = new Configuration();
        overrideConfiguration.orientation = Configuration.ORIENTATION_PORTRAIT;
        this.createConfigurationContext(overrideConfiguration);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = (ResolveInfo) list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(this, cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(NoteAddActivity.this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                if (alert != null) {
                    alert.show();
                }
            }
        }
    }


    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }


//    public Bitmap CompressResizeImage(Bitmap bm) {
//        int bmWidth = bm.getWidth();
//        int bmHeight = bm.getHeight();
//        int ivWidth = propik.getWidth();
//        // int ivHeight = propik.getHeight();
//
//
//        int new_height = (int) Math.floor((double) bmHeight * ((double) ivWidth / (double) bmWidth));
//        Bitmap newbitMap = Bitmap.createScaledBitmap(bm, ivWidth, new_height, true);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        newbitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//
//
//        byte[] b = baos.toByteArray();
//
//
//        return BitmapFactory.decodeByteArray(b, 0, b.length);
//    }


    public void getLoginData() {
        textTitie.setError(null);
        textContent.setError(null);

        getTitle = titleEditText.getText().toString().trim();
        getContent = contentEditText.getText().toString().trim();

    }

    private boolean validate() {


        getLoginData();
        if (TextUtils.isEmpty(getTitle)) {
            textTitie.setError(getString(R.string.enter_title));
            focusView = titleEditText;
            cancel = true;
            return false;
        } else if (TextUtils.isEmpty(getContent)) {
            textContent.setError(getString(R.string.enter_content));
            focusView = contentEditText;
            cancel = true;
            return false;
        } else {
            return true;
        }
    }


    private void hideKeyBoard() {
        try {
            if (getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }



    private void chooseColor() {
        chooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteActionsLayout.getVisibility() == View.GONE) {
                    noteActionsLayout.setVisibility(View.VISIBLE);
                    ///noteActionsButton.setBackgroundColor(darkenNoteColor(Color.parseColor(noteColor), 0.9f));
                } else if (noteActionsLayout.getVisibility() == View.VISIBLE) {
                    // noteActionsButton.setBackgroundColor(Color.parseColor(noteColor));
                    noteActionsLayout.setVisibility(View.GONE);
                }


            }
        });

        // Check the color picker
        colorPickerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.default_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteDefault);

                } else if (checkedId == R.id.red_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteRed);
                } else if (checkedId == R.id.orange_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteOrange);
                } else if (checkedId == R.id.yellow_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteYellow);
                } else if (checkedId == R.id.green_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteGreen);
                } else if (checkedId == R.id.cyan_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteCyan);
                } else if (checkedId == R.id.light_blue_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteLightBlue);
                } else if (checkedId == R.id.dark_blue_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteDarkBlue);
                } else if (checkedId == R.id.purple_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNotePurple);
                } else if (checkedId == R.id.pink_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNotePink);
                } else if (checkedId == R.id.brown_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteBrow);
                } else if (checkedId == R.id.grey_color_checkbox) {
                    noteColor = getResources().getString(R.color.colorNoteGrey);
                }
                scrollView.setBackgroundColor(Color.parseColor(noteColor));
                noteActionsLayout.setBackgroundColor(Color.parseColor(noteColor));
                main_card.setBackgroundColor(Color.parseColor(noteColor));
                toolbar.setBackgroundColor(Color.parseColor(noteColor));
                bottom_bar.setBackgroundColor(Color.parseColor(noteColor));
                getWindow().setStatusBarColor(darkenNoteColor(Color.parseColor(noteColor), 0.7f));


            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            closeActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    private void closeActivity() {

        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }
}
