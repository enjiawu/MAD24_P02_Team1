package sg.edu.np.mad.pocketchef;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Context context;
    boolean isReady = false;
    private MotionLayout motionLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isReady) {
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                dismissSplashScreen();
                return false;
            }
        });
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FindViews(); // Initialize views after setContentView()
        // Custom setOnTouchListener for swipe gestures (in-built Gesture Detector is not working)
        motionLayout.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private VelocityTracker velocityTracker;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        if (velocityTracker == null) {
                            velocityTracker = VelocityTracker.obtain();
                        } else {
                            velocityTracker.clear();
                        }
                        velocityTracker.addMovement(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        velocityTracker.addMovement(event);
                        velocityTracker.computeCurrentVelocity(1000); // 1000 milliseconds
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float diffX = endX - startX;
                        if (Math.abs(diffX) > 50 && velocityTracker != null) { // Minimum swipe distance threshold
                            velocityTracker.computeCurrentVelocity(1000); // 1000 milliseconds
                            float velocityX = velocityTracker.getXVelocity();
                            if (Math.abs(velocityX) > 300) { // Minimum swipe velocity threshold
                                if (diffX > 0) {
                                    // Right swipe (forward)
                                    motionLayout.transitionToEnd();
                                } else {
                                    // Left swipe (backward)
                                    motionLayout.transitionToStart();
                                }
                            }
                        }
                        if (velocityTracker != null) {
                            velocityTracker.recycle();
                            velocityTracker = null;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (velocityTracker != null) {
                            velocityTracker.recycle();
                            velocityTracker = null;
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void FindViews() {
        motionLayout = findViewById(R.id.main_activity);
    }

    private void dismissSplashScreen() {
        new Handler().postDelayed(() -> isReady = true, 3000);
    }
}