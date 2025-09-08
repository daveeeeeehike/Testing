package com.keeperrl.offlinemapsforwearos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidBitmap;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.layers.MyLocationOverlay;
import org.mapsforge.map.android.util.AndroidPreferences;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.labels.MapDataStoreLabelStore;
import org.mapsforge.map.layer.labels.ThreadedLabelLayer;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.IMapViewPosition;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.XmlRenderThemeStyleMenu;
import org.mapsforge.map.scalebar.DefaultMapScaleBar;
import org.mapsforge.map.scalebar.MapScaleBar;
import org.mapsforge.map.scalebar.MetricUnitAdapter;
import org.mapsforge.map.view.InputListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* loaded from: /storage/emulated/0/Android/data/com.apktools.app.decompile/files/decompile_out/com.keeperrl.offlinemapsforwearos/classes4.dex */
public class MainActivity extends Activity implements LocationListener {
    static final String TAG = "OfflineMaps";
    Track currentTrack;
    private MyLocationOverlay gpxLocationOverlay;
    private LocationManager locationManager;
    ArrayAdapter mapDownloadsAdapter;
    protected MapView mapView;
    private MyLocationOverlay myLocationOverlay;
    private PreferencesFacade preferencesFacade;
    private XmlRenderThemeStyleMenu renderThemeStyleMenu;
    List tracks;
    private boolean isTracking = false;
    protected List tileCaches = new ArrayList();
    private Location lastLocation = null;
    private boolean isLockedLocation = false;
    private boolean displayOn = false;
    private Long timerStart = null;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new 1();
    HashMap downloadDialogs = new HashMap();
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    final Map allMaps = Map.of("Africa", new DownloadInfo[]{new DownloadInfo("algeria", "africa"), new DownloadInfo("angola", "africa"), new DownloadInfo("benin", "africa"), new DownloadInfo("botswana", "africa"), new DownloadInfo("burkina-faso", "africa"), new DownloadInfo("burundi", "africa"), new DownloadInfo("cameroon", "africa"), new DownloadInfo("canary-islands", "africa"), new DownloadInfo("cape-verde", "africa"), new DownloadInfo("central-african-republic", "africa"), new DownloadInfo("chad", "africa"), new DownloadInfo("comores", "africa"), new DownloadInfo("congo-brazzaville", "africa"), new DownloadInfo("congo-democratic-republic", "africa"), new DownloadInfo("djibouti", "africa"), new DownloadInfo("egypt", "africa"), new DownloadInfo("equatorial-guinea", "africa"), new DownloadInfo("eritrea", "africa"), new DownloadInfo("ethiopia", "africa"), new DownloadInfo("gabon", "africa"), new DownloadInfo("ghana", "africa"), new DownloadInfo("guinea-bissau", "africa"), new DownloadInfo("guinea", "africa"), new DownloadInfo("ivory-coast", "africa"), new DownloadInfo("kenya", "africa"), new DownloadInfo("lesotho", "africa"), new DownloadInfo("liberia", "africa"), new DownloadInfo("libya", "africa"), new DownloadInfo("madagascar", "africa"), new DownloadInfo("malawi", "africa"), new DownloadInfo("mali", "africa"), new DownloadInfo("mauritania", "africa"), new DownloadInfo("mauritius", "africa"), new DownloadInfo("morocco", "africa"), new DownloadInfo("mozambique", "africa"), new DownloadInfo("namibia", "africa"), new DownloadInfo("niger", "africa"), new DownloadInfo("nigeria", "africa"), new DownloadInfo("rwanda", "africa"), new DownloadInfo("saint-helena-ascension-and-tristan-da-cunha", "africa"), new DownloadInfo("sao-tome-and-principe", "africa"), new DownloadInfo("senegal-and-gambia", "africa"), new DownloadInfo("seychelles", "africa"), new DownloadInfo("sierra-leone", "africa"), new DownloadInfo("somalia", "africa"), new DownloadInfo("south-africa-and-lesotho", "africa"), new DownloadInfo("south-sudan", "africa"), new DownloadInfo("sudan", "africa"), new DownloadInfo("swaziland", "africa"), new DownloadInfo("tanzania", "africa"), new DownloadInfo("togo", "africa"), new DownloadInfo("tunisia", "africa"), new DownloadInfo("uganda", "africa"), new DownloadInfo("zambia", "africa"), new DownloadInfo("zimbabwe", "africa")}, "Europe", new DownloadInfo[]{new DownloadInfo("albania", "europe"), new DownloadInfo("alps", "europe"), new DownloadInfo("andorra", "europe"), new DownloadInfo("austria", "europe"), new DownloadInfo("azores", "europe"), new DownloadInfo("belarus", "europe"), new DownloadInfo("belgium", "europe"), new DownloadInfo("bosnia-herzegovina", "europe"), new DownloadInfo("bulgaria", "europe"), new DownloadInfo("croatia", "europe"), new DownloadInfo("cyprus", "europe"), new DownloadInfo("czech-republic", "europe"), new DownloadInfo("denmark", "europe"), new DownloadInfo("estonia", "europe"), new DownloadInfo("faroe-islands", "europe"), new DownloadInfo("finland", "europe"), new DownloadInfo("france", "europe"), new DownloadInfo("georgia", "europe"), new DownloadInfo("germany", "europe"), new DownloadInfo("great-britain", "europe"), new DownloadInfo("greece", "europe"), new DownloadInfo("guernsey-jersey", "europe"), new DownloadInfo("hungary", "europe"), new DownloadInfo("iceland", "europe"), new DownloadInfo("ireland-and-northern-ireland", "europe"), new DownloadInfo("isle-of-man", "europe"), new DownloadInfo("italy", "europe"), new DownloadInfo("kosovo", "europe"), new DownloadInfo("latvia", "europe"), new DownloadInfo("liechtenstein", "europe"), new DownloadInfo("lithuania", "europe"), new DownloadInfo("luxembourg", "europe"), new DownloadInfo("macedonia", "europe"), new DownloadInfo("malta", "europe"), new DownloadInfo("moldova", "europe"), new DownloadInfo("monaco", "europe"), new DownloadInfo("montenegro", "europe"), new DownloadInfo("netherlands", "europe"), new DownloadInfo("norway", "europe"), new DownloadInfo("poland", "europe"), new DownloadInfo("portugal", "europe"), new DownloadInfo("romania", "europe"), new DownloadInfo("serbia", "europe"), new DownloadInfo("slovakia", "europe"), new DownloadInfo("slovenia", "europe"), new DownloadInfo("spain", "europe"), new DownloadInfo("sweden", "europe"), new DownloadInfo("switzerland", "europe"), new DownloadInfo("turkey", "europe"), new DownloadInfo("ukraine", "europe")}, "North America", new DownloadInfo[]{new DownloadInfo("greenland", "north-america"), new DownloadInfo("mexico", "north-america"), new DownloadInfo("alabama", "north-america/us"), new DownloadInfo("alaska", "north-america/us"), new DownloadInfo("arizona", "north-america/us"), new DownloadInfo("arkansas", "north-america/us"), new DownloadInfo("california", "north-america/us"), new DownloadInfo("colorado", "north-america/us"), new DownloadInfo("connecticut", "north-america/us"), new DownloadInfo("delaware", "north-america/us"), new DownloadInfo("district-of-columbia", "north-america/us"), new DownloadInfo("florida", "north-america/us"), new DownloadInfo("georgia", "north-america/us"), new DownloadInfo("hawaii", "north-america/us"), new DownloadInfo("idaho", "north-america/us"), new DownloadInfo("illinois", "north-america/us"), new DownloadInfo("indiana", "north-america/us"), new DownloadInfo("iowa", "north-america/us"), new DownloadInfo("kansas", "north-america/us"), new DownloadInfo("kentucky", "north-america/us"), new DownloadInfo("louisiana", "north-america/us"), new DownloadInfo("maine", "north-america/us"), new DownloadInfo("maryland", "north-america/us"), new DownloadInfo("massachusetts", "north-america/us"), new DownloadInfo("michigan", "north-america/us"), new DownloadInfo("minnesota", "north-america/us"), new DownloadInfo("mississippi", "north-america/us"), new DownloadInfo("missouri", "north-america/us"), new DownloadInfo("montana", "north-america/us"), new DownloadInfo("nebraska", "north-america/us"), new DownloadInfo("nevada", "north-america/us"), new DownloadInfo("new-hampshire", "north-america/us"), new DownloadInfo("new-jersey", "north-america/us"), new DownloadInfo("new-mexico", "north-america/us"), new DownloadInfo("new-york", "north-america/us"), new DownloadInfo("north-carolina", "north-america/us"), new DownloadInfo("north-dakota", "north-america/us"), new DownloadInfo("ohio", "north-america/us"), new DownloadInfo("oklahoma", "north-america/us"), new DownloadInfo("oregon", "north-america/us"), new DownloadInfo("pennsylvania", "north-america/us"), new DownloadInfo("puerto-rico", "north-america/us"), new DownloadInfo("rhode-island", "north-america/us"), new DownloadInfo("south-carolina", "north-america/us"), new DownloadInfo("south-dakota", "north-america/us"), new DownloadInfo("tennessee", "north-america/us"), new DownloadInfo("texas", "north-america/us"), new DownloadInfo("us-virgin-islands", "north-america/us"), new DownloadInfo("utah", "north-america/us"), new DownloadInfo("vermont", "north-america/us"), new DownloadInfo("virginia", "north-america/us"), new DownloadInfo("washington", "north-america/us"), new DownloadInfo("west-virginia", "north-america/us"), new DownloadInfo("wisconsin", "north-america/us"), new DownloadInfo("wyoming", "north-america/us"), new DownloadInfo("alberta", "north-america/canada"), new DownloadInfo("british-columbia", "north-america/canada"), new DownloadInfo("manitoba", "north-america/canada"), new DownloadInfo("new-brunswick", "north-america/canada"), new DownloadInfo("newfoundland-and-labrador", "north-america/canada"), new DownloadInfo("northwest-territories", "north-america/canada"), new DownloadInfo("nova-scotia", "north-america/canada"), new DownloadInfo("nunavut", "north-america/canada"), new DownloadInfo("ontario", "north-america/canada"), new DownloadInfo("prince-edward-island", "north-america/canada"), new DownloadInfo("quebec", "north-america/canada"), new DownloadInfo("saskatchewan", "north-america/canada"), new DownloadInfo("yukon", "north-america/canada")}, "Asia", new DownloadInfo[]{new DownloadInfo("afghanistan", "asia"), new DownloadInfo("armenia", "asia"), new DownloadInfo("azerbaijan", "asia"), new DownloadInfo("bangladesh", "asia"), new DownloadInfo("bhutan", "asia"), new DownloadInfo("cambodia", "asia"), new DownloadInfo("china", "asia"), new DownloadInfo("east-timor", "asia"), new DownloadInfo("gcc-states", "asia"), new DownloadInfo("india", "asia"), new DownloadInfo("indonesia", "asia"), new DownloadInfo("iran", "asia"), new DownloadInfo("iraq", "asia"), new DownloadInfo("israel-and-palestine", "asia"), new DownloadInfo("japan", "asia"), new DownloadInfo("jordan", "asia"), new DownloadInfo("kazakhstan", "asia"), new DownloadInfo("kyrgyzstan", "asia"), new DownloadInfo("laos", "asia"), new DownloadInfo("lebanon", "asia"), new DownloadInfo("malaysia-singapore-brunei", "asia"), new DownloadInfo("maldives", "asia"), new DownloadInfo("mongolia", "asia"), new DownloadInfo("myanmar", "asia"), new DownloadInfo("nepal", "asia"), new DownloadInfo("north-korea", "asia"), new DownloadInfo("pakistan", "asia"), new DownloadInfo("philippines", "asia"), new DownloadInfo("south-korea", "asia"), new DownloadInfo("sri-lanka", "asia"), new DownloadInfo("syria", "asia"), new DownloadInfo("taiwan", "asia"), new DownloadInfo("tajikistan", "asia"), new DownloadInfo("thailand", "asia"), new DownloadInfo("turkmenistan", "asia"), new DownloadInfo("uzbekistan", "asia"), new DownloadInfo("vietnam", "asia"), new DownloadInfo("yemen", "asia")}, "Australia-Oceania", new DownloadInfo[]{new DownloadInfo("american-oceania", "australia-oceania"), new DownloadInfo("australia", "australia-oceania"), new DownloadInfo("cook-islands", "australia-oceania"), new DownloadInfo("fiji", "australia-oceania"), new DownloadInfo("ile-de-clipperton", "australia-oceania"), new DownloadInfo("kiribati", "australia-oceania"), new DownloadInfo("marshall-islands", "australia-oceania"), new DownloadInfo("micronesia", "australia-oceania"), new DownloadInfo("nauru", "australia-oceania"), new DownloadInfo("new-caledonia", "australia-oceania"), new DownloadInfo("new-zealand", "australia-oceania"), new DownloadInfo("niue", "australia-oceania"), new DownloadInfo("palau", "australia-oceania"), new DownloadInfo("papua-new-guinea", "australia-oceania"), new DownloadInfo("pitcairn-islands", "australia-oceania"), new DownloadInfo("polynesie-francaise", "australia-oceania"), new DownloadInfo("samoa", "australia-oceania"), new DownloadInfo("solomon-islands", "australia-oceania"), new DownloadInfo("tokelau", "australia-oceania"), new DownloadInfo("tonga", "australia-oceania"), new DownloadInfo("tuvalu", "australia-oceania"), new DownloadInfo("vanuatu", "australia-oceania"), new DownloadInfo("wallis-et-futuna", "australia-oceania")}, "Central America", new DownloadInfo[]{new DownloadInfo("bahamas", "central-america"), new DownloadInfo("belize", "central-america"), new DownloadInfo("costa-rica", "central-america"), new DownloadInfo("cuba", "central-america"), new DownloadInfo("el-salvador", "central-america"), new DownloadInfo("guatemala", "central-america"), new DownloadInfo("haiti-and-domrep", "central-america"), new DownloadInfo("honduras", "central-america"), new DownloadInfo("jamaica", "central-america"), new DownloadInfo("nicaragua", "central-america"), new DownloadInfo("panama", "central-america")}, "South America", new DownloadInfo[]{new DownloadInfo("argentina", "south-america"), new DownloadInfo("bolivia", "south-america"), new DownloadInfo("brazil", "south-america"), new DownloadInfo("chile", "south-america"), new DownloadInfo("colombia", "south-america"), new DownloadInfo("ecuador", "south-america"), new DownloadInfo("guyana", "south-america"), new DownloadInfo("paraguay", "south-america"), new DownloadInfo("peru", "south-america"), new DownloadInfo("suriname", "south-america"), new DownloadInfo("uruguay", "south-america"), new DownloadInfo("venezuela", "south-america")});
    private BroadcastReceiver downloadReceiver = new 19();
    private final BroadcastReceiver batteryReceiver = new 20();
    private final int REQUEST_PERMISSION_GPS_STATE = 1;

