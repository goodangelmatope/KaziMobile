package com.geesoft.kazimobile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Zamswitch on 30/06/2017.
 */

public class DBTools extends SQLiteOpenHelper {

    CommonFunctions commonFunctions = new CommonFunctions();

    public DBTools(Context context) {
        super(context, "kazimobile.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // TODO Auto-generated method stub

        /* Creating the database tables in the database */

        String query = "CREATE TABLE gps_history( id INTEGER PRIMARY KEY AUTOINCREMENT, gpsdate TEXT, gps_latitude REAL, gps_longitude REAL, submitted INTEGER )";
        database.execSQL(query);


        /* creating and inserting gender table and values */
        query = "CREATE TABLE resolution_type( type INTEGER, installation_type INTEGER, code TEXT, name TEXT)";
        database.execSQL(query);


        query = "CREATE TABLE incident( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "incident_id INTEGER, " +
                "company_id INTEGER, " +
                "reference_number TEXT, " +
                "manufacturer TEXT, " +
                "model TEXT, " +
                "serial_number TEXT, " +
                "creation_date TEXT, " +
                "description TEXT, " +
                "comments TEXT, " +
                "target_resolution_date TEXT, " +
                "assigned_user INTEGER, " +
                "assigned_date TEXT, " +
                "read_by_user INTEGER, " +
                "read_date TEXT, " +
                "read_gps_latitude REAL, " +
                "read_gps_longitude REAL, " +
                "travel_start_time TEXT, " +
                "arrival_time TEXT, " +
                "job_start_date TEXT, " +
                "job_start_gps_latitude REAL, " +
                "job_start_gps_longitude REAL, " +
                "job_end_time TEXT, " +
                "resolved INTEGER, " +
                "resolution_type INTEGER, " +
                "resolution_time TEXT, " +
                "status INTEGER, " +
                "replenishment_quantity REAL, " +
                "customer_name TEXT, " +
                "site_name TEXT, " +
                "site_gps_latitude REAL, " +
                "site_gps_longitude REAL, " +
                "installation_type TEXT, " +
                "incident_type TEXT, " +
                "submitted INT)";

        database.execSQL(query);

        /*creating the users table and inserting two test users*/

        query = "CREATE TABLE users( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, password TEXT, administrator INT, supervisor INT)";

        database.execSQL(query);

        query = "INSERT INTO users(username, password, administrator, supervisor) " +
                "VALUES('admin','admin',1,1)";

        database.execSQL(query);

        query = "INSERT INTO users(username, password, administrator, supervisor) " +
                "VALUES('test','test',1,1)";

        database.execSQL(query);

        query = "INSERT INTO users(username, password, administrator, supervisor) " +
                "VALUES('260972702708','test',1,1)";

        database.execSQL(query);

    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        String query = "DROP TABLE if exists gps_history";database.execSQL(query);
        query = "DROP TABLE if exists resolution_type";database.execSQL(query);
        query = "DROP TABLE if exists incident";database.execSQL(query);
        query = "DROP TABLE if exists users";database.execSQL(query);

        onCreate(database);

    }

    //----------------------------------------------------------------------------------------------

    public void InsertNewIncident(Incident lvIncident) {

        SQLiteDatabase database = this.getReadableDatabase();

        String saveQuery = "INSERT INTO incident(incident_id,company_id, reference_number, " +
                "manufacturer, model, serial_number, status, " +
                "creation_date, description, comments, target_resolution_date, assigned_user, assigned_date, resolved, submitted) " +
                "VALUES (" + String.valueOf(lvIncident.getId()) + ", " + String.valueOf(lvIncident.getCompanyID()) + ", '" + lvIncident.getReferenceNumber() + "','" +
                lvIncident.getManufacturer() + "', '" + lvIncident.getModel() + "', '" + lvIncident.getSerialNumber() + "', " + String.valueOf(lvIncident.getStatus()) + ",'" +
                commonFunctions.DateToDBString(lvIncident.getCreationDate()) + "', '" + lvIncident.getDescription() + "', '" +
                lvIncident.getComments() + "','" + commonFunctions.DateToDBString(lvIncident.getTargetResolutionTime()) + "', " +
                String.valueOf(lvIncident.getAssignedUserID()) + ", '" + commonFunctions.DateToDBString(lvIncident.getAssignedDate()) + "', 0, 0)";

        database.execSQL(saveQuery);

    }


    //----------------------------------------------------------------------------------------------

     /* This function updates the password for the local user. Typically this will be used to update
       a user whose password has been reset or changed at the server level */

    public void updateLocalUserPassword(String lvUserName, String lvPassword){

        SQLiteDatabase database = this.getWritableDatabase();
        String userInsertQuery = "UPDATE users SET password = '" + lvPassword + "' WHERE username = '" + lvUserName +"'";
        database.execSQL(userInsertQuery);

    }

    //----------------------------------------------------------------------------------------------

    public void addRemoteUserToLocalDB(String lvUserName, String lvPassword, int lvAdministrator, int lvSupervisor){

        SQLiteDatabase database = this.getWritableDatabase();
        String userInsertQuery = "INSERT INTO users(username, password, administrator, supervisor) VALUES('" + lvUserName + "','" + lvPassword + "'," +
                String.valueOf(lvAdministrator) + "," + String.valueOf(lvSupervisor)  + ")";
        database.execSQL(userInsertQuery);

    }

    //----------------------------------------------------------------------------------------------

    public boolean localUserExists(String lvUserName) {

        /* this function checks if the user who is logging in exists in the
           local user database. The local user database is used to cache users so that
           when they are offline or the device has no connectivity, the user can
           log in using data saved in the local users table.
         */
        boolean userExists = false;

        SQLiteDatabase database = this.getReadableDatabase();

        String searchQuery = "SELECT * FROM users where username = '" + lvUserName + "'";
        Cursor cursor = database.rawQuery(searchQuery, null);

        if (cursor.getCount() > 0) {
            Log.e("UserExists","User already exists");
            userExists = true;
        } else {
            Log.e("UserExists", "User does not exist");
            userExists = false;
        }

        return userExists;

    }

    //----------------------------------------------------------------------------------------------
    /* This function checks the local database to check if the local user is an
       administrator. This is used to allow or disallow administrative tasks in offline mode
     */

    public boolean isAdmin(String lvUserName) {

        boolean isAdm = false;
        int intAdm =0;

        SQLiteDatabase database = this.getReadableDatabase();

        String searchQuery = "SELECT * FROM users where username = '" + lvUserName + "'";
        Cursor cursor = database.rawQuery(searchQuery, null);

        if (cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {
                do {
                    intAdm = cursor.getInt(cursor.getColumnIndex("administrator"));
                } while (cursor.moveToNext());

            } else {
                Log.e("isAdmin", "User does not exist");
                intAdm = 0;
            }
        }

        if (intAdm == 1) {isAdm = true;}

        return isAdm;

    }

    //----------------------------------------------------------------------------------------------

    /*  This function cosumes a user name and password and returns a boolean indicating whether
        or not a local login has been successful.

     */
    public boolean localLogin(String lvUserName, String lvPassword){

        boolean loginSuccessful = false;

        SQLiteDatabase database = this.getReadableDatabase();

        String loginQuery = "SELECT * FROM users where username = '" + lvUserName + "' and password = '" + lvPassword + "'";
        Cursor cursor = database.rawQuery(loginQuery, null);

        if (cursor.getCount() > 0) {
            Log.e("localLogin", "Local login Successful");
            loginSuccessful = true;
        } else {
            Log.e("localLogin", "Local login Unsuccessful");
            loginSuccessful = false;
        }

        return loginSuccessful;
    }

    //----------------------------------------------------------------------------------------------

    public List<Incident> parseIncidentList(String incidentListJSON){

        List<Incident> resultList = new ArrayList<Incident>();
        try {

            //JSONObject searchJSONObject = new JSONObject(businessOwnerJSON);
            //JSONArray BusinessOwnerJSONArray = searchJSONObject.getJSONArray("businessowners");
            Log.e("parseincidentlist","incidentlistJSON->" + incidentListJSON);
            JSONArray IncidentJSONArray = new JSONArray(incidentListJSON);

            if (IncidentJSONArray.length() > 0) {

                Log.e("RecordSearchActivity", "JSONSubmitted->" + incidentListJSON);

                try {

                    JSONArray incidentsJSONArray = new JSONArray(incidentListJSON);

                    for (int i = 0; i < incidentsJSONArray.length(); i++) {

                        Log.e("RecordSearchActivity", "Inside Featured List JSONArray Loop");
                        JSONObject jsonObject = incidentsJSONArray.getJSONObject(i);

                        int lvID = jsonObject.getInt("ID");
                        int lvCompanyID = 0;
                        int lvStatus = jsonObject.getInt("Status");

                        float lvReadGPSLatitude = 0;
                        float lvReadGPSLongitude = 0;
                        float lvJobStartGPSLatitude = 0;
                        float lvJobStartGPSLongitude = 0;
                        float lvSiteGPSLatitude = 0;
                        float lvSiteGPSLongitude = 0;

                        String lvAssignedDateStr = "1900-01-01T00:00:00";
                        String lvUserReadTimeStr = "1900-01-01T00:00:00";
                        String lvTravelStartTimeStr = "1900-01-01T00:00:00";
                        String lvArrivalTimeStr = "1900-01-01T00:00:00";
                        String lvJobStartTimeStr = "1900-01-01T00:00:00";
                        String lvCreationDateStr = "1900-01-01T00:00:00";
                        String lvTargetResolutionDateStr = "1900-01-01T00:00:00";
                        String lvJobEndTimeStr = "1900-01-01T00:00:00";

                        String lvResolutionComments = "";
                        int lvResolutionType = 0;

                        Boolean lvResolved = false;


                        String lvReferenceNumber = jsonObject.getString("ReferenceNumber");
                        String lvCustomerName = jsonObject.getString("CustomerName");
                        String lvSiteName = jsonObject.getString("SiteName");
                        String lvSerialNumber = jsonObject.getString("SerialNumber");

                        if (!jsonObject.isNull("GPSLatitude")) {
                            lvSiteGPSLatitude = (float)jsonObject.getDouble("GPSLatitude");
                        }

                        if (!jsonObject.isNull("GPSLongitude")) {
                            lvSiteGPSLongitude = (float)jsonObject.getDouble("GPSLongitude");
                        }

                        String lvIncidentType = jsonObject.getString("IncidentType");
                        String lvInstallationType = jsonObject.getString("InstallationType");
                        String lvManufacturer = jsonObject.getString("Manufacturer");
                        String lvModel = jsonObject.getString("Model");

                        if (!jsonObject.isNull("CreationDate")) {
                            lvCreationDateStr = jsonObject.getString("CreationDate");
                        }


                        if (!jsonObject.isNull("AssignedDate")) {
                            lvAssignedDateStr = jsonObject.getString("AssignedDate");
                        }

                        String lvDescription = jsonObject.getString("Description");
                        String lvComments = jsonObject.getString("Comments");

                        int lvAssignedUserID = jsonObject.getInt("AssignedUserID");
                        boolean lvReadByUser = jsonObject.getBoolean("ReadByUser");

                        if (!jsonObject.isNull("ReadGPSLatitude")) {
                            lvReadGPSLatitude = (float) jsonObject.getDouble("ReadGPSLatitude");
                        }

                        if (!jsonObject.isNull("ReadGPSLongitude")) {
                            lvReadGPSLongitude = (float) jsonObject.getDouble("ReadGPSLongitude");
                        }

                        if (!jsonObject.isNull("UserReadTime")) {
                            lvUserReadTimeStr = jsonObject.getString("UserReadTime");
                        }

                        if (!jsonObject.isNull("TravelStartTime")) {
                            lvTravelStartTimeStr = jsonObject.getString("TravelStartTime");
                        }

                        if (!jsonObject.isNull("ArrivalTime")) {
                            lvArrivalTimeStr = jsonObject.getString("ArrivalTime");
                        }

                        if (!jsonObject.isNull("JobStartTime")) {
                            lvJobStartTimeStr = jsonObject.getString("JobStartTime");
                        }

                        if (!jsonObject.isNull("JobStartGPSLatitude")) {
                            lvJobStartGPSLatitude = (float) jsonObject.getDouble("JobStartGPSLatitude");
                        }

                        if (!jsonObject.isNull("JobStartGPSLongitude")) {
                            lvJobStartGPSLongitude = (float) jsonObject.getDouble("JobStartGPSLongitude");
                        }

                        if (!jsonObject.isNull("JobEndTime")) {
                            lvJobEndTimeStr = jsonObject.getString("JobEndTime");
                        }

                        if (!jsonObject.isNull("TargetResolutionTime")) {
                            lvTargetResolutionDateStr = jsonObject.getString("TargetResolutionTime");
                        }

                        if (!jsonObject.isNull("ResolutionComments")) {
                            lvResolutionComments = jsonObject.getString("ResolutionComments");
                        }

                        if (!jsonObject.isNull("ResolutionType")) {
                            lvResolutionType = jsonObject.getInt("ResolutionType");
                        }

                        if (!jsonObject.isNull("Resolved")) {
                            lvResolved = jsonObject.getBoolean("Resolved");
                        }

                        float lvReplenishmentQuantity = 0;

                        Date creationDate = commonFunctions.StringToDateNoMS(lvCreationDateStr);
                        Date assignedDate = commonFunctions.StringToDateNoMS(lvAssignedDateStr);
                        Date targetResolutionTime = commonFunctions.StringToDateNoMS(lvTargetResolutionDateStr);
                        Date readTime = commonFunctions.StringToDateNoMS(lvUserReadTimeStr);
                        Date travelStartTime = commonFunctions.StringToDateNoMS(lvTravelStartTimeStr);
                        Date arrivalTime = commonFunctions.StringToDateNoMS(lvArrivalTimeStr);
                        Date jobStartTime = commonFunctions.StringToDateNoMS(lvJobStartTimeStr);
                        Date jobEndTime = commonFunctions.StringToDateNoMS(lvJobEndTimeStr);

                        Incident incident = new Incident(lvID, lvCompanyID, lvStatus, lvReferenceNumber, lvCustomerName,
                                lvSiteName, lvSiteGPSLatitude, lvSiteGPSLongitude, lvIncidentType,
                                lvInstallationType, lvManufacturer, lvModel, lvSerialNumber,
                                creationDate, assignedDate, lvDescription, lvComments,
                                targetResolutionTime, lvAssignedUserID, lvReadByUser,
                                lvReadGPSLatitude, lvReadGPSLongitude, readTime,
                                travelStartTime, arrivalTime, jobStartTime, lvJobStartGPSLatitude,
                                lvJobStartGPSLongitude, jobEndTime, lvResolutionType, lvResolutionComments,
                                lvResolved, lvReplenishmentQuantity);

                        resultList.add(incident);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (resultList.isEmpty()) {
            Log.e("dbtools","Empty Result Set");
        }
        return resultList;
    }

    //----------------------------------------------------------------------------------------------

    public String getTestIncidentListJSON() {

       String lvJSON = "[{\"ID\":1,\"ReferenceNumber\":\"20170606-000000\",\"Manufacturer\":\"NCR\",\"Model\":\"SELFSERV 26\",\"SerialNumber\":\"111222333444\",\"CreationDate\":\"1900-01-01T00:00:00\",\"Description\":\"Dead ATM\",\"Comments\":\"Rush Please\",\"TargetResolutionTime\":\"2017-06-05T06:00:00\",\"AssignedUserID\":1,\"ReadByUser\":false,\"ReadGPSLatitude\":null,\"ReadGPSLongitude\":null,\"UserReadTime\":null,\"TravelStartTime\":null,\"ArrivalTime\":null,\"JobStartTime\":null,\"JobStartGPSLatitude\":null,\"JobStartGPSLongitude\":null,\"JobEndTime\":null,\"ResolutionComments\":null,\"Resolved\":false,\"Status\":1,\"ReplenishmentQuantity\":0.000,\"AssignedDateTime\":\"2017-06-05T06:00:00\",\"ReadDateTime\":null,\"ResolutionTime\":null,\"ResolutionType\":null,\"CustomerName\":\"INDO-ZAMBIA BANK LIMITED\",\"SiteName\":\"MANDA HILL\",\"GPSLatitude\":-15.3978000,\"GPSLongitude\":28.3061000,\"InstallationType\":\"ATM\",\"IncidentType\":\"DISPENSER JAMING\"},{\"ID\":2,\"ReferenceNumber\":\"20170606-000001\",\"Manufacturer\":\"NCR\",\"Model\":\"SELFSERV 26\",\"SerialNumber\":\"111222333444\",\"CreationDate\":\"1900-01-01T00:00:00\",\"Description\":\"Dead ATM\",\"Comments\":\"Rush Please\",\"TargetResolutionTime\":\"2017-06-05T06:00:00\",\"AssignedUserID\":1,\"ReadByUser\":false,\"ReadGPSLatitude\":null,\"ReadGPSLongitude\":null,\"UserReadTime\":null,\"TravelStartTime\":null,\"ArrivalTime\":null,\"JobStartTime\":null,\"JobStartGPSLatitude\":null,\"JobStartGPSLongitude\":null,\"JobEndTime\":null,\"ResolutionComments\":null,\"Resolved\":false,\"Status\":1,\"ReplenishmentQuantity\":0.000,\"AssignedDateTime\":null,\"ReadDateTime\":null,\"ResolutionTime\":null,\"ResolutionType\":null,\"CustomerName\":\"STANDARD CHARTERED BANK\",\"SiteName\":\"MANDA HILL\",\"GPSLatitude\":-15.3978000,\"GPSLongitude\":28.3061000,\"InstallationType\":\"ATM\",\"IncidentType\":\"DISPENSER JAMING\"},{\"ID\":3,\"ReferenceNumber\":\"20170606-000001\",\"Manufacturer\":\"NCR\",\"Model\":\"SELFSERV 26\",\"SerialNumber\":\"111222333444\",\"CreationDate\":\"2017-06-06T14:44:33.13\",\"Description\":\"Dead ATM\",\"Comments\":\"Rush Please\",\"TargetResolutionTime\":\"2017-06-05T06:00:00\",\"AssignedUserID\":1,\"ReadByUser\":false,\"ReadGPSLatitude\":null,\"ReadGPSLongitude\":null,\"UserReadTime\":null,\"TravelStartTime\":null,\"ArrivalTime\":null,\"JobStartTime\":null,\"JobStartGPSLatitude\":null,\"JobStartGPSLongitude\":null,\"JobEndTime\":null,\"ResolutionComments\":null,\"Resolved\":false,\"Status\":1,\"ReplenishmentQuantity\":0.000,\"AssignedDateTime\":null,\"ReadDateTime\":null,\"ResolutionTime\":null,\"ResolutionType\":null,\"CustomerName\":\"INDO-ZAMBIA BANK LIMITED\",\"SiteName\":\"MANDA HILL\",\"GPSLatitude\":-15.3978000,\"GPSLongitude\":28.3061000,\"InstallationType\":\"ATM\",\"IncidentType\":\"DISPENSER JAMING\"}]";
        return lvJSON;
    }

    //----------------------------------------------------------------------------------------------

}
