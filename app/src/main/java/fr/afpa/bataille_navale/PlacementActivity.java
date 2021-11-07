package fr.afpa.bataille_navale;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.Collections;

public class PlacementActivity extends AppCompatActivity  {

    private Boolean mDroppedIn = null;
    private View porteAvionImg;
    private View croiseurImg;
    private View contreTorpilleur1Img;
    private View contreTorpilleur2Img;
    private View torpilleurImg;
    private Button button;
    private GridLayout gridStore;
    private MyRecyclerViewAdapter adapter;
    private GridLayout layout;
    private int midTile;
    private int nextTile = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        button = findViewById(R.id.startGameBtn);
        gridStore = findViewById(R.id.StoreShips);

        /**
         * Create boats
         */
        Object porteAvion = new Boat(6);
        Object croiseur = new Boat(3);
        Object contreTorpilleur1 = new Boat(5);
        Object contreTorpilleur2 = new Boat(5);
        Object torpilleur = new Boat(4);


        /**
         * Get the boat ImageViews
         */
        porteAvionImg = findViewById(R.id.porte_avion);
        porteAvionImg.setTag("porteAvion");
        croiseurImg = findViewById(R.id.croiseur);
        croiseurImg.setTag("croiseur");
        contreTorpilleur1Img = findViewById(R.id.contre_torpilleur1);
        contreTorpilleur1Img.setTag("1contreTorpilleur");
        contreTorpilleur2Img = findViewById(R.id.contre_torpilleur2);
        contreTorpilleur1Img.setTag("2contreTorpilleur");
        torpilleurImg = findViewById(R.id.torpilleur);
        torpilleurImg.setTag("torpilleur");

        /**
         * Set the onTouch for Drag and Drop on each boat ImageView
         */
        porteAvionImg.setOnTouchListener(new TouchBoat());
        croiseurImg.setOnTouchListener(new TouchBoat());
        contreTorpilleur1Img.setOnTouchListener(new TouchBoat());
        contreTorpilleur2Img.setOnTouchListener(new TouchBoat());
        torpilleurImg.setOnTouchListener(new TouchBoat());

        /**
         * Create the board programmatically in the GridLayout view
         */
        layout = (GridLayout) findViewById(R.id.MyGrid);
        layout.setRowCount(BoardSize.ROWS);
        layout.setColumnCount(BoardSize.COLUMNS);

        for(int i = 0; i < BoardSize.ROWS; i++) {
            GridLayout.Spec rowSpec = GridLayout.spec(i, 1, 1);
            for(int j = 0; j < BoardSize.COLUMNS; j++) {
                GridLayout.Spec colSpec = GridLayout.spec(j, 1, 1);
                LinearLayout linearLayout = new LinearLayout(new ContextThemeWrapper(this,R.style.Grid));
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setId(R.id.row + i + R.id.col + j);
                linearLayout.setTag("row"+ i + "col" + j);
                linearLayout.setGravity(Gravity.FILL);
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

                Log.i("idMain : ", String.valueOf(linearLayout.getTag()));
            }
        }

        // data to populate the RecyclerView with
        /*for(int i = 1; i <BoardSize.ROWS; i++) {
            for(int j = 1; j < BoardSize.COLUMNS; j++) {

            }
        }*/
       /*f String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48"};

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvNumbers);
        int numberOfColumns = 10;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MyRecyclerViewAdapter(this, data);
        recyclerView.setAdapter(adapter);*/

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
            String clickedTag = String.valueOf(view.getTag());
            ArrayList views = getViewsByTag(layout, clickedTag.substring(0, clickedTag.length() - 1));
            for(int i = 0; i < views.size(); i++) {
                ImageView obj = (ImageView) views.get(i);
                Log.i("FIRST view tags :", String.valueOf(obj.getTag()));
            }
            Log.i("views size at onclick :", String.valueOf(views.size()));
            for(int i = 0; i < views.size(); i++) {
                Object item = views.get(i);
                ImageView newImage = (ImageView) views.get(i);
                String tag = String.valueOf(newImage.getTag());
                char lastChar = tag.charAt(tag.length() - 1);
                Log.i("lastChar :", String.valueOf(lastChar));

                if (!TextUtils.isDigitsOnly(Character.toString(lastChar))) {
                    newImage.setTag(null);
                    views.remove(item);
                }
            }
            views = getViewsByTag(layout, clickedTag.substring(0, clickedTag.length() - 1));
            for(int i = 0; i < views.size(); i++) {
                ImageView obj = (ImageView) views.get(i);
                Log.i("SECOND view tags :", String.valueOf(obj.getTag()));
            }
            for(int i = 0; i < views.size(); i++) {
                ImageView newImage = (ImageView) views.get(i);
                newImage.setImageBitmap(rotateBitmap(((BitmapDrawable) newImage.getDrawable()).getBitmap(), 90));
            }
            rotatePlacementTileOnGrid(views, view);
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

