package org.protocoderrunner.api.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGBuilder;

import org.protocoderrunner.api.widgets.PImage;
import org.protocoderrunner.base.utils.FileIO;
import org.protocoderrunner.base.utils.Image;
import org.protocoderrunner.base.utils.MLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PHelper {

    private static final java.lang.String TAG = PHelper.class.getSimpleName();

    /**
     * This class lets us set images from a file asynchronously
     */
    public static class SetImageTask extends AsyncTask<String, Void, Object> {
        private PImage image;
        private String imagePath;
        private String fileExtension;

        public SetImageTask(PImage image) {
            this.image = image;
        }

        @Override
        protected Object doInBackground(String... paths) {
            imagePath = paths[0];
            this.fileExtension = FileIO.getFileExtension(imagePath);
            Object ret = null;

            // download from web
            if (imagePath.startsWith("http")) {
                try {
                    InputStream in = new java.net.URL(imagePath).openStream();
                    ret = BitmapFactory.decodeStream(in);

                } catch (Exception e) {
                    MLog.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            // read from file
            } else {
                File imgFile = new File(imagePath);

                if (imgFile.exists()) {
                    if (fileExtension.equals("svg")) {
                        File file = new File(imagePath);
                        FileInputStream fileInputStream = null;
                        try {
                            fileInputStream = new FileInputStream(file);
                            SVG svg = new SVGBuilder().readFromInputStream(fileInputStream).build();
                            ret = svg.getDrawable();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ret = Image.loadBitmap(imagePath);
                    }
                }
            }
            return ret;
        }


        @Override
        protected void onPostExecute(Object result) {
            MLog.d(TAG, "image" + image);
            image.mode(null);
            // image.setScaleType(ImageView.ScaleType.FIT_XY);

            if (fileExtension.equals("svg")) {
                MLog.d("svg", "is SVG 2 " + result);
                image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                image.setImageDrawable((Drawable) result);
            } else {
                image.setImageBitmap((Bitmap) result);
            }

        }
    }

}
