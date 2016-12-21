package com.tryagain.com.fleetmanagmentsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class CarConditionActivity extends AppCompatActivity {

    private CheckBox air,bettery,radiator,lights,breaks,indicator,mobil,atires;
    private Button saveBtn;
    FileHandler fl = new FileHandler();
    String[] carConditionData = new String[10];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_condition);
        air = (CheckBox) findViewById(R.id.chkAirMachine);
        bettery = (CheckBox) findViewById(R.id.chkBattery);
        radiator = (CheckBox) findViewById(R.id.chkRadiatorWater);
        lights = (CheckBox) findViewById(R.id.chkLights);
        breaks = (CheckBox) findViewById(R.id.chkBreaks);
        indicator = (CheckBox) findViewById(R.id.chkIndicatorLight);
        mobil = (CheckBox) findViewById(R.id.chkMobile);
        saveBtn = (Button) findViewById(R.id.btnSave);
        atires = (CheckBox) findViewById(R.id.chkatires);


        saveBtn.setOnClickListener(new View.OnClickListener() {

            String airStatus = "";
            String betteryStatus = "";
            String radiatorStatus = "";
            String lightsStatus = "";
            String breakStatus = "";
            String indicatorStatus = "";
            String mobilStatus = "";
            String atiresStatus = "";
            @Override
            public void onClick(View v)
            {
                if(air.isChecked())
                {
                    airStatus = "air_okay";
                }
                else
                {
                    airStatus = "air_not_okay";
                }

                if(atires.isChecked())
                {
                    atiresStatus = "additional_tires_okay";
                }
                else
                {
                    atiresStatus = "additional_tires_not_okay";
                }

                if(bettery.isChecked())
                {
                    betteryStatus = "battery_okay";
                }
                else
                {
                    betteryStatus = "battery_not_okay";
                }

                if(radiator.isChecked())
                {
                    radiatorStatus = "radiator_okay";
                }
                else
                {
                    radiatorStatus = "radiator_not_okay";
                }

                if(lights.isChecked())
                {
                    lightsStatus = "lights_okay";
                }
                else
                {
                    lightsStatus = "lights_not_okay";
                }

                if(breaks.isChecked())
                {
                    breakStatus = "breaks_okay";
                }
                else
                {
                    breakStatus = "breaks_not_okay";
                }

                if(indicator.isChecked())
                {
                    indicatorStatus = "indicator_okay";
                }
                else
                {
                    indicatorStatus = "indicator_not_okay";
                }

                if(bettery.isChecked())
                {
                    betteryStatus = "bettery_okay";
                }
                else
                {
                    betteryStatus = "bettery_not_okay";
                }

                if(mobil.isChecked())
                {
                    mobilStatus = "mobil_okay";
                }
                else
                {
                    mobilStatus = "mobil_not_okay";
                }

                carConditionData[1] = mobilStatus;
                carConditionData[2] = betteryStatus;
                carConditionData[3] = breakStatus;
                carConditionData[4] = airStatus;
                carConditionData[5] = radiatorStatus;
                carConditionData[6] = indicatorStatus;
                carConditionData[7] = lightsStatus;
                carConditionData[8] = atiresStatus;

                fl.write(carConditionData,"conditionRecord");

                Toast.makeText(getApplicationContext(), "Records Saved", Toast.LENGTH_LONG).show();
            }

        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
