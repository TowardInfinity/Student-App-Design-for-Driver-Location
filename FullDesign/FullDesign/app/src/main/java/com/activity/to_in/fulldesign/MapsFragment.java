package com.activity.to_in.fulldesign;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback,
        EasyPermissions.PermissionCallbacks, ActivityCompat.OnRequestPermissionsResultCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MapsFragment() {
        // Required empty public constructor
    }

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private FloatingActionButton fab;

    private Location studentLocation;
    private Location defaultLocation;
    private Location driverLocation;

    private LatLng studentLatLng;
    private LatLng driverLatLng;

    private Marker studentMarker, driverMarker;
    private Polyline line;
    private List<Polyline> polylines;

    private static final double DEFAULT_LAT = 34.124929;
    private static final double DEFAULT_LNG = 74.840259;
    private static final float CAMERA_ZOOM_LEVEL = 15f;

    private Polyline currentPolyline;

    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private final String TAG = "MapsActivity";
    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        initializeOnCreate();
        return view;

    }

    @SuppressLint("MissingPermission")
    private void initializeOnCreate() {
//        fab = getActivity().findViewById(R.id.fab);

        defaultLocation = new Location("Default");
        defaultLocation.setLatitude(DEFAULT_LAT);
        defaultLocation.setLongitude(DEFAULT_LNG);

        mFusedLocationClient = new FusedLocationProviderClient(getActivity());
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            studentLocation = location;
//                            getDriverLocation();
                            initCamera(location);
//                            manageMarker();
                            Toast.makeText(getContext(), "Location Updated.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    studentLocation = location;
//                    getDriverLocation();
                    initCamera(location);
//                    manageMarker();
                    Toast.makeText(getContext(), "Location Updated.", Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

    @SuppressLint("MissingPermission")
    private void initCamera(Location location) {
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude() - 0.0015,
                        location.getLongitude()))
                .zoom(CAMERA_ZOOM_LEVEL)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        requestLocationPermission();

        try {

            studentLatLng = new LatLng
                    (studentLocation.getLatitude(), studentLocation.getLongitude());
//            driverLatLng = new LatLng
//                    (driverLocation.getLatitude(), driverLocation.getLongitude());

            if (studentMarker != null)
                studentMarker.remove();
            studentMarker = mMap.addMarker(new MarkerOptions().position(studentLatLng).title("Student"));

//            if (driverMarker != null)
//                driverMarker.remove();
//            driverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Driver"));

            cameraZoom();
        } catch (NullPointerException e) {
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), null);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestLocationPermission();
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationPermission() {
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), perms)) {
            if (mMap != null) {
                // Access to the location has been granted to the app.
                mMap.setMyLocationEnabled(true);
            }
            Toast.makeText(getContext(), "Opening camera", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions because For Location Services.",
                    123, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        Toast.makeText(getContext(), "Thanks For Permissions", Toast.LENGTH_SHORT).show();
        initializeOnCreate();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
    Fragment fragment = new MapsFragment();
    if (EasyPermissions.somePermissionPermanentlyDenied(fragment, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void cameraZoom(){
        final int width = 256;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(studentLocation.getLatitude(), studentLocation.getLongitude()));
//        builder.include(new LatLng(defaultLocation.getLatitude(), defaultLocation.getLongitude()));
//        builder.include(new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()));

        LatLngBounds bounds = builder.build();
        int padding = ((width * 50) / 100); // offset from edges of the map
        // in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                padding);
        mMap.animateCamera(cu);
    }
}