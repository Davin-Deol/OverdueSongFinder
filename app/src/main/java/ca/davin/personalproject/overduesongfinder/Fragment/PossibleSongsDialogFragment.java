package ca.davin.personalproject.overduesongfinder.Fragment;

import android.app.Dialog;
import android.content.Context;
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

import java.util.ArrayList;

import ca.davin.personalproject.overduesongfinder.R;

public class PossibleSongsDialogFragment extends DialogFragment {
    // Use this instance of the interface to deliver action events
    PossibleSongsDialogListener mListener;

    public interface PossibleSongsDialogListener {
        public void onDialogItemClicked(int position);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();
        int resultCount = args.getInt("resultCount");
        String results = args.getString("results");
        String resultsString[] = new String[resultCount];
        JSONArray resultsJSONArray = null;
        JSONObject resultJSONObject = null;
        ArrayList<JSONObject> resultsArrayList = new ArrayList<>();

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
                        mListener.onDialogItemClicked(which);
                    }
                });
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PossibleSongsDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Must implement NoticeDialogListener");
        }
    }
}