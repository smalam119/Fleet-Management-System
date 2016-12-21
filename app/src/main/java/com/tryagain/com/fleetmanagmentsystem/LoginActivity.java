package com.tryagain.com.fleetmanagmentsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tryagain.com.fleetmanagmentsystem.gpstracking.GpsTrackerActivity;

public class LoginActivity extends Activity implements OnClickListener {

    private Button btnLogin;
    private EditText inputID,inputPassword;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        String logged_driver = sharedPreferences.getString("logged_driver", "");
        if(!logged_driver.equals("")){
            Intent j = new Intent(getApplicationContext(), GpsTrackerActivity.class);
            startActivity(j);
            finish();
        }

        inputID = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.btnLogin:
                id = inputID.getText().toString();
                String password = inputPassword.getText().toString();

                if (id.trim().length() > 0 && password.trim().length() > 0){
                    new ValidateDriverAsyncTask(this, id, password).execute();
                }
                else{
                    // Prompt user to enter credentials
                    showLoginFailedMessage();
                }
                break;
            default:
                break;
        }
    }

    public void showLoginFailedMessage() {
        Toast.makeText(getApplicationContext(), "Login failed. Please enter valid credentials.", Toast.LENGTH_LONG).show();
    }

    public void launchActivity(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.tryagain.com.fleetmanagmentsystem.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("logged_driver", id);
        editor.apply();

        Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_LONG).show();
        Intent j = new Intent(getApplicationContext(), GpsTrackerActivity.class);
        startActivity(j);
        finish();
    }

    private void goToRecord()
    {
        Intent j = new Intent(getApplicationContext(),
                ShowRecordView.class);
        startActivity(j);
    }
}