    public enum MapDownloadStatus {
        ABSENT,
        READY,
        ERROR
    }

    static /* synthetic */ Long access$000(MainActivity x0) {
        return x0.timerStart;
    }

    static /* synthetic */ Long access$002(MainActivity x0, Long x1) {
        x0.timerStart = x1;
        return x1;
    }

    static /* synthetic */ boolean access$100(MainActivity x0) {
        return x0.isLockedLocation;
    }

    static /* synthetic */ void access$200(MainActivity x0, Location x1) {
        x0.setLocation(x1);
    }

    static /* synthetic */ void access$300(MainActivity x0, View x1) {
        x0.toggleView(x1);
    }

    static /* synthetic */ boolean access$400(MainActivity x0) {
        return x0.displayOn;
    }

    static /* synthetic */ boolean access$402(MainActivity x0, boolean x1) {
        x0.displayOn = x1;
        return x1;
    }

    static /* synthetic */ MapIndex access$500(MainActivity x0, String x1) {
        return x0.getMapIndex(x1);
    }

    static /* synthetic */ void access$600(MainActivity x0, String x1, int x2) {
        x0.requestPermission(x1, x2);
    }

    public boolean onGenericMotionEvent(MotionEvent ev) {
        Log.i("OfflineMaps", "Generic motion " + ev.toString());
        if (ev.getAction() != 8 || !ev.isFromSource(4194304)) {
            return false;
        }
        Log.i("OfflineMaps", "Rotary motion " + ev.toString());
        if (ev.getAxisValue(26) < 0.0f) {
            this.mapView.getModel().mapViewPosition.zoomIn(false);
            return true;
        }
        if (ev.getAxisValue(26) > 0.0f) {
            this.mapView.getModel().mapViewPosition.zoomOut(false);
            return true;
        }
        return true;
    }

