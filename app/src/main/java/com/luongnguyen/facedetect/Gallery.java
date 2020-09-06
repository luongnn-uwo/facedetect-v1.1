package com.luongnguyen.facedetect;

import java.util.Arrays;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity {

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        mListView = findViewById(R.id.List);
        CustomAdaptor customAdaptor = new CustomAdaptor();
        mListView.setAdapter(customAdaptor);

    }
    class CustomAdaptor extends BaseAdapter{

        ArrayList images = ReadGallery().imagepath;
        ArrayList names = ReadGallery().imagename;

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.customlayout,null);
            ImageView mImageView = v.findViewById(R.id.Photo);
            TextView mTextView = v.findViewById(R.id.Name);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(images.get(position).toString()));
            mTextView.setText(names.get(position).toString());
            return v;
        }

    }
    //--------------------------------------------------------------------------------------------//
    //                        METHOD READ ALL PHOTOS FROM GALLERY
    //--------------------------------------------------------------------------------------------//

    public FolderInfo ReadGallery()
    {
        FolderInfo outputinfo = new FolderInfo(new ArrayList(),new ArrayList());
        File RootPath = getExternalFilesDir(null);
        File GalleryPath = new File(RootPath.getAbsolutePath() +"/"+ Methods.GALLERY_FOLDER);
        File[] PicDirArray = GalleryPath.listFiles();
        ExtensionFilter PhotoFilter = new ExtensionFilter("jpg", "png");

        if (PicDirArray != null) {
            for(File dir:PicDirArray) {
                if(dir.isDirectory()) {
                    File[] filelist = dir.listFiles(PhotoFilter);
                    Arrays.sort(filelist);

                    for (int i = 0; i < filelist.length; i++) {
                        File file = filelist[i];
                        String filePath = file.getPath();
                        outputinfo.imagepath.add(filePath);
                        String fileName = file.getName().substring(0, (file.getName().length() - 4));
                        outputinfo.imagename.add(fileName);
                    }
                }
            }
        }
        return outputinfo;
    }

    //--------------------------------------------------------------------------------------------//
    //           A class to combine Imagepath and Imagename together for List View
    //--------------------------------------------------------------------------------------------//

    public class FolderInfo {
        public final ArrayList imagepath;
        public final ArrayList imagename;

        public FolderInfo(ArrayList imagepath, ArrayList imagename) {
            this.imagepath = imagepath;
            this.imagename = imagename;
        }
    }

}

