package com.tryagain.com.fleetmanagmentsystem;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowRecordView extends Activity {
    public static String[] yo;
    public static String tts;
    ListView listview;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record_view);
        listview=(ListView) findViewById(R.id.list_view);
        listview.setAdapter(new myBaseAdapter(this));
        //tv = (TextView) findViewById(R.id.text_View);
       // tv.setText("Total Time: "+tts+" minutes");
        //Toast.makeText(getApplicationContext(), yo[yo.length-1], Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void submit(View v){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        String dis = String.valueOf(sharedPreferences.getFloat("totalDistanceInMeters", 0f) / 1000.0) + " km";
        String fuel = String.valueOf(sharedPreferences.getInt("fuel", 0));
        String time = getTimeOnTheRoad2();
        if(time.equals(""))
            time = "0";

        time += " minutes";

        String speed = String.valueOf(sharedPreferences.getFloat("speed", 0f)) + " km/h";

        String start_latitude = String.valueOf(sharedPreferences.getFloat("start_latitude", 0));
        String start_longitude = String.valueOf(sharedPreferences.getFloat("start_longitude", 0));

        String end_latitude = String.valueOf(sharedPreferences.getFloat("end_latitude", 0));
        String end_longitude = String.valueOf(sharedPreferences.getFloat("end_longitude", 0));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("start_latitude", 0f);
        editor.putFloat("totalDistanceInMeters", 0f);
        editor.apply();

        Toast.makeText(getApplicationContext(), "Submit pressed." + start_latitude + " " + start_longitude, Toast.LENGTH_SHORT).show();
    }

    public String getTimeOnTheRoad2() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());

        try {
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
            Date date1 = dateFormat.parse(sharedPreferences.getString("start_time", ""));
            Date date2 = dateFormat.parse(sharedPreferences.getString("end_time", ""));

            long diffInMillies = date2.getTime() - date1.getTime();
            return TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS) + "";
        }catch (Exception ex){
            return "";
        }
    }

}

class SingleRow {
    String title;
    String description;
    int img;

    public SingleRow(String title,String description,int img) {
        this.title=title;
        this.description=description;
        this.img=img;
    }

}

class myBaseAdapter extends BaseAdapter{
    ArrayList<SingleRow> list;
    Context context;
    String[] A={};
    myBaseAdapter(Context c) {
        context = c;
        SharedPreferences sharedPreferences = c.getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        String dis = String.valueOf(sharedPreferences.getFloat("distance", 0));
        String fuel = String.valueOf(sharedPreferences.getInt("fuel", 0));
        //String time = getTimeOnTheRoad();
        //if(time.equals(""))
            //time = "0";

        //time += " minutes";

        String speed = String.valueOf(sharedPreferences.getFloat("speed", 0)) + " km";
        String[] records = {fuel+" litres","30 km/hour","2 hrs","70 km", "2 times"};
        list = new ArrayList<SingleRow>();
        Resources res = c.getResources();
        String[] titles=  res.getStringArray(R.array.description);
        String[] description = records;
        int[] images = {R.drawable.fuel,R.drawable.speed,R.drawable.time,R.drawable.distance,R.drawable.exclamation30};

        for(int i=0;i<5;i++){
            list.add(new SingleRow(titles[i], description[i], images[i]));
        }
    }

    /*public String getTimeOnTheRoad() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
            Date date1 = dateFormat.parse(sharedPreferences.getString("start_time", ""));
            Date date2 = dateFormat.parse(sharedPreferences.getString("end_time", ""));

            long diffInMillies = date2.getTime() - date1.getTime();
            return TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS) + "";
        }catch (Exception ex){
            return "";
        }
    }*/

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        // TODO Auto-generated method stub
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.single_row,viewGroup,false);
        TextView title = (TextView) row.findViewById(R.id.textView1);
        TextView description = (TextView) row.findViewById(R.id.textView2);
        ImageView image = (ImageView) row.findViewById(R.id.imageView1);

        SingleRow temp = list.get(i);

        title.setText(temp.title);
        description.setText(temp.description);
        image.setImageResource(temp.img);
        return row;
    }

}

