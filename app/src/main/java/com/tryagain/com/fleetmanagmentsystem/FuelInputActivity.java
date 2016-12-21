package com.tryagain.com.fleetmanagmentsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.sule.gaugelibrary.GaugeView;

public class FuelInputActivity extends Activity implements View.OnClickListener {

    private GaugeView mGaugeView;
    private Button pBtn, nBtn,recBtn;

    private int gaugeValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_input);
        Toast.makeText(getApplicationContext(),"Touch The Meter To Input Initial Fuel Status",Toast.LENGTH_LONG).show();
        mGaugeView = (GaugeView) findViewById(R.id.gauge_view);
        mGaugeView.setTargetValue(0);
        mGaugeView.setOnClickListener(this);
        pBtn = (Button) findViewById(R.id.plusButton);
        nBtn = (Button) findViewById(R.id.minusButton);
        recBtn = (Button) findViewById(R.id.rec);
        pBtn.setOnClickListener(this);
        nBtn.setOnClickListener(this);
        recBtn.setOnClickListener(this);
    }

    protected void showInputDialog(final String sign) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(FuelInputActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_fuel_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FuelInputActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String value = editText.getText().toString();
                        int fuel = Integer.valueOf(value);
                        if (sign.equals("plus") && !value.isEmpty()) {
                            gaugeValue = gaugeValue + fuel;
                            mGaugeView.setTargetValue(gaugeValue);
                        }
                        if (sign.equals("minus") && !value.isEmpty()) {
                            gaugeValue = gaugeValue - fuel;
                            mGaugeView.setTargetValue(gaugeValue);
                        }
                        if (value.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Enter Value", Toast.LENGTH_SHORT).show();
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

    protected void initialFuel() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(FuelInputActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_fuel_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FuelInputActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FileHandler fl = new FileHandler();
                        String value = editText.getText().toString();
                        int fuel = Integer.valueOf(value);

                            gaugeValue = fuel;
                            mGaugeView.setTargetValue(gaugeValue);
                            String[] initialFuel = {"Initial Fuel Level: "+value};


                        fl.write(initialFuel,"FuelRecord");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuel_input, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.plusButton:
                showInputDialog("plus");
                break;

            case R.id.minusButton:
                showInputDialog("minus");
                break;

            case R.id.gauge_view:
                initialFuel();
                break;
            case R.id.rec:
                FileHandler fl = new FileHandler();
                String[] data = {"Final Fuel Level: "+String.valueOf(gaugeValue)};
                fl.write(data,"FuelRecord");

            default:
                break;
        }

    }
}