    private static Paint getPaint(int color, int strokeWidth, Style style) {
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        return paint;
    }

    private void setLocation(Location location) {
        this.mapView.setCenter(new LatLong(location.getLatitude(), location.getLongitude()));
    }

    double getLatLongMult(LatLong position) {
        return position.vincentyDistance(new LatLong(position.latitude - 0.01d, position.longitude)) / position.vincentyDistance(new LatLong(position.latitude, position.longitude - 0.01d));
    }

    LatLong getClosestPoint(LatLong p, LatLong s1, LatLong s2, double mult) {
        double param;
        double a = p.latitude - s1.latitude;
        double b = (p.longitude - s1.longitude) / mult;
        double c = s2.latitude - s1.latitude;
        double d = (s2.longitude - s1.longitude) / mult;
        double lenSq = (c * c) + (d * d);
        if (lenSq == 0.0d) {
            param = -1.0d;
        } else {
            double param2 = a * c;
            param = (param2 + (b * d)) / lenSq;
        }
        return param < 0.0d ? s1 : param > 1.0d ? s2 : new LatLong(s1.latitude + (param * c), s1.longitude + (param * d * mult));
    }

    public void onLocationChanged(Location location) {
        LatLong myPos;
        this.myLocationOverlay.setPosition(location.getLatitude(), location.getLongitude(), location.getAccuracy());
        this.lastLocation = location;
        ImageButton button = findViewById(2131230938);
        if (isLockedLocation()) {
            setLocation(location);
        }
        button.setBackgroundColor(getResources().getColor(this.isLockedLocation ? 2131034146 : 2131034334));
        if (this.currentTrack == null) {
            findViewById(2131231141).setVisibility(8);
            return;
        }
        LatLong myPos2 = new LatLong(location.getLatitude(), location.getLongitude());
        double latLongMult = getLatLongMult(myPos2);
        Log.i("OfflineMaps", "Lat long mult " + Double.toString(latLongMult));
        double distCounter = 0.0d;
        double distCounter2 = 0.0d;
        double completedDist = 0.0d;
        LatLong closest = null;
        for (int i = 1; i < this.currentTrack.points.size(); i++) {
            LatLong p1 = (LatLong) this.currentTrack.points.get(i - 1);
            LatLong p2 = (LatLong) this.currentTrack.points.get(i);
            LatLong p = getClosestPoint(myPos2, p1, p2, latLongMult);
            double dist = p.vincentyDistance(myPos2);
            if (closest == null || dist < completedDist) {
                completedDist = dist;
                closest = p;
                distCounter2 = distCounter + p1.vincentyDistance(p);
            }
            distCounter += p1.vincentyDistance(p2);
        }
        if (closest == null) {
            myPos = myPos2;
        } else {
            myPos = myPos2;
            this.gpxLocationOverlay.setPosition(closest.latitude, closest.longitude, 0.0f);
        }
        TextView distanceLabel = findViewById(2131230867);
        DecimalFormat df = new DecimalFormat("0.00");
        distanceLabel.setText(df.format(distCounter2 / 1609.34d) + " m");
        TextView distanceTotalLabel = findViewById(2131230868);
        distanceTotalLabel.setText(df.format(this.currentTrack.length / 1609.34d) + " m");
        if (findViewById(2131231131).getVisibility() == 0) {
            findViewById(2131231141).setVisibility(0);
        }
    }

    private void toggleView(View view) {
        if (view.getVisibility() == 8) {
            view.setVisibility(0);
        } else {
            view.setVisibility(8);
        }
    }

    public void onBackPressed() {
        MapView mapView = findViewById(2131230940);
        boolean state = !mapView.isClickable();
        mapView.setClickable(state);
        findViewById(2131230938).setClickable(state);
        findViewById(2131230866).setClickable(state);
        findViewById(2131230921).setClickable(state);
        toggleView(findViewById(2131231043));
    }

    private boolean isLockedLocation() {
        return this.isLockedLocation;
    }

    class 1 implements Runnable {
        1() {
        }

