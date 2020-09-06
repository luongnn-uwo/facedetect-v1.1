package com.luongnguyen.facedetect;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.luongnguyen.facedetect.Methods.DATABASE_FILE;
import static com.luongnguyen.facedetect.Methods.DATA_FOLDER;
import static com.luongnguyen.facedetect.Methods.Printest;
import static com.luongnguyen.facedetect.Methods.ReadCSV;
import static com.luongnguyen.facedetect.TFLiteAPIModel.ImageDatabase;
import static com.luongnguyen.facedetect.Welcome.ClassID;
import static com.luongnguyen.facedetect.Welcome.ClassPath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = ".ActivityMain";
    Button RecognizeButton, InputFaceButton, GalleryButton,AttendListButton;
    TextView ClassName;
    FloatingActionButton EmailButton;
    public static List<StudentInfo> TempList = new ArrayList<>();

    public static Classifier myClassifier;
    public static Classifier.Recognition myRecognition;
    // MobileFaceNet model information
    private static final int TF_OD_API_INPUT_SIZE = 112;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "mobile_face_net.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    public static int NumofIDs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Layout items
        RecognizeButton = findViewById(R.id.RecognizeButton);
        InputFaceButton = findViewById(R.id.InputFaceButton);
        GalleryButton = findViewById(R.id.GalleryButton);
        EmailButton = findViewById(R.id.EmailButton);
        ClassName =  findViewById(R.id.ClassName);
        AttendListButton = findViewById(R.id.AttendListButton);

        // Set onClick action
        RecognizeButton.setOnClickListener(this);
        InputFaceButton.setOnClickListener(this);
        GalleryButton.setOnClickListener(this);
        EmailButton.setOnClickListener(this);
        AttendListButton.setOnClickListener(this);

        //Set ClassID title on screen
        //ClassPath ???
        ClassName.setText("CLASS ID: "+ClassID);
        Log.d(TAG,"start initializing Classlist");
        Methods.ProcessInputCSV(this);
        Printest();

        // start loading Gallery photos
        Log.d(TAG,"start initializing Gallery with some sample pics for Demo");
        Methods.DemoGallery(this);

        // Initialize myClassifier from APIModel
        try {
            myClassifier =
                    TFLiteAPIModel.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
        } catch (final IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Cannot initialize classifier!");
            Toast.makeText(this , "Cannot initialize classifier!", Toast.LENGTH_SHORT).show();

            finish();
        }
        Log.d(TAG, "Successfully initialize classifier!");

        //Check Phone memory for existing database, create database folder if there is none
        File RootPath = this.getExternalFilesDir(null);
        File DataPath = new File(RootPath.getAbsolutePath() +"/"+ DATA_FOLDER);

        if(!DataPath.exists()){
            DataPath.mkdir();
            Log.d(TAG, "Successfully create DATA folder !");
        }

        //Check and read info from Database text file if there is any
        File FilePath = new File(DataPath.getAbsolutePath()+"/"+DATABASE_FILE);
        if((FilePath.exists())&&(NumofIDs==0)){
            Log.d(TAG , "Found database, can read from it now !");
            Methods.ReadDatabase(ImageDatabase,this);
            Log.d(TAG, "successfully read ImageDataBase file number of "+ImageDatabase.size()+"IDs stored" );
            //Set next ID instance to save into Database
            NumofIDs = ImageDatabase.size();

        }else{
            Log.d(TAG , "No data database to read from !");
        }



        Toast.makeText(this , "Found  Database of size: "+ImageDatabase.size(), Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.InputFaceButton:
                Intent InputAct = new Intent(MainActivity.this, InputFace.class);
                startActivity(InputAct);
                break;

            case R.id.RecognizeButton:
                Intent RecogAct = new Intent(MainActivity.this, Recognizer.class);
                startActivity(RecogAct);
                break;
            case R.id.GalleryButton:
                Intent ListAct = new Intent(MainActivity.this, Gallery.class);
                startActivity(ListAct);
                break;
            case R.id.EmailButton:
                Printest();
                Methods.EmailCSV(this);
                break;
            case R.id.AttendListButton:
                File filepath = this.getExternalFilesDir(null);
                FilePicker filePicker = new FilePicker(MainActivity.this,filepath);
                filePicker.setFileListener(new FilePicker.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) {
                        String filepath = file.getAbsolutePath();
                        if(filepath.contains("ClassList")){
                            TempList = ReadCSV(filepath,true);
                        }else{
                            TempList = ReadCSV(filepath,false);
                        }

                        Log.i("onlick attend","created templist successfully");
                        Log.i("Chosen File Name", file.getName());
                        Intent ViewFile = new Intent(MainActivity.this, CSVContent.class);
                        startActivity(ViewFile);

                    }
                })
                ;
                filePicker.setExtension("csv");
                filePicker.showDialog();

                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}//end of main