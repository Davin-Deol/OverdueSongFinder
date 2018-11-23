package ca.davin.personalproject.overduesongfinder.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Songs")
public class SongModel {
    @ColumnInfo(name = "filePath")
    @NonNull
    @PrimaryKey
    private String filePath;

    @ColumnInfo(name = "price")
    private Double price;

    public SongModel(String filePath) {
        setFilePath(filePath);
    }

    public void setFilePath(String filePath) {
        if (filePath != null) {
            this.filePath = filePath;
        }
    }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    public String getFilePath() {
        return this.filePath;
    }

    public Double getPrice() {
        return this.price;
    }
}