        public void run() {
            TextView timerButton = MainActivity.this.findViewById(2131231130);
            if (MainActivity.access$000(MainActivity.this) == null) {
                timerButton.setText("00:00");
                return;
            }
            long millis = System.currentTimeMillis() - MainActivity.access$000(MainActivity.this).longValue();
            int seconds = ((int) millis) / 1000;
            int minutes = seconds / 60;
            timerButton.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds % 60)}));
            MainActivity.this.timerHandler.postDelayed(this, 500L);
        }
    }

    protected void createControls() {
        initializePosition(this.mapView.getModel().mapViewPosition);
        TextView timerButton = findViewById(2131231130);
        timerButton.setOnClickListener(new 2());
        ImageButton locationButton = findViewById(2131230938);
        locationButton.setOnClickListener(new 3(locationButton));
        ImageButton infoButton = findViewById(2131230921);
        infoButton.setOnClickListener(new 4());
        ImageButton displayButton = findViewById(2131230866);
        displayButton.setOnClickListener(new 5(displayButton));
    }

    class 2 implements View.OnClickListener {
        2() {
        }

        public void onClick(View v) {
            if (MainActivity.access$000(MainActivity.this) == null) {
                MainActivity.access$002(MainActivity.this, Long.valueOf(System.currentTimeMillis()));
                MainActivity.this.timerHandler.postDelayed(MainActivity.this.timerRunnable, 0L);
            } else {
                MainActivity.access$002(MainActivity.this, null);
            }
        }
    }

    class 3 implements View.OnClickListener {
        final /* synthetic */ ImageButton val$locationButton;

        3(ImageButton imageButton) {
            this.val$locationButton = imageButton;
        }

        public void onClick(View v) {
            if (!MainActivity.access$100(MainActivity.this)) {
                MainActivity.this.isLockedLocation = false;
                if (MainActivity.this.lastLocation != null) {
                    this.val$locationButton.setBackgroundColor(MainActivity.this.getResources().getColor(2131034146));
                    MainActivity.access$200(MainActivity.this, MainActivity.this.lastLocation);
                    return;
                }
                return;
            }
            MainActivity.this.isLockedLocation = true;
            ImageButton button = MainActivity.this.findViewById(2131230938);
            button.setBackgroundColor(MainActivity.this.getResources().getColor(2131034334));
        }
    }

    class 4 implements View.OnClickListener {
        4() {
        }

        public void onClick(View v) {
            MainActivity.access$300(MainActivity.this, MainActivity.this.findViewById(2131231131));
            MainActivity.access$300(MainActivity.this, MainActivity.this.findViewById(2131231141));
            if (MainActivity.this.currentTrack == null) {
                MainActivity.this.findViewById(2131231141).setVisibility(8);
            }
        }
    }

    class 5 implements View.OnClickListener {
        final /* synthetic */ ImageButton val$displayButton;

        5(ImageButton imageButton) {
            this.val$displayButton = imageButton;
        }

        public void onClick(View v) {
            if (!MainActivity.access$400(MainActivity.this)) {
                MainActivity.this.getWindow().addFlags(128);
                this.val$displayButton.setBackgroundColor(MainActivity.this.getResources().getColor(2131034146));
            } else {
                MainActivity.this.getWindow().clearFlags(128);
                this.val$displayButton.setBackgroundColor(MainActivity.this.getResources().getColor(2131034334));
            }
            MainActivity.access$402(MainActivity.this, !MainActivity.access$400(MainActivity.this));
        }
    }

    void downloadGpx(String url, String code) {
        DownloadManager downloadManager = (DownloadManager) getSystemService("download");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(true);
        request.setTitle("Gpx file download");
        request.setDescription("");
        String tmpName = code + ".gpx";
        File target = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), tmpName);
        if (target.exists()) {
            target.delete();
        }
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, tmpName);
        long id = downloadManager.enqueue(request);
        downloadingDialog(id, null);
    }

    void downloadTrackExplanation() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService("layout_inflater");
        View popupView = layoutInflater.inflate(2131427357, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(popupView, -1, -1);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ListView listView = popupView.findViewById(2131230815);
        String[] elems = {"OSM.org"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 2131427356, elems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new 6(popupWindow));
        popupWindow.showAsDropDown(this.mapView, 50, -30);
    }

    class 6 implements AdapterView.OnItemClickListener {
        final /* synthetic */ PopupWindow val$popupWindow;

        6(PopupWindow popupWindow) {
            this.val$popupWindow = popupWindow;
        }

        public void onItemClick(AdapterView adapterView, View view, int i, long l) {
            switch (i) {
                case 0:
                    MainActivity.this.downloadTrackMenu("Enter numeric OSM trace ID.", "https://www.openstreetmap.org/traces/", "/data", 2);
                    break;
            }
            this.val$popupWindow.dismiss();
        }
    }

    void downloadingDialog(long downloadId, DownloadInfo mapDownload) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView myMsg = new TextView(this);
        myMsg.setText("Downloading file");
        myMsg.setGravity(1);
        myMsg.setTextSize(14.0f);
        builder.setCustomTitle(myMsg);
        builder.setNegativeButton("Cancel", new 7(downloadId, mapDownload));
        if (mapDownload != null) {
            builder.setPositiveButton("Background", new 8(downloadId));
        }
        builder.setMessage("0%");
        AlertDialog dialog = builder.show();
        this.downloadDialogs.put(Long.valueOf(downloadId), dialog);
        this.executor.execute(new 9(dialog, downloadId));
    }

    class 7 implements DialogInterface.OnClickListener {
        final /* synthetic */ long val$downloadId;
        final /* synthetic */ DownloadInfo val$mapDownload;

        7(long j, DownloadInfo downloadInfo) {
            this.val$downloadId = j;
            this.val$mapDownload = downloadInfo;
        }

        public void onClick(DialogInterface dialog, int which) {
            DownloadManager downloadManager = (DownloadManager) MainActivity.this.getSystemService("download");
            downloadManager.remove(new long[]{this.val$downloadId});
            if (this.val$mapDownload != null) {
                this.val$mapDownload.setStatus(MapDownloadStatus.ABSENT);
                MainActivity.this.mapDownloadsAdapter.notifyDataSetChanged();
            }
        }
    }

    class 8 implements DialogInterface.OnClickListener {
        final /* synthetic */ long val$downloadId;

        8(long j) {
            this.val$downloadId = j;
        }

        public void onClick(DialogInterface dialog, int which) {
            MainActivity.this.downloadDialogs.remove(Long.valueOf(this.val$downloadId));
        }
    }

    class 9 implements Runnable {
        final /* synthetic */ AlertDialog val$dialog;
        final /* synthetic */ long val$downloadId;

        9(AlertDialog alertDialog, long j) {
            this.val$dialog = alertDialog;
            this.val$downloadId = j;
        }

        public void run() {
            boolean isDownloadFinished = false;
            while (!isDownloadFinished && this.val$dialog.isShowing()) {
                DownloadManager downloadManager = (DownloadManager) MainActivity.this.getSystemService("download");
                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(new long[]{this.val$downloadId}));
                if (cursor.moveToFirst()) {
                    int downloadStatus = cursor.getInt(cursor.getColumnIndex("status"));
                    switch (downloadStatus) {
                        case 2:
                            long totalBytes = cursor.getLong(cursor.getColumnIndex("total_size"));
                            if (totalBytes <= 0) {
                                break;
                            } else {
                                long downloadedBytes = cursor.getLong(cursor.getColumnIndex("bytes_so_far"));
                                MainActivity.this.runOnUiThread(new 1(downloadedBytes, totalBytes));
                                break;
                            }
                        case 8:
                        case 16:
                            isDownloadFinished = true;
                            break;
                    }
                }
            }
        }

        class 1 implements Runnable {
            final /* synthetic */ long val$downloadedBytes;
            final /* synthetic */ long val$totalBytes;

            1(long j, long j2) {
                this.val$downloadedBytes = j;
                this.val$totalBytes = j2;
            }

            public void run() {
                9.this.val$dialog.setMessage(((int) ((this.val$downloadedBytes * 100) / this.val$totalBytes)) + "% of " + this.val$totalBytes + " bytes");
            }
        }
    }

    void chooseGpxSite() {
    }

    void downloadTrackMenu(String message, String url, String suffix, int inputType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);
        EditText input = new EditText(this);
        input.setInputType(inputType);
        builder.setView(input);
        builder.setPositiveButton("OK", new 10(input, url, suffix));
        builder.setNegativeButton("Cancel", new 11());
        builder.show();
    }

    class 10 implements DialogInterface.OnClickListener {
        final /* synthetic */ EditText val$input;
        final /* synthetic */ String val$suffix;
        final /* synthetic */ String val$url;

        10(EditText editText, String str, String str2) {
            this.val$input = editText;
            this.val$url = str;
            this.val$suffix = str2;
        }

        public void onClick(DialogInterface dialog, int which) {
            String code = this.val$input.getText().toString();
            MainActivity.this.downloadGpx(this.val$url + code + this.val$suffix, code);
        }
    }

    class 11 implements DialogInterface.OnClickListener {
        11() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    class 12 implements Runnable {
        final /* synthetic */ int val$button;

        12(int i) {
            this.val$button = i;
        }

        public void run() {
            MainActivity.this.currentTrack = (Track) MainActivity.this.tracks.get(this.val$button);
            MainActivity.this.reloadLayers();
        }
    }

    void trackClick(int button) {
        multiMenu(Arrays.asList(new String[]{"Activate", "Erase"}), Arrays.asList(new Runnable[]{new 12(button), new 13(button)}));
    }

    class 13 implements Runnable {
        final /* synthetic */ int val$button;

        13(int i) {
            this.val$button = i;
        }

        class 1 implements Runnable {
            1() {
            }

            public void run() {
                Track toRemove = (Track) MainActivity.this.tracks.get(13.this.val$button);
                if (MainActivity.this.currentTrack == toRemove) {
                    MainActivity.this.currentTrack = null;
                    MainActivity.this.reloadLayers();
                }
                toRemove.file.delete();
                MainActivity.this.tracks.remove(13.this.val$button);
                MainActivity.this.tracksMenu();
            }
        }

        public void run() {
            MainActivity.this.confirmationDialog("Erase this track?", new 1());
        }
    }

    void tracksMenu() {
        View popupView = ((LayoutInflater) getBaseContext().getSystemService("layout_inflater")).inflate(2131427357, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(popupView, -1, -1);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ListView listView = popupView.findViewById(2131230815);
        ArrayList arrayList = new ArrayList();
        arrayList.add("Start/Stop GPX Tracking");
        arrayList.add("Import");
        arrayList.add("Files");
        for (Track t : this.tracks) {
            arrayList.add(t.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 2131427356, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MainActivity$$ExternalSyntheticLambda0(this, popupWindow));
        popupWindow.showAsDropDown(this.mapView, 50, -30);
    }

    /* synthetic */ void lambda$tracksMenu$0$com-keeperrl-offlinemapsforwearos-MainActivity(PopupWindow popupWindow, AdapterView adapterView, View view, int position, long id) {
        if (position == 0) {
            Intent intent = new Intent(this, GPXTrackingService.class);
            if (!this.isTracking) {
                startService(intent);
                this.isTracking = true;
                Toast.makeText(this, "GPX tracking started", 0).show();
            } else {
                stopService(intent);
                this.isTracking = false;
                Toast.makeText(this, "GPX tracking stopped", 0).show();
                reloadLayers();
            }
        } else if (position == 1) {
            importMenu();
        } else if (position == 2) {
            filesMenu();
        } else {
            trackClick(position - 3);
        }
        popupWindow.dismiss();
    }

    void importMenu() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService("layout_inflater");
        View popupView = layoutInflater.inflate(2131427357, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(popupView, -1, -1);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ListView listView = popupView.findViewById(2131230815);
        String[] elems = {"OSM.org"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 2131427356, elems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new 24(popupWindow));
        popupWindow.showAsDropDown(this.mapView, 50, -30);
    }

    class 24 implements AdapterView.OnItemClickListener {
        final /* synthetic */ PopupWindow val$popupWindow;

        24(PopupWindow popupWindow) {
            this.val$popupWindow = popupWindow;
        }

        public void onItemClick(AdapterView adapterView, View view, int i, long l) {
            switch (i) {
                case 0:
                    MainActivity.this.downloadTrackMenu("Enter numeric OSM trace ID.", "https://www.openstreetmap.org/traces/", "/data", 2);
                    break;
            }
            this.val$popupWindow.dismiss();
        }
    }

    void filesMenu() {
        View popupView = ((LayoutInflater) getBaseContext().getSystemService("layout_inflater")).inflate(2131427357, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(popupView, -1, -1);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ListView listView = popupView.findViewById(2131230815);
        ArrayList arrayList = new ArrayList();
        for (Track t : this.tracks) {
            arrayList.add(t.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 2131427356, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new 25());
        popupWindow.showAsDropDown(this.mapView, 50, -30);
    }

    class 25 implements AdapterView.OnItemClickListener {
        25() {
        }

        public void onItemClick(AdapterView adapterView, View view, int i, long l) {
            MainActivity.this.trackClick(i);
        }
    }

    void popupMenu() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService("layout_inflater");
        View popupView = layoutInflater.inflate(2131427357, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(popupView, -1, -1);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ListView listView = popupView.findViewById(2131230815);
        String[] elems = {"GPX Tracks", "Offline Maps"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 2131427356, elems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new 14(popupWindow));
        popupWindow.showAsDropDown(this.mapView, 50, -30);
    }

    class 14 implements AdapterView.OnItemClickListener {
        final /* synthetic */ PopupWindow val$popupWindow;

        14(PopupWindow popupWindow) {
            this.val$popupWindow = popupWindow;
        }

        public void onItemClick(AdapterView adapterView, View view, int i, long l) {
            switch (i) {
                case 0:
                    MainActivity.this.tracksMenu();
                    this.val$popupWindow.dismiss();
                    return;
                case 1:
                    MainActivity.this.chooseMapGroupAction();
                    return;
                default:
                    throw new RuntimeException("Unknown menu item: " + Integer.toString(i));
            }
        }
    }

    void chooseMapGroupAction() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService("layout_inflater");
        View popupView = layoutInflater.inflate(2131427357, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(popupView, -1, -1);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ListView listView = popupView.findViewById(2131230815);
        String[] elems = (String[]) this.allMaps.keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 2131427356, elems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new 15(elems));
        popupWindow.showAsDropDown(this.mapView, 50, -30);
    }

    class 15 implements AdapterView.OnItemClickListener {
        final /* synthetic */ String[] val$elems;

        15(String[] strArr) {
            this.val$elems = strArr;
        }

        public void onItemClick(AdapterView adapterView, View view, int i, long l) {
            MainActivity.this.downloadMapsAction(this.val$elems[i]);
        }
    }

    private class DownloadInfo {
        String name;
        int size;
        String url;
        public long downloadId = 0;
        private MapDownloadStatus status = MapDownloadStatus.ABSENT;

        static /* synthetic */ MapDownloadStatus access$700(DownloadInfo x0) {
            return x0.status;
        }

        public DownloadInfo(String name, String subdir) {
            this.name = name;
            this.url = "http://ftp-stud.hs-esslingen.de/pub/Mirrors/download.mapsforge.org/maps/v5/" + subdir + "/" + name.toLowerCase() + ".map";
        }

        public String getFileName() {
            return this.name + ".map";
        }

        public File getDownloadedPath() {
            return new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), getFileName());
        }

        public String getTmpName() {
            return getFileName() + ".tmp";
        }

        public File getTmpPath() {
            return new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), getTmpName());
        }

        public boolean isFetching() {
            return this.downloadId != 0;
        }

        public String toString() {
            if (isFetching()) {
                return this.name + " (fetching)";
            }
            switch (this.status) {
                case ABSENT:
                    return this.name;
                case READY:
                    return this.name + " (ready)";
                case ERROR:
                    return this.name + " (error downloading)";
                default:
                    return this.name;
            }
        }

        void setStatus(MapDownloadStatus status) {
            this.downloadId = 0L;
            this.status = status;
        }
    }

    static class MapIndex {
        public String group;
        public int index;

        public MapIndex(String group, int i) {
            this.group = group;
            this.index = i;
        }
    }

    DownloadInfo getMap(MapIndex index) {
        return ((DownloadInfo[]) this.allMaps.get(index.group))[index.index];
    }

    private MapIndex getMapIndex(String name) {
        for (String group : this.allMaps.keySet()) {
            for (int i = 0; i < ((DownloadInfo[]) this.allMaps.get(group)).length; i++) {
                if (((DownloadInfo[]) this.allMaps.get(group))[i].getTmpName().equals(name)) {
                    return new MapIndex(group, i);
                }
            }
        }
        throw new RuntimeException("Bad map name: " + name);
    }

    void downloadMapsAction(String group) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService("layout_inflater");
        View popupView = layoutInflater.inflate(2131427357, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(popupView, -1, -1);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ListView listView = popupView.findViewById(2131230815);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterByStatus(2);
        DownloadInfo[] chosenMaps = (DownloadInfo[]) this.allMaps.get(group);
        for (int i = 0; i < chosenMaps.length; i++) {
            if (chosenMaps[i].getDownloadedPath().exists()) {
                chosenMaps[i].setStatus(MapDownloadStatus.READY);
            }
        }
        DownloadManager downloadManager = (DownloadManager) getSystemService("download");
        Cursor cursor = downloadManager.query(q);
        if (cursor.moveToFirst()) {
            do {
                String uri = cursor.getString(cursor.getColumnIndex("uri"));
                cursor.getString(cursor.getColumnIndex("local_uri"));
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                for (int i2 = 0; i2 < chosenMaps.length; i2++) {
                    if (chosenMaps[i2].url.equals(uri)) {
                        chosenMaps[i2].downloadId = id;
                    }
                }
            } while (cursor.moveToNext());
        }
        this.mapDownloadsAdapter = new ArrayAdapter(this, 2131427356, chosenMaps);
        listView.setAdapter(this.mapDownloadsAdapter);
        listView.setOnItemClickListener(new 16(chosenMaps, popupWindow));
        popupWindow.showAsDropDown(this.mapView, 50, -30);
    }

    class 16 implements AdapterView.OnItemClickListener {
        final /* synthetic */ DownloadInfo[] val$chosenMaps;
        final /* synthetic */ PopupWindow val$popupWindow;

        16(DownloadInfo[] downloadInfoArr, PopupWindow popupWindow) {
            this.val$chosenMaps = downloadInfoArr;
            this.val$popupWindow = popupWindow;
        }

        public void onItemClick(AdapterView adapterView, View view, int i, long l) {
            if (this.val$chosenMaps[i].isFetching()) {
                MainActivity.this.downloadingDialog(this.val$chosenMaps[i].downloadId, this.val$chosenMaps[i]);
            } else if (DownloadInfo.access$700(this.val$chosenMaps[i]) == MapDownloadStatus.READY) {
                MainActivity.this.multiMenu(Arrays.asList(new String[]{"Erase file"}), Arrays.asList(new Runnable[]{new 1(i)}));
            } else {
                MainActivity.this.downloadMap(this.val$chosenMaps[i]);
                MainActivity.this.mapDownloadsAdapter.notifyDataSetChanged();
            }
        }

        class 1 implements Runnable {
            final /* synthetic */ int val$i;

            1(int i) {
                this.val$i = i;
            }

            class 1 implements Runnable {
                1() {
                }

                public void run() {
                    16.this.val$chosenMaps[1.this.val$i].getDownloadedPath().delete();
                    MainActivity.this.mapDownloadsAdapter.notifyDataSetChanged();
                    16.this.val$popupWindow.dismiss();
                    MainActivity.this.reloadLayers();
                }
            }

            public void run() {
                MainActivity.this.confirmationDialog("Erase " + 16.this.val$chosenMaps[this.val$i].name + "?", new 1());
            }
        }
    }

    void confirmationDialog(String message, Runnable action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(message).setIcon(17301543);
        if (action != null) {
            builder.setPositiveButton(17039379, new 17(action)).setNegativeButton(17039369, (DialogInterface.OnClickListener) null);
        } else {
            builder.setPositiveButton(17039379, (DialogInterface.OnClickListener) null);
        }
        builder.show();
    }

    class 17 implements DialogInterface.OnClickListener {
        final /* synthetic */ Runnable val$action;

        17(Runnable runnable) {
            this.val$action = runnable;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            this.val$action.run();
        }
    }

    void multiMenu(List list, List list2) {
        View popupView = ((LayoutInflater) getBaseContext().getSystemService("layout_inflater")).inflate(2131427357, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(popupView, -1, -1);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        ListView listView = popupView.findViewById(2131230815);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 2131427356, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new 18(list2, popupWindow));
        popupWindow.showAsDropDown(this.mapView, 50, -30);
    }

    class 18 implements AdapterView.OnItemClickListener {
        final /* synthetic */ List val$actions;
        final /* synthetic */ PopupWindow val$popupWindow;

        18(List list, PopupWindow popupWindow) {
            this.val$actions = list;
            this.val$popupWindow = popupWindow;
        }

        public void onItemClick(AdapterView adapterView, View view, int button, long l) {
            ((Runnable) this.val$actions.get(button)).run();
            this.val$popupWindow.dismiss();
        }
    }

    void downloadMap(DownloadInfo map) {
        DownloadManager downloadManager = (DownloadManager) getSystemService("download");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(map.url));
        request.setAllowedOverRoaming(false);
        request.setTitle("Offline map download (" + map.name + ")");
        request.setDescription("");
        File target = map.getTmpPath();
        if (target.exists()) {
            target.delete();
        }
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, map.getTmpName());
        map.downloadId = downloadManager.enqueue(request);
        downloadingDialog(map.downloadId, map);
    }

    class 19 extends BroadcastReceiver {
        19() {
        }

        public void onReceive(Context context, Intent intent) {
            Long downloadId = Long.valueOf(intent.getLongExtra("extra_download_id", 0L));
            AlertDialog dialog = (AlertDialog) MainActivity.this.downloadDialogs.get(downloadId);
            if (dialog != null) {
                dialog.hide();
                MainActivity.this.downloadDialogs.remove(downloadId);
            }
            Log.i("OfflineMaps", "Checking download status for id: " + downloadId);
            DownloadManager downloadManager = (DownloadManager) MainActivity.this.getSystemService("download");
            Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(new long[]{downloadId.longValue()}));
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                if (status == 16) {
                    Toast.makeText(MainActivity.this, "Download failed.", 0).show();
                    return;
                }
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex("local_uri")));
                String path = uri.getPath();
                String fileExt = MimeTypeMap.getFileExtensionFromUrl(path);
                File file = new File(path);
                if (fileExt.equals("tmp")) {
                    MapIndex mapIndex = MainActivity.access$500(MainActivity.this, file.getName());
                    DownloadInfo map = MainActivity.this.getMap(mapIndex);
                    if (status == 8) {
                        file.renameTo(map.getDownloadedPath());
                        map.setStatus(MapDownloadStatus.READY);
                        MainActivity.this.mapDownloadsAdapter.notifyDataSetChanged();
                        MainActivity.this.reloadLayers();
                    }
                } else if (fileExt.equals("gpx")) {
                    MainActivity.this.reloadLayers();
                    MainActivity.this.tracksMenu();
                }
            }
            cursor.close();
        }
    }

    class 20 extends BroadcastReceiver {
        20() {
        }

        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            TextView view = MainActivity.this.findViewById(2131230806);
            view.setText(Integer.toString(level) + "%");
        }
    }

    File getAssetFile(String path) {
        try {
            InputStream stream = getAssets().open(path);
            File tmp = File.createTempFile("world", "map");
            byte[] buffer = new byte[1024];
            FileOutputStream fileOutputStream = new FileOutputStream(tmp);
            while (true) {
                int read = stream.read(buffer);
                if (read != -1) {
                    fileOutputStream.write(buffer, 0, read);
                } else {
                    return tmp;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static double getTrackLength(List list) {
        double ret = 0.0d;
        for (int i = 1; i < list.size(); i++) {
            ret += ((LatLong) list.get(i)).vincentyDistance((LatLong) list.get(i - 1));
        }
        return ret;
    }

    class Track {
        File file;
        double length;
        String name;
        List points;

        Track(String name, File file, List list) {
            this.name = name;
            this.file = file;
            this.points = list;
            this.length = MainActivity.getTrackLength(list);
        }
    }

    void reloadTracks() {
        this.tracks = new ArrayList();
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".gpx")) {
                Log.i("OfflineMaps", "Found GPX file: " + f.getAbsolutePath());
                try {
                    GPXData data = decodeGPX(f);
                    String name = data.name;
                    if (name == null) {
                        name = f.getName().substring(0, f.getName().length() - 4);
                    }
                    this.tracks.add(new Track(name, f, data.points));
                } catch (Exception e) {
                    Log.e("OfflineMaps", e.toString());
                }
            }
        }
    }

    MapDataStore getDownloadedMaps() {
        MultiMapDataStore dataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL);
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".map")) {
                Log.i("OfflineMaps", "Found map file: " + f.getAbsolutePath());
                MapFile file = new MapFile(f);
                file.restrictToZoomRange((byte) 6, Byte.MAX_VALUE);
                dataStore.addMapDataStore(file, false, false);
                if (Character.isDigit(f.getName().charAt(f.getName().length() - 5))) {
                    f.delete();
                }
            } else if (!f.getName().endsWith(".poi") && !f.getName().endsWith(".gpx")) {
                Log.i("OfflineMaps", "Removing unknown file: " + f.getAbsolutePath());
                f.delete();
            }
        }
        dataStore.addMapDataStore(new MapFile(getAssetFile("world.map")), false, false);
        return dataStore;
    }

    void reloadLayers() {
        ((TileCache) this.tileCaches.get(0)).purge();
        MapDataStore mapStore = getDownloadedMaps();
        TileRendererLayer tileRendererLayer = new 21((TileCache) this.tileCaches.get(0), mapStore, this.mapView.getModel().mapViewPosition, false, false, false, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(new CustomTheme("/assets/default.xml"));
        this.mapView.getLayerManager().getLayers().clear(true);
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
        MapDataStoreLabelStore labelStore = new MapDataStoreLabelStore(mapStore, tileRendererLayer.getRenderThemeFuture(), tileRendererLayer.getTextScale(), tileRendererLayer.getDisplayModel(), AndroidGraphicFactory.INSTANCE);
        this.mapView.getLayerManager().getLayers().add(new ThreadedLabelLayer(AndroidGraphicFactory.INSTANCE, labelStore));
        this.mapView.getLayerManager().getLayers().add(this.myLocationOverlay);
        this.mapView.getLayerManager().getLayers().add(this.gpxLocationOverlay);
        if (this.currentTrack != null) {
            addTrack(this.mapView.getLayerManager().getLayers(), this.currentTrack.points);
        }
        reloadTracks();
    }

    class 21 extends TileRendererLayer {
        21(TileCache arg0, MapDataStore arg1, IMapViewPosition arg2, boolean arg3, boolean arg4, boolean arg5, GraphicFactory arg6) {
            super(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
        }

        public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY) {
            MainActivity.this.popupMenu();
            return true;
        }
    }

    private void createLayers() {
        Marker marker = new Marker((LatLong) null, new AndroidBitmap(BitmapFactory.decodeResource(getResources(), 2131165312)), 0, 0);
        Circle circle = new Circle((LatLong) null, 0.0f, getPaint(AndroidGraphicFactory.INSTANCE.createColor(48, 0, 0, 255), 0, Style.FILL), getPaint(AndroidGraphicFactory.INSTANCE.createColor(160, 0, 0, 255), 2, Style.STROKE));
        this.myLocationOverlay = new MyLocationOverlay(marker, circle);
        Marker marker2 = new Marker((LatLong) null, new AndroidBitmap(BitmapFactory.decodeResource(getResources(), 2131165313)), 0, 0);
        this.gpxLocationOverlay = new MyLocationOverlay(marker2, (Circle) null);
        reloadLayers();
        boolean isWatch = getResources().getBoolean(2130968579);
        if (isWatch) {
            this.mapView.setBuiltInZoomControls(false);
        }
        DefaultMapScaleBar scaleBar = new DefaultMapScaleBar(this.mapView.getModel().mapViewPosition, this.mapView.getModel().mapViewDimension, AndroidGraphicFactory.INSTANCE, this.mapView.getModel().displayModel);
        this.mapView.setMapScaleBar(scaleBar);
        scaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_CENTER);
        scaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.SINGLE);
        scaleBar.setDistanceUnitAdapter(new MetricUnitAdapter());
        this.locationManager = (LocationManager) getSystemService("location");
        this.locationManager.removeUpdates(this);
        this.mapView.addInputListener(new 22());
        subscribeToLocation();
        this.mapView.getModel().displayModel.setBackgroundColor(0);
    }

    class 22 implements InputListener {
        22() {
        }

        public void onMoveEvent() {
            if (MainActivity.access$100(MainActivity.this)) {
                MainActivity.this.lockedLocation = System.currentTimeMillis();
            }
        }

        public void onZoomEvent() {
        }
    }

    void subscribeToLocation() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0 && ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            Log.i("OfflineMaps", "No location permission");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.ACCESS_FINE_LOCATION")) {
                Log.i("OfflineMaps", "Explaining");
                showExplanation("Permission Needed", "Rationale", "android.permission.ACCESS_FINE_LOCATION", 1);
                return;
            } else {
                requestPermission("android.permission.ACCESS_FINE_LOCATION", 1);
                Log.i("OfflineMaps", "Requesting");
                return;
            }
        }
        Log.i("OfflineMaps", "Have location permission");
        for (String provider : this.locationManager.getProviders(true)) {
            if ("gps".equals(provider) || "network".equals(provider)) {
                this.locationManager.requestLocationUpdates(provider, 0L, 0.0f, this);
            }
        }
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permissionName}, permissionRequestCode);
    }

    private void showExplanation(String title, String message, String permission, int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setPositiveButton(17039370, new 23(permission, permissionRequestCode));
        builder.create().show();
    }

    class 23 implements DialogInterface.OnClickListener {
        final /* synthetic */ String val$permission;
        final /* synthetic */ int val$permissionRequestCode;

        23(String str, int i) {
            this.val$permission = str;
            this.val$permissionRequestCode = i;
        }

        public void onClick(DialogInterface dialog, int id) {
            MainActivity.access$600(MainActivity.this, this.val$permission, this.val$permissionRequestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == 0) {
                    subscribeToLocation();
                    break;
                }
                break;
        }
    }

    static Paint createPaint(int color, int strokeWidth, Style style) {
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        return paint;
    }

    protected void addTrack(Layers layers, List list) {
        Polyline polyline = new Polyline(createPaint(AndroidGraphicFactory.INSTANCE.createColor(Color.RED), (int) (this.mapView.getModel().displayModel.getScaleFactor() * 4.0f), Style.STROKE), AndroidGraphicFactory.INSTANCE);
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            LatLong p = (LatLong) it.next();
            arrayList.add(p);
        }
        polyline.setPoints(arrayList);
        layers.add(polyline);
    }

    class GPXData {
        String name;
        List points;

        public GPXData(List list, String name) {
            this.points = list;
            this.name = name;
        }
    }

    private GPXData decodeGPX(File file) throws IOException, SAXException, ParserConfigurationException {
        ArrayList arrayList = new ArrayList();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        Element elementRoot = document.getDocumentElement();
        NodeList nameList = elementRoot.getElementsByTagName("name");
        String name = null;
        if (nameList.getLength() > 0) {
            name = nameList.item(0).getFirstChild().getNodeValue();
        }
        NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");
        int i = 0;
        while (i < nodelist_trkpt.getLength()) {
            Node node = nodelist_trkpt.item(i);
            NamedNodeMap attributes = node.getAttributes();
            String newLatitude = attributes.getNamedItem("lat").getTextContent();
            Double newLatitude_double = Double.valueOf(Double.parseDouble(newLatitude));
            String newLongitude = attributes.getNamedItem("lon").getTextContent();
            Double newLongitude_double = Double.valueOf(Double.parseDouble(newLongitude));
            arrayList.add(new LatLong(newLatitude_double.doubleValue(), newLongitude_double.doubleValue()));
            i++;
            documentBuilderFactory = documentBuilderFactory;
            documentBuilder = documentBuilder;
            document = document;
            elementRoot = elementRoot;
        }
        return new GPXData(arrayList, name);
    }

    private void createTileCaches() {
        Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        android.graphics.Point point = new android.graphics.Point();
        display.getSize(point);
        int hypot = (int) Math.hypot(point.x, point.y);
        this.tileCaches.add(AndroidUtil.createTileCache(this, getPersistableId(), this.mapView.getModel().displayModel.getTileSize(), hypot, hypot, this.mapView.getModel().frameBufferModel.getOverdrawFactor(), true));
    }

    protected IMapViewPosition initializePosition(IMapViewPosition mvp) {
        LatLong center = mvp.getCenter();
        if (center.equals(new LatLong(0.0d, 0.0d))) {
            mvp.setMapPosition(new MapPosition(new LatLong(61.814784429854d, 54.514741140922d), (byte) 17));
        }
        mvp.setZoomLevelMax((byte) 24);
        mvp.setZoomLevelMin((byte) 0);
        return mvp;
    }

    protected void onPause() {
        this.mapView.getModel().save(this.preferencesFacade);
        this.preferencesFacade.save();
        super.onPause();
    }

    protected void onDestroy() {
        this.mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }

    private MapView getMapView() {
        setContentView(2131427374);
        return findViewById(2131230940);
    }

    private void createMapViews() {
        this.mapView = getMapView();
        this.mapView.getModel().init(this.preferencesFacade);
        this.mapView.getMapScaleBar().setVisible(true);
    }

    private String getPersistableId() {
        return getClass().getSimpleName();
    }

    private String queryName(Uri uri) {
        File file = new File(uri.getPath());
        return file.getName();
    }

    private void tryImportingTrack(Uri data) {
        if (data != null) {
            String path = data.getEncodedPath();
            String name = queryName(data);
            Log.i("OfflineMaps", "Opening " + path);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(data)));
                File target = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name);
                BufferedWriter out = new BufferedWriter(new FileWriter(target));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        out.write(line);
                        out.newLine();
                    } else {
                        out.close();
                        Toast.makeText(this, "Imported " + name, 0).show();
                        reloadLayers();
                        return;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryImportingTrack(getIntent().getData());
        AndroidGraphicFactory.createInstance(this);
        this.preferencesFacade = new AndroidPreferences(getSharedPreferences(getPersistableId(), 0));
        createMapViews();
        createTileCaches();
        createLayers();
        createControls();
        setTitle(getClass().getSimpleName());
        registerReceiver(this.downloadReceiver, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
        registerReceiver(this.batteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }
}