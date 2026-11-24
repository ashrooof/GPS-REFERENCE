package com.ashgps.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

public class MockLocationService extends Service {
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_ROUTE_LATITUDES = "route_latitudes";
    public static final String EXTRA_ROUTE_LONGITUDES = "route_longitudes";
    public static final String EXTRA_INTERVAL_MS = "interval_ms";
    private static final String CHANNEL_ID = "ash_gps_location";
    private static final int NOTIFICATION_ID = 701;
    private static final String PROVIDER = LocationManager.GPS_PROVIDER;
    private static final int STEPS_PER_SEGMENT = 10;

    private LocationManager locationManager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private double latitude;
    private double longitude;
    private long intervalMs = 1000L;
    private double[] routeLatitudes;
    private double[] routeLongitudes;
    private int routeSegment;
    private int routeStep;
    private boolean providerReady;

    private final Runnable refreshLocation = new Runnable() {
        @Override public void run() {
            if (!providerReady) return;
            try {
                advanceRoute();
                pushLocation();
                handler.postDelayed(this, intervalMs);
            } catch (SecurityException | IllegalArgumentException error) {
                stopSelf();
            }
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        createNotificationChannel();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            latitude = intent.getDoubleExtra(EXTRA_LATITUDE, latitude);
            longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, longitude);
            intervalMs = Math.max(250L, Math.min(10000L, intent.getLongExtra(EXTRA_INTERVAL_MS, 1000L)));
            double[] incomingLatitudes = intent.getDoubleArrayExtra(EXTRA_ROUTE_LATITUDES);
            double[] incomingLongitudes = intent.getDoubleArrayExtra(EXTRA_ROUTE_LONGITUDES);
            if (incomingLatitudes != null && incomingLongitudes != null && incomingLatitudes.length >= 2
                    && incomingLatitudes.length == incomingLongitudes.length) {
                routeLatitudes = incomingLatitudes;
                routeLongitudes = incomingLongitudes;
                routeSegment = 0;
                routeStep = 0;
                latitude = routeLatitudes[0];
                longitude = routeLongitudes[0];
            } else {
                routeLatitudes = null;
                routeLongitudes = null;
            }
        }
        startForeground(NOTIFICATION_ID, buildNotification());
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_NOT_STICKY;
        }
        try {
            try { locationManager.removeTestProvider(PROVIDER); }
            catch (IllegalArgumentException ignored) { }
            locationManager.addTestProvider(PROVIDER, false, false, false, false,
                    true, true, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
            locationManager.setTestProviderEnabled(PROVIDER, true);
            providerReady = true;
            handler.removeCallbacks(refreshLocation);
            pushLocation();
            handler.postDelayed(refreshLocation, intervalMs);
        } catch (SecurityException | IllegalArgumentException error) {
            providerReady = false;
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void advanceRoute() {
        if (routeLatitudes == null || routeSegment >= routeLatitudes.length - 1) return;
        routeStep++;
        double fraction = Math.min(1.0d, routeStep / (double) STEPS_PER_SEGMENT);
        double startLat = routeLatitudes[routeSegment];
        double startLon = routeLongitudes[routeSegment];
        double endLat = routeLatitudes[routeSegment + 1];
        double endLon = routeLongitudes[routeSegment + 1];
        latitude = startLat + (endLat - startLat) * fraction;
        longitude = startLon + (endLon - startLon) * fraction;
        if (fraction >= 1.0d) {
            routeSegment++;
            routeStep = 0;
        }
    }

    private void pushLocation() {
        Location mock = new Location(PROVIDER);
        mock.setLatitude(latitude);
        mock.setLongitude(longitude);
        mock.setAccuracy(3.0f);
        mock.setAltitude(0.0d);
        mock.setSpeed(0.0f);
        mock.setBearing(0.0f);
        mock.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mock.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        locationManager.setTestProviderLocation(PROVIDER, mock);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this);
        return builder.setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setOngoing(true).build();
    }

    @Override public void onDestroy() {
        handler.removeCallbacks(refreshLocation);
        if (providerReady && locationManager != null) {
            try {
                locationManager.setTestProviderEnabled(PROVIDER, false);
                locationManager.removeTestProvider(PROVIDER);
            } catch (SecurityException | IllegalArgumentException ignored) { }
        }
        providerReady = false;
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}