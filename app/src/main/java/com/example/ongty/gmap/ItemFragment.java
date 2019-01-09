package com.example.ongty.gmap;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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
    private DatabaseReference database;
    private List<place> placeList;
    public ItemFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.item_fragment, container, false);

        /** Instantialize firebase */
        FirebaseApp.initializeApp(getContext());
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        final DatabaseReference database = data.getReference();

        Button button = view.findViewById(R.id.submitBtn);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AutoCompleteTextView itemLocationNameTxt = view.findViewById(R.id.locationName);
                EditText itemNameTxt = view.findViewById(R.id.itemName);
                Spinner itemCategorySpin = view.findViewById(R.id.itemCategory);
                EditText itemPriceTxt = view.findViewById(R.id.itemPrice);

                // TODO: GET PICTURE FROM INPUT

                // TODO: VALIDATION

                place placeEntered;
                item newItem;

                if(selectedLatitude != null && selectedLongitude != null){
                    placeEntered = new place(itemLocationNameTxt.getText().toString(), selectedLatitude, selectedLongitude);
                    database.child("places").child(UUID.randomUUID().toString()).setValue(placeEntered);
                    newItem = new item(itemNameTxt.getText().toString(), itemCategorySpin.getSelectedItem().toString(), Double.valueOf(itemPriceTxt.getText().toString()), placeEntered);
                    database.child("items").child(UUID.randomUUID().toString()).setValue(newItem);
                } else {
                    for(place p : placeList){
                        if (p.getName().equals(itemLocationNameTxt.getText().toString())){
                            placeEntered = p;
                            newItem = new item(itemNameTxt.getText().toString(), itemCategorySpin.getSelectedItem().toString(), Double.valueOf(itemPriceTxt.getText().toString()), placeEntered);
                            database.child("items").child(UUID.randomUUID().toString()).setValue(newItem);
                            break;
                        }
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        placeList = new ArrayList<>();
        try {
            selectedLatitude = getArguments().getDouble("latitude");
            selectedLongitude = getArguments().getDouble("longitude");
        } catch (NullPointerException e){

        }
        /** Instantialize firebase */
        FirebaseApp.initializeApp(getContext());
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference database = data.getReference();

        if(selectedLatitude == null && selectedLongitude == null) {
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
                    if(!b) {
                        // on focus off
                        String str = ACTV.getText().toString();

                        ListAdapter listAdapter = ACTV.getAdapter();
                        for(int i = 0; i < listAdapter.getCount(); i++) {
                            String temp = listAdapter.getItem(i).toString();
                            if(str.compareTo(temp) == 0) {
                                return;
                            }
                        }

                        ACTV.setText("");

                    }
                }
            });
        }
    }

    public void onButtonPressed(Uri uri){
        if(mListener != null)
            mListener.onFragmentInteraction(uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener)
            mListener = (OnFragmentInteractionListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
