package org.tensorflow.lite.examples.detection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import java.util.List;
import android.os.Handler;

import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.tflite.Detector;

//import org.tensorflow.lite.examples.detection;

public class MainActivity extends Activity  {
    Button button;
    ImageView image;
    Detector detector;
    Bitmap rgbFrameBitmap;
    Bitmap croppedBitmap;
    Bitmap cropCopyBitmap;
    Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    image = (ImageView) findViewById(R.id.imageView1);
    image.setImageResource(R.drawable.img_0107);
    //image.setVisibility(View.VISIBLE);
    //button.setVisibility(View.VISIBLE);
    addListenerOnButton();
    }

    public void addListenerOnButton() {
        image = (ImageView) findViewById(R.id.imageView1);
        button = (Button) findViewById(R.id.Detection);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Logger LOGGER = new Logger();
                BitmapFactory.Options op = new BitmapFactory.Options();
                op.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap origin_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_0107, op);

                int cropSize = 512;

                Bitmap croppedbitmap = Bitmap.createScaledBitmap(origin_bitmap, cropSize, cropSize, true);
                /*runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                //final List<Detector.Recognition> results = detector.recognizeImage(croppedbitmap);
                                image.setImageBitmap(croppedbitmap);
                            }
                        });
                        */
                final Canvas canvas = new Canvas(croppedbitmap);
                final List<Detector.Recognition> results = detector.recognizeImage(croppedbitmap);
                image.setImageBitmap(croppedbitmap);
                button.setVisibility(View.GONE);
            }
        });


    }

}