package com.example.ongty.gmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ongty.gmap.models.item;
import com.example.ongty.gmap.models.place;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class DiscoverFragment extends Fragment {
    //TODO add fragment function
    private OnFragmentInteractionListener mListener;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;

    /** Constructor */
    public DiscoverFragment() {}

    /** Create a fragment view */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.discover_fragment, container, false);

        /** initialize */
        setItemScroller(view);

        return view;
    }

    public void onButtonPressed(Uri uri){
        if(mListener != null)
            mListener.onFragmentInteraction(uri);
    }

    /** Fragment Listener*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener)
            mListener = (OnFragmentInteractionListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }

    /** stop fragment when detached */
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

    // if else for all onclick event
    private List<item> onClickEvent(List<item> itemList, String cat){
        List<item> filteredList = new ArrayList<>();
        for(item i : itemList){
            if(i.getCategory().equals("Frozen") && cat.equals("Frozen")){
                filteredList.add(i);
            }else if(i.getCategory().equals("Dairy") && cat.equals("Dairy")){
                filteredList.add(i);
            }else if(i.getCategory().equals("Grain") && cat.equals("Grain")){
                filteredList.add(i);
            }else if(i.getCategory().equals("Canned") && cat.equals("Canned")){
                filteredList.add(i);
            }else if(i.getCategory().equals("Fresh") && cat.equals("Fresh")){
                filteredList.add(i);
            }else if(i.getCategory().equals("Dry") && cat.equals("Dry")){
                filteredList.add(i);
            }
        }
        return filteredList;
    }

    private void setItemScroller(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.recycle_view_list);
//        final RecyclerView.Adapter mAdapter;

        /** use this setting to
         improve performance if you know that changes
         in content do not change the layout size
         of the RecyclerView */
        recyclerView.setHasFixedSize(false);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        /** instantiate 100 lists */
        /** repalce thsi with firebase */

        /** Instantialize firebase */
        FirebaseApp.initializeApp(getContext());
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference database = data.getReference();

        /**List to extract from firebase*/
        final List<item> itemList = new ArrayList<>();
        //Child the root before all the push() keys are found and add a ValueEventListener()
        database.child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Basically, this says "For each DataSnapshot *Data* in dataSnapshot, do what's inside the method.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Get the suggestion by childing the key of the string you want to get.
                    item i = snapshot.getValue(item.class);
                    itemList.add(i);
                }
                // define an adapter
                RecyclerView.Adapter mAdapter = new Adapter(itemList);
                recyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**Filter list*/
        /** get button action from discover_fragment*/
        ImageButton frozenBtn = view.findViewById(R.id.frozenBtn);
        ImageButton dairyBtn = view.findViewById(R.id.dairyBtn);
        ImageButton grainBtn = view.findViewById(R.id.grainBtn);
        ImageButton cannedBtn = view.findViewById(R.id.cannedBtn);
        ImageButton freshBtn = view.findViewById(R.id.freshBtn);
        ImageButton dryBtn = view.findViewById(R.id.dryBtn);

        frozenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.Adapter mAdapter = new Adapter(onClickEvent(itemList, "Frozen"));
                recyclerView.setAdapter(mAdapter);
            }
        });
        dairyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.Adapter mAdapter = new Adapter(onClickEvent(itemList, "Dairy"));
                recyclerView.setAdapter(mAdapter);
            }
        });

        grainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.Adapter mAdapter = new Adapter(onClickEvent(itemList, "Grain"));
                recyclerView.setAdapter(mAdapter);
            }
        });

        cannedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.Adapter mAdapter = new Adapter(onClickEvent(itemList, "Canned"));
                recyclerView.setAdapter(mAdapter);
            }
        });

        freshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.Adapter mAdapter = new Adapter(onClickEvent(itemList, "Fresh"));
                recyclerView.setAdapter(mAdapter);
            }
        });

        dryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.Adapter mAdapter = new Adapter(onClickEvent(itemList, "Dry"));
                recyclerView.setAdapter(mAdapter);
            }
        });



/** ------------------------------------------------------------------------------------------------------------------------------------- */
        /** put this after your definition of your recyclerview
         input in your data mode in this example a java.util.List, adjust if necessary
         adapter is your adapter */
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                private boolean swipeBack = false;

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                    RecyclerView.Adapter mAdapter = new Adapter(itemList);
                    if (swipeDir == ItemTouchHelper.RIGHT) {
                        //add into shopping listss

                        Log.d("Select", "Selected");
                        mAdapter.onDetachedFromRecyclerView(recyclerView);
                    } else if (swipeDir == ItemTouchHelper.LEFT) {
                        itemList.remove(viewHolder.getAdapterPosition());
                        mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        swipeBack = true;
                    }
                }

                /** Swipe directly to left */
                @Override
                public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                    if (swipeBack) {
                        swipeBack = false;
                        return 0;
                    }
                    return super.convertToAbsoluteDirection(flags, layoutDirection);
                }

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                        float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    if (actionState == ACTION_STATE_SWIPE) {
                        recyclerView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                                return false;
                            }
                        });
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };
/** ------------------------------------------------------------------------------------------------------------------------------------- */
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // if drag to refresh, disable the animation
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // detach and attach fragment
                        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(fragment).attach(fragment).commit();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }
}