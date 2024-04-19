package my.edu.utar.utartransit;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class FullScreenImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private float minScale = 1f;
    private float maxScale = 5f;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Get references to views
        imageView = findViewById(R.id.full_screen_image);
        ImageView closeButton = findViewById(R.id.close_button);

        // Get image resource ID from intent
        int imageResId = getIntent().getIntExtra("imageResId", 0);
        if (imageResId != 0) {
            imageView.setImageResource(imageResId);
        }

        // Set up pinch-to-zoom gesture detector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Add click listener to close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity
            }
        });

        fitImageToScreen();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(minScale, Math.min(scale, maxScale));
            matrix.setScale(scale, scale);
            imageView.setImageMatrix(matrix);
            return true;
        }
    }

    private void fitImageToScreen() {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            int imageWidth = drawable.getIntrinsicWidth();
            int imageHeight = drawable.getIntrinsicHeight();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;

            float scaleX = (float) screenWidth / imageWidth;
            float scaleY = (float) screenHeight / imageHeight;
            float scaleFactor = Math.min(scaleX, scaleY);

            matrix.setScale(scaleFactor, scaleFactor);

            // Calculate translation to center the image
            float scaledImageWidth = imageWidth * scaleFactor;
            float scaledImageHeight = imageHeight * scaleFactor;
            float translateX = (screenWidth - scaledImageWidth) / 2f;
            float translateY = (screenHeight - scaledImageHeight) / 2f;

            matrix.postTranslate(translateX, translateY);

            imageView.setImageMatrix(matrix);
            scale = scaleFactor;
        }
    }
}