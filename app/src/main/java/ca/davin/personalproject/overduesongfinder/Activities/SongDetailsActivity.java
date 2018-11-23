package ca.davin.personalproject.overduesongfinder.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.TextInputLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.ImageData;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ca.davin.personalproject.overduesongfinder.Database.AppDatabase;
import ca.davin.personalproject.overduesongfinder.Database.SongModel;
import ca.davin.personalproject.overduesongfinder.Fragment.PossibleSongsDialogFragment;
import ca.davin.personalproject.overduesongfinder.R;

public class SongDetailsActivity extends AppCompatActivity implements PossibleSongsDialogFragment.PossibleSongsDialogListener {

    private SongModel currentSong;
    private MediaMetadataRetriever mmr;
    private ImageView artImageView;
    private TextView priceTextView;
    private TextView storeTextView;
    private TextInputLayout titleTextInputLayout;
    private TextInputLayout artistTextInputLayout;
    private TextInputLayout albumTextInputLayout;
    private TextInputLayout albumArtistTextInputLayout;
    private TextInputLayout genreTextInputLayout;
    private TextInputLayout composerTextInputLayout;
    private TextInputLayout yearTextInputLayout;
    private JSONObject responseJSON;
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE=2;
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

        priceTextView = (TextView) findViewById(R.id.songsDetails_priceTextView);
        storeTextView = (TextView) findViewById(R.id.songsDetails_storeTextView);
        artImageView = (ImageView) findViewById(R.id.songsDetails_artImageView);
        titleTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_titleTextInputLayout);
        artistTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_artistTextInputLayout);
        albumTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_albumTextInputLayout);
        albumArtistTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_albumArtistTextInputLayout);
        genreTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_genreTextInputLayout);
        composerTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_composerTextInputLayout);
        yearTextInputLayout = (TextInputLayout) findViewById(R.id.songsDetails_yearTextInputLayout);

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
        yearTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
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
            case R.id.action_saveSong:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                    saveMetaData();
                } else {
                    saveMetaData();
                }

                Toast.makeText(this, "Saved Meta Data", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_purchaseSong:
                Toast.makeText(this, "Purchase Song Pressed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogItemClicked(int position) {
        // User touched the dialog's negative button
        JSONArray resultsJSONArray = null;
        ArrayList<JSONObject> resultsArrayList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            JSONObject result = responseJSON.getJSONArray("results").getJSONObject(position);
            String url ="https://itunes.apple.com/lookup?entity=song&id=" + result.getString("trackId");

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                responseJSON = new JSONObject(response);
                                final JSONObject resultJSONObject = responseJSON.getJSONArray("results").getJSONObject(0);

                                String urlString = resultJSONObject.getString("artworkUrl100");
                                new DownloadImageTask(artImageView).execute(urlString);
                                /*
                                URL url = new URL(urlString);
                                url.openConnection();
                                url.getContent();
                                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                                artImageView.setImageBitmap(bitmap);
                                */

                                priceTextView.setText("Price: " + resultJSONObject.getString("trackPrice") + " " + resultJSONObject.getString("currency"));
                                SpannableString spannableString = new SpannableString("Store: iTunes");
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(View textView) {
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(resultJSONObject.getString("trackViewUrl"))));
                                        } catch (JSONException jEx) {
                                            Log.d("JSONException: ", jEx.getMessage());
                                        }
                                    }
                                };
                                spannableString.setSpan(clickableSpan, 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                storeTextView.setText(spannableString, TextView.BufferType.SPANNABLE);
                                storeTextView.setMovementMethod(LinkMovementMethod.getInstance());
                                //storeTextView.setText("Store: <a href=\"" + resultJSONObject.getString("trackViewUrl") + "\">iTunes</a>");
                                //storeTextView.setMovementMethod(LinkMovementMethod.getInstance());
                                titleTextInputLayout.getEditText().setText(resultJSONObject.getString("trackName"));
                                artistTextInputLayout.getEditText().setText(resultJSONObject.getString("artistName"));
                                albumTextInputLayout.getEditText().setText(resultJSONObject.getString("collectionName"));
                                albumArtistTextInputLayout.getEditText().setText(resultJSONObject.getString("artistName"));
                                genreTextInputLayout.getEditText().setText(resultJSONObject.getString("primaryGenreName"));
                                currentSong.setPrice(resultJSONObject.getDouble("trackPrice"));

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                Date date = format.parse(resultJSONObject.getString("releaseDate"));
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                yearTextInputLayout.getEditText().setText("" + cal.get(Calendar.YEAR));
                            } catch (JSONException jEx) {
                                Log.d("JSONException: ", jEx.getMessage());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SongDetailsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        } catch (JSONException jEx) {
            Log.d("JSONException: ", jEx.getMessage());
        }
    }

    public void saveMetaData() {
        try {
            File src = new File(currentSong.getFilePath());
            MusicMetadataSet src_set = new MyID3().read(src);
            MusicMetadata meta = new MusicMetadata("name");
            meta.setSongTitle(titleTextInputLayout.getEditText().getText().toString());
            meta.setArtist(artistTextInputLayout.getEditText().getText().toString());
            meta.setAlbum(albumTextInputLayout.getEditText().getText().toString());
            meta.setGenre(genreTextInputLayout.getEditText().getText().toString());
            meta.setComposer(composerTextInputLayout.getEditText().getText().toString());
            meta.setYear(yearTextInputLayout.getEditText().getText().toString());

            Bitmap bitmap = ((BitmapDrawable) artImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();
            meta.addPicture(new ImageData(imageInByte, "image/jpeg", "Image", 3));
            new MyID3().update(src, src_set, meta);
        } catch (IOException ioEx) {
            Log.d("IOException: ", ioEx.getMessage());
        } catch (NullPointerException npEx) {
            Log.d("NullPointerException: ", npEx.getMessage());
        } catch (ID3WriteException id3Ex) {
            Log.d("ID3WriteException: ", id3Ex.getMessage());
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ProgressDialog mDialog;
        private ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected void onPreExecute() {

            mDialog = ProgressDialog.show(SongDetailsActivity.this,"Please wait...", "Retrieving data ...", true);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "image download error");
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            //set image of your imageview
            bmImage.setImageBitmap(result);
            //close
            mDialog.dismiss();
        }
    }
}
