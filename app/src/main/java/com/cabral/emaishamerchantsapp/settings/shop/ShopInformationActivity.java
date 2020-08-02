package com.cabral.emaishamerchantApp.settings.shop;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cabral.emaishamerchantApp.Maps.PlacesFieldSelector;
import com.cabral.emaishamerchantApp.R;
import com.cabral.emaishamerchantApp.database.DatabaseAccess;
import com.cabral.emaishamerchantApp.settings.SettingsActivity;
import com.cabral.emaishamerchantApp.utils.BaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ShopInformationActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener , OnMapReadyCallback {


    TextView txtUpdate, txtShopEdit;
    EditText etxtShopName, etxtShopContact, etxtShopEmail, etxtShopAddress, etxtShopCurrency;

    //location variables

    TextView proceed_checkout_btn;
    int PLACE_PICKER_REQUEST = 1463;
    String latitude, longitude;
    private static final String TAG ="Maps Error";
    public static GoogleMap map;
    private CameraPosition cameraPosition;
    // The entry point to the Places API.
    private PlacesClient placesClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(0.347596, 32.582520);//A default location  kampala
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    public static Location lastKnownLocation;
    // [START maps_current_place_state_keys]
    public static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private LatLng mCenterLatLong;
    PlacesFieldSelector fieldSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_information);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.shop_information);


        etxtShopName = findViewById(R.id.etxt_shop_name);
        etxtShopContact = findViewById(R.id.etxt_shop_contact);
        etxtShopEmail = findViewById(R.id.etxt_shop_email);
        etxtShopAddress = findViewById(R.id.etxt_shop_address);
        etxtShopCurrency = findViewById(R.id.etxt_shop_currency);
        txtUpdate = findViewById(R.id.txt_update);
        txtShopEdit = findViewById(R.id.txt_shop_edit);
        txtShopEdit = findViewById(R.id.txt_shop_edit);


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ShopInformationActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();

        String shop_name = shopData.get(0).get("shop_name");
        String shop_contact = shopData.get(0).get("shop_contact");
        String shop_email = shopData.get(0).get("shop_email");
        String shop_address = shopData.get(0).get("shop_address");
        String shop_currency = shopData.get(0).get("shop_currency");
        mCenterLatLong= new LatLng(Double.parseDouble(shopData.get(0).get("latitude")), Double.parseDouble(shopData.get(0).get("longitude")) );

        etxtShopName.setText(shop_name);
        etxtShopContact.setText(shop_contact);
        etxtShopEmail.setText(shop_email);
        etxtShopAddress.setText(shop_address);
        etxtShopCurrency.setText(shop_currency);

        etxtShopName.setEnabled(false);
        etxtShopContact.setEnabled(false);
        etxtShopEmail.setEnabled(false);
        etxtShopAddress.setEnabled(false);
        etxtShopCurrency.setEnabled(false);

        txtUpdate.setVisibility(View.GONE);


        txtShopEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etxtShopName.setEnabled(true);
                etxtShopContact.setEnabled(true);
                etxtShopEmail.setEnabled(true);
                etxtShopAddress.setEnabled(true);
                etxtShopCurrency.setEnabled(true);

                etxtShopName.setTextColor(Color.RED);
                etxtShopContact.setTextColor(Color.RED);
                etxtShopEmail.setTextColor(Color.RED);
                etxtShopAddress.setTextColor(Color.RED);
                etxtShopCurrency.setTextColor(Color.RED);
                txtUpdate.setVisibility(View.VISIBLE);

            }
        });


        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Toasty.warning(ShopInformationActivity.this, "Shop Information can't be change. Please purchase from codecanyon. Thank you", Toast.LENGTH_LONG).show();

                String shop_name = etxtShopName.getText().toString().trim();
                String shop_contact = etxtShopContact.getText().toString().trim();
                String shop_email = etxtShopEmail.getText().toString().trim();
                String shop_address = etxtShopAddress.getText().toString().trim();
                String shop_currency = etxtShopCurrency.getText().toString().trim();

                if (shop_name.isEmpty()) {
                    etxtShopName.setError(getString(R.string.shop_name_cannot_be_empty));
                    etxtShopName.requestFocus();
                } else if (shop_contact.isEmpty()) {
                    etxtShopContact.setError(getString(R.string.shop_contact_cannot_be_empty));
                    etxtShopContact.requestFocus();
                } else if (shop_email.isEmpty() || !shop_email.contains("@") || !shop_email.contains(".")) {
                    etxtShopEmail.setError(getString(R.string.enter_valid_email));
                    etxtShopEmail.requestFocus();
                } else if (shop_address.isEmpty()) {
                    etxtShopAddress.setError(getString(R.string.shop_address_cannot_be_empty));
                    etxtShopAddress.requestFocus();
                } else if (shop_currency.isEmpty()) {
                    etxtShopCurrency.setError(getString(R.string.shop_currency_cannot_be_empty));
                    etxtShopCurrency.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ShopInformationActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateShopInformation(shop_name, shop_contact, shop_email, shop_address, shop_currency,latitude,longitude);

                    if (check) {
                        Toasty.success(ShopInformationActivity.this, R.string.shop_information_updated_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ShopInformationActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(ShopInformationActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }

            }

        });

        fieldSelector = new PlacesFieldSelector();

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // [END maps_current_place_on_create_

        // [START_EXCLUDE silent]
        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        placesClient = Places.createClient(ShopInformationActivity.this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ShopInformationActivity.this);

        // Build the map.
        // [START maps_current_place_map_fragment]
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //MapView mapView = (MapView) rootView.findViewById(R.id.map);

        mapFragment.getMapAsync(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setCountries("UG");
        autocompleteFragment.setHint(getString(R.string.searchShippingAddress));
        // ((Button)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button)).setTextColor(getResources().getColor(R.color.colorWhite));
        ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.white));
        ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(10.5f);
        ImageView searchIcon = (ImageView)((LinearLayout) autocompleteFragment.getView()).getChildAt(0);
        ImageView cancelIcon = (ImageView)((LinearLayout) autocompleteFragment.getView()).getChildAt(2);
        cancelIcon.setColorFilter(getResources().getColor(R.color.white));
        searchIcon.setColorFilter(getResources().getColor(R.color.white));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.w(TAG, "Place: " + place.getName() + ", " +place.getAddress() + ", " + place.getId());
                LatLng selectedLocation= place.getLatLng();
                latitude = selectedLocation.latitude+"";
                longitude = selectedLocation.longitude+"";
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(selectedLocation.latitude,
                                selectedLocation.longitude), DEFAULT_ZOOM));

                etxtShopAddress.setText(place.getAddress()+" "+ place.getName());

            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);

            }
        });



    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    // [END maps_current_place_on_save_instance_state]


    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                cameraPosition= map.getCameraPosition();
                Log.w("Camera postion change" + "", cameraPosition + "");


                //mGoogleMap.clear();
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(ShopInformationActivity.this, Locale.getDefault());
                if( map!=null){
                    mCenterLatLong = cameraPosition.target;
                    latitude = mCenterLatLong.latitude+"";
                    longitude = mCenterLatLong.longitude+"";
                }

                try {
                    if(mCenterLatLong!=null)
                        addresses = geocoder.getFromLocation(mCenterLatLong.latitude, mCenterLatLong.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }

                final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                final String city = addresses.get(0).getLocality();
                final String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                //final String streetname = addresses.get(0).getThoroughfare()+", "+addresses.get(0).getSubThoroughfare();
                etxtShopAddress.setText( city+", "+ address);

            }
        });


        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        if(mCenterLatLong==null){
            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }else {
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(mCenterLatLong, DEFAULT_ZOOM));
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                String lat = data.getStringExtra("lat");
                String lon = data.getStringExtra("lon");
                latitude = lat;
                longitude = lon;
            } else {
                Log.e("ShippingAddress", "Error in data fetching");
                // Toast.makeText(getActivity(),   "Error in data fetching", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener( ShopInformationActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission( ShopInformationActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(ShopInformationActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [END maps_current_place_location_permission]

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    // [END maps_current_place_on_request_permissions_result]

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(ShopInformationActivity.this, connectionResult.getErrorMessage() + "", Toast.LENGTH_SHORT).show();
    }


}
