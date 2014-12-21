package se.stny.thegridclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crittercism.app.Crittercism;

import org.json.JSONException;
import org.json.JSONObject;

import se.stny.thegridclient.gridCom.gridCom;
import se.stny.thegridclient.util.userSettings;

public class main extends Activity {

    private EditText txtUsername, txtPassword;

    private userSettings session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crittercism.initialize(getApplicationContext(), getString(R.string.critterID));
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Session Manager
        session = new userSettings(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        session.checkLogin(false, true);

        // Login button
        Button btnLoginKey = (Button) findViewById(R.id.btnLoginKey);
        //Button btnLoginPin = (Button) findViewById(R.id.btnLoginPin);


        // Login button click event
        btnLoginKey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Get username, password from EditText
                String username = txtUsername.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                // Check if username, password is filled
                if (username.length() > 0 && password.length() > 0) {
                    gridCom client = new gridCom("userinfo", getString(R.string.API_KEY));
                    client.addHttpPost("user", password);
                    JSONObject tmp = client.getJSONFromUrl();
                    if (client.getState() != gridCom.runState.ERROR) {
                        debug(tmp.toString());
                        Log.i("request done", tmp.toString());
                        try {
                            int status = tmp.getInt("status");
                            if (status == 200) {

                                if (tmp.get("codename").toString().toLowerCase().equals(username.toLowerCase())) {
                                    session.createLoginSession(tmp.getString("codename"), tmp.getString("profilepic").replace("\\/", "/"), password, tmp.getString("innovator"));
                                    Intent i = new Intent(getApplicationContext(), user.class);
                                    startActivity(i);
                                    finish();

                                } else {
                                    // username / password doesn't match
                                    session.logoutUser();
                                    debug("Wrong username/key)");
                                }

                            } else {
                                debug("Data request failed with status: " + tmp.get("status"));
                                debug("Data request failed with error: " + tmp.get("error"));
                            }
                        } catch (JSONException ej) {
                            Log.e("main", ej.getMessage());
                            ej.printStackTrace();
                        }

                    } else
                        debug("ERROR STATE");


                    // Staring MainActivity


                } else

                {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    debug("please provide User Information");

                }

            }


        });
    }

    private void debug(String string) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, string, duration);
        toast.show();
    }
}