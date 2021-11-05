package fr.afpa.bataille_navale;

import android.os.Parcel;
import android.os.Parcelable;

public class Board implements Parcelable {

    private BoardStatus[][] statuses;

    public Board() {
        this.statuses = new BoardStatus[BoardSize.COLUMNS][BoardSize.ROWS];
    }

    protected Board(Parcel in) {
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    public BoardStatus getStatus(int x, int y) {
        return statuses[x][y];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    enum BoardStatus {
        HIDDEN_EMPTY, HIDDEN_SHIP, HIT, MISS, SUNK
    }
}
