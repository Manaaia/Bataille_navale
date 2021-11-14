package fr.afpa.bataille_navale;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Board {
    public static final int ROWS = 10;
    public static final int COLUMNS = 10;

    public void createBoard(Context context, GridLayout layout) {
        /**
         * Create the positioning board programmatically in the GridLayout view
         */
        layout.setRowCount(Board.ROWS);
        layout.setColumnCount(Board.COLUMNS);

        for(int i = 0; i < Board.ROWS; i++) {
            GridLayout.Spec rowSpec = GridLayout.spec(i, 1, 1);
            for(int j = 0; j < Board.COLUMNS; j++) {
                GridLayout.Spec colSpec = GridLayout.spec(j, 1, 1);
                LinearLayout linearLayout = new LinearLayout(new ContextThemeWrapper(context,R.style.Grid));
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setId(R.id.row + i + R.id.col + j);
                linearLayout.setTag("row"+ i + "col" + j);
                linearLayout.setGravity(Gravity.FILL);
                ImageView imageView = new ImageView(context);
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

    public void createGameBoard(Context context, GridLayout layout) {
        /**
         * Create the game board programmatically in the GridLayout view
         */
        layout.setRowCount(Board.ROWS);
        layout.setColumnCount(Board.COLUMNS);

        for(int i = 0; i < Board.ROWS; i++) {
            GridLayout.Spec rowSpec = GridLayout.spec(i, 1, 1);
            for(int j = 0; j < Board.COLUMNS; j++) {
                GridLayout.Spec colSpec = GridLayout.spec(j, 1, 1);
                LinearLayout linearLayout = new LinearLayout(new ContextThemeWrapper(context,R.style.Grid));
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setId(R.id.row + i + R.id.col + j);
                linearLayout.setTag("row"+ i + "col" + j);
                linearLayout.setGravity(Gravity.FILL);
                FrameLayout frameLayout = new FrameLayout(context);
                frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                ImageView imageView1 = new ImageView(context);
                imageView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView1.setAdjustViewBounds(true);
                imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
                ImageView imageView2 = new ImageView(context);
                imageView2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView2.setAdjustViewBounds(true);
                imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
                frameLayout.addView(imageView1);
                frameLayout.addView(imageView2);
                linearLayout.addView(frameLayout);
                GridLayout.LayoutParams myGLP = new GridLayout.LayoutParams();
                myGLP.rowSpec = rowSpec;
                myGLP.columnSpec = colSpec;
                myGLP.width = 0;
                myGLP.height = 0;
                layout.addView(linearLayout, myGLP);
            }
        }
    }
}
