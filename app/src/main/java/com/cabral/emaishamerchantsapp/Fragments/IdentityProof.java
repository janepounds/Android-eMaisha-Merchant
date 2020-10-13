package com.cabral.emaishamerchantsapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.wallet.CardDetail;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.io.ByteArrayOutputStream;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static android.app.Activity.RESULT_OK;

public class IdentityProof extends Fragment {
    String[] descriptionData = {"Personal\n Details", "Contact\n Details", "Identity\n Proof"};
    String mediaPathNationalID, encodedImageID = "N/A", mediaPathCustomerPhoto, encodedImageCustomerPhoto = "N/A", mediaPathPhotoWithID, encodedImagePhotoWithID = "N/A";
    EditText etxt_national_id, etxt_customer_photo, etxt_photo_with_id;
    String firstname, lastname, middlename, gender, date_of_birth, district, village, sub_county, landmark, phone_number, email, next_of_kin_name, next_of_kin_second_name, next_of_kin_relationship, next_of_kin_contact;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firstname = getArguments().getString("firstname");
        lastname = getArguments().getString("lastname");
        middlename = getArguments().getString("middlename");
        gender = getArguments().getString("customer_gender");
        date_of_birth = getArguments().getString("date_of_birth");
        district = getArguments().getString("district");
        sub_county = getArguments().getString("sub_county");
        village = getArguments().getString("village");
        landmark = getArguments().getString("landmark");
        phone_number = getArguments().getString("phone_number");
        next_of_kin_name = getArguments().getString("next_of_kin_name");
        next_of_kin_second_name = getArguments().getString("next_of_kin_second_name");
        next_of_kin_relationship = getArguments().getString("next_of_kin_relationship");
        next_of_kin_contact = getArguments().getString("next_of_kin_contact");
        return inflater.inflate(R.layout.identity_proof, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StateProgressBar stateProgressBar = view.findViewById(R.id.your_state_progress_bar_identity_proof);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-Bold.ttf");

        Button next = view.findViewById(R.id.txt_next_submit);
        TextView finger_print = view.findViewById(R.id.txt_next_finger_print);
        TextView txt_upload_national_id = view.findViewById(R.id.txt_browse_national_id_photo);
        TextView txt_upload_customer_photo = view.findViewById(R.id.txt_browse_customer_photo);
        TextView txt_upload_photo_with_id = view.findViewById(R.id.txt_browse_photo_with_id);


        EditText etxt_nin = view.findViewById(R.id.etxt_nin);
        EditText etxt_nin_expiry_date = view.findViewById(R.id.etxt_id_expiry_date);
        etxt_national_id = view.findViewById(R.id.etxt_national_id);
        etxt_customer_photo = view.findViewById(R.id.etxt_customer_photo);
        etxt_photo_with_id = view.findViewById(R.id.etxt_photo_with_id);

        txt_upload_national_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1231);
            }
        });
        txt_upload_customer_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1232);
            }
        });

        txt_upload_photo_with_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1233);
            }
        });


        finger_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new FingerprintAuthentication());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nin = etxt_nin.getText().toString().trim();
                String valid_upto = etxt_nin_expiry_date.getText().toString().trim();

                Log.d("Kin Name",next_of_kin_name);
                Log.d("Kin Second Name",next_of_kin_second_name);
                Log.d("Kin Relationship",next_of_kin_relationship);
                Log.d("Kin Contact",next_of_kin_contact);

                Intent intent = new Intent(getActivity(), CardDetail.class);
                intent.putExtra("firstname", firstname);
                intent.putExtra("lastname", lastname);
                intent.putExtra("middlename", middlename);
                intent.putExtra("date_of_birth", date_of_birth);
                intent.putExtra("gender", gender);
                intent.putExtra("district", district);
                intent.putExtra("sub_county", sub_county);
                intent.putExtra("village", village);
                intent.putExtra("landmark", landmark);
                intent.putExtra("phone_number", phone_number);
                intent.putExtra("email", email);
                intent.putExtra("next_of_kin_name", next_of_kin_name);
                intent.putExtra("next_of_kin_second_name", next_of_kin_second_name);
                intent.putExtra("next_of_kin_relationship", next_of_kin_relationship);
                intent.putExtra("next_of_kin_contact", next_of_kin_contact);
                intent.putExtra("nin", nin);
                intent.putExtra("national_id_valid_upto", valid_upto);
                intent.putExtra("national_id_photo", encodedImageID);
                intent.putExtra("customer_photo", encodedImageCustomerPhoto);
                intent.putExtra("customer_photo_with_id", encodedImagePhotoWithID);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (resultCode == RESULT_OK && null != data) {
                if (requestCode == 1231) {


                    mediaPathNationalID = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                    Bitmap selectedImage = BitmapFactory.decodeFile(mediaPathNationalID);
                    encodedImageID = encodeImage(selectedImage);
                    etxt_national_id.setText(mediaPathNationalID);


                }
                if (requestCode == 1232) {


                    mediaPathCustomerPhoto = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                    Bitmap selectedImage = BitmapFactory.decodeFile(mediaPathCustomerPhoto);
                    encodedImageCustomerPhoto = encodeImage(selectedImage);
                    etxt_customer_photo.setText(mediaPathCustomerPhoto);


                }
                if (requestCode == 1233) {


                    mediaPathPhotoWithID = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                    Bitmap selectedImage = BitmapFactory.decodeFile(mediaPathPhotoWithID);
                    encodedImagePhotoWithID = encodeImage(selectedImage);
                    etxt_photo_with_id.setText(mediaPathPhotoWithID);


                }

            }


        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }

    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.open_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
