package com.luongnguyen.facedetect;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


import static com.luongnguyen.facedetect.InputFace.resize_height;
import static com.luongnguyen.facedetect.InputFace.resize_width;
import static com.luongnguyen.facedetect.MainActivity.myClassifier;
import static com.luongnguyen.facedetect.MainActivity.myRecognition;
import static com.luongnguyen.facedetect.TFLiteAPIModel.ImageDatabase;
import static com.luongnguyen.facedetect.Welcome.ClassID;
import static com.luongnguyen.facedetect.Welcome.ClassPath;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.resize;


public class Methods extends AppCompatActivity {

    private static final String TAG = ".Methods";
    public static final String DATA_FOLDER = "DATA";
    public static  String LIST_SUBFOLDER;
    static final String GALLERY_FOLDER = "Gallery";
    private static final String EMAIL_RECIPIENT = "lnguy223@uwo.ca";
    public static String ATTENDANCELIST_FILE;

    public static final String DATABASE_FILE ="ImageDatabase.txt";
    public static List<StudentInfo> AttendanceList = new ArrayList<>();
    public static int numoffeature = 192;



    //--------------------------------------------------------------------------------------------//
    //   Method to initialize  sample photo items to Gallery if no Gallery found - For DEMO
    //--------------------------------------------------------------------------------------------//

    static public void DemoGallery(Context context) {
        java.lang.reflect.Field[] fields = R.raw.class.getFields();
        File RootPath = context.getExternalFilesDir(null);
        File GalleryPath = new File(RootPath.getAbsolutePath() + "/" + GALLERY_FOLDER);

        if (!GalleryPath.exists()) {
            GalleryPath.mkdir();

            File dir2 = new File((GalleryPath.getAbsolutePath()+"/brad"));
            dir2.mkdir();
            File dir3 = new File((GalleryPath.getAbsolutePath()+"/galgadot"));
            dir3.mkdir();
            File dir4 = new File((GalleryPath.getAbsolutePath()+"/leonardo"));
            dir4.mkdir();
            File dir5 = new File((GalleryPath.getAbsolutePath()+"/natalie"));
            dir5.mkdir();
            File dir6 = new File((GalleryPath.getAbsolutePath()+"/robert"));
            dir6.mkdir();
            File dir7 = new File((GalleryPath.getAbsolutePath()+"/scarlet"));
            dir7.mkdir();
            File dir8 = new File((GalleryPath.getAbsolutePath()+"/tom"));
            dir8.mkdir();
            File dir9 = new File((GalleryPath.getAbsolutePath()+"/tony"));
            dir9.mkdir();


            for (int count = 0; count < fields.length; count++) {
                try {
                    OutputStream out = null;
                    int resourceID = fields[count].getInt(fields[count]);
                    InputStream in = context.getResources().openRawResource(resourceID);
                    Log.d(TAG,"here is the filename " +fields[count].getName());
                    if (fields[count].getName().substring(0, fields[count].getName().length() - 1).equals("brad")) {
                        File  file = new File(RootPath + "/" + GALLERY_FOLDER + "/" + "brad" + "/" + fields[count].getName() + ".jpg");
                        out = new FileOutputStream(file);
                    } else if (fields[count].getName().substring(0, fields[count].getName().length() - 1).equals("galgadot")) {
                        File  file = new File(RootPath + "/" + GALLERY_FOLDER + "/" + "galgadot" + "/" + fields[count].getName() + ".jpg");
                        out = new FileOutputStream(file);
                    }else if (fields[count].getName().substring(0, fields[count].getName().length() - 1).equals("leonardo")) {
                        File  file = new File(RootPath + "/" + GALLERY_FOLDER + "/" + "leonardo" + "/" + fields[count].getName() + ".jpg");
                        out = new FileOutputStream(file);
                    }else if (fields[count].getName().substring(0, fields[count].getName().length() - 1).equals("natalie")) {
                        File  file = new File(RootPath + "/" + GALLERY_FOLDER + "/" + "natalie" + "/" + fields[count].getName() + ".jpg");
                        out = new FileOutputStream(file);
                    }else if (fields[count].getName().substring(0, fields[count].getName().length() - 1).equals("robert")) {
                        File  file = new File(RootPath + "/" + GALLERY_FOLDER + "/" + "robert" + "/" + fields[count].getName() + ".jpg");
                        out = new FileOutputStream(file);
                    }else if (fields[count].getName().substring(0, fields[count].getName().length() - 1).equals("scarlet")) {
                        File  file = new File(RootPath + "/" + GALLERY_FOLDER + "/" + "scarlet" + "/" + fields[count].getName() + ".jpg");
                        out = new FileOutputStream(file);
                    }else if (fields[count].getName().substring(0, fields[count].getName().length() - 1).equals("tom")) {
                        File  file = new File(RootPath + "/" + GALLERY_FOLDER + "/" + "tom" + "/" + fields[count].getName() + ".jpg");
                        out = new FileOutputStream(file);
                    }else if (fields[count].getName().substring(0, fields[count].getName().length() - 1).equals("tony")) {
                        File  file = new File(RootPath + "/" + GALLERY_FOLDER + "/" + "tony" + "/" + fields[count].getName() + ".jpg");
                        out = new FileOutputStream(file);
                    }
                    byte[] buf = new byte[2048];
                    int len;
                    if(out!=null) {
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        in.close();
                        out.close();
                    }

                }
                    catch(IllegalArgumentException e){
                        e.printStackTrace();
                    } catch(IllegalAccessException e){
                        e.printStackTrace();
                    } catch(FileNotFoundException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }

            }
        }
    }


