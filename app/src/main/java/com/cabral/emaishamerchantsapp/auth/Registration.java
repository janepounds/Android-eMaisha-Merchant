package com.cabral.emaishamerchantsapp.auth;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cabral.emaishamerchantsapp.HomeActivity;
import com.cabral.emaishamerchantsapp.Maps.PlacesFieldSelector;
import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.network.RetrofitClient;
import com.cabral.emaishamerchantsapp.storage.SharedPrefManager;
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
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private TextView txtRegister;
    EditText etxtShopName, etxtShopContact, etxtShopEmail, etxtShopAddress, etxtShopCurrency, etxtPassword, etxtConfirmPassword;

    TextView proceed_checkout_btn;
    int PLACE_PICKER_REQUEST = 1463;
    String latitude, longitude;
    private static final String TAG = "Maps Error";
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
    private LatLng mCenterLatLong= new LatLng(0.347596, 32.582520);//kampala
    PlacesFieldSelector fieldSelector;

    //Custom Dialog Vies
    Dialog dialogOTP;
    EditText ed_otp;
    //It is the verification id that will be sent to the user
    private String mVerificationId;
    //firebase auth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setTitle("Merchant Registration");

        etxtShopName = findViewById(R.id.etxt_shop_name);
        etxtShopContact = findViewById(R.id.etxt_shop_contact);
        etxtShopEmail = findViewById(R.id.etxt_shop_email);
        etxtShopAddress = findViewById(R.id.etxt_shop_address);
        etxtShopCurrency = findViewById(R.id.etxt_shop_currency);
        etxtPassword = findViewById(R.id.etxt_shop_password);
        etxtConfirmPassword = findViewById(R.id.etxt_shop_confirm_password);

        txtRegister = findViewById(R.id.txt_shop_register);

        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etxtShopName.getText().toString().trim();
                String contact = etxtShopContact.getText().toString().trim();
                String email = etxtShopEmail.getText().toString().trim();
                String address = etxtShopAddress.getText().toString().trim();
                String currency = etxtShopCurrency.getText().toString().trim();
                String password = etxtPassword.getText().toString().trim();
                String comfirm_password = etxtConfirmPassword.getText().toString().trim();

                if (name.isEmpty()) {
                    etxtShopName.setError("Shop Name Required");
                    etxtShopName.requestFocus();
                    return;
                }
                if (contact.isEmpty()) {
                    etxtShopContact.setError("Contact is required");
                    etxtShopContact.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    etxtShopEmail.setError("Email is required");
                    etxtShopEmail.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    etxtShopAddress.setError("Shop Address is required");
                    etxtShopAddress.requestFocus();
                    return;
                }
                if (currency.isEmpty()) {
                    etxtShopAddress.setError("Shop Address is required");
                    etxtShopAddress.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    etxtPassword.setError("Password is required");
                    etxtPassword.requestFocus();
                    return;
                }
                if (!password.equals(comfirm_password)) {
                    etxtConfirmPassword.setError("The passwords are not the same");
                    etxtConfirmPassword.requestFocus();
                    return;
                }
                Log.d("Latitude", latitude);
                Log.d("Latitude", longitude);

                sendVerificationCode(getResources().getString(R.string.ugandan_code)+etxtShopContact.getText().toString().trim());


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
        placesClient = Places.createClient(Registration.this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Registration.this);

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
        ((EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.white));
        ((EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(10.5f);
        ImageView searchIcon = (ImageView) ((LinearLayout) autocompleteFragment.getView()).getChildAt(0);
        ImageView cancelIcon = (ImageView) ((LinearLayout) autocompleteFragment.getView()).getChildAt(2);
        cancelIcon.setColorFilter(getResources().getColor(R.color.white));
        searchIcon.setColorFilter(getResources().getColor(R.color.white));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.w(TAG, "Place: " + place.getName() + ", " + place.getAddress() + ", " + place.getId());
                LatLng selectedLocation = place.getLatLng();
                latitude = selectedLocation.latitude + "";
                longitude = selectedLocation.longitude + "";

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(selectedLocation.latitude,
                                selectedLocation.longitude), DEFAULT_ZOOM));

                etxtShopAddress.setText(place.getAddress() + " " + place.getName());

            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);

            }
        });

    }

    /// Custom dialog for OTP
    public void showOTPDialog(Activity activity, String msg) {
        dialogOTP = new Dialog(activity);
        dialogOTP.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOTP.setCancelable(false);
        dialogOTP.setContentView(R.layout.dialog_otp);

        ed_otp = dialogOTP.findViewById(R.id.ed_otp);
        AppCompatButton btn_resend, btn_submit;
        btn_resend = dialogOTP.findViewById(R.id.btn_resend);
        btn_submit = dialogOTP.findViewById(R.id.btn_submit);

        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(getResources().getString(R.string.ugandan_code)+etxtShopContact.getText().toString().trim());
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ed_otp.getText().toString().trim().isEmpty()) {
                    verifyVerificationCode(ed_otp.getText().toString().trim());
                }
            }
        });

        dialogOTP.show();
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        Log.w("Mobile fomart Errpr: ",mobile);
        showOTPDialog(Registration.this, "");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);

    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {

                ed_otp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Error : ", e.getMessage());
            dialogOTP.dismiss();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.w("Error", "  verification id  is :"+s);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    //*********** This method is invoked for every call on requestPermissions(Activity, String[], int) ********//

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            dialogOTP.dismiss();
                            String name = etxtShopName.getText().toString().trim();
                            String contact = etxtShopContact.getText().toString().trim();
                            String email = etxtShopEmail.getText().toString().trim();
                            String address = etxtShopAddress.getText().toString().trim();
                            String currency = etxtShopCurrency.getText().toString().trim();
                            String password = etxtPassword.getText().toString().trim();

                            registerShop(
                                    name,
                                    contact,
                                    email,
                                    address,
                                    currency,
                                    latitude,
                                    longitude,
                                    password
                            );

                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    // [END maps_current_place_on_save_instance_state]
    private void registerShop(String name, String contact, String email, String address, String currency, String latitude, String longitude, String password) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .registerShop(
                        name,
                        contact,
                        email,
                        address,
                        currency,
                        latitude,
                        longitude,
                        password
                );
        ProgressDialog progressDialog = new ProgressDialog(Registration.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please Wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    try {
                        String s = response.body().string();
                        Log.d("Response", s);
                        JSONObject jsonObject = new JSONObject(s);
                        SharedPrefManager.getInstance(Registration.this).saveShopId(jsonObject.getInt("shop_id"));
                        Toasty.success(Registration.this, "Shop Registered Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Registration.this, Login.class);
                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    progressDialog.dismiss();
                    try {
                        String s = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(s);
                        Toasty.error(Registration.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toasty.error(Registration.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                cameraPosition = map.getCameraPosition();
                Log.w("Camera postion change" + "", cameraPosition + "");


                //mGoogleMap.clear();
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(Registration.this, Locale.getDefault());
                if (map != null) {
                    mCenterLatLong = cameraPosition.target;
                    latitude = mCenterLatLong.latitude + "";
                    longitude = mCenterLatLong.longitude + "";
                }

                try {
                    if (mCenterLatLong != null)
                        addresses = geocoder.getFromLocation(mCenterLatLong.latitude, mCenterLatLong.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

                final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                final String city = addresses.get(0).getLocality();
                final String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                //final String streetname = addresses.get(0).getThoroughfare()+", "+addresses.get(0).getSubThoroughfare();
                etxtShopAddress.setText(city + ", " + address);

            }
        });


        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
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
                locationResult.addOnCompleteListener(Registration.this, new OnCompleteListener<Location>() {
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
        } catch (SecurityException e) {
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
        if (ContextCompat.checkSelfPermission(Registration.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(Registration.this,
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
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Registration.this, connectionResult.getErrorMessage() + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (SharedPrefManager.getInstance(this).isShopSynced()) {
            Intent intent = new Intent(Registration.this, HomeActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

}