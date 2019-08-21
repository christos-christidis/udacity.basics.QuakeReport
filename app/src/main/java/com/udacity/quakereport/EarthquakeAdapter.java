package com.udacity.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    EarthquakeAdapter(@NonNull Context context, @NonNull List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Earthquake earthquake = getItem(position);
        if (earthquake != null) {
            String magnitude = formatNumber(earthquake.getMagnitude());

            TextView magnitudeView = listItemView.findViewById(R.id.magnitude);
            magnitudeView.setText(magnitude);

            GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();
            int magnitudeColor = getMagnitudeColor(earthquake.getMagnitude());
            magnitudeCircle.setColor(magnitudeColor);

            String location = earthquake.getLocation();
            String[] locationComponents = splitLocationString(location);

            TextView locationOffset = listItemView.findViewById(R.id.location_offset);
            locationOffset.setText(locationComponents[0]);

            TextView primaryLocation = listItemView.findViewById(R.id.primary_location);
            primaryLocation.setText(locationComponents[1]);

            Date date = new Date(earthquake.getTimeInMilliseconds());

            TextView dateView = listItemView.findViewById(R.id.date);
            dateView.setText(formatDate(date));

            TextView timeView = listItemView.findViewById(R.id.time);
            timeView.setText(formatTime(date));
        }

        return listItemView;
    }

    private String formatNumber(double number) {
        NumberFormat numberFormat = new DecimalFormat("0.0");
        return numberFormat.format(number);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("LLL dd, yyy", Locale.US);
        return sdf.format(date);
    }

    private String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        return sdf.format(date);
    }

    private String[] splitLocationString(String location) {
        if (location.contains(" of ")) {
            String[] components = location.split("of", 2);
            components[0] += " of";
            return components;
        } else {
            String[] components = new String[2];
            components[0] = "Near the";
            components[1] = location;
            return components;
        }
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResId);
    }
}
