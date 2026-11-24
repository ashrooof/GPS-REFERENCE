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
    private static final String CHANNEL_ID = "ash_gps_location";
    private static final int NOTIFICATION_ID = 701;
    private static final String PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationManager locationManager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private double latitude;
    private double longitude;
    private boolean providerReady;

    private final Runnable refreshLocation = new Runnable() {
        @Override public void run() {
            if (!providerReady) return;
            try {
                pushLocation();
                handler.postDelayed(this, 1000L);
            } catch (SecurityException | IllegalArgumentException error) {
                stopSelf();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            latitude = intent.getDoubleExtra(EXTRA_LATITUDE, latitude);
            longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, longitude);
        }

        startForeground(NOTIFICATION_ID, buildNotification());

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_NOT_STICKY;
        }

        try {
            try {
                locationManager.removeTestProvider(PROVIDER);
            } catch (IllegalArgumentException ignored) {
                // No previous test provider exists.
            }

            locationManager.addTestProvider(
                    PROVIDER,
                    false, false, false, false,
                    true, true, true,
                    Criteria.POWER_LOW,
                    Criteria.ACCURACY_FINE
            );
            locationManager.setTestProviderEnabled(PROVIDER, true);
            providerReady = true;
            handler.removeCallbacks(refreshLocation);
            pushLocation();
            handler.postDelayed(refreshLocation, 1000L);
        } catch (SecurityException | IllegalArgumentException error) {
            providerReady = false;
            stopSelf();
        }
        return START_NOT_STICKY;
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
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        return builder
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setOngoing(true)
                .build();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(refreshLocation);
        if (providerReady && locationManager != null) {
            try {
                locationManager.setTestProviderEnabled(PROVIDER, false);
                locationManager.removeTestProvider(PROVIDER);
            } catch (SecurityException | IllegalArgumentException ignored) {
                // Android may already have removed the test provider.
            }
        }
        providerReady = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
