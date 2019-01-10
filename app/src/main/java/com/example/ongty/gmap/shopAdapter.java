package com.example.ongty.gmap;

import java.util.List;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ongty.gmap.models.item;
import com.example.ongty.gmap.models.shoppingList;

public class shopAdapter extends RecyclerView.Adapter<shopAdapter.ViewHolder> {
    private List<shoppingList> values;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout root;
        public TextView txtTitle;
        public TextView txtCat;
        public TextView txtPrice;
        public TextView txtLocation;

        public View layout;

        public ViewHolder(View v) {
            super(v);
//            layout = v;
//            txtColumn1 = v.findViewById(R.id.itemName);
//            txtColumn2 = v.findViewById(R.id.itemPrice);
//            txtColumn3 = v.findViewById(R.id.locationName);
            root = itemView.findViewById(R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtCat = itemView.findViewById(R.id.list_category);
            txtPrice = itemView.findViewById(R.id.list_price);
            txtLocation = itemView.findViewById(R.id.list_location);
        }
    }
    /**
     * @param position : position of List
     * @param itemList : item name of the position
     * */
    public void add(int position, shoppingList itemList) {
        values.add(position, itemList);
        notifyItemInserted(position);
    }
    /**
     * @param position : remove List according to position
     * */
    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    /** Provide a suitable constructor (depends on the kind of dataset) */
    public shopAdapter(List<shoppingList> myDataset) {
        values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public shopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item, parent, false);
        /** set the view's size, margins, paddings and layout parameters */
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final shoppingList items = values.get(position);
        holder.txtTitle.setText(items.getName());
        holder.txtTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });

        // get Price
        holder.txtCat.setText(items.getCategory());
        holder.txtPrice.setText(String.format("RM %.2f", items.getPrice()));
        // get place name
        holder.txtLocation.setText(items.getItemPlace().getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}