    //--------------------------------------------------------------------------------------------//
    //           Method to read ClassList CSV file for List of students and IDs.                  //
    //           Upon finished , AttendenceList will be updated based on previous CSV file        //
    //--------------------------------------------------------------------------------------------//

    static public void ProcessInputCSV(Context context) {

        //If copy info from existing AttendanceList CSV file - FLAG = FALSE
        boolean flag = false;
        File RootPath = context.getExternalFilesDir(null);
        File folder = new File(RootPath.getAbsolutePath()+"/LIST");
        if(!folder.exists()){
            folder.mkdir();
        }
        ATTENDANCELIST_FILE = ClassID + "-AttendanceList-Date: "+getDate() +".csv";
        LIST_SUBFOLDER = "LIST/CLASS-"+ClassID +"-ATTENDANCE";
        String SubFolderPath = RootPath.getAbsolutePath() +"/"+LIST_SUBFOLDER ;
        File subfolder = new File(SubFolderPath);
        if(!subfolder.exists()){
            subfolder.mkdir();
        }

        String filepath = SubFolderPath +"/" + ATTENDANCELIST_FILE;
        Log.d(TAG,"Attendance list file path looks like:"+filepath);
        File listfile = new File(filepath);

        //If copy info from ClassList CSV file - FLAG = TRUE
        if(!listfile.exists()) {
            Log.d(TAG, "attendancelist for today not exists, copy from Classlist");
            filepath = ClassPath;//folder.getAbsolutePath()+"/CLASSLIST/"+"ClassList-"+ClassID+".csv" ;
            flag= true;
        }
        //Make AttendanceList Array a copy of ClassList.CSV or existing Attendance List.CSV

        AttendanceList = ReadCSV(filepath,flag);

        //For the fisrt time, add info to  attendance list regarding Status, Date, Class ID fields.
        int counter =0;
        for(StudentInfo info:AttendanceList){
            if(counter==0){
                //first line will be the title , not the content value
                info.setStatus("Status");
                info.setDate("Date");
                info.setClassID("Class ID");
                counter++;
            }else if(flag) {
                    //Content: Status, Date, and ClassID will be default - if copy from ClassList.csv
                    info.setStatus("absent");
                    info.setDate(getDate());
                    info.setClassID(ClassID);
                    Log.d("Update Attend.List info",ClassID +"was added");
                    }
            Log.d("ProcessInputCSV Method","Student info: "+info);
        }
        if(flag){
            //if copy from Classlist.csv, save another copy for AttendanceList.csv
            Methods.WriteCSV(AttendanceList,context,ATTENDANCELIST_FILE);
        }
    }


