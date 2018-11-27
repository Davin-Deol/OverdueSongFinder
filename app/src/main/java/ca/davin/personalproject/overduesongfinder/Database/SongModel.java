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
    private String filePath = "";

    @ColumnInfo(name = "price")
    private Double price;

    @ColumnInfo(name = "storeURL")
    private String storeURL;

    @ColumnInfo(name = "storeName")
    private String storeName;

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

    @NonNull
    public String getFilePath() {
        return this.filePath;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setStoreName(String storeName) {
        if (storeName != null) {
            this.storeName = storeName;
        }
    }

    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreURL(String storeURL) {
        if (storeURL != null) {
            this.storeURL = storeURL;
        }
    }

    public String getStoreURL() {
        return this.storeURL;
    }
}
