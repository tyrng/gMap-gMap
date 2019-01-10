package com.example.ongty.gmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
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

import com.example.ongty.gmap.models.item;
import com.example.ongty.gmap.models.shopping;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class ShoppingList extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ShoppingList() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.shopping_list, container, false);

        /** init */
        initShoppingList();

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

    private void initShoppingList() {
        final RecyclerView recyclerView = view.findViewById(R.id.recycle_shopping_list);
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
        final List<shopping> itemList = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Child the root before all the push() keys are found and add a ValueEventListener()
        database.child("shopping").orderByChild("userid").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Get the suggestion by childing the key of the string you want to get.
                    shopping i = snapshot.getValue(shopping.class);
                    itemList.add(i);
                }
                // define an adapter
                RecyclerView.Adapter mAdapter = new shopAdapter(itemList);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                                RecyclerView.Adapter mAdapter = new shopAdapter(itemList);
                                if (swipeDir == ItemTouchHelper.RIGHT) {
                                    //add into shopping listss


                                    Log.d("Select", "Selected");
                                    mAdapter.onDetachedFromRecyclerView(recyclerView);
                                } else if (swipeDir == ItemTouchHelper.LEFT) {
                                    itemList.remove(viewHolder.getAdapterPosition());
                                    mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                }
                            }

                            /**
                             * Swip directly to left
                             */
//                @Override
//                public int convertToAbsoluteDirection(int flags, int layoutDirection) {
//                    if (swipeBack) {
//                        swipeBack = false;
//                        return 0;
//                    }
//                    return super.convertToAbsoluteDirection(flags, layoutDirection);
//                }
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
            /** ------------------------------------------------------*/

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
