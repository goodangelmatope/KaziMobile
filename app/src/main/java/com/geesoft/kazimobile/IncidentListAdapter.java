package com.geesoft.kazimobile;

/**
 * Created by Zamswitch on 22/04/2016.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IncidentListAdapter extends ArrayAdapter<Incident> {

    private int resource;
    private LayoutInflater  inflater;
    private Context context;

    public IncidentListAdapter(Context ctx, int resourceId, List<Incident> objects) {
        super( ctx, resourceId, objects );
        resource = resourceId;
        inflater = LayoutInflater.from( ctx );
        context=ctx;
    }

    @Override
    public View getView ( int position, View convertView, ViewGroup parent ) {
        convertView = ( RelativeLayout ) inflater.inflate( resource, null );

        Incident incident = getItem( position );

        Date lvIncidentDate = incident.getCreationDate();

        //declaring roboto typeface
        Typeface robotoTypeFace = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/roboto-light.ttf");

        TextView tvCustomerName = (TextView) convertView.findViewById(R.id.tvCustomerName);
        //tvReadingDate.setText(readingRecord.getReadingDate().toString());
        //tvReadingDate.setText(android.text.format.DateFormat.format("EEE, MMM dd, yyyy", readingRecord.getReadingDate()));
        tvCustomerName.setText(incident.getCustomerName());
        tvCustomerName.setTypeface(robotoTypeFace); //using the typeface

        TextView tvSiteName = (TextView) convertView.findViewById(R.id.tvSiteName);
        tvSiteName.setTypeface(robotoTypeFace);

        tvSiteName.setText(incident.getSiteName());

        TextView tvInstallationType = (TextView) convertView.findViewById(R.id.tvInstallationType);
        tvInstallationType.setTypeface(robotoTypeFace);
        tvInstallationType.setText(incident.getInstallationType());

        TextView tvIncidentType = (TextView) convertView.findViewById(R.id.tvIncidentType);
        tvIncidentType.setTypeface(robotoTypeFace);
        tvIncidentType.setText(incident.getIncidentType());

        TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
        tvDescription.setTypeface(robotoTypeFace);
        tvDescription.setText(incident.getDescription());

        ImageView imgRecordStatus = (ImageView) convertView.findViewById(R.id.imgRecordStatus);

        String uri = "drawable/unsubmitted";

        if (incident.isResolved() == true) {
            uri = "drawable/submitted";
        }

        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        Drawable image = context.getResources().getDrawable(imageResource);
        imgRecordStatus.setImageDrawable(image);

        return convertView;
    }

}


