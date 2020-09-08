/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luongnguyen.facedetect;

import android.graphics.Bitmap;
import android.graphics.RectF;
import java.util.List;

/** Generic interface for interacting with different recognition engines. */
public interface Classifier {

    Recognition FaceRecognizer(Bitmap bitmap, boolean getFeature, String InputName);

    void close();

   /** An immutable result returned by a Classifier describing what was recognized. */
    public class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final String id;

        /** Display name for the recognition. */
        private final String label;

        /**
         * A sortable score for how good the recognition is relative to others. Lower should be better.
         */
        private final float distance;
        private final float[] features;

        /** Optional location within the source image for the location of the recognized object. */



        public Recognition(
                final String id, final String label, final float distance, final float[] features) {
            this.id = id;
            this.label = label;
            this.distance = distance;
            this.features = features;

        }

        public float[] getFeature() {return features;}

        public String getID() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public Float getDistance() {
            return distance;
        }


        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString +=" "+ id;
            }

            if (label != null) {
                resultString +=" "+ label;
            }

            if (distance != 0.0f) {
                resultString += String.format(" %.5f", distance ); //*100f
            }
            if (features != null) {
                for(float feat:features) {
                    resultString += String.format(" %.5f", feat); //*100.0ff
                    }
            }

            return resultString.trim();
        }


    }
}
