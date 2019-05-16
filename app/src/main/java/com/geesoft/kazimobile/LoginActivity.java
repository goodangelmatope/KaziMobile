package com.geesoft.kazimobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    private static final int RESULT_SETTINGS = 1;
    DBTools dbtools = new DBTools(this);
    CommonFunctions commonFunctions = new CommonFunctions();

    String loginJSON;
    String lvHostIP = "";
    String lvHostPort = "";
    boolean lvUseSSL = false;
    boolean lvUseExtendedURL = true;
    String lvURLExtender = "/MCTI";
    String lvDeviceID = "00001";
    String cachedUserName = "";
    String cachedPassword = "";
    boolean lvCacheResult;
    boolean isAdministrator = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createSharedPreferences();

        processCachedLoginDetails();
        addButtonClickListeners();
        showProgress(false);
    }

    //----------------------------------------------------------------------------------------------

    public void createSharedPreferences() {

        Log.e("LoginActivity","Inside create shared preferences");
        final SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        if (sharedPrefs.contains("serverHost") && sharedPrefs.getString("serverHost","").trim().length() >= 2) {
            Log.e("LoginActivity","Shared Preferences already exist");

        } else { //if the server host shared preference does not exist
            Log.e("LoginActivity", "Shared Preferences do not exist");

            SharedPreferences.Editor sharedPrefEditor = sharedPrefs.edit();
            sharedPrefEditor.putString("serverHost", "192.168.8.103");
            sharedPrefEditor.putString("serverPort", "83");
            sharedPrefEditor.putString("deviceID", "000001");
            sharedPrefEditor.putBoolean("demoMode", false);
            sharedPrefEditor.putBoolean("useSSL", false);
            sharedPrefEditor.putBoolean("cachePassword", false);
            sharedPrefEditor.putString("cachedUser", "test");
            sharedPrefEditor.putString("cachedPassword", "test");
            sharedPrefEditor.putBoolean("useExtendedURL", true);
            sharedPrefEditor.putString("urlExtender", "/api");
            sharedPrefEditor.commit();

        }

    }

    //----------------------------------------------------------------------------------------------

    public void cacheUser(String lvUserName, String lvPassword) {
        final SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        SharedPreferences.Editor sharedPrefEditor = sharedPrefs.edit();
        sharedPrefEditor.putBoolean("cachePassword", true);
        sharedPrefEditor.putString("cachedUser", lvUserName);
        sharedPrefEditor.putString("cachedPassword", lvPassword);
        sharedPrefEditor.commit();

    }


    //----------------------------------------------------------------------------------------------

    private Context getCurrentContext() {
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public void addButtonClickListeners(){


        //adding listener for the Login button on the login screen
        final Button buttonLogin = (Button)findViewById(R.id.btnLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                /*final EditText edtUserName = (EditText) findViewById(R.id.edtLoginName);
                final EditText edtPassword = (EditText) findViewById(R.id.edtPassword);

                buttonLogin.setEnabled(false);

                String loginResultCount;

                if (!edtUserName.getText().toString().trim().equals("") && !edtPassword.getText().toString().trim().equals("")) {


                    Intent mainConsoleIntent = new Intent(getApplication(), MainActivity.class);
                    mainConsoleIntent.putExtra("LoginResultJSON", loginJSON);
                    mainConsoleIntent.putExtra("AppUserName", edtUserName.getText().toString().trim());
                    mainConsoleIntent.putExtra("LoggedInMode", "Remote Server");
                    mainConsoleIntent.putExtra("isAdministrator",isAdministrator);
                    startActivity(mainConsoleIntent);
                    //we write the appusername to shared preferences  to allow retreival later
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Error logging in. Please enter correct credentials.",
                            Toast.LENGTH_LONG).show();
                    buttonLogin.setEnabled(true);
                }
                */

                final EditText edtUserName = (EditText) findViewById(R.id.edtLoginName);
                final EditText edtPassword = (EditText) findViewById(R.id.edtPassword);

                buttonLogin.setEnabled(false);

                String urlPrefix = "http://";
                String loginResultCount;

                if (!edtUserName.getText().toString().trim().equals("") && !edtPassword.getText().toString().trim().equals("")) {

                    boolean demoMode = false;
                    boolean cachePassword = false;

                    showProgress(true);

                    final SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());

                    lvHostIP = sharedPrefs.getString("serverHost", "192.160.8.102");
                    lvHostPort = sharedPrefs.getString("serverPort", "85");
                    lvDeviceID = sharedPrefs.getString("deviceID", "00001");
                    lvUseSSL = sharedPrefs.getBoolean("useSSL",false);
                    lvUseExtendedURL = sharedPrefs.getBoolean("useExtendedURL",false);
                    lvURLExtender = sharedPrefs.getString("urlExtender","/kazi");
                    demoMode = sharedPrefs.getBoolean("demoMode", false);

                    if (lvUseSSL) { urlPrefix = "https://";}
                    if (!lvUseExtendedURL) {lvURLExtender = "";}

                    String lvURL = urlPrefix + lvHostIP + ":" + lvHostPort + /*lvURLExtender + */ "/api/login?mobileNumber=" + edtUserName.getText().toString().trim() +
                                    "&password=" + edtPassword.getText().toString().trim();

                    Log.e("LoginActivity", "Url used for login ->" + lvURL);

                    //InputStream agentListJSONStream = getResources().openRawResource(R.raw.agentdemojson); // getting JSON
                    //agentListJSON = readTextFile(agentListJSONStream);

                    RequestQueue queue = null;

                    if (!lvUseSSL) { //if we are not using SSL its very simple
                        queue = Volley.newRequestQueue(LoginActivity.this);
                    }
                    if (lvUseSSL) {

                        HurlStack hurlStack = new HurlStack() {
                            @Override
                            protected HttpURLConnection createConnection(URL url) throws IOException {
                                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                                try {
                                    httpsURLConnection.setSSLSocketFactory(commonFunctions.getSSLSocketFactory(getCurrentContext()));
                                    httpsURLConnection.setHostnameVerifier(commonFunctions.getHostnameVerifier());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return httpsURLConnection;
                            }
                        };
                        queue = Volley.newRequestQueue(LoginActivity.this, hurlStack);
                    }

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, lvURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    loginJSON = response;
                                    Log.e("LoginActivity", "JSON Response ->" + loginJSON.toString());

                                    try {

                                        //JSONObject lgJSONObject = new JSONObject(loginJSON);
                                        //JSONArray loginJSONArray = lgJSONObject.getJSONArray("logins");
                                        JSONArray loginJSONArray = new JSONArray(loginJSON);

                                        int lvAdministrator = 0;
                                        int lvSupervisor = 0;

                                        if (loginJSONArray.length() > 0) {

                                            try {

                                                //JSONObject usersJSONObject = new JSONObject(loginJSON);
                                                //JSONArray userJSONArray = usersJSONObject.getJSONArray("logins");

                                                Log.e("HistoryActivity","Number of users = " + String.valueOf(loginJSONArray.length()));

                                                for (int i = 0; i < loginJSONArray.length(); i++) {

                                                    Log.e("HistoryActivity", "Inside Featured List JSONArray Loop");
                                                    JSONObject userJSONObject = loginJSONArray.getJSONObject(i);

                                                    //lvDailyReadingRecord.setId(jsonObject.getInt("id"));
                                                    lvAdministrator = 1;
                                                    lvSupervisor = 1;

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (dbtools.localUserExists(edtUserName.getText().toString().trim()) != true){
                                                dbtools.addRemoteUserToLocalDB(edtUserName.getText().toString().trim(),edtPassword.getText().toString().trim(), lvAdministrator, lvSupervisor);
                                            } else {
                                                dbtools.updateLocalUserPassword(edtUserName.getText().toString().trim(), edtPassword.getText().toString().trim());
                                            }

                                            Log.e("LoginActivity","Successfully logged in via web");
                                            isAdministrator = dbtools.isAdmin(edtUserName.getText().toString().trim());

                                            //if (ShowYesNoDialog("Save Credentials","Do you want to cache your user name and password?") == true) {
                                            cacheUser(edtUserName.getText().toString().trim(), edtPassword.getText().toString().trim());
                                            //}

                                            Intent mainConsoleIntent = new Intent(getApplication(), MainActivity.class);
                                            mainConsoleIntent.putExtra("LoginResultJSON", loginJSON);
                                            mainConsoleIntent.putExtra("AppUserName", edtUserName.getText().toString().trim());
                                            mainConsoleIntent.putExtra("LoggedInMode", "Remote Server");
                                            mainConsoleIntent.putExtra("isAdministrator",isAdministrator);
                                            startActivity(mainConsoleIntent);
                                            /* we write the appusername to shared preferences  to allow retreival later */
                                            finish();

                                        } else {
                                            ShowAlert("Login Error","Invalid Credentials. Please enter correct credentials");
                                            buttonLogin.setEnabled(true);
                                            showProgress(false);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        buttonLogin.setEnabled(true);
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("LoginAcivity", "Failed to access remote server " + lvHostIP + ". Check your internet connection.");

                            Log.e("LoginActivity", error.getMessage());
                            //SystemClock.sleep(3000);

                            if (dbtools.localLogin(edtUserName.getText().toString().trim(), edtPassword.getText().toString().trim()) == true) {

                                cacheUser(edtUserName.getText().toString().trim(), edtPassword.getText().toString().trim());
                                isAdministrator = dbtools.isAdmin(edtUserName.getText().toString().trim());

                                Intent mainConsoleIntent = new Intent(getApplication(), MainActivity.class);
                                mainConsoleIntent.putExtra("LoginResultJSON", loginJSON);
                                mainConsoleIntent.putExtra("AppUserName", edtUserName.getText().toString().trim());
                                mainConsoleIntent.putExtra("LoggedInMode", "Local Database");
                                mainConsoleIntent.putExtra("isAdministrator",isAdministrator);
                                startActivity(mainConsoleIntent);
                                Log.e("LoginActivity", "Successfully logged in locally");
                                            /* we write the appusername to shared preferences  to allow retreival later */
                                finish();
                            } else {
                                Log.e("LoginAcivity", "Failed to log in to local db");
                                ShowAlert("Local login Error", "Failed to log in to remote server and local database. Please contact your administrator.");
                                buttonLogin.setEnabled(true);
                                showProgress(false);
                            }

                            /*if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                                Log.e("LoginAcivity", "Failed to access, Check your internet connection.");
                                Toast.makeText(getApplicationContext(), "Failed to access server " + lvHostIP + " Please check your connection or server settings",
                                        Toast.LENGTH_LONG).show();
                                buttonLogin.setEnabled(true);
                            } else {
                                Log.e("LoginAcivity", "Failed to log in, Connection failed." + error.toString());
                                ShowAlert("Login Error","Failed to log in to remote server. Local login will be attempted.");
                            }*/

                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);

                } else {
                    showProgress(false);

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.loginFailedText),
                            Toast.LENGTH_LONG).show();
                    buttonLogin.setEnabled(true);

                }


            }
        });


    }

    //----------------------------------------------------------------------------------------------

    public void processCachedLoginDetails() {

        String lvCachedUserName = "";
        String lvCachedPassword = "";
        EditText edtUserName = (EditText) findViewById(R.id.edtLoginName);
        EditText edtPassword = (EditText) findViewById(R.id.edtPassword);

        final SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        if (sharedPrefs.contains("cachePassword")) {

            if (sharedPrefs.getBoolean("cachePassword",false) == true) {

                lvCachedUserName = sharedPrefs.getString("cachedUser", "");
                lvCachedPassword = sharedPrefs.getString("cachedPassword", "");

                edtUserName.setText(lvCachedUserName);
                edtPassword.setText(lvCachedPassword);
                edtPassword.requestFocus();

            } else {
                edtUserName.setText("");
                edtPassword.setText("");
            }
        }

    }

    //----------------------------------------------------------------------------------------------

    public void showProgress(boolean lvShowProgress) {

        TableRow trLoginName = (TableRow)findViewById(R.id.tableRowUserName);
        TableRow trPassword = (TableRow)findViewById(R.id.tableRowPassword);
        TableRow trButton = (TableRow)findViewById(R.id.tableRowLoginButton);

        TableRow trProgressBar = (TableRow)findViewById(R.id.trProgressBar);

        if (lvShowProgress == true) {

            trLoginName.setVisibility(View.GONE);
            trPassword.setVisibility(View.GONE);
            trButton.setVisibility(View.GONE);

            trProgressBar.setVisibility(View.VISIBLE);

        } else {

            trLoginName.setVisibility(View.VISIBLE);
            trPassword.setVisibility(View.VISIBLE);
            trButton.setVisibility(View.VISIBLE);

            trProgressBar.setVisibility(View.GONE);

        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //if (isAdministrator == true) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //}
        return true;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //if (isAdministrator == true) {
            Intent i = new Intent(this, UserSettingsActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
            return true;
            //} else {
            //    ShowAlert("Permissions Error","This feature is only accessible to administrators");
            //}
        }

        if (id == R.id.action_set_beanstalk) {
            //if (isAdministrator == true) {
            final SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor sharedPrefEditor = sharedPrefs.edit();
            sharedPrefEditor.putString("serverHost", "geebusinesssoln-env.us-east-2.elasticbeanstalk.com");
            sharedPrefEditor.putString("serverPort", "80");
            sharedPrefEditor.commit();
            ShowAlert("Settings","Beanstalk URL Set.");
            return true;
            //} else {
            //    ShowAlert("Permissions Error","This feature is only accessible to administrators");
            //}
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------

    public void ShowAlert(String lvDialogTitle, String lvDialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                LoginActivity.this);
        builder.setTitle(lvDialogTitle);
        builder.setMessage(lvDialogMessage);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Toast.makeText(getApplicationContext(),"Waiting for valid data",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

}
