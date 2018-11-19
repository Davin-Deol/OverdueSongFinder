package ca.davin.personalproject.overduesongfinder.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import ca.davin.personalproject.overduesongfinder.R;
import android.widget.TextView;

import java.util.ArrayList;

import ca.davin.personalproject.overduesongfinder.Database.SongModel;

public class SongAdapter extends BaseExpandableListAdapter {

    // For access to the Resources
    private Context context;

    // Stores the list of devices to be displayed
    private ArrayList<SongModel> songs;

    // Stores the groups views and their associated child view
    private String[][] options;

    /**
     * For this constructor we need to update our global variables so that the methods have access to the
     * @param context - As stated in the variables section, this gives us access to the Resources folder
     * @param songs - As stated in the variables section, this stores the devices to be displayed
     */
    public SongAdapter(Activity context, ArrayList<SongModel> songs) {
        setContext(context);
        setSongs(songs);
        /*
        options = new String[][]{
                {context.getResources().getString(R.string.menu_charts),
                        context.getResources().getString(R.string.menu_map),
                        context.getResources().getString(R.string.menu_alarms)}
        };
        */
    }

    /**
     * Gets the number of groups.
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return songs.size();
    }

    /**
     * Gets the number of children in a specified group.
     * @param groupPosition - the position of the group for which the children count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    /**
     * Gets the data associated with the given group.
     * @param groupPosition - the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition) {
        return songs.get(groupPosition);
    }

    /**
     * Gets the data associated with the given child within the given group.
     * @param groupPosition - the position of the group that the child resides in
     * @param childPosition - the position of the child with respect to other children in the group
     * @return the data of the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return options[groupPosition][childPosition];
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be unique across groups.
     * @param groupPosition - the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be unique across all children within the group.
     * @param groupPosition - the position of the group that contains the child
     * @param childPosition - the position of the child within the group for which the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the underlying data.
     * @return whether or not the same ID always refers to the same object
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * This method sets up how the data is displayed for the group view
     * @param groupPosition - the position of the group for which the View is returned
     * @param isExpanded - whether the group is expanded or collapsed
     * @param convertView - the old view to reuse, if possible
     * @param parent - the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SongModel device = (SongModel) getGroup(groupPosition);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_list_parent_layout, parent, false);
        /*
        // Lookup view for data population
        TextView deviceNameTextView = convertView.findViewById(R.id.deviceName);
        TextView deviceTypeTextView = convertView.findViewById(R.id.deviceType);
        TextView deviceStatusTextView = convertView.findViewById(R.id.deviceStatus);

        String deviceName = device.getName();
        String deviceType = convertFirstLettersToUpperCase(device.getType());
        String deviceStatus = convertFirstLettersToUpperCase(device.getStatus());


        deviceNameTextView.setText(deviceName);
        deviceTypeTextView.setText(deviceType);
        deviceStatusTextView.setText(deviceStatus);

        if (!deviceStatus.equals("Active")) {
            deviceStatusTextView.setBackground(convertView.getResources().getDrawable(R.drawable.device_status_background_red));
        }

        */
        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * This method specifies what view to display for the child view both when the group is
     * expanded and not expanded.
     * @param groupPosition - the position of the group that contains the child
     * @param childPosition - the position of the child (for which the View is returned) within the group
     * @param isLastChild - Whether the child is the last child within the group
     * @param convertView - the old view to reuse, if possible
     * @param parent - the parent that this view will eventually be attached to
     * @return the view that the child of the group will use to be displayed as
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Holds the activity that we're in
        final Activity activity = (Activity) getContext();

        // Now we inflate the view so that we have access to the elements in it
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_list_child_layout, parent, false);

        // Holds the position of the group that the child resides in. We need this to access the
        // device that the group view references.
        final int index = groupPosition;

        /*
        // Now we set the onClick listeners for when the user selects what activity to go to for the
        // device associated with the group we're in
        convertView.findViewById(R.id.deviceCharts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                String currentDevice = devices.get(index).getName();
                new SharedPreferencesVariables(getContext()).setLastDevice(currentDevice);
                Intent intent = new Intent(activity, ChartsPage.class);
                intent.putExtra("CurrentDevice", currentDevice);
                b.putSerializable("ARRAYLIST", devices);
                b.putInt("CurrentDeviceIndex", 0);
                intent.putExtra("BUNDLE",b);
                intent.putExtras(b);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        convertView.findViewById(R.id.deviceMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                String currentDevice = devices.get(index).getName();
                new SharedPreferencesVariables(getContext()).setLastDevice(currentDevice);
                Intent intent = new Intent(activity, MapActivity.class);
                intent.putExtra("CurrentDevice", currentDevice);
                b.putSerializable("ARRAYLIST", devices);
                b.putInt("CurrentDeviceIndex", 0);
                intent.putExtra("BUNDLE",b);
                intent.putExtras(b);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        convertView.findViewById(R.id.deviceAlarms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                String currentDevice = devices.get(index).getName();
                new SharedPreferencesVariables(getContext()).setLastDevice(currentDevice);
                Intent intent = new Intent(activity, AlarmsActivity.class);
                intent.putExtra("CurrentDevice", currentDevice);
                b.putSerializable("ARRAYLIST", devices);
                b.putInt("CurrentDeviceIndex", 0);
                intent.putExtra("BUNDLE",b);
                intent.putExtras(b);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (isLastChild)
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.colorGrey));
            */
        return convertView;
    }

    /**
     * Whether the child at the specified position is selectable.
     * @param groupPosition - the position of the group that contains the child
     * @param childPosition - the position of the child within the group
     * @return whether the child is selectable
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * Assigns the value given be the developer, to the global variable.
     * @param context - holds the context of the activity using this adapter
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * @return the context of the activity using this adapter
     */
    public Context getContext() {
        return this.context;
    }

    /**
     * Assigns the value given be the developer, to the global variable.
     * @param songs - holds the list of songs containing the data we need to display
     */
    public void setSongs(ArrayList<SongModel> songs) {
        this.songs = songs;
    }

    /**
     * @return the list of devices containing the data we need to display
     */
    public ArrayList<SongModel> getSongs() {
        return this.songs;
    }
}
