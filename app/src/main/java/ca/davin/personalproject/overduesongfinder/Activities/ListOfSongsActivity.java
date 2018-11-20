package ca.davin.personalproject.overduesongfinder.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ca.davin.personalproject.overduesongfinder.Adapters.SongAdapter;
import ca.davin.personalproject.overduesongfinder.Database.SongModel;
import ca.davin.personalproject.overduesongfinder.R;

public class ListOfSongsActivity extends AppCompatActivity {

    ExpandableListView songsListView;
    private ArrayList<SongModel> songs;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_songs);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        songsListView = findViewById(R.id.listOfSongs_listView);

        songs = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }
    private void setUpListView(ArrayList<SongModel> songs) {
        songsListView.setAdapter(new SongAdapter(ListOfSongsActivity.this, songs));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMP3Files(Environment.getExternalStorageDirectory().getPath());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
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
