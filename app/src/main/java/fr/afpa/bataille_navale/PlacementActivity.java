package fr.afpa.bataille_navale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PlacementActivity extends AppCompatActivity {
    private Boolean mDroppedIn = null;
    private View porte_avion;
    private View croiseur;
    private View contre_torpilleur1;
    private View contre_torpilleur2;
    private View torpilleur;
    private Button button;
    private GridLayout gridStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        button = findViewById(R.id.startGameBtn);
        gridStore = findViewById(R.id.StoreShips);

        /**
         * Get the boat ImageViews
         */
        porte_avion = findViewById(R.id.porte_avion);
        croiseur = findViewById(R.id.croiseur);
        contre_torpilleur1 = findViewById(R.id.contre_torpilleur1);
        contre_torpilleur2 = findViewById(R.id.contre_torpilleur2);
        torpilleur = findViewById(R.id.torpilleur);

        /**
         * Set the onTouch for Drag and Drop on each boat ImageView
         */
        porte_avion.setOnTouchListener(new TouchBoat());
        croiseur.setOnTouchListener(new TouchBoat());
        contre_torpilleur1.setOnTouchListener(new TouchBoat());
        contre_torpilleur2.setOnTouchListener(new TouchBoat());
        torpilleur.setOnTouchListener(new TouchBoat());

        /**
         * Create the board programmatically in the GridLayout view
         */
        GridLayout layout = (GridLayout) findViewById(R.id.MyGrid);
        layout.setRowCount(BoardSize.ROWS);
        layout.setColumnCount(BoardSize.COLUMNS);

        for(int i = 0; i < 9; i++) {
            GridLayout.Spec rowSpec = GridLayout.spec(i, 1, 1);
            for(int j = 0; j < 9; j++) {
                GridLayout.Spec colSpec = GridLayout.spec(j, 1, 1);
                LinearLayout linearLayout = new LinearLayout(new ContextThemeWrapper(this,R.style.Grid));
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                //linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setId(R.id.row + i + R.id.col + j);
                //linearLayout.setGravity(Gravity.FILL_HORIZONTAL);
                linearLayout.setOnDragListener(new MyDragListener());
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                linearLayout.addView(imageView);
                GridLayout.LayoutParams myGLP = new GridLayout.LayoutParams();
                myGLP.rowSpec = rowSpec;
                myGLP.columnSpec = colSpec;
                myGLP.width = 0;
                myGLP.height = 0;
                layout.addView(linearLayout, myGLP);
            }
        }

    }

    /**
     * Create onTouch drag and drop with shadowBuilder for boats
     */
    private final class TouchBoat implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);

                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Set onClick 90° rotation for boats on grid
     */
    class MyDragListener implements View.OnDragListener {
        private View.OnClickListener myListener = view -> {
            Log.e("Image name", view.getContentDescription() + "");
            ImageView newImage = (ImageView) view;
            newImage.setImageBitmap(rotateBitmap(((BitmapDrawable) newImage.getDrawable()).getBitmap(),90));
        };

        private Bitmap rotateBitmap(Bitmap bitmap, int rotationAngleDegree) {

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int newW = w, newH = h;
            if (rotationAngleDegree == 90 || rotationAngleDegree == 270) {
                newW = h;
                newH = w;
            }
            Bitmap rotatedBitmap = Bitmap.createBitmap(newW, newH, bitmap.getConfig());
            Canvas canvas = new Canvas(rotatedBitmap);

            Rect rect = new Rect(0, 0, newW, newH);
            Matrix matrix = new Matrix();
            float px = rect.exactCenterX();
            float py = rect.exactCenterY();
            matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
            matrix.postRotate(rotationAngleDegree);
            matrix.postTranslate(px, py);
            canvas.drawBitmap(bitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();

            return rotatedBitmap;
        }

        /**
         * Define actions depending on drag and drop status
         */
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    /**
                     * Change background of the layout where item is entering
                     */
                    v.setBackgroundColor(Color.parseColor("#ECECEC"));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    /**
                     * Change background of the layout back to normal once item is moved out of it
                     */
                    v.setBackgroundColor(Color.parseColor("#003C5F"));
                    v.setBackgroundResource(R.drawable.grid_stroke);
                    break;
                case DragEvent.ACTION_DROP:
                    /**
                     * Reassign view to view group
                     */
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);

                    /**
                     * Copy all dragged image data into target image data
                     * Attach OnClickListener and Rotation method to target image data
                     */
                    ImageView oldView = (ImageView) view;
                    ImageView newView = (ImageView) container.getChildAt(0);
                    newView.setId(oldView.getId());
                    newView.setContentDescription(oldView.getContentDescription());
                    newView.setOnClickListener(myListener);
                    newView.setImageBitmap(((BitmapDrawable) oldView.getDrawable()).getBitmap());

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    /**
                     * Check if element dropped in target or outside target
                     * If outside, set it back to its initial place
                     */
                    View currentView = (View) event.getLocalState();
                    reportResult(event.getResult());
                    if(mDroppedIn == false) {
                        currentView.setVisibility(View.VISIBLE);
                    }

                    /**
                     * Get the boat's parent's id
                     */
                    int porte_avionParentId = ((View) porte_avion.getParent()).getId();
                    int croiseurParentId = ((View) croiseur.getParent()).getId();
                    int contre_torpilleur1ParentId = ((View) contre_torpilleur1.getParent()).getId();
                    int contre_torpilleur2ParentId = ((View) contre_torpilleur2.getParent()).getId();
                    int torpilleurParentId = ((View) torpilleur.getParent()).getId();

                    /**
                     * Check if all the boats were dropped outside the Store
                     * If true, set button Start Game visible and Store invisible
                     */
                    if(porte_avionParentId != R.id.StoreShips && croiseurParentId != R.id.StoreShips
                    && contre_torpilleur1ParentId != R.id.StoreShips && contre_torpilleur2ParentId != R.id.StoreShips
                    && torpilleurParentId != R.id.StoreShips) {
                        button.setVisibility(View.VISIBLE);
                        gridStore.setVisibility(View.GONE);
                    }

                default:
                    break;
            }
            return true;
        }
    }

    /**
     * Check drop status (in target = true, out of target = false)
     */
    private void reportResult(final boolean result) {
        mDroppedIn = result;
        //Toast.makeText(this, "Dropped in: " + result, Toast.LENGTH_SHORT).show();
    }

    /**
     * Reset position of boats / Reload page
     */
    public void clickResetPlacement(View v) {
        finish();
        startActivity(getIntent());
    }

    /**
     * Sent intent to GameActivity
     */
    public void clickLaunchGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }
}