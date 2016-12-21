package com.tryagain.com.fleetmanagmentsystem;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SM Alam on 12/4/2015.
 */

class ValidateDriverAsyncTask extends AsyncTask<String, String, String> {

    private ProgressDialog pd;
    private LoginActivity activity;
    private int success;
    private String pin;
    private String password;

    public ValidateDriverAsyncTask(LoginActivity activity, String pin, String password){
        this.activity = activity;
        success = 0;
        this.pin = pin;
        this.password = password;
        pd = new ProgressDialog(activity);
        pd.setMessage("Loading...");
        pd.show();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("driver", "driver"));
        params.add(new BasicNameValuePair("pin", pin));
        params.add(new BasicNameValuePair("password", password));

        JSONParser jParser = new JSONParser();

        JSONObject json = jParser.makeHttpRequest(CommonUtilities.DRIVER_OR_PASSENGER_VALIDATION_URL, "POST", params);
        try {
            success = json.getInt("success");
            if (success == 1) {
                Log.d("Login", "Login successful.");
            } else {
                Log.d("Login", "Login not successful.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String file_url) {
        if (pd != null){
            pd.dismiss();
        }

        if (success == 1) {
            activity.launchActivity();
            Log.d("Login", "Login successful.");
        } else {
            activity.showLoginFailedMessage();
            Log.d("Login", "Login not successful.");
        }
    }
}