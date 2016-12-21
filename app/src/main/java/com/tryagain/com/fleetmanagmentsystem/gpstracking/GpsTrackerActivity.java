package com.tryagain.com.fleetmanagmentsystem.gpstracking;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tryagain.com.fleetmanagmentsystem.LoginActivity;
import com.tryagain.com.fleetmanagmentsystem.R;
import com.tryagain.com.fleetmanagmentsystem.ShowRecordView;
import com.tryagain.com.fleetmanagmentsystem.ValidatePassengerAsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import io.sule.gaugelibrary.GaugeView;

public class GpsTrackerActivity extends ActionBarActivity {
    private static final String TAG = "GpsTrackerActivity";

    private String defaultUploadWebsite;

    //private static EditText txtUserName;
    //private static EditText txtWebsite;
    private static Button trackingButton;

    private boolean currentlyTracking;
    //private RadioGroup intervalRadioGroup;
    private int intervalInMinutes = 1;
    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;

    private GaugeView mGaugeView;

    private int gaugeValue = 0;
    private View trackButtonView;

    private Boolean needToShowRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        defaultUploadWebsite = getString(R.string.default_upload_website);
        mGaugeView = (GaugeView) findViewById(R.id.gauge_view);
        mGaugeView.setTargetValue(0);

        needToShowRecord = false;

