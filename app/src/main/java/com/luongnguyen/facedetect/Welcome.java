package com.luongnguyen.facedetect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Welcome extends AppCompatActivity implements OnClickListener{
    Button ChooseFileButton,OK;
    TextView ClassName, FoundClass;
    public static String ClassID="ClassID";
    private boolean GotPermission;
    public static String ClassPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int PermissionCam = ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission.CAMERA);
        int PermissionWrite = ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int PermissionRead = ContextCompat.checkSelfPermission(Welcome.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        ClassName = findViewById(R.id.ClassName);
        FoundClass = findViewById(R.id.FoundClass);
        ChooseFileButton =  findViewById(R.id.ChooseFileButton);
        OK = findViewById(R.id.OK);

        ChooseFileButton.setOnClickListener(Welcome.this);
        OK.setOnClickListener(Welcome.this);

        // Request permission if not granted
        GotPermission = PermissionCam == PackageManager.PERMISSION_GRANTED
                && PermissionWrite == PackageManager.PERMISSION_GRANTED
                && PermissionRead == PackageManager.PERMISSION_GRANTED;
        if (!GotPermission) {
            requestPermission();
        }

        //copy template from resource to Phone memory for Demo purpose
        InputStream in = getResources().openRawResource(R.raw.classlist);
        File rootpath = getExternalFilesDir(null);
        String filepath = rootpath.getAbsolutePath()+"/ClassList-ECE0000.csv";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filepath);

            byte[] buff = new byte[1024];
            int read = 0;

            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }

        in.close();
        out.close();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
                e.printStackTrace();}
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ChooseFileButton:
                String filepath = this.getExternalFilesDir(null).getAbsolutePath();
                Log.d("choose file","file path is"+filepath);
                File rootfolder =new File(filepath);
                FilePicker filePicker = new FilePicker(Welcome.this,rootfolder);

                filePicker.setFileListener(new FilePicker.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) {
                        String filename = file.getName();
                        ClassPath = file.getAbsolutePath();
                        ClassID = filename.replaceAll("ClassList-","");
                        ClassID = ClassID.replaceAll(".csv","");
                        Log.i("File Name", filename);
                        Toast.makeText(Welcome.this, "Class ID: " + ClassID + " was found. Click OK to continue", Toast.LENGTH_SHORT).show();
                        ClassName.setText(ClassID);
                        FoundClass.setText("was found !");
                    }
                });

                //filePicker.setExtension("csv");
                filePicker.showDialog();
                break;

            case R.id.OK:
                Intent Main = new Intent(Welcome.this,MainActivity.class);
                startActivity(Main);
                break;

        }
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(Welcome.this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Integer> perms = new HashMap<>();
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_DENIED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_DENIED);
        perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_DENIED);
        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
        }
        if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            GotPermission = true;
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(Welcome.this, Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(Welcome.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(Welcome.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(Welcome.this)
                        .setMessage("Please grant Camera and Storage permission for this application")
                        .setPositiveButton("Dismiss all", null)
                        .show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
