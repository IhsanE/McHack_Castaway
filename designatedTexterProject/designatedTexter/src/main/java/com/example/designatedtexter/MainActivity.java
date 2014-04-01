package com.example.designatedtexter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.os.Build;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Context context =   getApplicationContext();
        CharSequence text = "Please fill in both text fields";
        int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, text, duration);
        final Toast sent = Toast.makeText(context, "Sent!", duration);



        final Button button = (Button) findViewById(R.id.button);


        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        button.setBackground(getResources().getDrawable(R.drawable.send_touched));
                        return true;
                    case MotionEvent.ACTION_UP:
                        button.setBackground(getResources().getDrawable(R.drawable.send_idle));
                        final TextView from = (TextView) findViewById(R.id.myLoc);
                        final TextView to = (TextView) findViewById(R.id.destLoc);
                        String startingLocation = (from.getText()).toString();
                        String endingLocation = (to.getText()).toString();
                        String transMode = "";

                        final RadioButton bus = (RadioButton) findViewById( R.id.busButton);
                        final RadioButton drive = (RadioButton) findViewById( R.id.driveButton);
                        final RadioButton cycle= (RadioButton) findViewById( R.id.cycleButton);
                        final RadioButton walk = (RadioButton) findViewById( R.id.walkButton);


                        if (bus.isChecked()) transMode = "Bus";
                        else if (drive.isChecked()) transMode = "Drive";
                        else if (cycle.isChecked()) transMode = "Cycle";
                        else if (walk.isChecked()) transMode = "Walk";


                        if (startingLocation.equals("") | endingLocation.equals("")) toast.show();

                        else{
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage("12892733720", null, transMode+" from "+startingLocation.trim()+" to "+endingLocation.trim(), null, null);
                            from.setText("");
                            to.setText("");
                            sent.show();
                            //289 273 3720
                            //castAway
                        }


                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