        trackingButton = (Button)findViewById(R.id.trackingButton);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);

        if(currentlyTracking){
            mGaugeView.setVisibility(View.INVISIBLE);
        }

        boolean firstTimeLoadindApp = sharedPreferences.getBoolean("firstTimeLoadindApp", true);
        if (firstTimeLoadindApp) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTimeLoadindApp", false);
            editor.putString("appID", UUID.randomUUID().toString());
            editor.apply();
        }

        saveInterval();

        mGaugeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showInputDialog();
            }
        });

        trackingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                trackButtonView = view;
                if (currentlyTracking) {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.tryagain.com.fleetmanagmentsystem", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("end_time", getCurrentDate());
                    editor.apply();
                    subFuel();
                } else {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.tryagain.com.fleetmanagmentsystem", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("start_time", getCurrentDate());
                    editor.apply();
                    showPassengerLoginDialogWithSkip();
                }
                //trackLocation(view);
            }
        });
    }

    private String getCurrentDate(){
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected void showInputDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(GpsTrackerActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_fuel_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GpsTrackerActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String value = editText.getText().toString();
                        int fuel = Integer.valueOf(value);
                        if (!value.isEmpty()) {
                            if (gaugeValue + fuel < 60) {
                                gaugeValue = gaugeValue + fuel;
                                mGaugeView.setTargetValue(gaugeValue);
                            } else {
                                Toast.makeText(getApplicationContext(), "Please enter a valid value.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (value.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please enter a valid value.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void addFuel() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(GpsTrackerActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_fuel_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GpsTrackerActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String value = editText.getText().toString();

                        int fuel = Integer.valueOf(value);
                        if (!value.isEmpty()) {
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            int oldFuel = sharedPreferences.getInt("fuel", 0);
                            editor.putInt("fuel", oldFuel + fuel);
                            editor.apply();
                        }

                        if (value.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please enter a valid value.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void subFuel() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(GpsTrackerActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_fuel_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GpsTrackerActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String value = editText.getText().toString();

                        int fuel = Integer.valueOf(value);
                        if (!value.isEmpty()) {
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            int oldFuel = sharedPreferences.getInt("fuel", 0);
                            editor.putInt("fuel", oldFuel - fuel);
                            editor.apply();
                            showPassengerLoginDialogWithoutSkip();
                        }

                        if (value.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please enter a valid value.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void saveInterval() {
        if (currentlyTracking) {
            Toast.makeText(getApplicationContext(), R.string.user_needs_to_restart_tracking, Toast.LENGTH_LONG).show();
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("intervalInMinutes", 1);
        editor.apply();
    }

    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                intervalInMinutes * 60000,
                pendingIntent);
    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    // called when trackingButton is tapped
    protected void trackLocation(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!saveUserSettings()) {
            return;
        }

        if (!checkIfGooglePlayEnabled()) {
            return;
        }

        if (currentlyTracking) {
            cancelAlarmManager();

            currentlyTracking = false;
            editor.putBoolean("currentlyTracking", false);
            editor.putString("sessionID", "");
            //int totalFuel = sharedPreferences.getInt("fuel", 0);
            //float totalDistance = sharedPreferences.getFloat("distance", -1);
            //Toast.makeText(getApplicationContext(),""+totalFuel+" "+totalDistance,Toast.LENGTH_SHORT).show();
        } else {
            startAlarmManager();

            currentlyTracking = true;
            editor.putBoolean("currentlyTracking", true);
            //editor.putFloat("totalDistanceInMeters", 0f);
            editor.putBoolean("firstTimeGettingPosition", true);
            editor.putString("sessionID", UUID.randomUUID().toString());
            editor.putInt("fuel", gaugeValue);

            mGaugeView.setVisibility(View.INVISIBLE);
        }

        editor.apply();
        setTrackingButtonState();
    }

    private boolean saveUserSettings() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("defaultUploadWebsite", defaultUploadWebsite);
        editor.apply();
        return true;
    }

    private boolean checkIfGooglePlayEnabled() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            return true;
        } else {
            Log.e(TAG, "unable to connect to google play services.");
            Toast.makeText(getApplicationContext(), R.string.google_play_services_unavailable, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void setTrackingButtonState() {
        if (currentlyTracking) {
            trackingButton.setBackgroundResource(R.drawable.green_tracking_button);
            trackingButton.setTextColor(Color.BLACK);
            trackingButton.setText(R.string.tracking_is_on);
        } else {
            trackingButton.setBackgroundResource(R.drawable.red_tracking_button);
            trackingButton.setTextColor(Color.WHITE);
            trackingButton.setText(R.string.tracking_is_off);

            //goToRecord();
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        //displayUserSettings();
        setTrackingButtonState();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_condition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            addFuel();
            return true;
        } else if (id == R.id.action_log_out){
            Toast.makeText(getApplication(), "Logged out successfully.", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("logged_driver", "");
            editor.apply();

            Intent j = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(j);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showPassengerLoginDialogWithSkip() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(GpsTrackerActivity.this);
        View promptView = layoutInflater.inflate(R.layout.activity_passenger_login_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GpsTrackerActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Passenger Login");
        final EditText etPassword = (EditText) promptView.findViewById(R.id.ppassword);
        final EditText etPin = (EditText) promptView.findViewById(R.id.ppin);

        //final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setNeutralButton("Skip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //setTrackingButtonState("y");
                        goToRecord();
                        trackLocation(trackButtonView);
                        Toast.makeText(getApplicationContext(), "Tracking started without passenger verification.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String pinInput = etPin.getText().toString();
                        String passwordInput = etPassword.getText().toString();

                        if (pinInput.trim().length() > 0 && passwordInput.trim().length() > 0) {
                            // start tracking here
                            validatePassenger(pinInput, passwordInput);
                        } else {
                            Toast.makeText(getApplication(), "Passenger login failed. Please enter a valid credential.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void goToRecord() {
            Intent j = new Intent(getApplicationContext(), ShowRecordView.class);
            startActivity(j);
    }

    protected void showPassengerLoginDialogWithoutSkip() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(GpsTrackerActivity.this);
        View promptView = layoutInflater.inflate(R.layout.activity_passenger_login_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GpsTrackerActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Passenger Login");
        final EditText etPassword = (EditText) promptView.findViewById(R.id.ppassword);
        final EditText etPin = (EditText) promptView.findViewById(R.id.ppin);

        //final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String pinInput = etPin.getText().toString();
                        String passwordInput = etPassword.getText().toString();

                        if (pinInput.trim().length() > 0 && passwordInput.trim().length() > 0) {
                            // start tracking here
                            validatePassenger(pinInput, passwordInput);
                            needToShowRecord = true;
                        } else {
                            Toast.makeText(getApplication(), "Passenger login failed. Please enter a valid credential.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void validatePassenger(String pinInput, String passwordInput) {
        new ValidatePassengerAsyncTask(this, pinInput, passwordInput).execute();
    }

    public void startTrackLocation(int success){
        if(success == 1) {
            trackLocation(trackButtonView);
            Toast.makeText(getApplication(), "Passenger verified. Tracking started.", Toast.LENGTH_SHORT).show();
            if(needToShowRecord){
                needToShowRecord = false;
                goToRecord();
            }
        } else {
            Toast.makeText(getApplication(), "Passenger login failed. Please enter a valid credential.", Toast.LENGTH_SHORT).show();
        }
    }
}