package com.luongnguyen.facedetect;

import java.io.File;
import java.io.FilenameFilter;

public class ExtensionFilter implements FilenameFilter  {

    //--------------------------------------------------------------------------------------------//
    //              A filter to get photo files filtered - with extensions of choice
    //--------------------------------------------------------------------------------------------//
    private String[] exts;

    public ExtensionFilter(String... exts) {
        this.exts = exts;
    }

    @Override
    public boolean accept(File dir, String name) {
        for (String ext : exts) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
