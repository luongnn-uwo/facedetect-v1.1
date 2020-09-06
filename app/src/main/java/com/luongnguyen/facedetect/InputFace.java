package com.luongnguyen.facedetect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.luongnguyen.facedetect.MainActivity.myClassifier;
import static com.luongnguyen.facedetect.MainActivity.myRecognition;
import static com.luongnguyen.facedetect.Methods.GALLERY_FOLDER;
import static com.luongnguyen.facedetect.TFLiteAPIModel.ImageDatabase;


public class InputFace extends AppCompatActivity implements View.OnClickListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG ="input_face" ;
    public static final int imgwidth = 400;
    public static final int imgheight = 400;

    public static final int resize_width = 112;
    public static final int resize_height = 112;

    EditText Studentname;
    Button SavephotoButton;
    ImageView CropImage;
    File cascFile;
    public static CascadeClassifier faceDetector;
    Mat mRgba, mGrey;
    JavaCameraView InputFaceView;

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGrey = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGrey.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGrey = inputFrame.gray();

        //detect face
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(mRgba,faceDetections);
        for(Rect rect:faceDetections.toArray()){
            Imgproc.rectangle(mRgba, new Point(rect.x,rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0,0,255));

            }

        return mRgba;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_face);

        SavephotoButton = findViewById(R.id.SavephotoButton);
        InputFaceView = findViewById(R.id.InputFaceView);
        Studentname = findViewById(R.id.Studentname);
        CropImage = findViewById(R.id.CropImage);

        //Ask for permission
        isExternalStoragewritable();
        isExternalStoragereadable();

        // Set onClick action
        SavephotoButton.setOnClickListener(this);

        //OpenCV Manager calling
        if(!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseCallback);

        }else{
                baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }
        InputFaceView.setCvCameraViewListener(this);





    }
    //---------------------------------------------------------------------------------------------//
    // Calling Baseloadercallback method from OPENCV to assist face detection process
    //---------------------------------------------------------------------------------------------//
    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case LoaderCallbackInterface.SUCCESS: {
                    try {
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                        File CascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        cascFile = new File(CascadeDir, "haarcascade_frontalface_alt2.xml");

                        FileOutputStream fos = new FileOutputStream(cascFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        fos.close();

                        faceDetector = new CascadeClassifier(cascFile.getAbsolutePath());
                        if (faceDetector.empty()) {
                            faceDetector = null;
                        } else {
                            CascadeDir.delete();
                        }
                        InputFaceView.enableView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:

                        super.onManagerConnected(status);

                    break;
            }

        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.SavephotoButton:
                try {
                    Takephoto(mRgba);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
    private boolean isExternalStoragereadable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            Log.i("State", "Yes, it is readable");
            return true;

        } else {
            return false;
        }
    }

    private boolean isExternalStoragewritable(){
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.i("State","Yes, it is writable");
            return true;

        }else{
            return false;
        }
    }

    //---------------------------------------------------------------------------------------------//
    //                     METHOD OF TAKING PHOTO FOR INPUT  STUDENT IMAGES
    //---------------------------------------------------------------------------------------------//

    public void Takephoto(Mat rgbaMat){
        String InputName = Studentname.getText().toString();

        File RootPath = getExternalFilesDir(null);
        File GalleryPath = new File(RootPath.getAbsolutePath() +"/"+ GALLERY_FOLDER);
        //Create gallery folder if not exists
        if(!GalleryPath.exists()){
            GalleryPath.mkdir();
            Toast.makeText(this, "Gallery was created", Toast.LENGTH_SHORT).show();
        }

        File[] PicDirArray = GalleryPath.listFiles();
        String filepath = new String();
        boolean Redundantflag = false;
        int size = 0;
        if(PicDirArray.length>0) {
            for (File dir : PicDirArray) {
                //save student image to archived folder if exists
                if (!dir.isDirectory()) {
                    dir.delete();
                } else {
                    if (dir.getName().equals(InputName)) {
                        size = dir.listFiles().length;
                        filepath = dir.getAbsolutePath() + "/" + InputName + size + ".jpg";
                        Redundantflag = true;
                        Toast.makeText(this, "Student name exists in Gallery",
                                                                        Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }

        // if cannot find student folder --> create new one
        if(!Redundantflag) {
            File newstudent = new File(GalleryPath.getAbsolutePath() + "/" + InputName);
            newstudent.mkdir();
            filepath = newstudent.getAbsolutePath() + "/" + InputName +  ".jpg";
            Toast.makeText(this, "new student found, saved to new folder", Toast.LENGTH_SHORT).show();
        }

        Mat grayMat = new Mat();
        //Converting RGBA to GRAY
        Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGBA2BGR);

        //take a crop of face from detected area
        MatOfRect detectedFaces = new MatOfRect();
        faceDetector.detectMultiScale(rgbaMat, detectedFaces);
        Rect[] facesArray = detectedFaces.toArray();
        if(facesArray.length>0){

            int maxvalue = 0;
            //take only the biggest face detected (ignore small face-like patterns)
            for (Rect rect : facesArray) {
                if(rect.width > maxvalue){
                    maxvalue =rect.width;
                }
            }


            Bitmap InputBmp = Bitmap.createBitmap(resize_width, resize_height, Bitmap.Config.ARGB_8888);
            Mat InputMat = new Mat();


            //Write image to given filepath
            for (Rect rect : facesArray) {
                if(rect.width==maxvalue) {
                    Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
                    Mat image_roi = new Mat(grayMat, rectCrop);
                    Imgproc.resize(image_roi, image_roi, new Size(imgwidth, imgheight));
                    Imgcodecs.imwrite(filepath, image_roi);

                    //Tensorflow input modify
                    Imgproc.resize(image_roi,InputMat, new Size(resize_width, resize_height));
                    Utils.matToBitmap(InputMat, InputBmp);
                }
            }
            //Start recognize inputface to collect Feature information.
            myRecognition = myClassifier.FaceRecognizer(InputBmp,true,InputName);
            Log.d(TAG, "Found myrecognition info is:"+ myRecognition);

            //Update database with output myRecognition and write down copy of Database
            ImageDatabase.put(myRecognition,InputName);
            Log.d(TAG, "successfully update ImageDatabase info ");

            Methods.WriteDatabase(ImageDatabase,this);
            Log.d(TAG, "successfully write myrecognition info to data file");

            //Show crop face as thumbnail at corner
            CropImage.setImageBitmap(BitmapFactory.decodeFile(filepath));
            Log.d(TAG, "myrecognition - above is done:");

        }else{
            Toast.makeText(this, "Cannot find any face", Toast.LENGTH_SHORT).show();
        }
    }


}





