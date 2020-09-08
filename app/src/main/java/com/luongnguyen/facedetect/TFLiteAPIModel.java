
/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.luongnguyen.facedetect;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.tensorflow.lite.Interpreter;
import static com.luongnguyen.facedetect.MainActivity.NumofIDs;

public class TFLiteAPIModel
        implements Classifier {
    final private String TAG = "TensorflowLite";
    private static final int OUTPUT_SIZE = 192;
    // Float model
    private static final float IMAGE_MEAN = 128.0f;
    private static final float IMAGE_STD = 128.0f;
    // Number of threads in the java app
    private static final int NUM_THREADS = 4;
    private boolean isModelQuantized;
    // Config values.
    private int inputSize;
    // Pre-allocated buffers.
    private Vector<String> labels = new Vector<String>();
    private int[] intValues;
    private float[] embedding;
    private ByteBuffer imgData;
    private Interpreter tfLite;


    public static HashMap<Recognition,String> ImageDatabase = new HashMap<>();



    private TFLiteAPIModel() {}

    /** Memory-map the model file in Assets. */
    private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilename The filepath of label file for classes.
     * @param inputSize The size of image input
     * @param isQuantized Boolean representing model is quantized or not
     */
    public static Classifier create(
            final AssetManager assetManager,
            final String modelFilename,
            final String labelFilename,
            final int inputSize,
            final boolean isQuantized)
            throws IOException {

        final TFLiteAPIModel mymodel = new TFLiteAPIModel();

        String actualFilename = labelFilename.split("file:///android_asset/")[1];
        InputStream labelsInput = assetManager.open(actualFilename);
        BufferedReader buffread = new BufferedReader(new InputStreamReader(labelsInput));
        String line;

        while ((line = buffread.readLine()) != null) {
           // Log.d("TensorFlowLite","model reading :"+line);
            mymodel.labels.add(line);
        }
        buffread.close();
        mymodel.inputSize = inputSize;

        try {
            mymodel.tfLite = new Interpreter(loadModelFile(assetManager, modelFilename));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mymodel.isModelQuantized = isQuantized;

        // Pre-allocate buffers.
        int numBytesPerChannel;Map<Integer, Object> outputMap = new HashMap<>();
        if (isQuantized) {
            numBytesPerChannel = 1; // Quantized
        } else {
            numBytesPerChannel = 4; // Floating point
        }
        mymodel.imgData = ByteBuffer.allocateDirect(1 * mymodel.inputSize * mymodel.inputSize * 3 * numBytesPerChannel);
        mymodel.imgData.order(ByteOrder.nativeOrder());
        mymodel.intValues = new int[mymodel.inputSize * mymodel.inputSize];
        mymodel.tfLite.setNumThreads(NUM_THREADS);

        return mymodel;
    }


    @Override
    public Recognition FaceRecognizer(final Bitmap bitmap, boolean AddingFlag, String InputName) {

        Log.d(TAG,"FaceRecognizer starts working...........");

        // Preprocess by bit manipulating from 0-255 int to normalized float [-1 to 1]
        // on the provided image data.
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imgData.rewind();
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                }
            }
        }

        // Copy the input image data into TensorFlow Input Array.
        Object[] inputArray = {imgData};
       // Initialize embedding in relation to OutputMap to match Interpreter requirement
        Map<Integer, Object> outputMap = new HashMap<>();
        float[][] EmbeddingOutput = new float[1][OUTPUT_SIZE];
        outputMap.put(0, EmbeddingOutput);
        embedding = EmbeddingOutput[0];
        //Log.d("FaceRecognizer Method","output from OutputMap, embedding size "+embedding.length);

        // start mapping inputs with outputs, loading info into embeddings
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);

        //Initialize recognition variables
        float distance = Float.MAX_VALUE;
        String id = "0";
        String label = "?";
        float[] feature = new float[] {0f} ;

        //If database stored any face information, start looking up and get Name + Distance
        if (ImageDatabase.size() > 0) {
            //Log.d(TAG,"Database SIZE: " + ImageDatabase.size());
            //Start look up embedding info to get nearest data point and return recognition info.
            final Pair<String, Float> nearest = LookUp(embedding);
            if (nearest != null) {
                final String name = nearest.first;
                label = name;
                distance = nearest.second;
                //Log.d(TAG,"nearest: " + name + " - distance: " + distance);
            }
        }

        // In case this is Adding Activity : Save embedding info as Feature
        if (AddingFlag) {
            id = String.valueOf(NumofIDs);
            feature = embedding;
            label = InputName;
            NumofIDs++;
        }
        //Log.d(TAG,"FaceRecognizer finishes working...........");

        // Create new recognition instance to save output info
        Recognition rec = new Recognition(id,label,distance,feature);

        return rec;
    }

    //--------------------------------------------------------------------------------------------//
    // METHOD to Look up the Classifier for the nearest embedding in the database (using L2 norm)
    // and returns the pair <id, distance>
    //--------------------------------------------------------------------------------------------//
    private Pair<String, Float> LookUp(float[] emb) {
        //Log.d("Lookup method","start looking up");
        Pair<String, Float> output = null;

        for (Map.Entry<Recognition,String> entry : ImageDatabase.entrySet()) {

            String ID = entry.getKey().getID();
            String name = entry.getValue();
            String test = "";
            float[] knownEmb =  entry.getKey().getFeature();
            for(float f:knownEmb){
                test = test + String.format(" %f",f);
            }
            //Log.d("Lookup method"," featurelist for "+name+" ID"+ID+"is: "+test );
            float distance = 0;
            test = "";
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                //test = test + String.format(" %f",diff);
                distance += diff*diff;
            }
          //  Log.d("Lookup method"," different list is:"+test);
            distance = (float) Math.sqrt(distance);
            Log.d("Lookup method"," distance is"+distance);
            if (output == null || distance < output.second) {
                output = new Pair<>(name, distance);
            }
        }
        return output;
    }

    @Override
    public void close() {}


}

