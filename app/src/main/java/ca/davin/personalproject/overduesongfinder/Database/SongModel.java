package ca.davin.personalproject.overduesongfinder.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Songs")
public class SongModel {
    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "price")
    private double price;

    public SongModel(String name, String artist) {
        setName(name);
        setArtist(artist);
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void setArtist(String artist) {
        if (artist != null) {
            this.artist = artist;
        }
    }

    public void setPrice(double price) {
        if (!(price < 0)) {
            this.price = price;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getArtist() {
        return this.artist;
    }

    public double getPrice() {
        return this.price;
    }
}
