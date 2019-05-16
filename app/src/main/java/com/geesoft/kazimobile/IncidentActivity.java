package com.geesoft.kazimobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class IncidentActivity extends AppCompatActivity {

    Incident activeIncident = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);

        activeIncident = (Incident)getIntent().getSerializableExtra("SelectedIncident");

    }
}
