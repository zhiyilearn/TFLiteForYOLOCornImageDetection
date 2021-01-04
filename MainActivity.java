package org.tensorflow.lite.examples.detection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import org.tensorflow.lite.examples.detection.customview.OverlayView;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;

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

    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 300;
    // private static final int TF_OD_API_INPUT_SIZE = 416;
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    // private static final String TF_OD_API_MODEL_FILE = "yolo_v3_with_metadata.tflite";
    private static final String TF_OD_API_LABELS_FILE = "labelmap.txt";
    // private static final String TF_OD_API_LABELS_FILE = "labelmap2.txt";
    // private static final String TF_OD_API_LABELS_FILE = "labelmap.txt";
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;

    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    image = (ImageView) findViewById(R.id.imageView1);
    image.setImageResource(R.drawable.img_0107);

        int cropSize = TF_OD_API_INPUT_SIZE;
        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            this,
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Detector could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
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
                Matrix cropToFrameTransform;

                int cropSize = TF_OD_API_INPUT_SIZE;
                Bitmap croppedbitmap = Bitmap.createScaledBitmap(origin_bitmap, cropSize, cropSize, true);
                final List<Detector.Recognition> results = detector.recognizeImage(croppedbitmap);
                //final List<Detector.Recognition> results = detector.recognizeImage(origin_bitmap);
                // image.setImageBitmap(croppedbitmap);
                // button.setVisibility(View.GONE);

                cropCopyBitmap = Bitmap.createScaledBitmap(origin_bitmap, cropSize, cropSize, true);
                final Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2.0f);

                float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                cropToFrameTransform = new Matrix();

                final List<Detector.Recognition> mappedRecognitions =
                        new ArrayList<Detector.Recognition>();
                Canvas canvas = new Canvas(cropCopyBitmap);
                for (final Detector.Recognition result : results) {
                    final RectF location = result.getLocation();
                    Log.d("Boudning boxes",  "left: " + String.valueOf(location.left));
                    // if (location != null && result.getConfidence() >= minimumConfidence) {
                    if (location != null) {
                        canvas.drawRect(location, paint);
                        cropToFrameTransform.mapRect(location);

                        result.setLocation(location);
                        mappedRecognitions.add(result);
                    }
                }
                Log.d("size",  String.valueOf(mappedRecognitions.size()));
                Log.d("size",  String.valueOf(results.size()));

                image = (ImageView) findViewById(R.id.imageView1);
                Bitmap scaledBackbitmap = Bitmap.createScaledBitmap(cropCopyBitmap, origin_bitmap.getWidth(), origin_bitmap.getHeight(), true);
                image.setImageBitmap(scaledBackbitmap);
                button.setVisibility(View.GONE);


            }
        });


    }

}