    public static List<StudentInfo> ReadCSV (String filepath, boolean fromClassList){

        List<StudentInfo> OutputList = new ArrayList<>();

        //Read all info from Classlist.csv or AttendanceList.csv file into ArrayList OutputList
        BufferedReader Reader = null;
        try {
            Reader = new BufferedReader(new FileReader(filepath));
        } catch (FileNotFoundException e) {
            Log.d("Main Activity :","Cannot find Classlist.csv");
            e.printStackTrace();
        }
        String line = "";

        int counter =0;
        try {
            while ((line = Reader.readLine()) != null) {
                //Split by comma ,
                String[] tokens = line.split(",");
                //Read data
                StudentInfo info = new StudentInfo();
                info.setID(tokens[0]);
                info.setName(tokens[1]);

                if(!fromClassList){ //if copy from AttendanceList, remove added quotation mark in String fields
                    info.setStatus(tokens[2]);
                    info.setDate(tokens[3]);
                    info.setClassID(tokens[4]);
                    Log.d("readCSV","can read token4 : "+tokens[4]);
                    info.setID(info.getID().replaceAll("^\"+|\"+$", ""));
                    info.setName(info.getName().replaceAll("^\"+|\"+$", ""));
                    info.setDate(info.getDate().replaceAll("^\"+|\"+$", ""));
                    info.setStatus(info.getStatus().replaceAll("^\"+|\"+$", ""));
                    info.setClassID(info.getClassID().replaceAll("^\"+|\"+$", ""));

                }
                OutputList.add(info);
                Log.d(TAG,"Just created info " + OutputList.get(counter));
                counter++;
            }
        }catch(IOException e){
            Log.d(TAG,"Error reading data file on line" + line,e);
            e.printStackTrace();
        }
        return OutputList;
    }





    //--------------------------------------------------------------------------------------------//
    // Method to print test the Attendance ArrayList
    //--------------------------------------------------------------------------------------------//
    /*
    static public void Printest(){
        for(StudentInfo info:AttendanceList){
            Log.d(TAG,"just update info" + info);
        }
    }*/
    //--------------------------------------------------------------------------------------------//
    // Method to save all Student attendance info from temporary ArrayList to CSV file inside LIST_SUBFOLDER
    //--------------------------------------------------------------------------------------------//

