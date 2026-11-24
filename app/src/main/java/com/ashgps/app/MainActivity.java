package com.ashgps.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends FragmentActivity {
    private static final int REQUEST_LOCATION = 41;
    private static final int BLUE = Color.rgb(0,102,229), TEXT = Color.rgb(34,34,34);
    private static final int MUTED = Color.rgb(125,130,138), GREEN = Color.rgb(50,205,50);
    private GoogleMap map;
    private Marker targetMarker;
    private TextView statusView, coordinateView;
    private EditText routeField;
    private SharedPreferences preferences;
    private double latitude=30.0444, longitude=31.2357;
    private boolean routeMode;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        preferences=getSharedPreferences("ash_gps_data",MODE_PRIVATE);
        buildMapScreen();
    }

    private void buildMapScreen() {
        FrameLayout root=new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(235,239,242));
        int mapId=View.generateViewId();
        FrameLayout mapHost=new FrameLayout(this); mapHost.setId(mapId);
        root.addView(mapHost,new FrameLayout.LayoutParams(-1,-1));
        SupportMapFragment fragment=SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(mapId,fragment).commit();
        fragment.getMapAsync(this::onMapReady);

        LinearLayout top=new LinearLayout(this);
        top.setPadding(dp(14),dp(8),dp(14),0);
        top.addView(buildHeader());
        root.addView(top,new FrameLayout.LayoutParams(-1,-2,Gravity.TOP));

        LinearLayout tools=new LinearLayout(this); tools.setOrientation(LinearLayout.VERTICAL);
        tools.addView(mapTool("⌖",v->moveToTarget()));
        tools.addView(mapTool("▱",v->showMapTypes()));
        FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(dp(48),-2,Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        tp.setMargins(0,0,dp(12),dp(20)); root.addView(tools,tp);

        LinearLayout bottom=buildBottomPanel();
        FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM);
        bp.setMargins(dp(10),0,dp(10),dp(12)); root.addView(bottom,bp);
        setContentView(root);
    }

    private View buildHeader() {
        LinearLayout h=new LinearLayout(this); h.setGravity(Gravity.CENTER_VERTICAL);
        h.setPadding(dp(14),dp(9),dp(8),dp(9)); h.setBackground(card());
        TextView title=new TextView(this); title.setText("GPS Location Faker");
        title.setTextColor(TEXT); title.setTextSize(17); title.setTypeface(null,Typeface.BOLD);
        h.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        h.addView(mapTool("☰",v->showMenu())); return h;
    }

    private LinearLayout buildBottomPanel() {
        LinearLayout p=new LinearLayout(this); p.setOrientation(LinearLayout.VERTICAL);
        p.setPadding(dp(15),dp(12),dp(15),dp(13)); p.setBackground(card());
        LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading=new TextView(this); heading.setText(routeMode?"Route setup":"Basic location");
        heading.setTextColor(TEXT); heading.setTextSize(16); heading.setTypeface(null,Typeface.BOLD);
        row.addView(heading,new LinearLayout.LayoutParams(0,-2,1));
        Button toggle=smallButton(routeMode?"BASIC":"ROUTE",BLUE);
        toggle.setOnClickListener(v->{routeMode=!routeMode;buildMapScreen();}); row.addView(toggle); p.addView(row);
        coordinateView=new TextView(this);
        coordinateView.setText(String.format(Locale.US,"%.6f, %.6f",latitude,longitude));
        coordinateView.setTextColor(MUTED); coordinateView.setTextSize(12);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.topMargin=dp(5); p.addView(coordinateView,cp);
        statusView=new TextView(this); statusView.setText("●  Ready"); statusView.setTextColor(GREEN); statusView.setTextSize(12);
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2); sp.topMargin=dp(6); p.addView(statusView,sp);
        if(routeMode) {
            routeField=new EditText(this); routeField.setHint("Latitude, longitude — one point per line");
            routeField.setTextColor(TEXT); routeField.setTextSize(13); routeField.setMinLines(2);
            routeField.setPadding(dp(9),dp(7),dp(9),dp(7)); routeField.setBackground(round(Color.rgb(248,249,251),8));
            LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(76)); rp.topMargin=dp(8); p.addView(routeField,rp);
        }
        LinearLayout actions=new LinearLayout(this); actions.setGravity(Gravity.CENTER);
        Button start=smallButton(routeMode?"PLAY ROUTE":"START",BLUE);
        Button save=smallButton("SAVE",Color.rgb(244,247,251)); save.setTextColor(BLUE);
        Button stop=smallButton("STOP",Color.rgb(244,247,251)); stop.setTextColor(TEXT);
        addAction(actions,start); addAction(actions,save); addAction(actions,stop);
        LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(43)); ap.topMargin=dp(10); p.addView(actions,ap);
        start.setOnClickListener(v->{if(routeMode)startRoute();else startMock();});
        save.setOnClickListener(v->saveLocation());
        stop.setOnClickListener(v->{stopService(new Intent(this,MockLocationService.class));setStatus("Stopped",MUTED);});
        return p;
    }

    private void addAction(LinearLayout row,Button b) {
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-1,1);
        lp.setMargins(dp(3),0,dp(3),0); row.addView(b,lp);
    }
    private View mapTool(String label,View.OnClickListener listener) {
        Button b=new Button(this); b.setText(label); b.setTextColor(TEXT); b.setTextSize(19);
        b.setMinWidth(0); b.setMinimumWidth(0); b.setPadding(0,0,0,0); b.setBackground(card()); b.setOnClickListener(listener);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(44),dp(44)); lp.setMargins(dp(2),dp(3),dp(2),dp(3)); b.setLayoutParams(lp); return b;
    }
    private Button smallButton(String label,int color) {
        Button b=new Button(this); b.setText(label); b.setTextColor(color==BLUE?Color.WHITE:TEXT);
        b.setTextSize(11); b.setTypeface(null,Typeface.BOLD); b.setAllCaps(false);
        b.setPadding(dp(7),0,dp(7),0); b.setBackground(round(color,8)); return b;
    }
    private GradientDrawable card() {
        GradientDrawable d=new GradientDrawable(); d.setColor(Color.WHITE); d.setCornerRadius(dp(13));
        d.setStroke(dp(1),Color.rgb(232,235,239)); return d;
    }
    private GradientDrawable round(int color,int radius) {
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}

    private void onMapReady(GoogleMap g) {
        map=g; map.getUiSettings().setZoomControlsEnabled(false); map.getUiSettings().setCompassEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng point=new LatLng(latitude,longitude); map.moveCamera(CameraUpdateFactory.newLatLngZoom(point,14f));
        targetMarker=map.addMarker(new MarkerOptions().position(point).title("Mock location"));
        map.setOnMapClickListener(p->{latitude=p.latitude;longitude=p.longitude;
            if(targetMarker!=null)targetMarker.setPosition(p);
            if(coordinateView!=null)coordinateView.setText(String.format(Locale.US,"%.6f, %.6f",latitude,longitude));});
    }
    private void moveToTarget(){if(map!=null)map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),16f));}
    private void showMapTypes(){
        String[] labels={"Normal","Satellite","Terrain","Hybrid"};
        new AlertDialog.Builder(this).setTitle("Map type").setItems(labels,(d,w)->{
            if(map==null)return; int[] types={1,2,3,4}; map.setMapType(types[w]);
        }).show();
    }
    private void showMenu(){
        String[] options={"Saved locations & history","Settings","Developer options"};
        new AlertDialog.Builder(this).setTitle("GPS Location Faker").setItems(options,(d,w)->{
            if(w==0)showHistory(); else if(w==1)showSettings();
            else startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
        }).show();
    }
    private void showSettings(){
        String[] options={"GPS attributes","Mock-location instructions"};
        new AlertDialog.Builder(this).setTitle("Settings").setItems(options,(d,w)->{
            if(w==0)showGpsAttributes(); else new AlertDialog.Builder(this)
                .setMessage("Enable Android Developer options, then select ASH GPS as the mock location app.")
                .setPositiveButton("OK",null).show();
        }).show();
    }
    private void showGpsAttributes(){
        LinearLayout box=new LinearLayout(this); box.setPadding(dp(20),dp(8),dp(20),0); box.setOrientation(LinearLayout.VERTICAL);
        EditText accuracy=numericField("Accuracy (m)","3"), altitude=numericField("Altitude (m)","0"), bearing=numericField("Bearing (°)","0");
        box.addView(accuracy);box.addView(altitude);box.addView(bearing);
        new AlertDialog.Builder(this).setTitle("GPS attributes").setView(box)
            .setPositiveButton("Save",(d,w)->Toast.makeText(this,"Settings saved.",Toast.LENGTH_SHORT).show())
            .setNegativeButton("Cancel",null).show();
    }
    private EditText numericField(String hint,String value){
        EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);e.setText(value);e.setInputType(8194|4096);return e;
    }
    private void startMock(){
        Intent i=new Intent(this,MockLocationService.class);
        i.putExtra(MockLocationService.EXTRA_LATITUDE,latitude);i.putExtra(MockLocationService.EXTRA_LONGITUDE,longitude);
        startServiceWithPermission(i);
    }
    private void startRoute(){
        if(routeField==null)return;
        ArrayList<Double> la=new ArrayList<>(),lo=new ArrayList<>();
        try{
            for(String line:routeField.getText().toString().trim().split("\\r?\\n")){
                if(line.trim().isEmpty())continue;String[] pair=line.trim().split("[,;\\s]+");
                if(pair.length!=2)throw new NumberFormatException();
                double a=Double.parseDouble(pair[0]),b=Double.parseDouble(pair[1]);
                if(a < -90||a>90||b < -180||b>180)throw new NumberFormatException();la.add(a);lo.add(b);
            }
            if(la.size()<2)throw new NumberFormatException();
        }catch(NumberFormatException e){Toast.makeText(this,"Enter at least two valid latitude, longitude pairs.",Toast.LENGTH_LONG).show();return;}
        double[] lats=new double[la.size()],lons=new double[lo.size()];
        PolylineOptions line=new PolylineOptions().color(BLUE).width(7f);
        for(int i=0;i<lats.length;i++){lats[i]=la.get(i);lons[i]=lo.get(i);line.add(new LatLng(lats[i],lons[i]));}
        Intent intent=new Intent(this,MockLocationService.class);
        intent.putExtra(MockLocationService.EXTRA_ROUTE_LATITUDES,lats);
        intent.putExtra(MockLocationService.EXTRA_ROUTE_LONGITUDES,lons);
        intent.putExtra(MockLocationService.EXTRA_INTERVAL_MS,1000L);
        startServiceWithPermission(intent);if(map!=null)map.addPolyline(line);
    }
    private void startServiceWithPermission(Intent intent){
        if(Build.VERSION.SDK_INT>=23&&checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION);return;
        }
        if(Build.VERSION.SDK_INT>=26)startForegroundService(intent);else startService(intent);setStatus("Mock location started",BLUE);
    }
    @Override public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] results){
        super.onRequestPermissionsResult(requestCode,permissions,results);
        if(requestCode==REQUEST_LOCATION&&results.length>0&&results[0]==PackageManager.PERMISSION_GRANTED)startMock();
        else if(requestCode==REQUEST_LOCATION)Toast.makeText(this,"Location permission is required.",Toast.LENGTH_LONG).show();
    }
    private void setStatus(String text,int color){if(statusView!=null){statusView.setText("●  "+text);statusView.setTextColor(color);}}
    private void saveLocation(){
        String stamp=new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault()).format(new Date());
        String old=preferences.getString("saved_locations","");
        String entry=stamp+" | "+String.format(Locale.US,"%.6f, %.6f",latitude,longitude);
        preferences.edit().putString("saved_locations",entry+(old.isEmpty()?"":"\n"+old)).apply();
        Toast.makeText(this,"Location saved.",Toast.LENGTH_SHORT).show();
    }
    private void showHistory(){
        String data=preferences.getString("saved_locations","");
        new AlertDialog.Builder(this).setTitle("Saved locations & history")
            .setMessage(data.isEmpty()?"No saved locations yet.":data)
            .setPositiveButton("Close",null)
            .setNegativeButton("Clear",(d,w)->preferences.edit().remove("saved_locations").apply()).show();
    }
}
