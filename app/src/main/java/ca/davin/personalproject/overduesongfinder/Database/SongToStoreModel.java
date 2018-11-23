package ca.davin.personalproject.overduesongfinder.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "SongToStore")
public class SongToStoreModel {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "storeURL")
    private String storeURL;

    @ColumnInfo(name = "storeName")
    private String storeName;

    @ColumnInfo(name = "fileName")
    private String filePath;

    public SongToStoreModel(String filePath, String storeName, String storeURL) {
        setFilePath(filePath);
        setStoreName(storeName);
        setStoreURL(storeURL);
    }

    public void setFilePath(String filePath) {
        if (filePath != null) {
            this.filePath = filePath;
        }
    }

    public String getFilePath() {
        return this.filePath;
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
