package ca.davin.personalproject.overduesongfinder.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.design.widget.TextInputLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ca.davin.personalproject.overduesongfinder.Database.SongModel;
import ca.davin.personalproject.overduesongfinder.Fragment.PossibleSongsDialogFragment;
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
    private JSONObject responseJSON;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mmr = new MediaMetadataRetriever();
        Bundle b = getIntent().getExtras();
        currentSong = new SongModel(b.getString("SelectedSongFilePath"));
        mmr.setDataSource(currentSong.getFilePath());
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
        getMenuInflater().inflate(R.menu.menu_song_details, menu);
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
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);
                String songName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                songName = songName.replace(' ', '+');
                String url ="https://itunes.apple.com/search?term=" + songName + "&entity=song&attribute=songTerm";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    responseJSON = new JSONObject(response);
                                    if (responseJSON.getInt("resultCount") > 1) {
                                        PossibleSongsDialogFragment possibleSongsDialogFragment = new PossibleSongsDialogFragment();
                                        Bundle b = new Bundle();
                                        String results = responseJSON.getJSONArray("results").toString();
                                        b.putString("results", results);
                                        b.putInt("resultCount", responseJSON.getInt("resultCount"));
                                        possibleSongsDialogFragment.setArguments(b);
                                        possibleSongsDialogFragment.show(getSupportFragmentManager(), "resultingSongs");
                                    }
                                } catch (JSONException jEx) {
                                    Log.d("JSONException: ", jEx.getMessage());
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SongDetailsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                return true;
            case R.id.action_purchaseSong:
                Toast.makeText(this, "Purchase Song Pressed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