        private void rotatePlacementTileOnGrid(ArrayList views, View clickedTile) {
            for(int i = 0; i < views.size(); i++) {
                ImageView obj = (ImageView) views.get(i);
                Log.i("view tags :", String.valueOf(obj.getTag()));
            }
            String clickedTileTag = String.valueOf(clickedTile.getTag());
Log.i("clickedTileTag :", String.valueOf(clickedTileTag));
            int clickedTileOrder = parseInt(clickedTileTag.substring(clickedTileTag.length() - 1));
            LinearLayout clickedParent = (LinearLayout) clickedTile.getParent();
            String clickedPlacement = String.valueOf(clickedParent.getTag());
            int clickedColI = Character.getNumericValue(clickedPlacement.charAt(7));
//Log.i("clickedColI :", String.valueOf(clickedColI));
            int clickedRowI = Character.getNumericValue(clickedPlacement.charAt(3));
//Log.i("clickedRowI :", String.valueOf(clickedRowI));
//Log.i("views size :", String.valueOf(views.size()));
//Log.i("Clicked tile order :", String.valueOf(clickedTileOrder));


            for(int i = 0; i < views.size(); i++) {
//Log.i("value of i :", String.valueOf(i));
                ImageView tile = (ImageView) views.get(i);

                String tileTag = String.valueOf(tile.getTag());
//Log.i("Old tag set : ", String.valueOf(tile.getTag()));
                int tileOrder = parseInt(tileTag.substring(tileTag.length() - 1));
//Log.i("tile order :", String.valueOf(tileOrder));
                int diffOrder = clickedTileOrder - tileOrder;
//Log.i("check diff order :", String.valueOf(diffOrder));

                LinearLayout oldParent = (LinearLayout) tile.getParent();
                String oldPlacement = String.valueOf(oldParent.getTag());

                //if(diffOrder != 0) {
                    oldParent.removeAllViews();
                    oldParent.setBackgroundResource(R.drawable.grid_stroke);
                    ImageView imageView = new ImageView(PlacementActivity.this);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    //imageView.setTag(null);
                    oldParent.addView(imageView);

                    int colI = Character.getNumericValue(oldPlacement.charAt(7));
                    int rowI = Character.getNumericValue(oldPlacement.charAt(3));
                    int newColI = colI;
                    int newRowI = rowI;


                    Log.i("tile previous location:", "row" + String.valueOf(rowI) + "col" + String.valueOf(colI));
                    // Checked position of boat
                    if(diffOrder > 0) {
                        if (clickedColI > colI && clickedRowI == rowI) {
                            // then horizontal left
                            newColI = colI + diffOrder;
                            newRowI = rowI - diffOrder;

                        } else if (clickedColI == colI && clickedRowI > rowI) {
                            // then vertical top
                            newColI = colI + diffOrder;
                            newRowI = rowI + diffOrder;

                        } else if (clickedColI < colI && clickedRowI == rowI) {
                            // then horizontal left
                            newColI = colI - diffOrder;
                            newRowI = rowI + diffOrder;

                        } else if (clickedColI == colI && clickedRowI < rowI) {
                            // then vertical top
                            newColI = colI - diffOrder;
                            newRowI = rowI - diffOrder;
                        }
                    } else if(diffOrder < 0) {
                        if (clickedColI < colI && clickedRowI == rowI) {
                            // then horizontal right
                            newColI = colI - Math.abs(diffOrder);
                            newRowI = rowI + Math.abs(diffOrder);

                        } else if (clickedColI == colI && clickedRowI > rowI) {
                            // then vertical down
                            newColI = colI + Math.abs(diffOrder);
                            newRowI = rowI + Math.abs(diffOrder);

                        } else if (clickedColI > colI && clickedRowI == rowI) {
                            // then horizontal right
                            newColI = colI + Math.abs(diffOrder);
                            newRowI = rowI - Math.abs(diffOrder);

                        } else if (clickedColI == colI && clickedRowI < rowI) {
                            // then vertical down
                            newColI = colI - Math.abs(diffOrder);
                            newRowI = rowI - Math.abs(diffOrder);
                        }
                    }


                    String sNewColI = String.valueOf(newColI);
                    String sNewRowI = String.valueOf(newRowI);

                    String newPosCol = oldPlacement.substring(0, 7) + sNewColI;
                    String newPos = newPosCol.substring(0, 3) + sNewRowI + newPosCol.substring(4);
                    Log.i("Tile new location : ", newPos);


                    LinearLayout newContainer = (LinearLayout) layout.findViewWithTag(newPos);
                    newContainer.setBackgroundResource(0);
                    ImageView newView = (ImageView) newContainer.getChildAt(0);
                Log.i("check number children: ", String.valueOf(newContainer.getChildCount()));



                Log.i("New tag set : ", String.valueOf(tile.getTag()));
                    newView.setTag(String.valueOf(tile.getTag()));




                    newView.setOnClickListener(myListener);
                    BitmapDrawable drawable = (BitmapDrawable) tile.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    newView.setImageBitmap(bitmap);


                //}
            }
            Log.i("views size :", String.valueOf(views.size()));

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
                    ImageView view = (ImageView) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);
                    //GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(view.getLayoutParams());
                    //container.setLayoutParams(layoutParams);

