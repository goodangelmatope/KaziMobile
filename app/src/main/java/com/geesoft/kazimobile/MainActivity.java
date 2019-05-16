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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {


    private static final int RESULT_SETTINGS = 1;
    private ListView listViewIncidents;
    CommonFunctions commonFunctions = new CommonFunctions();

    private Context ctx;
    private DBTools dbTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this.getApplicationContext(); //watch this and see if it causes errors
        dbTools = new DBTools(ctx);
        getIncidentListTest();

    }

    //----------------------------------------------------------------------------------------------

    private Context getCurrentContext() {
        return this;
    }

    //----------------------------------------------------------------------------------------------

    public void getIncidentListTest() {

                        String incidentListJSON = dbTools.getTestIncidentListJSON().toString();
                        List<Incident> resultList = dbTools.parseIncidentList(incidentListJSON);

                        //listViewFeaturedResults = ( ListView ) getActivity().findViewById( R.id.listViewYellowPagesResultFeatured);
                        listViewIncidents = (ListView) findViewById( R.id.incidentListView);

                        if (ctx == null) {Log.e("MainActivity","ctx is null");} else {Log.e("MainActivity","ctx is not null");}
                        if (resultList.isEmpty()) {Log.e("MainActivity","resultlist is empty");} else {Log.e("MainActivity","resultlist not emplty");}
                        /*
                                Collections.sort(resultList, new Comparator<BusinessOwner>() {
                                    @Override
                                    public int compare(BusinessOwner businessOwnerRecord, BusinessOwner t1) {
                                        return businessOwnerRecord.getGuid().compareToIgnoreCase(t1.getGuid());
                                    }
                                });*/

                        listViewIncidents.setAdapter(new IncidentListAdapter(ctx, R.layout.incidentlist_item, resultList));

                        // Click event for single list row
                        //listViewPeopleResults.setOnItemClickListener(listener);
                        listViewIncidents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final Incident selectedIncident = (Incident) parent.getItemAtPosition(position);

                                Log.e("MainActivity","Item touched");

                                        /*Intent dataCaptureIntent = new Intent(getApplication(), BusinessOwnerCaptureActivity.class);
                                        dataCaptureIntent.putExtra("BusinessOwnerRecord", businessOwnerRecord);
                                        dataCaptureIntent.putExtra("InsertMode",false);
                                        dataCaptureIntent.putExtra("AdministratorMode",false);
                                        dataCaptureIntent.putExtra("AppUserName", lvAppUserName);
                                        startActivity(dataCaptureIntent);*/

                            }
                        });
    }

    //----------------------------------------------------------------------------------------------

    public void getIncidentList() {

        String lvHostIP = "";
        String lvHostPort = "";
        Boolean lvUseSSL = false;
        Boolean lvUseExtendedURL = false;
        String lvURLExtender = "/MCTI";
        boolean demoMode = false;
        String urlPrefix = "http://";
        RequestQueue queue = null;

        //btnAgentList.setEnabled(false);

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        lvHostIP = sharedPrefs.getString("serverHost", "192.168.8.102");
        lvHostPort = sharedPrefs.getString("serverPort", "7980");
        lvUseSSL = sharedPrefs.getBoolean("useSSL", false);
        lvUseExtendedURL = sharedPrefs.getBoolean("useExtendedURL",false);
        lvURLExtender = sharedPrefs.getString("urlExtender","/MCTI");

        if (lvUseSSL) {urlPrefix = "https://";}

        if (!lvUseExtendedURL) {lvURLExtender = "";}

        //String lvURL = urlPrefix + lvHostIP + ":" + lvHostPort + lvURLExtender +  "/MCTIRegMobile/MCTIRegMobile?action=businessownersearch&searchtext=" + edtSearchText.getText();
        String lvURL = urlPrefix + lvHostIP + ":" + lvHostPort + lvURLExtender + "/api/IncidentList?userID=1";

        Log.e("RecordSearchActivity", "Url used for getting incident list ->" + lvURL);

        //InputStream agentListJSONStream = getResources().openRawResource(R.raw.agentdemojson); // getting JSON
        //agentListJSON = readTextFile(agentListJSONStream);

        if (!lvUseSSL) { //if we are not using SSL its very simple
            queue = Volley.newRequestQueue(getCurrentContext());
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
            queue = Volley.newRequestQueue(getCurrentContext(), hurlStack);
        }

        //RequestQueue queue = Volley.newRequestQueue(RecordSearchActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, lvURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        String incidentListJSON = response;

                        List<Incident> resultList = dbTools.parseIncidentList(incidentListJSON);



                                //listViewFeaturedResults = ( ListView ) getActivity().findViewById( R.id.listViewYellowPagesResultFeatured);
                                listViewIncidents = (ListView) findViewById( R.id.incidentListView);

                                if (ctx == null) {Log.e("MainActivity","ctx is null");} else {Log.e("MainActivity","ctx is not null");}
                                if (resultList.isEmpty()) {Log.e("MainActivity","resultlist is empty");} else {Log.e("MainActivity","resultlist not emplty");}
                        /*
                                Collections.sort(resultList, new Comparator<BusinessOwner>() {
                                    @Override
                                    public int compare(BusinessOwner businessOwnerRecord, BusinessOwner t1) {
                                        return businessOwnerRecord.getGuid().compareToIgnoreCase(t1.getGuid());
                                    }
                                });*/

                                listViewIncidents.setAdapter(new IncidentListAdapter(ctx, R.layout.incidentlist_item, resultList));

                                // Click event for single list row
                                //listViewPeopleResults.setOnItemClickListener(listener);
                                listViewIncidents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        final Incident selectedIncident = (Incident) parent.getItemAtPosition(position);

                                        Log.e("MainActivity","Item touched");

                                        /*Intent dataCaptureIntent = new Intent(getApplication(), BusinessOwnerCaptureActivity.class);
                                        dataCaptureIntent.putExtra("BusinessOwnerRecord", businessOwnerRecord);
                                        dataCaptureIntent.putExtra("InsertMode",false);
                                        dataCaptureIntent.putExtra("AdministratorMode",false);
                                        dataCaptureIntent.putExtra("AppUserName", lvAppUserName);
                                        startActivity(dataCaptureIntent);*/

                                    }
                                });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Log.e("MainActivity", "Failed to access server, Check your internet connection.");
                    Toast.makeText(getApplicationContext(), "Failed to access server. Please check your connection or server settings",
                            Toast.LENGTH_LONG).show();
                }

            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.putAll(headersSys);
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

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
                MainActivity.this);
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
