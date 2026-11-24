package com.ashgps.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQUEST_LOCATION = 41;
    private static final String PREFS = "ash_gps_data";
    private EditText latitudeField;
    private EditText longitudeField;
    private EditText routeField;
    private TextView statusView;
    private SharedPreferences preferences;
    private double pendingLatitude = 30.0444;
    private double pendingLongitude = 31.2357;

    private final int background = Color.rgb(8, 11, 18);
    private final int surface = Color.rgb(17, 23, 34);
    private final int accent = Color.rgb(54, 214, 165);
    private final int muted = Color.rgb(151, 164, 184);

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);
        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        buildScreen();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(background);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22), dp(26), dp(22), dp(28));
        scroll.addView(page);

        TextView brand = new TextView(this);
        brand.setText("ASH GPS");
        brand.setTextColor(Color.WHITE);
        brand.setTextSize(30);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        page.addView(brand);

        TextView subtitle = new TextView(this);
        subtitle.setText("PRECISION LOCATION CONTROL");
        subtitle.setTextColor(accent);
        subtitle.setTextSize(11);
        subtitle.setLetterSpacing(0.14f);
        LinearLayout.LayoutParams subtitleParams = params(-1, -2);
        subtitleParams.topMargin = dp(5);
        page.addView(subtitle, subtitleParams);

        statusView = new TextView(this);
        statusView.setText("●  READY — SET A LOCATION");
        statusView.setTextColor(muted);
        statusView.setTextSize(12);
        statusView.setPadding(dp(14), dp(13), dp(14), dp(13));
        statusView.setBackground(panel());
        LinearLayout.LayoutParams statusParams = params(-1, -2);
        statusParams.topMargin = dp(26);
        page.addView(statusView, statusParams);

        TextView section = heading("TARGET COORDINATES");
        LinearLayout.LayoutParams sectionParams = params(-1, -2);
        sectionParams.topMargin = dp(26);
        page.addView(section, sectionParams);

        LinearLayout coordinates = new LinearLayout(this);
        coordinates.setOrientation(LinearLayout.HORIZONTAL);
        coordinates.setGravity(Gravity.CENTER_VERTICAL);
        latitudeField = coordinateField("Latitude", "30.0444");
        longitudeField = coordinateField("Longitude", "31.2357");
        coordinates.addView(wrapField("LATITUDE", latitudeField), new LinearLayout.LayoutParams(0, -2, 1));
        View gap = new View(this);
        coordinates.addView(gap, new LinearLayout.LayoutParams(dp(12), 1));
        coordinates.addView(wrapField("LONGITUDE", longitudeField), new LinearLayout.LayoutParams(0, -2, 1));
        LinearLayout.LayoutParams coordinateParams = params(-1, -2);
        coordinateParams.topMargin = dp(12);
        page.addView(coordinates, coordinateParams);

        TextView routeTitle = heading("ROUTE POINTS (OPTIONAL)");
        LinearLayout.LayoutParams routeTitleParams = params(-1, -2);
        routeTitleParams.topMargin = dp(22);
        page.addView(routeTitle, routeTitleParams);

        routeField = new EditText(this);
        routeField.setHint("30.0444, 31.2357\\n30.0500, 31.2400\\nOne latitude, longitude pair per line");
        routeField.setHintTextColor(muted);
        routeField.setTextColor(Color.WHITE);
        routeField.setTextSize(13);
        routeField.setGravity(Gravity.TOP | Gravity.START);
        routeField.setMinLines(3);
        routeField.setPadding(dp(12), dp(12), dp(12), dp(12));
        routeField.setBackground(panel());
        LinearLayout.LayoutParams routeFieldParams = params(-1, dp(112));
        routeFieldParams.topMargin = dp(10);
        page.addView(routeField, routeFieldParams);

        Button startRoute = actionButton("PLAY ROUTE", surface, accent);
        LinearLayout.LayoutParams startRouteParams = params(-1, dp(48));
        startRouteParams.topMargin = dp(9);
        page.addView(startRoute, startRouteParams);
        startRoute.setOnClickListener(v -> requestAndStartRoute());

        Button start = actionButton("START MOCK LOCATION", accent, background);
        LinearLayout.LayoutParams startParams = params(-1, dp(54));
        startParams.topMargin = dp(22);
        page.addView(start, startParams);
        start.setOnClickListener(v -> requestAndStart());

        Button stop = actionButton("STOP SIMULATION", Color.rgb(42, 51, 66), Color.WHITE);
        LinearLayout.LayoutParams stopParams = params(-1, dp(50));
        stopParams.topMargin = dp(10);
        page.addView(stop, stopParams);
        stop.setOnClickListener(v -> {
            stopService(new Intent(this, MockLocationService.class));
            setStatus("SIMULATION STOPPED", muted);
        });

        TextView toolsHeading = heading("TOOLS");
        LinearLayout.LayoutParams toolsParams = params(-1, -2);
        toolsParams.topMargin = dp(30);
        page.addView(toolsHeading, toolsParams);

        Button save = actionButton("SAVE CURRENT COORDINATES", surface, Color.WHITE);
        LinearLayout.LayoutParams saveParams = params(-1, dp(50));
        saveParams.topMargin = dp(12);
        page.addView(save, saveParams);
        save.setOnClickListener(v -> saveLocation());

        Button history = actionButton("SAVED LOCATIONS & HISTORY", surface, Color.WHITE);
        LinearLayout.LayoutParams historyParams = params(-1, dp(50));
        historyParams.topMargin = dp(9);
        page.addView(history, historyParams);
        history.setOnClickListener(v -> showHistory());

        Button developer = actionButton("OPEN DEVELOPER OPTIONS", surface, muted);
        LinearLayout.LayoutParams developerParams = params(-1, dp(50));
        developerParams.topMargin = dp(9);
        page.addView(developer, developerParams);
        developer.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            } catch (Exception e) {
                Toast.makeText(this, "Open Android Settings → Developer options", Toast.LENGTH_LONG).show();
            }
        });

        TextView note = new TextView(this);
        note.setText("To use mock locations, enable Developer options and select ASH GPS as the mock location app. This is an Android system setting.");
        note.setTextColor(muted);
        note.setTextSize(12);
        note.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams noteParams = params(-1, -2);
        noteParams.topMargin = dp(22);
        page.addView(note, noteParams);

        TextView footer = new TextView(this);
        footer.setText("ASH GPS  •  DEVELOPMENT BUILD 0.1");
        footer.setTextColor(Color.rgb(91, 105, 126));
        footer.setTextSize(10);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerParams = params(-1, -2);
        footerParams.topMargin = dp(30);
        page.addView(footer, footerParams);

        setContentView(scroll);
    }

    private LinearLayout wrapField(String label, EditText field) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(this);
        title.setText(label);
        title.setTextColor(muted);
        title.setTextSize(10);
        title.setPadding(dp(2), 0, 0, dp(7));
        box.addView(title);
        box.addView(field);
        return box;
    }

    private EditText coordinateField(String hint, String value) {
        EditText field = new EditText(this);
        field.setSingleLine(true);
        field.setText(value);
        field.setHint(hint);
        field.setTextColor(Color.WHITE);
        field.setHintTextColor(muted);
        field.setTextSize(16);
        field.setInputType(8194 | 4096);
        field.setPadding(dp(12), dp(8), dp(8), dp(8));
        field.setBackground(panel());
        return field;
    }

    private TextView heading(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.WHITE);
        view.setTextSize(13);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setLetterSpacing(0.08f);
        return view;
    }

    private Button actionButton(String text, int fill, int textColor) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(13);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(textColor);
        button.setBackground(round(fill, 14));
        button.setPadding(dp(12), 0, dp(12), 0);
        return button;
    }

    private GradientDrawable panel() {
        return round(surface, 13);
    }

    private GradientDrawable round(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        shape.setStroke(dp(1), Color.rgb(31, 42, 57));
        return shape;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void requestAndStart() {
        try {
            pendingLatitude = Double.parseDouble(latitudeField.getText().toString().trim());
            pendingLongitude = Double.parseDouble(longitudeField.getText().toString().trim());
            if (pendingLatitude < -90 || pendingLatitude > 90 || pendingLongitude < -180 || pendingLongitude > 180) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid latitude and longitude values.", Toast.LENGTH_LONG).show();
            return;
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        startMockService();
    }

    private void requestAndStartRoute() {
        String[] lines = routeField.getText().toString().trim().split("\\r?\\n");
        java.util.ArrayList<Double> latitudes = new java.util.ArrayList<>();
        java.util.ArrayList<Double> longitudes = new java.util.ArrayList<>();
        try {
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] pair = line.trim().split("[,;\\s]+");
                if (pair.length != 2) throw new NumberFormatException();
                double lat = Double.parseDouble(pair[0]);
                double lon = Double.parseDouble(pair[1]);
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) throw new NumberFormatException();
                latitudes.add(lat);
                longitudes.add(lon);
            }
            if (latitudes.size() < 2) throw new NumberFormatException();
        } catch (NumberFormatException error) {
            Toast.makeText(this, "Add at least two valid lines: latitude, longitude", Toast.LENGTH_LONG).show();
            return;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            Toast.makeText(this, "Grant location permission, then tap PLAY ROUTE again.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, MockLocationService.class);
        double[] lats = new double[latitudes.size()];
        double[] lons = new double[longitudes.size()];
        for (int i = 0; i < lats.length; i++) { lats[i] = latitudes.get(i); lons[i] = longitudes.get(i); }
        intent.putExtra(MockLocationService.EXTRA_ROUTE_LATITUDES, lats);
        intent.putExtra(MockLocationService.EXTRA_ROUTE_LONGITUDES, lons);
        intent.putExtra(MockLocationService.EXTRA_INTERVAL_MS, 1000L);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent); else startService(intent);
        setStatus("ROUTE PLAYBACK STARTED", accent);
    }

    private void startMockService() {
        Intent intent = new Intent(this, MockLocationService.class);
        intent.putExtra(MockLocationService.EXTRA_LATITUDE, pendingLatitude);
        intent.putExtra(MockLocationService.EXTRA_LONGITUDE, pendingLongitude);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent);
        else startService(intent);
        setStatus("START REQUESTED — CHECK MOCK APP SETTING", accent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQUEST_LOCATION && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
            startMockService();
        } else if (requestCode == REQUEST_LOCATION) {
            Toast.makeText(this, "Location permission is required.", Toast.LENGTH_LONG).show();
        }
    }

    private void setStatus(String text, int color) {
        statusView.setText("●  " + text);
        statusView.setTextColor(color);
    }

    private void saveLocation() {
        String lat = latitudeField.getText().toString().trim();
        String lon = longitudeField.getText().toString().trim();
        String stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        String old = preferences.getString("saved_locations", "");
        String entry = stamp + "  |  " + lat + ", " + lon;
        preferences.edit().putString("saved_locations", entry + (old.isEmpty() ? "" : "\n" + old)).apply();
        Toast.makeText(this, "Location saved on this device.", Toast.LENGTH_SHORT).show();
    }

    private void showHistory() {
        String data = preferences.getString("saved_locations", "");
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle("ASH GPS — Saved locations");
        TextView content = new TextView(this);
        content.setText(data.isEmpty() ? "No saved locations yet." : data);
        content.setTextColor(Color.WHITE);
        content.setPadding(dp(20), dp(16), dp(20), dp(16));
        dialog.setView(content);
        dialog.setPositiveButton("CLOSE", null);
        dialog.setNegativeButton("CLEAR", (d, w) -> preferences.edit().remove("saved_locations").apply());
        dialog.show();
    }
}
