package ca.davin.personalproject.overduesongfinder.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ca.davin.personalproject.overduesongfinder.Adapters.SongAdapter;
import ca.davin.personalproject.overduesongfinder.Database.AppDatabase;
import ca.davin.personalproject.overduesongfinder.Database.SongModel;
import ca.davin.personalproject.overduesongfinder.R;

public class ListOfSongsActivity extends AppCompatActivity {
    private AppDatabase db;

    private final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE=1;

    //ExpandableListView songsListView;
    ListView songsListView;
    private ArrayList<SongModel> songs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_songs);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        db = AppDatabase.getInstance(this);


        // Clear data code

        /*
        List<SongModel> songModels = db.songDAO().loadAllSongs();

        for (SongModel s : songModels) {
            File file = new File(s.getFilePath());
            file.delete();
        }


        db.songDAO().deleteSongs(songModels.toArray(new SongModel[0]));
        */



        songsListView = findViewById(R.id.listOfSongs_listView);

        songs = new ArrayList<>();

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
        SongModel songModel = null;
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
                    songModel = new SongModel(file.getAbsolutePath());
                    fileList.add(songModel);
                }
            }
        } catch (Exception e) {
            Log.d("Exception: ", e.getMessage());
        }
        return fileList;
    }
    private void setUpListView(ArrayList<SongModel> songs) {
        db.songDAO().insertSongs(songs.toArray(new SongModel[0]));
        //songs.clear();
        //songs.addAll(db.songDAO().loadAllSongs());
        songsListView.setAdapter(new CustomAdapter());
        //songsListView.setAdapter(new SongAdapter(ListOfSongsActivity.this, songs));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    songs = getMP3Files(Environment.getExternalStorageDirectory().getPath());
                    setUpListView(songs);
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

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.song_list_parent_layout, null);
            final SongModel song = (SongModel) db.songDAO().find(songs.get(i).getFilePath());
            // Lookup view for data population
            ImageView songArtView = view.findViewById(R.id.listOfSongs_artImageView);
            TextView songNameTextView = view.findViewById(R.id.listOfSongs_SongName);
            TextView songPriceTextView = view.findViewById(R.id.listOfSongs_SongPrice);

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            String c = song.getFilePath();
            mmr.setDataSource(c);

            byte [] data = mmr.getEmbeddedPicture();
            if(data != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                songArtView.setImageBitmap(bitmap); //associated cover art in bitmap
            }
            else
            {
                songArtView.setImageResource(R.drawable.ic_music_note_24dp); //any default cover resourse folder
            }

            String songName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            Double songPrice = song.getPrice();

            songNameTextView.setText(songName);


            if (songPrice < 0) {
                songPriceTextView.setText("");
            } else {
                String priceString = "";
                if (songPrice > 0)
                    priceString = String.format(Locale.CANADA, "%2.2f %s", songPrice, song.getCurrency());
                else
                    priceString = getString(R.string.wordForFree);

                SpannableString spannableString = new SpannableString(priceString);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(song.getStoreURL())));
                    }
                };
                spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                songPriceTextView.setText(spannableString, TextView.BufferType.SPANNABLE);
                songPriceTextView.setMovementMethod(LinkMovementMethod.getInstance());
            }

            //songArtView.setMaxHeight(songNameTextView.getHeight());
            //int h = songArtView.getHeight();
            //int h2 = songNameTextView.getHeight();
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(ListOfSongsActivity.this, SongDetailsActivity.class);
                    intent.putExtra("SelectedSongFilePath", song.getFilePath());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            return view;
        }
    }
}
