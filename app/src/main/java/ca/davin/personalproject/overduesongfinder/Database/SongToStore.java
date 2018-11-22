package ca.davin.personalproject.overduesongfinder.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "SongToStore")
public class SongToStore {
    @ColumnInfo(name = "fileName")
    @PrimaryKey
    private String filePath;

    @ColumnInfo(name = "storeURL")
    private String storeURL;

    public SongToStore(String filePath, String storeURL) {
        setFilePath(filePath);
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
    public void setStoreURL(String storeURL) {
        if (storeURL != null) {
            this.storeURL = storeURL;
        }
    }
    public String getStoreURL() {
        return this.storeURL;
    }
}
