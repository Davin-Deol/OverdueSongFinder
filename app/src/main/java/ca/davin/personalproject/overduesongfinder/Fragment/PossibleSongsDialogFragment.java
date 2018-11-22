package ca.davin.personalproject.overduesongfinder.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.davin.personalproject.overduesongfinder.R;

public class PossibleSongsDialogFragment extends DialogFragment {
    String[] array;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();
        int resultCount = args.getInt("resultCount");
        String results = args.getString("results");
        String resultsString[] = new String[resultCount];
        JSONArray resultsJSONArray = null;
        JSONObject resultJSONObject = null;
        String songName = null;
        String songArtist = null;
        try {
            resultsJSONArray = new JSONArray(results);
            for (int i = 0; i < resultCount; i++) {
                resultJSONObject = new JSONObject(resultsJSONArray.get(i).toString());
                songName = resultJSONObject.getString("trackName");
                songArtist = resultJSONObject.getString("artistName");
                resultsString[i] = songName + ", " + songArtist;
            }
        } catch (JSONException jEx) {
            Log.d("JSONException: ", jEx.getMessage());
        }
        builder.setTitle("Which one?")
                .setItems(resultsString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }
}