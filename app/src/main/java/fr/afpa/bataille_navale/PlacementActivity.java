package fr.afpa.bataille_navale;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class PlacementActivity extends AppCompatActivity  {

    private Boolean mDroppedIn = null;
    private View porteAvionImg;
    private View croiseurImg;
    private View contreTorpilleur1Img;
    private View contreTorpilleur2Img;
    private View torpilleurImg;
    private Button button;
    private GridLayout gridStore;
    private GridLayout layout;
    private int midTile;
    private int nextTile = 1;
    private Boat porteAvion;
    private Boat croiseur;
    private Boat contreTorpilleur1;
    private Boat contreTorpilleur2;
    private Boat torpilleur;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        mediaPlayer = MediaPlayer.create(PlacementActivity.this,R.raw.wave2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        button = findViewById(R.id.startGameBtn);
        gridStore = findViewById(R.id.StoreShips);

        /**
         * Create boats
         */
        porteAvion = new Boat("player","porteAvion",6, 6);
        croiseur = new Boat("player","croiseur",3, 3);
        contreTorpilleur1 = new Boat("player","1contreTorpilleur",5, 5);
        contreTorpilleur2 = new Boat("player","2contreTorpilleur",5, 5);
        torpilleur = new Boat("player","torpilleur",4, 4);


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
        contreTorpilleur2Img.setTag("2contreTorpilleur");
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
        layout = findViewById(R.id.MyGrid);
        Board board = new Board();
        board.createBoard(this, layout);
        final int childCount = layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final LinearLayout linearLayout = (LinearLayout) layout.getChildAt(i);
            linearLayout.setOnDragListener(new MyDragListener());
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
     * OnClickListener get list of bitmaps and check its validity
     * Check if rotation is possible
     * Call to rotation functions if true
     */
    class MyDragListener implements View.OnDragListener {
        private View.OnClickListener myListener = view -> {
            Log.e("Image name", view.getContentDescription() + "");
            String clickedTag = String.valueOf(view.getTag());
            View parent = (View) view.getParent();
            String clickedPlacement = String.valueOf(parent.getTag());
            ArrayList views = getViewsByTag(layout, clickedTag.substring(0, clickedTag.length() - 1));

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

            if(checkFullValidityOfNewRotatedPosition(views, view, clickedPlacement)) {
                for (int i = 0; i < views.size(); i++) {
                    ImageView newImage = (ImageView) views.get(i);
                    // Rotate bitmaps
                    newImage.setImageBitmap(Rotate.rotateBitmap(((BitmapDrawable) newImage.getDrawable()).getBitmap(), 90));
                }
                rotatePlacementTileOnGrid(views, view);
            } else {
                Toast.makeText(PlacementActivity.this, "Placement non valide", Toast.LENGTH_SHORT).show();
            }
        };

        /**
         * Rotate same boat tile around the clicked tile
         */
        private void rotatePlacementTileOnGrid(ArrayList views, View clickedTile) {
            View boat;
            for(int i = 0; i < views.size(); i++) {
                ImageView obj = (ImageView) views.get(i);
                Log.i("view tags :", String.valueOf(obj.getTag()));
            }
            String clickedTileTag = String.valueOf(clickedTile.getTag());
            Log.i("clickedTileTag :", clickedTileTag);
            int clickedTileOrder = parseInt(clickedTileTag.substring(clickedTileTag.length() - 1));
            LinearLayout clickedParent = (LinearLayout) clickedTile.getParent();
            String clickedPlacement = String.valueOf(clickedParent.getTag());

            for (int i = 0; i < views.size(); i++) {

                ImageView tile = (ImageView) views.get(i);
                String tileTag = String.valueOf(tile.getTag());
                String tagBoat = tileTag.substring(0, tileTag.length() - 1);
                int tileOrder = parseInt(tileTag.substring(tileTag.length() - 1));
                int diffOrder = clickedTileOrder - tileOrder;
                LinearLayout oldParent = (LinearLayout) tile.getParent();
                String oldPlacement = String.valueOf(oldParent.getTag());

                oldParent.removeAllViews();
                oldParent.setBackgroundResource(R.drawable.grid_stroke);
                ImageView imageView = new ImageView(PlacementActivity.this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                oldParent.addView(imageView);

                String orientation = getOrientation(oldPlacement, clickedPlacement, diffOrder);
                String newPos = getNewPositionOfTileOnRotation(oldPlacement, orientation, diffOrder);
                Log.i("Tile new location : ", newPos);

                LinearLayout newContainer = layout.findViewWithTag(newPos);

                boat = getViewBoatFromTag(tagBoat);
                Log.i("tagBoat : ", tagBoat);

                ViewGroup parent = (ViewGroup) boat.getParent();
                if (parent != null) {
                    parent.removeView(boat);
                    boat.setVisibility(View.GONE);
                } else {
                    Log.i("erreur", "pas de parent");
                }
                newContainer.addView(boat);
                newContainer.setBackgroundResource(0);
                newContainer.setBackgroundResource(R.drawable.grid_stroke);
                ImageView newView = (ImageView) newContainer.getChildAt(0);

                Log.i("New tag set : ", String.valueOf(tile.getTag()));
                newView.setTag(String.valueOf(tile.getTag()));

                newView.setOnClickListener(myListener);
                BitmapDrawable drawable = (BitmapDrawable) tile.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                newView.setImageBitmap(bitmap);
            }

            Log.i("views size :", String.valueOf(views.size()));
        }

        /**
         * Define actions depending on drag and drop status
         */
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            ImageView oldView = (ImageView) event.getLocalState();
            String tagView = String.valueOf(oldView.getTag());
            LinearLayout container = (LinearLayout) v;
            ArrayList<Bitmap> chunkedImages = Crop.defineSplit(oldView);
            ArrayList listOfPos = getNewPositionOfTileOnDrop(container, chunkedImages, tagView);

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (!mediaPlayer.isPlaying())
                        mediaPlayer.reset();  // Clears mp state
                        mediaPlayer.start();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    /**
                     * Change background of the layout where item is entering
                     */
                    if(checkFullValidityOfNewDroppedPosition(container, chunkedImages, tagView)) {
                        for(int i = 0; i < listOfPos.size(); i++) {
                            Log.i("listSize", String.valueOf(listOfPos.size()));
                            Log.i("listSize", String.valueOf(listOfPos));
                            String tag = String.valueOf(listOfPos.get(i));
                            int tagLength = tag.length();
                            int col;
                            if(tagLength == 8) {
                                col = Character.getNumericValue(tag.charAt(7));
                            } else {
                                int col1 = Character.getNumericValue(tag.charAt(7));
                                int col2 = Character.getNumericValue(tag.charAt(8));
                                String sCol = String.valueOf(col1) + String.valueOf(col2);
                                col = parseInt(sCol);
                            }
                            int row = Character.getNumericValue(tag.charAt(3));
                            if((row >= 0 && row < 10) && (col >= 0 && col < 10)) {
                                View possibleDrop = layout.findViewWithTag(listOfPos.get(i));
                                possibleDrop.setBackgroundColor(Color.parseColor("#D1F6FF"));
                            }
                        }
                    } else {
                        for(int i = 0; i < listOfPos.size(); i++) {
                            String tag = String.valueOf(listOfPos.get(i));
                            int tagLength = tag.length();
                            int col;
                            if(tagLength == 8) {
                                col = Character.getNumericValue(tag.charAt(7));
                            } else {
                                int col1 = Character.getNumericValue(tag.charAt(7));
                                int col2 = Character.getNumericValue(tag.charAt(8));
                                String sCol = String.valueOf(col1) + String.valueOf(col2);
                                col = parseInt(sCol);
                            }
                            int row = Character.getNumericValue(tag.charAt(3));
                            if((row >= 0 && row < 10) && (col >= 0 && col < 10)) {
                                View possibleDrop = layout.findViewWithTag(listOfPos.get(i));
                                possibleDrop.setBackgroundColor(Color.parseColor("#FFBABA"));
                            }
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    /**
                     * Change background of the layout back to normal once item is moved out of it
                     */
                    for(int i = 0; i < listOfPos.size(); i++) {
                        String tag = String.valueOf(listOfPos.get(i));
                        int tagLength = tag.length();
                        int col;
                        if(tagLength == 8) {
                            col = Character.getNumericValue(tag.charAt(7));
                        } else {
                            int col1 = Character.getNumericValue(tag.charAt(7));
                            int col2 = Character.getNumericValue(tag.charAt(8));
                            String sCol = String.valueOf(col1) + String.valueOf(col2);
                            col = parseInt(sCol);
                        }
                        int row = Character.getNumericValue(tag.charAt(3));
                        if((row >= 0 && row < 10) && (col >= 0 && col < 10)) {
                            View possibleDrop = layout.findViewWithTag(listOfPos.get(i));
                            possibleDrop.setBackgroundColor(Color.parseColor("#003C5F"));
                            possibleDrop.setBackgroundResource(R.drawable.grid_stroke);
                        }
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    /**
                     * Check validity of new position
                     */
                    if(checkFullValidityOfNewDroppedPosition(container, chunkedImages, tagView)) {

                        /**
                         * Reassign view to view group
                         */
                        ViewGroup owner = (ViewGroup) oldView.getParent();
                        owner.removeView(oldView);
                        container.addView(oldView);
                        oldView.setVisibility(View.VISIBLE);

                        /**
                         * get MidTile
                         */
                        midTile = getMidTile(chunkedImages);

                        /**
                         * Get My grid cases corresponding to size of chunkedImages list for display
                         * Copy all dragged image data into target image data
                         * Attach OnClickListener and Rotation method to target image data
                         */
                        Object objTagMain = container.getTag();
                        String tagMain = String.valueOf(objTagMain);
                        Log.i("id dropped : ", tagMain);
                        char cMainI = tagMain.charAt(7);
                        int MainI = Character.getNumericValue(cMainI);

                        for (int i = 0; i <= midTile; i++) {
                            int neighbourTileI = MainI - i;
                            Log.i("id dropped col-i : ", String.valueOf(neighbourTileI));
                            String sNeighbourTileI = String.valueOf(neighbourTileI);
                            String tagNeighbour = tagMain.substring(0, 7) + sNeighbourTileI;
                            Log.i("id tag previous tile : ", tagNeighbour);
                            LinearLayout neighbourContainer = layout.findViewWithTag(tagNeighbour);
                            neighbourContainer.setBackgroundResource(0);
                            neighbourContainer.setBackgroundResource(R.drawable.grid_stroke);
                            ImageView neighbourView = (ImageView) neighbourContainer.getChildAt(0);
                            neighbourView.setTag(String.valueOf(oldView.getTag()) + (midTile - i));
                            neighbourView.setOnClickListener(myListener);
                            neighbourView.setImageBitmap(chunkedImages.get(midTile - i));
                        }

                        for (int i = midTile; i < chunkedImages.size() - 1; i++) {
                            int neighbourTileI = MainI + nextTile;
                            Log.i("next Tile : ", String.valueOf(nextTile));
                            Log.i("id dropped col+i : ", String.valueOf(neighbourTileI));
                            String sNeighbourTileI = String.valueOf(neighbourTileI);
                            String tagNeighbour = tagMain.substring(0, 7) + sNeighbourTileI;
                            Log.i("id tag next tile : ", tagNeighbour);
                            LinearLayout neighbourContainer = layout.findViewWithTag(tagNeighbour);
                            neighbourContainer.setBackgroundResource(0);
                            neighbourContainer.setBackgroundResource(R.drawable.grid_stroke);
                            ImageView neighbourView = (ImageView) neighbourContainer.getChildAt(0);
                            neighbourView.setTag(String.valueOf(oldView.getTag()) + (i + 1));
                            neighbourView.setOnClickListener(myListener);
                            neighbourView.setImageBitmap(chunkedImages.get(i + 1));
                            nextTile++;

                        }
                        nextTile = 1;
                    } else {
                        Toast.makeText(PlacementActivity.this, "Placement non valide", Toast.LENGTH_SHORT).show();
                        for(int i = 0; i < listOfPos.size(); i++) {
                            String tag = String.valueOf(listOfPos.get(i));
                            int tagLength = tag.length();
                            int col;
                            if(tagLength == 8) {
                                col = Character.getNumericValue(tag.charAt(7));
                            } else {
                                int col1 = Character.getNumericValue(tag.charAt(7));
                                int col2 = Character.getNumericValue(tag.charAt(8));
                                String sCol = String.valueOf(col1) + String.valueOf(col2);
                                col = parseInt(sCol);
                            }
                            int row = Character.getNumericValue(tag.charAt(3));
                            if((row >= 0 && row < 10) && (col >= 0 && col < 10)) {
                                View possibleDrop = layout.findViewWithTag(listOfPos.get(i));
                                possibleDrop.setBackgroundColor(Color.parseColor("#003C5F"));
                                possibleDrop.setBackgroundResource(R.drawable.grid_stroke);
                            }
                        }
                        View currentView = (View) event.getLocalState();
                        currentView.setVisibility(View.VISIBLE);
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    /**
                     * Check if element dropped in target or outside target
                     * If outside, set it back to its initial place
                     */
                    View currentView = (View) event.getLocalState();
                    reportResult(event.getResult());
                    if(!mDroppedIn) {
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
     * Get upper mid value of an ArrayList
     */
    public static int getMidTile(ArrayList chunkedImages) {
        double chunkedImagesSize = chunkedImages.size();
        int midTile = 0;
        if (chunkedImagesSize % 2 == 0) {
            midTile = (int) chunkedImagesSize / 2;
        } else {
            midTile = (int) Math.ceil(chunkedImagesSize / 2);
        }
        return midTile;
    }

    /**
     * Check drop status (in target = true, out of target = false)
     */
    private void reportResult(final boolean result) {
        mDroppedIn = result;
    }

    /**
     * Get all ImageViews from same boat
     */
    private static ArrayList<View> getViewsByTag(GridLayout root, String tag){
        ArrayList<View> views = new ArrayList<>();
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
     * Return ImageView of boat according to tag
     */
    private View getViewBoatFromTag(String tagBoat) {
        View boat = null;
        if(tagBoat.equals("croiseur")) {
            boat = croiseurImg;
        } else if(tagBoat.equals("torpilleur")) {
            boat = torpilleurImg;
        } else if(tagBoat.equals("porteAvion")) {
            boat = porteAvionImg;
        } else if(tagBoat.equals("1contreTorpilleur")) {
            boat = contreTorpilleur1Img;
        } else if(tagBoat.equals("2contreTorpilleur")) {
            boat = contreTorpilleur2Img;
        }
        return boat;
    }

    /**
     * Calculate the new location of tile on grid for rotation
     */
    public String getNewPositionOfTileOnRotation(String oldPlacement,String  orientation, int diffOrder) {

        int colI = Character.getNumericValue(oldPlacement.charAt(7));
        int rowI = Character.getNumericValue(oldPlacement.charAt(3));
        int newColI = colI;
        int newRowI = rowI;


        Log.i("tile previous location:", "row" + rowI + "col" + colI);
        // Checked position of boat
        if(diffOrder > 0) {
            //then tile before clicked tile
            if (orientation == "horizontal_left") {
                // then horizontal left
                newColI = colI + diffOrder;
                newRowI = rowI - diffOrder;

            } else if (orientation == "vertical_top") {
                // then vertical top
                newColI = colI + diffOrder;
                newRowI = rowI + diffOrder;

            } else if (orientation == "horizontal_right") {
                // then horizontal right
                newColI = colI - diffOrder;
                newRowI = rowI + diffOrder;

            } else if (orientation == "vertical_down") {
                // then vertical down
                newColI = colI - diffOrder;
                newRowI = rowI - diffOrder;
            }
        } else if(diffOrder < 0) {
            //then tile after clicked tile
            if (orientation == "horizontal_left") {
                // then horizontal left
                newColI = colI - Math.abs(diffOrder);
                newRowI = rowI + Math.abs(diffOrder);

            } else if (orientation == "vertical_top") {
                // then vertical top
                newColI = colI + Math.abs(diffOrder);
                newRowI = rowI + Math.abs(diffOrder);

            } else if (orientation == "horizontal_right") {
                // then horizontal right
                newColI = colI + Math.abs(diffOrder);
                newRowI = rowI - Math.abs(diffOrder);

            } else if (orientation == "vertical_down") {
                // then vertical down
                newColI = colI - Math.abs(diffOrder);
                newRowI = rowI - Math.abs(diffOrder);
            }
        }

        String sNewColI = String.valueOf(newColI);
        String sNewRowI = String.valueOf(newRowI);

        String newPosCol = oldPlacement.substring(0, 7) + sNewColI;

        return newPosCol.substring(0, 3) + sNewRowI + newPosCol.substring(4);
    }

    /**
     * Get full orientation of boat (0, 90, 180, 270)
     */
    public String getOrientation(String tilePos,String  nextTilePos, int diffOrder) {
        String orientation = null;
        int clickedColI = Character.getNumericValue(nextTilePos.charAt(7));
        int clickedRowI = Character.getNumericValue(nextTilePos.charAt(3));
        int colI = Character.getNumericValue(tilePos.charAt(7));
        int rowI = Character.getNumericValue(tilePos.charAt(3));

        // Checked position of boat
        if(diffOrder > 0) {
            //then tile before clicked tile
            if (clickedColI > colI && clickedRowI == rowI) {
                // then horizontal left
                orientation = "horizontal_left";

            } else if (clickedColI == colI && clickedRowI > rowI) {
                // then vertical top
                orientation = "vertical_top";

            } else if (clickedColI < colI && clickedRowI == rowI) {
                // then horizontal right
                orientation = "horizontal_right";

            } else if (clickedColI == colI && clickedRowI < rowI) {
                // then vertical down
                orientation = "vertical_down";
            }
        } else if(diffOrder < 0) {
            //then tile after clicked tile
            if (clickedColI < colI && clickedRowI == rowI) {
                // then horizontal left
                orientation = "horizontal_left";

            } else if (clickedColI == colI && clickedRowI > rowI) {
                // then vertical top
                orientation = "vertical_top";

            } else if (clickedColI > colI && clickedRowI == rowI) {
                // then horizontal right
                orientation = "horizontal_right";

            } else if (clickedColI == colI && clickedRowI < rowI) {
                // then vertical down
                orientation = "vertical_down";
            }
        }

        return orientation;
    }

    /**
     * Check if boat is horizontal or vertical
     */
    public String getSimpleOrientation(String tilePos,String  nextTilePos, int diffOrder) {
        String orientation = getOrientation(tilePos,nextTilePos,diffOrder);
        String simpleOrientation = null;
        if(orientation.contains("horizontal")) {
            simpleOrientation = "horizontal";
        } else {
            simpleOrientation = "vertical";
        }
        return simpleOrientation;
    }

    /**
     * Calculate the new location of each tile on drop
     * Check if new position is valid
     */
    public boolean checkFullValidityOfNewDroppedPosition (LinearLayout ll, ArrayList<Bitmap> list, String tag) {
        boolean flag;
        int ok = list.size();

        double listSize = list.size();
        if (listSize % 2 == 0) {
            midTile = (int) listSize / 2;
        } else {
            midTile = (int) Math.ceil(listSize / 2);
        }

        for (int i = 0; i <= midTile; i++) {
            Object objTagMain = ll.getTag();
            String tagMain = String.valueOf(objTagMain);
            char cMainI = tagMain.charAt(7);
            int MainI = Character.getNumericValue(cMainI);
            int neighbourTileI = MainI - i;
            String sNeighbourTileI = String.valueOf(neighbourTileI);
            String tagNeighbour = tagMain.substring(0, 7) + sNeighbourTileI;

            if(!checkValidityOfNewTilePosition(tagNeighbour, tagMain)) {
                ok--;
            }
        }

        for (int i = midTile; i < listSize - 1; i++) {
            Object objTagMain = ll.getTag();
            String tagMain = String.valueOf(objTagMain);
            char cMainI = tagMain.charAt(7);
            int MainI = Character.getNumericValue(cMainI);
            int neighbourTileI = MainI + nextTile;
            String sNeighbourTileI = String.valueOf(neighbourTileI);
            String tagNeighbour = tagMain.substring(0, 7) + sNeighbourTileI;

            if(!checkValidityOfNewTilePosition(tagNeighbour, tag)) {
                ok--;
            }
            nextTile++;
        }
        nextTile = 1;

        if(ok != list.size()) {
            flag = false;
        } else {
            flag = true;
        }

        return flag;
    }

    /**
     * Calculate the new location of each tile on drop and return it
     */
    public ArrayList getNewPositionOfTileOnDrop(LinearLayout ll, ArrayList<Bitmap> list, String tag) {
        ArrayList listOfPos = new ArrayList();
        double listSize = list.size();
        if (listSize % 2 == 0) {
            midTile = (int) listSize / 2;
        } else {
            midTile = (int) Math.ceil(listSize / 2);
        }

        for (int i = 0; i <= midTile; i++) {
            Object objTagMain = ll.getTag();
            String tagMain = String.valueOf(objTagMain);
            char cMainI = tagMain.charAt(7);
            int MainI = Character.getNumericValue(cMainI);
            int neighbourTileI = MainI - i;
            String sNeighbourTileI = String.valueOf(neighbourTileI);
            String tagNeighbour = tagMain.substring(0, 7) + sNeighbourTileI;
            listOfPos.add(tagNeighbour);
        }

        for (int i = midTile; i < listSize - 1; i++) {
            Object objTagMain = ll.getTag();
            String tagMain = String.valueOf(objTagMain);
            char cMainI = tagMain.charAt(7);
            int MainI = Character.getNumericValue(cMainI);
            int neighbourTileI = MainI + nextTile;
            //Log.i("nextTile", String.valueOf(nextTile));
            String sNeighbourTileI = String.valueOf(neighbourTileI);
            String tagNeighbour = tagMain.substring(0, 7) + sNeighbourTileI;
            //Log.i("tagNeighbour", String.valueOf(tagNeighbour));
            listOfPos.add(tagNeighbour);
            nextTile++;
        }
        nextTile = 1;

        return listOfPos;
    }

    /**
     * Calculate the new location of each tile on click
     * Check if new position is valid
     */
    private boolean checkFullValidityOfNewRotatedPosition(ArrayList list, View clickedTile, String clickedPlacement) {
        boolean flag;
        int ok = list.size();
        String clickedTileTag = String.valueOf(clickedTile.getTag());
        int clickedTileOrder = parseInt(clickedTileTag.substring(clickedTileTag.length() - 1));

        for(int i = 0; i < list.size(); i++) {

            ImageView tile = (ImageView) list.get(i);
            String tileTag = String.valueOf(tile.getTag());
            int tileOrder = parseInt(tileTag.substring(tileTag.length() - 1));
            int diffOrder = clickedTileOrder - tileOrder;
            LinearLayout oldParent = (LinearLayout) tile.getParent();
            String oldPlacement = String.valueOf(oldParent.getTag());

            String orientation = getOrientation(oldPlacement, clickedPlacement, diffOrder);
            String newPos = getNewPositionOfTileOnRotation(oldPlacement, orientation, diffOrder);

            if(!checkValidityOfNewTilePosition(newPos, clickedTileTag)) {
                ok--;
            }
        }

        if(ok != list.size()) {
            flag = false;
        } else {
            flag = true;
        }

        return flag;
    }

    /**
     * Check if new position is valid
     */
    public boolean checkValidityOfNewTilePosition(String tag, String clickedTag) {
        View childCheck = null;
        boolean flag = true;
        String tagChild;

        try {
            LinearLayout neighbourContainer = layout.findViewWithTag(tag);
            childCheck = neighbourContainer.getChildAt(0);
        } catch (NullPointerException e) {
           flag = false;
        }
        if(childCheck != null) {
            tagChild = String.valueOf(childCheck.getTag());
            if(!tagChild.equals("null") && !tagChild.contains(clickedTag)) {
                flag = false;
            }
        } else {
            flag = false;
        }

        return flag;
    }

    /**
     * Get positions of all tiles of boat on grid
     */
    public ArrayList getCurrentPositionOfBoat(String tag) {
        ArrayList listOfTiles = getViewsByTag(layout, tag);
        ArrayList positionList = new ArrayList();
        for(int i = 0; i < listOfTiles.size(); i++) {
            View tile = (View) listOfTiles.get(i);
            View parent = (View) tile.getParent();
            String pos = String.valueOf(parent.getTag());
            positionList.add(pos);
        }
        return positionList;
    }

    /**
     * Reset position of boats / Reload page
     */
    public void clickResetPlacement(View v) {
        finish();
        startActivity(getIntent());
    }

    /**
     * set position of boats on grid
     * set simple orientation of boats on grid
     * add boats to database
     * Sent intent to GameActivity
     */
    public void clickLaunchGame(View v) {

        // Add final position to boat object
        porteAvion.setPosition(getCurrentPositionOfBoat(porteAvion.getName()));
        croiseur.setPosition(getCurrentPositionOfBoat(croiseur.getName()));
        contreTorpilleur1.setPosition(getCurrentPositionOfBoat(contreTorpilleur1.getName()));
        contreTorpilleur2.setPosition(getCurrentPositionOfBoat(contreTorpilleur2.getName()));
        torpilleur.setPosition(getCurrentPositionOfBoat(torpilleur.getName()));

        // Add final orientation to boat object
        porteAvion.setOrientation(getSimpleOrientation(String.valueOf(porteAvion.getPosition().get(0)), String.valueOf(porteAvion.getPosition().get(1)), 1));
        croiseur.setOrientation(getSimpleOrientation(String.valueOf(croiseur.getPosition().get(0)), String.valueOf(croiseur.getPosition().get(1)), 1));
        contreTorpilleur1.setOrientation(getSimpleOrientation(String.valueOf(contreTorpilleur1.getPosition().get(0)), String.valueOf(contreTorpilleur1.getPosition().get(1)), 1));
        contreTorpilleur2.setOrientation(getSimpleOrientation(String.valueOf(contreTorpilleur2.getPosition().get(0)), String.valueOf(contreTorpilleur2.getPosition().get(1)), 1));
        torpilleur.setOrientation(getSimpleOrientation(String.valueOf(torpilleur.getPosition().get(0)), String.valueOf(torpilleur.getPosition().get(1)), 1));

        // Insert boat object into database
        BoatMgr boatMgr = new BoatMgr(PlacementActivity.this);
        boatMgr.open();
        boatMgr.deleteBoat();
        boatMgr.insertBoat(porteAvion);
        boatMgr.insertBoat(croiseur);
        boatMgr.insertBoat(contreTorpilleur1);
        boatMgr.insertBoat(contreTorpilleur2);
        boatMgr.insertBoat(torpilleur);
        boatMgr.close();

        // Start GameActivity
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }
}