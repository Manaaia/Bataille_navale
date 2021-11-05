import android.content.Context;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import fr.afpa.bataille_navale.BoardSize;

public class Cases {
    private ArrayList cases[];

    public ArrayList[] getCases(Context context) {
        //cases = new ArrayList[];
        for(int i = 1; i < BoardSize.ROWS; i++) {
            for(int j = 1; j < BoardSize.COLUMNS; j++) {
                ImageView img = new ImageView(context);
            }
        }
        return cases;
    }
}
