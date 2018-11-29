package ca.davin.personalproject.overduesongfinder.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import ca.davin.personalproject.overduesongfinder.Database.SongModel;

@Database(entities = {SongModel.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract SongDAO songDAO();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app-database")
                    //.addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }


    /*
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Songs' RENAME TO 'Songs_old'");
            database.execSQL("CREATE TABLE 'Songs' (" +
                    "'filepath' TEXT PRIMARY KEY NOT NULL DEFAULT ''," +
                    "'price' REAL NOT NULL DEFAULT 0," +
                    "'storeURL' TEXT," +
                    "'storeName' TEXT)");
            //database.execSQL("INSERT INTO 'Songs'('filepath', 'price', 'storeURL', 'storeName') SELECT filepath, price, storeURL, storeName FROM 'Songs_old'");
            database.execSQL("DROP TABLE 'Songs_old'");
        }
    };
    */

    public static void destroyInstance() {
        instance = null;
    }
}
