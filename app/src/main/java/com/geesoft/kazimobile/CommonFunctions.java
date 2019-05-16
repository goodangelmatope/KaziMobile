package com.geesoft.kazimobile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Zamswitch on 08/02/2017.
 */

public class CommonFunctions implements Serializable {

    public Date EncodeDate(int day, int month, int year){

        Calendar c = Calendar.getInstance();
        c.set(year, month -1, day, 0, 0);

        return c.getTime();
    }


    //----------------------------------------------------------------------------------------------

    public Date StringToDate(String dateString){
        /* this converts a date from the format yyyy-MM-ddTHH:mm:ss.SS to a date */
        Date lvdate = null;
        try {
            SimpleDateFormat formatter, FORMATTER;
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
            //String oldDate = "2011-03-10T11:54:30.207";
            lvdate = formatter.parse(dateString.substring(0, 22));
        } catch(ParseException pe) {
            lvdate = new Date();
        }
        return lvdate;
    }

    //----------------------------------------------------------------------------------------------

    public Date StringToDateNoMS(String dateString){
        /* this converts a date from the format yyyy-MM-ddTHH:mm:ss.SS to a date */
        Date lvdate = null;
        try {
            SimpleDateFormat formatter, FORMATTER;
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            //String oldDate = "2011-03-10T11:54:30.207";
            lvdate = formatter.parse(dateString.substring(0, 19));
        } catch(ParseException pe) {
            lvdate = new Date();
        }
        return lvdate;
    }


    //----------------------------------------------------------------------------------------------

    public String DateToDBString(Date lvDate){
        /* this converts a date from the format yyyy-MM-ddTHH:mm:ss.SS to a date */
        SimpleDateFormat formatter, FORMATTER;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");

        String lvDateString = null;

        //String oldDate = "2011-03-10T11:54:30.207";
        lvDateString = formatter.format(lvDate);

        return lvDateString;
    }

    //----------------------------------------------------------------------------------------------

    public String DBStringDateToDisplayDate(String dateString){
        /* this converts a date from the format yyyy-MM-ddTHH:mm:ss.SS to a date */
        SimpleDateFormat dbFormatter, displayFormatter;
        String displayDateString;
        Date lvDate = null;
        dbFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        displayFormatter = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            //String oldDate = "2011-03-10T11:54:30.207";
            /* we first parse the db string to date then we format it as we wish */
            lvDate = dbFormatter.parse(dateString.substring(0, 24));

            displayDateString = displayFormatter.format(lvDate);
        } catch(ParseException pe) {
            lvDate = new Date();
            displayDateString = displayFormatter.format(lvDate);
        }
        return displayDateString;
    }

    //----------------------------------------------------------------------------------------------

    public  boolean isStoragePermissionGranted(Activity lvActivity, int PERMISSION_REQUEST_CODE) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(lvActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("isStoragePermission","Permission is granted");
                return true;
            } else {

                Log.v("isStoragePermission","Permission is revoked");
                ActivityCompat.requestPermissions(lvActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("isStoragePermission","Permission is granted");
            return true;
        }
    }

    //----------------------------------------------------------------------------------------------

    public static Bitmap shrinkBitmap(Bitmap realImage, float maxImageSize,
                                      boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    //----------------------------------------------------------------------------------------------

    public void shrinkJPEG(String lvFullImagePathName, float maxImageSize)
    {
        try {
            File f = new File(lvFullImagePathName);

            FileInputStream inputStream = new FileInputStream(f);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap smallBitmap = shrinkBitmap(originalBitmap, maxImageSize, true);
            inputStream.close();

            f.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(f);
            smallBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

        } catch (FileNotFoundException fnfe) {
            Log.e("shrinkJPEG","File not found -> " + fnfe.toString());
        }  catch (IOException ioe) {
            Log.e("shrinkJPEG","IO Exception -> " + ioe.toString());
        }
    }

    //----------------------------------------------------------------------------------------------

    // Let's assume your server app is hosting inside a server machine
    // which has a server certificate in which "Issued to" is "localhost",for example.
    // Then, inside verify method you can verify "localhost".
    // If not, you can temporarily return true
    public HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("localhost", session);
            }
        };
    }

    public TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    public SSLSocketFactory getSSLSocketFactory(Context lvContext)
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = lvContext.getResources().openRawResource(R.raw.my_cert); // this cert file stored in \app\src\main\res\raw folder path

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }


}
