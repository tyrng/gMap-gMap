package com.example.ongty.gmap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;

import com.example.ongty.gmap.models.item;
import com.example.ongty.gmap.models.place;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View view;
    private Double selectedLatitude;
    private Double selectedLongitude;
    private String selectedAddress;
    private String uploadedImage;
    private List<place> placeList;

    public ItemFragment() {
    }

    AutoCompleteTextView itemLocationNameTxt;
    EditText itemNameTxt;
    Spinner itemCategorySpin;
    EditText itemPriceTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.item_fragment, container, false);

        /** Instantialize firebase */
        FirebaseApp.initializeApp(getContext());
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        final DatabaseReference database = data.getReference();

        /** Submit Button OnClick */
        Button button = view.findViewById(R.id.submitBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemLocationNameTxt = view.findViewById(R.id.locationName);
                itemNameTxt = view.findViewById(R.id.itemName);
                itemCategorySpin = view.findViewById(R.id.itemCategory);
                itemPriceTxt = view.findViewById(R.id.itemPrice);

                // TODO: GET PICTURE FROM INPUT

                // TODO: VALIDATION

                place placeEntered;
                item newItem;

                if (selectedLatitude != null && selectedLongitude != null) {
                    placeEntered = new place(itemLocationNameTxt.getText().toString(), selectedLatitude, selectedLongitude, selectedAddress);
                    database.child("places").push().setValue(placeEntered);
                    if (uploadedImage != null) {
                        newItem = new item(itemNameTxt.getText().toString(), itemCategorySpin.getSelectedItem().toString(), Double.valueOf(itemPriceTxt.getText().toString()), placeEntered, uploadedImage);
                        uploadedImage = null;
                    } else {
                        newItem = new item(itemNameTxt.getText().toString(), itemCategorySpin.getSelectedItem().toString(), Double.valueOf(itemPriceTxt.getText().toString()), placeEntered);
                    }

                    database.child("items").push().setValue(newItem);
                } else {
                    for (place p : placeList) {
                        if (p.getName().equals(itemLocationNameTxt.getText().toString())) {
                            placeEntered = p;
                            newItem = new item(itemNameTxt.getText().toString(), itemCategorySpin.getSelectedItem().toString(), Double.valueOf(itemPriceTxt.getText().toString()), placeEntered);
                            database.child("items").push().setValue(newItem);
                            break;
                        }
                    }
                }
            }
        });
        Button resetButton = view.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemLocationNameTxt = view.findViewById(R.id.locationName);
                itemNameTxt = view.findViewById(R.id.itemName);
                itemCategorySpin = view.findViewById(R.id.itemCategory);
                itemPriceTxt = view.findViewById(R.id.itemPrice);
                itemLocationNameTxt.setText("");
                itemNameTxt.setText("");
                itemPriceTxt.setText("");
                itemCategorySpin.setSelection(0);
                ((ImageView)view.findViewById(R.id.ivPreview)).setImageResource(android.R.color.transparent);
            }
        });
        /** Image Upload Button Onclick*/
        Button uploadBtn = view.findViewById(R.id.uploadImage);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CAMERA}, 2);
                    } else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CAMERA}, 2);
                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        selectImage();
                        ;
                    }
                } catch (Exception e) {
                    Log.d("UPLOAD", e.toString());
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            selectedLatitude = getArguments().getDouble("latitude");
            selectedLongitude = getArguments().getDouble("longitude");
            selectedAddress = getArguments().getString("address");
            uploadedImage = getArguments().getString("image");
        } catch (NullPointerException e) {

        }
        placeList = new ArrayList<>();
        /** Instantialize firebase */
        FirebaseApp.initializeApp(getContext());
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference database = data.getReference();

        if (selectedLatitude == null && selectedLongitude == null) {
            //Create a new ArrayAdapter with your context and the simple layout for the dropdown menu provided by Android
            final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
            //Child the root before all the push() keys are found and add a ValueEventListener()
            database.child("places").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                    for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
                        //Get the suggestion by childing the key of the string you want to get.
                        place suggestion = suggestionSnapshot.getValue(place.class);
                        placeList.add(suggestion);
                        //Add the retrieved string to the list
                        autoComplete.add(suggestion.getName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            final AutoCompleteTextView ACTV = (AutoCompleteTextView) view.findViewById(R.id.locationName);
            ACTV.setAdapter(autoComplete);
            ACTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        // on focus off
                        String str = ACTV.getText().toString();

                        ListAdapter listAdapter = ACTV.getAdapter();
                        for (int i = 0; i < listAdapter.getCount(); i++) {
                            String temp = listAdapter.getItem(i).toString();
                            if (str.compareTo(temp) == 0) {
                                return;
                            }
                        }

                        ACTV.setText("");

                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            uploadedImage = getArguments().getString("image");
        } catch (NullPointerException e) {

        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null)
            mListener.onFragmentInteraction(uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
            mListener = (OnFragmentInteractionListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(intent, 2); // CAMERA
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    getActivity().startActivityForResult(Intent.createChooser(intent, "Select File"), 1); // GALLERY
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);

    }
}
