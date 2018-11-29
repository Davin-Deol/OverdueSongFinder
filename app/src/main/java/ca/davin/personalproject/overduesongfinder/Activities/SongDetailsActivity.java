package ca.davin.personalproject.overduesongfinder.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.davin.personalproject.overduesongfinder.Database.AppDatabase;
import ca.davin.personalproject.overduesongfinder.Database.SongModel;
import ca.davin.personalproject.overduesongfinder.Fragment.PossibleSongsDialogFragment;
import ca.davin.personalproject.overduesongfinder.R;

public class SongDetailsActivity extends AppCompatActivity implements PossibleSongsDialogFragment.PossibleSongsDialogListener {
    private AppDatabase db;

    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 2;
    private JSONObject responseJSON;
    private MediaMetadataRetriever mmr;

    private SongModel currentSong;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);
        db = AppDatabase.getInstance(this);

        mmr = new MediaMetadataRetriever();
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        setSupportActionBar(myToolbar);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            currentSong = db.songDAO().find(b.getString("SelectedSongFilePath"));
        }
        mmr.setDataSource(currentSong.getFilePath());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        priceTextView = findViewById(R.id.songsDetails_priceTextView);
        storeTextView = findViewById(R.id.songsDetails_storeTextView);
        artImageView = findViewById(R.id.songsDetails_artImageView);
        titleTextInputLayout = findViewById(R.id.songsDetails_titleTextInputLayout);
        artistTextInputLayout = findViewById(R.id.songsDetails_artistTextInputLayout);
        albumTextInputLayout = findViewById(R.id.songsDetails_albumTextInputLayout);
        albumArtistTextInputLayout = findViewById(R.id.songsDetails_albumArtistTextInputLayout);
        genreTextInputLayout = findViewById(R.id.songsDetails_genreTextInputLayout);
        composerTextInputLayout = findViewById(R.id.songsDetails_composerTextInputLayout);
        yearTextInputLayout = findViewById(R.id.songsDetails_yearTextInputLayout);

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

        String priceString = getString(R.string.wordForPrice) + ": ";
        if (currentSong.getPrice() == 0)
            priceString += getString(R.string.wordForFree);
        else if (currentSong.getPrice() > 0)
            priceString += String.format(Locale.CANADA, "%2.2f %s", currentSong.getPrice(), currentSong.getCurrency());

        if (priceTextView != null) {
            priceTextView.setText(priceString);
        }
        if (storeTextView != null) {
            String storeString = "Store: ";
            if (currentSong.getStoreName() != null)
                storeString += currentSong.getStoreName();
            SpannableString spannableString = new SpannableString(storeString);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentSong.getStoreURL())));
                }
            };
            spannableString.setSpan(clickableSpan, 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            storeTextView.setText(spannableString, TextView.BufferType.SPANNABLE);
            storeTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (titleTextInputLayout.getEditText() != null)
            titleTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        if (artistTextInputLayout.getEditText() != null)
            artistTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        if (albumTextInputLayout.getEditText() != null)
            albumTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        if (albumArtistTextInputLayout.getEditText() != null)
            albumArtistTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST));
        if (genreTextInputLayout.getEditText() != null)
            genreTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
        if (composerTextInputLayout.getEditText() != null)
            composerTextInputLayout.getEditText().setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER));
        if (yearTextInputLayout.getEditText() != null)
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
                                String priceString = getString(R.string.wordForPrice) + ": ";
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

                                currentSong.setStoreName("iTunes");
                                currentSong.setStoreURL(resultJSONObject.getString("trackViewUrl"));
                                currentSong.setCurrency(resultJSONObject.getString("currency"));

                                if (resultJSONObject.getDouble("trackPrice") >= 0)
                                    priceString += resultJSONObject.getString("trackPrice") + " " + resultJSONObject.getString("currency");
                                else
                                    priceString += "n/a";

                                priceTextView.setText(priceString);
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
                                if (titleTextInputLayout.getEditText() != null)
                                    titleTextInputLayout.getEditText().setText(resultJSONObject.getString("trackName"));
                                if (artistTextInputLayout.getEditText() != null)
                                    artistTextInputLayout.getEditText().setText(resultJSONObject.getString("artistName"));
                                if (albumTextInputLayout.getEditText() != null)
                                    albumTextInputLayout.getEditText().setText(resultJSONObject.getString("collectionName"));
                                if (albumArtistTextInputLayout.getEditText() != null)
                                    albumArtistTextInputLayout.getEditText().setText(resultJSONObject.getString("artistName"));
                                if (genreTextInputLayout.getEditText() != null)
                                    genreTextInputLayout.getEditText().setText(resultJSONObject.getString("primaryGenreName"));

                                if (resultJSONObject.getDouble("trackPrice") >= 0)
                                    currentSong.setPrice(resultJSONObject.getDouble("trackPrice"));

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                                Date date = format.parse(resultJSONObject.getString("releaseDate"));
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                String yearString = "" + cal.get(Calendar.YEAR);
                                if (yearTextInputLayout.getEditText() != null)
                                    yearTextInputLayout.getEditText().setText(yearString);
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
            if (titleTextInputLayout.getEditText() != null)
                meta.setSongTitle(titleTextInputLayout.getEditText().getText().toString());
            if (artistTextInputLayout.getEditText() != null)
                meta.setArtist(artistTextInputLayout.getEditText().getText().toString());
            if (albumTextInputLayout.getEditText() != null)
                meta.setAlbum(albumTextInputLayout.getEditText().getText().toString());
            if (genreTextInputLayout.getEditText() != null)
                meta.setGenre(genreTextInputLayout.getEditText().getText().toString());
            if (composerTextInputLayout.getEditText() != null)
                meta.setComposer(composerTextInputLayout.getEditText().getText().toString());
            if (yearTextInputLayout.getEditText() != null)
                meta.setYear(yearTextInputLayout.getEditText().getText().toString());

            Bitmap bitmap = ((BitmapDrawable) artImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();
            meta.addPicture(new ImageData(imageInByte, "image/jpeg", "Image", 3));
            new MyID3().update(src, src_set, meta);
            db.songDAO().updateSongs(currentSong);
        } catch (IOException ioEx) {
            Log.d("IOException: ", ioEx.getMessage());
        } catch (NullPointerException npEx) {
            Log.d("NullPointerException: ", npEx.getMessage());
        } catch (ID3WriteException id3Ex) {
            Log.d("ID3WriteException: ", id3Ex.getMessage());
        }
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private ProgressDialog mDialog;
        private ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
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
