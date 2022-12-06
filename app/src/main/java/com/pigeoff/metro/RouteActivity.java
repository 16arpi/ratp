package com.pigeoff.metro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.pigeoff.metro.adapters.RouteAdapter;
import com.pigeoff.metro.data.Station;
import com.pigeoff.metro.http.Client;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.advancedpolyline.MonochromaticPaintList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class RouteActivity extends AppCompatActivity {

    String from = new String();
    String to = new String();

    FrameLayout bottomFrame;
    MapView mapRoute;
    ViewPager2 viewPager;
    RouteAdapter routeAdapter;
    Context context;

    HashMap<Integer, Integer> sortiesImgMap;

    Client httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        context = this;

        sortiesImgMap = new HashMap<>();
        sortiesImgMap.put(0, R.drawable.s_0);
        sortiesImgMap.put(1, R.drawable.s_1);
        sortiesImgMap.put(2, R.drawable.s_2);
        sortiesImgMap.put(3, R.drawable.s_3);
        sortiesImgMap.put(4, R.drawable.s_4);
        sortiesImgMap.put(5, R.drawable.s_5);
        sortiesImgMap.put(6, R.drawable.s_6);
        sortiesImgMap.put(7, R.drawable.s_7);
        sortiesImgMap.put(8, R.drawable.s_8);
        sortiesImgMap.put(9, R.drawable.s_9);
        sortiesImgMap.put(10, R.drawable.s_10);
        sortiesImgMap.put(11, R.drawable.s_11);
        sortiesImgMap.put(12, R.drawable.s_12);
        sortiesImgMap.put(13, R.drawable.s_13);
        sortiesImgMap.put(14, R.drawable.s_14);
        sortiesImgMap.put(15, R.drawable.s_15);
        sortiesImgMap.put(16, R.drawable.s_16);
        sortiesImgMap.put(17, R.drawable.s_17);
        sortiesImgMap.put(18, R.drawable.s_18);
        sortiesImgMap.put(19, R.drawable.s_19);

        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");

        System.out.println("Stations :");
        System.out.println(from + " " + to);

        bottomFrame = findViewById(R.id.bottomFrame);
        mapRoute = findViewById(R.id.mapRoute);
        viewPager = findViewById(R.id.viewPagerRoute);

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        httpClient = ((MetroApp) getApplication()).httpClient;

        requestPermissionsIfNecessary(Arrays.asList(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ));

        if (from.isEmpty()) stationsMissing();
        else prepareRouteUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapRoute.onResume();
    }

    private void stationsMissing() {

    }

    private void prepareRouteUI() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ArrayList<Station>> routeCall;
                if (to.isEmpty()) routeCall = httpClient.getStation(from);
                else routeCall = httpClient.getPath(from, to);
                try {
                    ArrayList<Station> route = routeCall.execute().body();
                    assert route != null;
                    assert route.size() > 0;

                    mapRoute.post(new Runnable() {
                        @Override
                        public void run() {
                            mapRoute.setTileSource(TileSourceFactory.MAPNIK);
                            mapRoute.setMultiTouchControls(true);

                            Station fromStation = route.get(0);

                            IMapController mapController = mapRoute.getController();
                            mapController.setZoom(18.0);
                            GeoPoint startPoint = new GeoPoint(fromStation.lat, fromStation.lon);
                            mapController.setCenter(startPoint);

                            List<GeoPoint> geoPoints = new ArrayList<>();
                            String lastLineColor = null;
                            for (int i = 0; i < route.size(); ++i) {
                                Station s = route.get(i);

                                geoPoints.add(new GeoPoint(s.lat, s.lon));

                                if (i > 1 && !lastLineColor.equals(s.color)) {
                                    Polyline line = getRoutePolyline(mapRoute, lastLineColor, geoPoints);
                                    mapRoute.getOverlayManager().add(line);
                                    geoPoints.clear();
                                    geoPoints.add(new GeoPoint(s.lat, s.lon));
                                }

                                Marker dot = getSimpleStationMarker(mapRoute, s);
                                mapRoute.getOverlays().add(dot);

                                lastLineColor = s.color;
                            }
                            Polyline line = getRoutePolyline(mapRoute, lastLineColor, geoPoints);
                            mapRoute.getOverlayManager().add(line);

                            // Gérer les sorties
                            Station lastStation = route.get(route.size() - 1);
                            List<Marker> sortieMarkers = new ArrayList<>();
                            for (Station.Sorties s : lastStation.sorties) {
                                Marker m = getSortieMarker(mapRoute, lastStation, s);
                                sortieMarkers.add(m);
                            }
                            mapRoute.getOverlays().addAll(sortieMarkers);

                            mapRoute.addMapListener(new MapListener() {
                                @Override
                                public boolean onScroll(ScrollEvent event) {
                                    return false;
                                }

                                @Override
                                public boolean onZoom(ZoomEvent event) {
                                    if (event.getZoomLevel() > 17.0) {
                                        for (Marker m : sortieMarkers)
                                            if (!mapRoute.getOverlays().contains(m)) mapRoute.getOverlays().add(m);
                                    } else {
                                        //List<Overlay> overlays = mapRoute.getOverlays();
                                        ArrayList<Overlay> newOverlays = new ArrayList<>();
                                        for (Overlay o : mapRoute.getOverlays())
                                            if (!sortieMarkers.contains(o)) newOverlays.add(o);
                                        mapRoute.getOverlays().clear();
                                        mapRoute.getOverlays().addAll(newOverlays);
                                    }
                                    return true;
                                }
                            });
                        }
                    });

                    viewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            routeAdapter = new RouteAdapter(context, route);
                            //LinearLayoutManager manager = new LinearLayoutManager(context);
                            viewPager.setAdapter(routeAdapter);

                            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                                    Station s = route.get(position);
                                    mapRoute.getController().animateTo(new GeoPoint(s.lat, s.lon));
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]), 1);
        }
    }

    private void requestPermissionsIfNecessary(List<String> permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]), 1);
        }
    }

    private Marker getSortieMarker(MapView map, Station station, Station.Sorties sortie) {
        System.out.println(sortie.nb);
        Drawable icon = AppCompatResources.getDrawable(context, sortiesImgMap.get(sortie.nb));
        //icon.setColorFilter(new PorterDuffColorFilter(Color.parseColor(station.color), PorterDuff.Mode.SRC_IN));
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(sortie.lat, sortie.lon));
        marker.setIcon(icon);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        return marker;
    }

    private Marker getSimpleStationMarker(MapView map, Station station) {
        Drawable icon = AppCompatResources.getDrawable(context, R.drawable.marker_station);
        icon.setColorFilter(new PorterDuffColorFilter(Color.parseColor(station.color), PorterDuff.Mode.SRC_IN));
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(station.lat, station.lon));
        marker.setIcon(icon);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        return marker;
    }

    private Polyline getRoutePolyline(MapView map, String color, List<GeoPoint> points) {
        Paint paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        System.out.println(color);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        Polyline polyline = new Polyline(map);
        polyline.setPoints(points);
        polyline.getOutlinePaintLists().add(new MonochromaticPaintList(paint));
        return polyline;
    }



    @Override
    public void onBackPressed() {
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomFrame);
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            super.onBackPressed();
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}