                    /**
                     * Split image in several images according to number of spans
                     */
                    ArrayList<Bitmap> chunkedImages = Crop.defineSplit(view);
                    double chunkedImagesSize = chunkedImages.size();
                    if(chunkedImagesSize %2 == 0) {
                        midTile = (int) chunkedImagesSize/2;
                    } else {
                        midTile = (int) Math.ceil(chunkedImagesSize/2);
                    }

                    /**
                     * Get My grid cases corresponding to size of chunkedImages list for display
                     * Copy all dragged image data into target image data
                     * Attach OnClickListener and Rotation method to target image data
                     */
                    Log.i("id taille image : ", String.valueOf(chunkedImagesSize));
                    Log.i("id taille image/2 : ", String.valueOf(midTile));
                    for(int i = 0; i <= midTile; i++) {
                        Object objTagMain = container.getTag();
                        String tagMain = String.valueOf(objTagMain);
                        Log.i("id dropped : ", tagMain);
                        char cMainI = tagMain.charAt(7);
                        int MainI = Character.getNumericValue(cMainI);
                        int neighbourTileI = MainI - i;
                        Log.i("id dropped col-i : ", String.valueOf(neighbourTileI));
                        String sNeighbourTileI = String.valueOf(neighbourTileI);
                        String tagNeighbour = tagMain.substring(0,7) + sNeighbourTileI;
                        Log.i("id tag previous tile : ", tagNeighbour);
                        LinearLayout neighbourContainer = (LinearLayout) layout.findViewWithTag(tagNeighbour);
                        neighbourContainer.setBackgroundResource(0);
                        ImageView oldView = (ImageView) view;
                        ImageView neighbourView = (ImageView) neighbourContainer.getChildAt(0);
                        neighbourView.setTag(String.valueOf(oldView.getTag())+(midTile - i));
                        neighbourView.setOnClickListener(myListener);
                        neighbourView.setImageBitmap(chunkedImages.get(midTile - i));
                    }

                    for(int i = midTile; i < chunkedImagesSize-1; i++) {
                        Object objTagMain = container.getTag();
                        String tagMain = String.valueOf(objTagMain);
                        Log.i("id dropped : ", tagMain);
                        char cMainI = tagMain.charAt(7);
                        int MainI = Character.getNumericValue(cMainI);
                        int neighbourTileI = MainI + nextTile;
                        Log.i("id dropped col+i : ", String.valueOf(neighbourTileI));
                        String sNeighbourTileI = String.valueOf(neighbourTileI);
                        String tagNeighbour = tagMain.substring(0,7) + sNeighbourTileI;
                        Log.i("id tag next tile : ", tagNeighbour);
                        LinearLayout neighbourContainer = (LinearLayout) layout.findViewWithTag(tagNeighbour);
                        neighbourContainer.setBackgroundResource(0);
                        ImageView oldView = (ImageView) view;
                        ImageView neighbourView = (ImageView) neighbourContainer.getChildAt(0);
                        neighbourView.setTag(String.valueOf(oldView.getTag())+(i+1));
                        neighbourView.setOnClickListener(myListener);
                        neighbourView.setImageBitmap(chunkedImages.get(i+1));
                        nextTile++;
                    }
                    nextTile = 1;

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
                    int porte_avionParentId = ((View) porteAvionImg.getParent()).getId();
                    int croiseurParentId = ((View) croiseurImg.getParent()).getId();
                    int contre_torpilleur1ParentId = ((View) contreTorpilleur1Img.getParent()).getId();
                    int contre_torpilleur2ParentId = ((View) contreTorpilleur2Img.getParent()).getId();
                    int torpilleurParentId = ((View) torpilleurImg.getParent()).getId();

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
     * Get all ImageViews from same boat
     */
    private static ArrayList<View> getViewsByTag(GridLayout root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final LinearLayout inner = (LinearLayout) root.getChildAt(i);
            final View grandChild = inner.getChildAt(0);
            final String tagObj = String.valueOf(grandChild.getTag());
            if (tagObj != null && tagObj.contains(tag)) {
                views.add(grandChild);
            }

        }
        return views;
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