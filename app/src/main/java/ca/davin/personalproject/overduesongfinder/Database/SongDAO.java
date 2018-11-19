package ca.davin.personalproject.overduesongfinder.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.davin.personalproject.overduesongfinder.Database.SongModel;

@Dao
public interface SongDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCaptions(SongModel... songs);

    @Query("SELECT * FROM Songs")
    public SongModel[] loadAllSongs();

    @Update
    public void updateCaptions(SongModel... songs);

    @Delete
    public void deleteCaptions(SongModel... songs);
}