    static public void WriteCSV(List<StudentInfo> list,Context context,String filename){
        File RootPath = context.getExternalFilesDir(null);
        String filepath;
        if(!filename.contains("ClassList")) {
            //if copy from Attendancelist
            filepath = RootPath.getAbsolutePath() + "/" + LIST_SUBFOLDER + "/" + ATTENDANCELIST_FILE;
        } else{

            //if copy from ClassList somewhere, need to check if ClassList folder exists.
            File folder = new File(RootPath.getAbsolutePath()+"/LIST");
            if(!folder.exists()){
                folder.mkdir();
            }
            String SubFolderPath = folder.getAbsolutePath() +"/CLASSLIST" ;
            File subfolder = new File(SubFolderPath);
            if(!subfolder.exists()){
                subfolder.mkdir();
            }
            filepath = SubFolderPath+"/"+filename;
        }

        List<String[]> entries = new ArrayList<>();
        for(StudentInfo info:list){
            String[] line = {info.getID(),info.getName(),info.getStatus(),info.getDate(),info.getClassID()};
            entries.add(line);
        }

        try (FileOutputStream fos = new FileOutputStream(filepath);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw))
        {
            writer.writeAll(entries);

        } catch (IOException e) {
            e.printStackTrace();
        }
       // Toast.makeText(context, "Successfully write CSV file  to Phone Memory at" + filepath,
       //         Toast.LENGTH_SHORT).show();

    }
    //--------------------------------------------------------------------------------------------//
    // Method to email CSV file to Email Recipient of choice
    //--------------------------------------------------------------------------------------------//

    static public void EmailCSV(Context context ){

        //Prepare URI
        File RootPath = context.getExternalFilesDir(null);
        String filepath = RootPath.getAbsolutePath()+"/"+LIST_SUBFOLDER;
        File filelocation = new File(filepath,ATTENDANCELIST_FILE);
        Uri uricontent = FileProvider.getUriForFile(context,
                "com.luongnguyen.facedetect.provider", filelocation);
        //Initialize email intent
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        String to[] = {EMAIL_RECIPIENT};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        // add the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, uricontent);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Class:"+ClassID+"-Attendance list update - Date: "+getDate());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "- Sent from FaceDetect app - ");

        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uricontent, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        //emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Log.d(TAG, "here is your file location : " + filelocation );
        Log.d(TAG, "here is your URI: " + uricontent );

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.startActivity(Intent.createChooser(emailIntent , "Sending Attendance CSV file"));
    }



    //--------------------------------------------------------------------------------------------//
    //Method to get the date
    //--------------------------------------------------------------------------------------------//
    static private String getDate(){
        //Get the current date with specific format
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat Formater = new SimpleDateFormat("MMM-dd-yyyy");
        String formateddate = Formater.format(date);
        return formateddate;
    }


    public static void WriteDatabase (HashMap<Classifier.Recognition, String> ImageDatabase, Context context) {

        File RootPath = context.getExternalFilesDir(null);
        File FilePath = new File(RootPath.getAbsolutePath() +"/"+ DATA_FOLDER+"/"+"ImageDatabase.txt");
        if(!FilePath.exists()) {
            try {
                FilePath.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bf = null;;

        try{

            //create new BufferedWriter for the output file
            bf = new BufferedWriter( new FileWriter(FilePath) );

            //iterate map entries
            for(Map.Entry<Classifier.Recognition, String> entry : ImageDatabase.entrySet()){

                //put key and value separated by a colon
                bf.write( entry.getValue()  + " " +  entry.getKey() );
                //new line
                bf.newLine();

            }

            bf.flush();

        }catch(IOException e){
            Log.d(TAG,"Cannot write Database to Phone Memory");
            e.printStackTrace();
        }finally{

            try{
                //always close the writer
                bf.close();
            }catch(Exception e){}
        }
        Log.d(TAG,"Successfully write Database to Phone Memory at" + FilePath);
    }


    public static void ReadDatabase (HashMap<Classifier.Recognition,String> ImageDatabase,Context context) {

        File RootPath = context.getExternalFilesDir(null);
        File FilePath = new File(RootPath.getAbsolutePath() + "/" + DATA_FOLDER + "/" + DATABASE_FILE);

        if (FilePath.exists()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(new FileReader(FilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Log.d("Readtest method", "successfully setup scanner " );
            try {
                int line = 0;
                String Name = "?";
                String id = "";
                String label = "";
                float distance = 0f;


                while (scanner.hasNextLine()) {
                    String[] tokens = scanner.nextLine().split(" ") ; // split space between word and float number embedding
                    ArrayList<Float> FeatureList = new ArrayList<>();
                    //Log.d("Readtest method", "successful reading line ");
                    Name = tokens[0];
                    //Log.d("Readtest method", "successful reading name " + Name);
                    id = tokens[1];
                    //Log.d("Readtest method", "successful reading ID "+id );
                    label = tokens[2];
                    //Log.d("Readtest method", "successful reading Label "+label );
                    if(tokens[3].equals("Infinity")||(tokens[3].length()>8))
                    {
                        distance = Float.POSITIVE_INFINITY;

                    }else{
                        distance = Float.parseFloat(tokens[3]);
                    }
                    //Log.d("Readtest method", "successful reading distance "+distance );

                    for(int i=4;i<numoffeature+4;i++){
                        FeatureList.add(Float.parseFloat(tokens[i]));
                    }

                    int i=0;
                    //Log.d("Created featurelist", "size : "+FeatureList.size());

                    float[] feature = ArrayUtils.toPrimitive(FeatureList.toArray(new Float[numoffeature]), 0.0F);
                    for(float f:feature){
                        //Log.d("Scan feature item", ""+String.format("is %f",f));
                    }

                   // Log.d("Created featurelist", "finish for loop");
                    Classifier.Recognition Rec = new Classifier.Recognition(id, label, distance, feature);
                    ImageDatabase.put(Rec, Name);
                    //Log.d("Created featurelist", "finish update database ");
                }

               // Log.d("Readtest method", "successfully update Imagedatabase using readtest " );
            }finally{
                    scanner.close();
            }
        }

    }

    //--------------------------------------------------------------------------------------------//
    //             Method to update Attendance list with students that are present
    //--------------------------------------------------------------------------------------------//

    public static void UpdateAttendance(String studentname, Context context){
        boolean flag =false;
        Log.d("update attendance:","Start checking for inputname "+studentname);
        for(StudentInfo info:AttendanceList){
            Log.d("update attendance:","looking at name"+info.getName());
            if(info.getName().equals(studentname)){
                info.setStatus("present");
                //Toast.makeText(context, "Attendance was updated", Toast.LENGTH_SHORT).show();
                flag =true;
            }

        }
        if(!flag){
            Log.d("update attendance:","Flag is now false");
            //Toast.makeText(context, "Predicted name is not in Classlist",
                                                                       // Toast.LENGTH_LONG)show();
        }

    }

    public static void BatchInputFace(Context context){
        Log.d("Method BatchInput","Enter batch input method");
        Classifier.Recognition myBatchRecognition;
        String IpName="";

        File RootPath = context.getExternalFilesDir(null);
        File GalleryPath = new File(RootPath.getAbsolutePath() +"/"+ GALLERY_FOLDER);
        File[] Gallery = GalleryPath.listFiles();
        Log.d("batchmodeinput","found gallery");
        String filepath = new String();

        int size = 0;
        if(Gallery.length>0) {
            for (File dir : Gallery) {
                //save student image to archived folder if exists
                if (!dir.isDirectory()) {
                    dir.delete();
                } else {
                    IpName = dir.getName();
                    Log.d("batchmodeinput","reading folder:"+IpName);
                    String NameFolderPath = GalleryPath.getAbsolutePath()+"/"+IpName;
                        File NameFolder = new File(NameFolderPath);
                        ExtensionFilter PhotoFilter = new ExtensionFilter("jpg", "png");
                        File[] AllPhotos = NameFolder.listFiles();
                        Log.d("batchmodeinput","setup ok");
                        Log.d("batchmodeinput","reading path"+NameFolder.getAbsolutePath());
                        if(AllPhotos.length>0){
                            for(File photo:AllPhotos){

                                Log.d("batchmodeinput","enter allphoto loop size"+AllPhotos.length);
                                //Bitmap source = Bitmap.createBitmap(resize_width, resize_height, Bitmap.Config.ARGB_8888);
                                Bitmap sourceimage = BitmapFactory.decodeFile(photo.getAbsolutePath());
                                Bitmap InputBmp = Bitmap.createScaledBitmap(sourceimage, resize_width, resize_height, true);
                                //Start recognize inputface to collect Feature information.
                                myBatchRecognition = myClassifier.FaceRecognizer(InputBmp,true,IpName);
                                Log.d("batchmodeinput","found recognition"+myBatchRecognition);

                                //Update database with output myRecognition and write down copy of Database
                                ImageDatabase.put(myBatchRecognition,IpName);
                                Log.d(TAG, "successfully update ImageDatabase info for"+IpName);
                            }
                        }else{

                            Log.d(TAG, "cannot find photo in this folder name"+dir.getName());
                        }

                    }
                }

            }

        Toast.makeText(context, "Successfully update ImageDatabase, its size now is:" + ImageDatabase.size(),
                                                                                        Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Successfully update ImageDatabase size"+ImageDatabase.size());

        Methods.WriteDatabase(ImageDatabase,context);
        Log.d(TAG, "successfully write myrecognition info to data file of size"+ImageDatabase.size());

        }





    }





