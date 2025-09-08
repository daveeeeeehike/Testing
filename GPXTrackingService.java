package com.keeperrl.offlinemapsforwearos;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GPXTrackingService extends Service implements LocationListener {

    private static final String CHANNEL_ID = "GPX_TRACKING_CHANNEL";
    private LocationManager locationManager;
    private FileOutputStream gpxStream;
    private File gpxFile;
    private boolean recording = false;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GPX Tracking")
                .setContentText("Recording track in background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, this);

        startNewTrack();
    }

    private void startNewTrack() {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) downloadsDir.mkdirs();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            gpxFile = new File(downloadsDir, "track_" + timestamp + ".gpx");
            gpxStream = new FileOutputStream(gpxFile, false);
            writeHeader();
            recording = true;
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    private void writeHeader() throws IOException {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<gpx version=\"1.1\" creator=\"OfflineMapsWearOS\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n" +
                "<trk>\n<trkseg>\n";
        gpxStream.write(header.getBytes());
        gpxStream.flush();
    }

    private void writeFooter() {
        try {
            if (gpxStream != null) {
                String footer = "</trkseg>\n</trk>\n</gpx>\n";
                gpxStream.write(footer.getBytes());
                gpxStream.flush();
                gpxStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        writeFooter();
        Toast.makeText(this, "GPX track saved: " + (gpxFile != null ? gpxFile.getAbsolutePath() : ""), Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (!recording || gpxStream == null) return;

        String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(new Date());
        String point = String.format(Locale.US,
                "<trkpt lat=\"%f\" lon=\"%f\">\n<ele>%f</ele>\n<time>%s</time>\n</trkpt>\n",
                location.getLatitude(), location.getLongitude(),
                location.getAltitude(), time);
        try {
            gpxStream.write(point.getBytes());
            gpxStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "GPX Tracking Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
