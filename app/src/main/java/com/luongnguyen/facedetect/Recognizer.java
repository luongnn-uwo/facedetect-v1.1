package com.luongnguyen.facedetect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.luongnguyen.facedetect.InputFace.resize_height;
import static com.luongnguyen.facedetect.InputFace.resize_width;
import static com.luongnguyen.facedetect.MainActivity.myClassifier;
import static com.luongnguyen.facedetect.MainActivity.myRecognition;
import static com.luongnguyen.facedetect.Methods.AttendanceList;
import static com.luongnguyen.facedetect.Methods.GALLERY_FOLDER;
import static com.luongnguyen.facedetect.Methods.UpdateAttendance;


public class Recognizer extends AppCompatActivity implements View.OnClickListener, CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG ="Recognizer Screen" ;

    // touch info variables
    boolean isTouch = false;
    public int touch_X=0;
    public int touch_Y=0;

    String PredictedName;

    // Screen items on XML
    Button ConfirmButton;
    ImageView HeadImage;
    TextView  Recognized,RecogName,RecogID;
    JavaCameraView RecogFaceView;

    // Source variables
    File cascFile1;
    CascadeClassifier faceDetector1;
    Mat mRgba1, mGrey1;

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba1 = new Mat();
        mGrey1 = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba1.release();
        mGrey1.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba1 = inputFrame.rgba();
        mGrey1 = inputFrame.gray();
        List<String> ShowUpName = new ArrayList<String>();
        boolean batchmode = false;
        if(isTouch){
            batchmode = true;
        }

        //FACE DETECTION AND DRAW BOUNDING BOX
        MatOfRect faceDetections = new MatOfRect();
        faceDetector1.detectMultiScale(mRgba1,faceDetections);
        Rect[] facesArray = faceDetections.toArray();

        for(Rect rect:facesArray){
            Imgproc.rectangle(mRgba1, new Point(rect.x,rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0,0,255));
        }

        //FACE RECOGNIZATION PROCESS

        //Scan through all faces detected and call recognizer method
        //take a crop of face from detected area
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap InputBmp = Bitmap.createBitmap(resize_width, resize_height, conf);
        Mat InputMat = new Mat();

        //Resize image before passing to Interpreter
        for (Rect face : facesArray) {
            //Crop the face area only
            Rect rectCrop = new Rect(face.x, face.y, face.width, face.height);
            Mat image_roi = new Mat(mRgba1, rectCrop);
            Imgproc.resize(image_roi, image_roi, new Size(resize_width, resize_width));

            //and resize Facepic to fit TensorflowLite Interpreter
            Imgproc.resize(image_roi, InputMat, new Size(resize_width, resize_height));
            Utils.matToBitmap(InputMat, InputBmp);

            //Start recognizer to find name and confidence of the face
            myRecognition = myClassifier.FaceRecognizer(InputBmp, false, "?");
            float confidence = myRecognition.getDistance();

            //Show Predicted Name on screen if confidence is high (distance < 1)
            String text = "";
            PredictedName = "?";
            if (confidence < 1.0f) {
                text = String.format("%.2f", confidence);
                PredictedName = myRecognition.getLabel();
                int posX = (int) Math.max(face.tl().x - 10, 0);
                int posY = (int) Math.max(face.tl().y - 10, 0);

                //Show name of predicted student on top of Bounding Box
                Imgproc.putText(mRgba1, PredictedName + text, new Point(posX, posY),
                        Core.FONT_HERSHEY_DUPLEX, 3, new Scalar(0, 0, 255));

            }

            ShowUpName.add(PredictedName);
            Log.d("inside batch recognize", "just add " +PredictedName);
            Log.d("inside batch recognize", "showup name size: " +ShowUpName.size());
        }
        if(batchmode) {
            Log.d("inside batch recognize", "batch mode flag is now " + "true");
        } else {
            Log.d("inside batch recognize","batch mode flag is now "+"false");
        }
        if(batchmode) {
            for (String name : ShowUpName) {
                UpdateAttendance(name, this);
            }
        }

        isTouch = false;
            return mRgba1;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognizer);

        ConfirmButton = findViewById(R.id.ConfirmButton);
        HeadImage =findViewById(R.id.HeadImage);
        Recognized =findViewById(R.id.Recognized);
        RecogName = findViewById(R.id.RecogName);
        RecogID = findViewById(R.id.RecogID);
        RecogFaceView = findViewById(R.id.RecogFaceView);

        // Set onClick action
        ConfirmButton.setOnClickListener(this);

        //calling OpenCV Manager
        if(!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseCallback);

        }else{
            baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        RecogFaceView.setCvCameraViewListener(this);
        //Check archived database and announce
        if (TFLiteAPIModel.ImageDatabase.size()==0) {
            Toast.makeText(this, "No ImageDatabase found, please input faces ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ConfirmButton:
                try {
                    if (RecogName.getText().toString()!=""){
                        //Update attendance list
                        Log.d("Recognizer :","Calling Update Attendance method for name:"+RecogName.getText().toString());
                        UpdateAttendance(RecogName.getText().toString(),this);
                        // Write Attendance to CSV file
                        Methods.WriteCSV(AttendanceList,this,Methods.ATTENDANCELIST_FILE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
        public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                //Get coordinates of touch point
                touch_X = (int) event.getX();
                touch_Y = (int) event.getY();
                //Reset head image at corner
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.headimage);
                HeadImage.setImageBitmap(bitmap);
                //Set touchflag
                isTouch = true;
                break;

        }
        return true;
    }

    private void UpdateUI(final String name,Context context){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecogName.setText(name);
                Recognized.setText("recognized");
                for(StudentInfo info:AttendanceList){
                    if(info.getName().equals(name)) {
                        RecogID.setText("ID: "+info.getID());
                    }
                }
                //Update student image at corner if found photos in Gallery
                File RootPath = context.getExternalFilesDir(null);
                File Dirpath = new File(RootPath.getAbsolutePath() +"/"+ GALLERY_FOLDER + "/"+PredictedName);
                if(Dirpath.exists()) {
                    File[] ImageArray = Dirpath.listFiles();
                    if (ImageArray.length > 0)
                        HeadImage.setImageBitmap(BitmapFactory.decodeFile(ImageArray[0].getAbsolutePath()));
                }

                Toast.makeText(context,"Click CONFIRM to update Attendance !",
                                                                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    //---------------------------------------------------------------------------------------------//
    // Method to call Baseloadercallback service from OPENCV to assist face detection process
    //---------------------------------------------------------------------------------------------//

    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch(status){
                case LoaderCallbackInterface.SUCCESS: {
                    try {
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                        File CascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        cascFile1 = new File(CascadeDir, "haarcascade_frontalface_alt2.xml");

                        FileOutputStream fos = new FileOutputStream(cascFile1);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        fos.close();

                        faceDetector1 = new CascadeClassifier(cascFile1.getAbsolutePath());
                        if (faceDetector1.empty()) {
                            faceDetector1 = null;
                        } else {
                            CascadeDir.delete();
                        }
                        RecogFaceView.enableView();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void singlerecognize(){
        //FACE DETECTION AND DRAW BOUNDING BOX
        MatOfRect faceDetections = new MatOfRect();
        faceDetector1.detectMultiScale(mRgba1,faceDetections);
        Rect[] facesArray = faceDetections.toArray();

        for(Rect rect:facesArray){
            Imgproc.rectangle(mRgba1, new Point(rect.x,rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0,0,255));
        }

        //FACE RECOGNIZATION PROCESS

        //Scan through all faces detected and call recognizer method
        //take a crop of face from detected area
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap InputBmp = Bitmap.createBitmap(resize_width, resize_height, conf);
        Mat InputMat = new Mat();

        //Resize image before passing to Interpreter
        for (Rect face : facesArray) {
            //Crop the face area only
            Rect rectCrop = new Rect(face.x, face.y, face.width, face.height);
            Mat image_roi = new Mat(mRgba1, rectCrop);
            Imgproc.resize(image_roi, image_roi, new Size(resize_width, resize_width));

            //and resize Facepic to fit TensorflowLite Interpreter
            Imgproc.resize(image_roi, InputMat, new Size(resize_width, resize_height));
            Utils.matToBitmap(InputMat, InputBmp);

            //Start recognizer to find name and confidence of the face
            myRecognition = myClassifier.FaceRecognizer(InputBmp, false, "?");
            float confidence = myRecognition.getDistance();

            //Show Predicted Name on screen if confidence is high (distance < 1)
            String text = "";
            PredictedName = "?";
            if (confidence < 1.0f) {
                text = String.format("%.2f", confidence);
                PredictedName = myRecognition.getLabel();
                int posX = (int) Math.max(face.tl().x - 10, 0);
                int posY = (int) Math.max(face.tl().y - 10, 0);

                //Show name of predicted student on top of Bounding Box
                Imgproc.putText(mRgba1, PredictedName + text, new Point(posX, posY),
                        Core.FONT_HERSHEY_DUPLEX, 3, new Scalar(0, 0, 255));

                if ((isTouch) && (face.tl().x <= touch_X)&&(touch_X<=face.br().x)&&(face.tl().y<= touch_Y)&&(touch_Y<=face.br().y)){  //

                    // Update on Textview about Student
                    UpdateUI(PredictedName,this);
                    Log.d("Touch testing:"," Correct area");
                }else if(isTouch){
                    Log.d("Touch testing:"," Wrong area");
                }
            }

        }
        isTouch = false;

    }




} //end of class
