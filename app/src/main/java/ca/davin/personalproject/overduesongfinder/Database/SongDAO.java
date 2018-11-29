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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertSongs(SongModel... songs);

    @Query("SELECT * FROM Songs")
    public List<SongModel> loadAllSongs();

    @Query("SELECT * FROM Songs WHERE filePath = :filePath LIMIT 1")
    public SongModel find(String filePath);

    @Update
    public void updateSongs(SongModel... songs);

    @Delete
    public void deleteSongs(SongModel... songs);
}
