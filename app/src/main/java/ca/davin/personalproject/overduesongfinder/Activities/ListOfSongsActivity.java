package ca.davin.personalproject.overduesongfinder.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ca.davin.personalproject.overduesongfinder.R;

public class ListOfSongsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_songs);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


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
