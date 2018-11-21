package ca.davin.personalproject.overduesongfinder.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.design.widget.TextInputLayout;

import ca.davin.personalproject.overduesongfinder.Database.SongModel;
import ca.davin.personalproject.overduesongfinder.R;

public class SongDetailsActivity extends AppCompatActivity {

    private SongModel currentSong;
    private MediaMetadataRetriever mmr;
    private ImageView artImageView;
    private TextInputLayout titleTextInputLayout;
    private TextInputLayout artistTextInputLayout;
    private TextInputLayout albumTextInputLayout;
    private TextInputLayout albumArtistTextInputLayout;
    private TextInputLayout genreTextInputLayout;
    private TextInputLayout composerTextInputLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mmr = new MediaMetadataRetriever();
        Bundle b = getIntent().getExtras();
        mmr.setDataSource(b.getString("SelectedSongFilePath"));
        myToolbar.setTitle(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        artImageView = (ImageView) findViewById(R.id.songsDetails_artImageView);
        titleTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_titleTextInputLayout);
        artistTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_artistTextInputLayout);
        albumTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_albumTextInputLayout);
        albumArtistTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_albumArtistTextInputLayout);
        genreTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_genreTextInputLayout);
        composerTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_composerTextInputLayout);

        // convert the byte array to a bitmap
        byte [] data = mmr.getEmbeddedPicture();
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            artImageView.setImageBitmap(bitmap); //associated cover art in bitmap
        }
        else
        {
            artImageView.setImageResource(R.drawable.ic_music_note_24dp); //any default cover resourse folder
        }

        titleTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        artistTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        albumTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        albumArtistTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST));
        genreTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
        composerTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_of_songs, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            case R.id.action_findSong:
                Toast.makeText(this, "Find Song Pressed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_purchaseSong:
                Toast.makeText(this, "Purchase Song Pressed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
