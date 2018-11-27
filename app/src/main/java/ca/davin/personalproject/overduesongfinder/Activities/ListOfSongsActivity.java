package ca.davin.personalproject.overduesongfinder.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ca.davin.personalproject.overduesongfinder.Adapters.SongAdapter;
import ca.davin.personalproject.overduesongfinder.Database.SongModel;
import ca.davin.personalproject.overduesongfinder.R;

public class ListOfSongsActivity extends AppCompatActivity {

    ExpandableListView songsListView;
    private final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_songs);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        songsListView = findViewById(R.id.listOfSongs_listView);

        ArrayList<SongModel> songs = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                setUpListView(songs);
        } else {
            songs = getMP3Files(Environment.getExternalStorageDirectory().getPath());
            setUpListView(songs);
        }
    }

    private ArrayList<SongModel> getMP3Files(String rootPath) {
        ArrayList<SongModel> fileList = new ArrayList<>();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getMP3Files(file.getAbsolutePath()) != null) {
                        fileList.addAll(getMP3Files(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    mmr.setDataSource(file.getAbsolutePath());
                    SongModel songModel = new SongModel(file.getAbsolutePath());
                    fileList.add(songModel);
                }
            }
        } catch (Exception e) {
            Log.d("Exception: ", e.getMessage());
        }
        return fileList;
    }
    private void setUpListView(ArrayList<SongModel> songs) {
        songsListView.setAdapter(new SongAdapter(ListOfSongsActivity.this, songs));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    getMP3Files(Environment.getExternalStorageDirectory().getPath());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Toast.makeText(this, "Info Pressed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_findSongs:
                Toast.makeText(this, "Find Songs Pressed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_purchase:
                Toast.makeText(this, "Purchase Pressed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_of_songs, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
