package com.example.ongty.gmap;

import java.util.List;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<String> values;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView txtColumn1;
        private TextView txtColumn2;
        private TextView txtColumn3;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtColumn1 = v.findViewById(R.id.itemName);
            txtColumn2 = v.findViewById(R.id.itemPrice);
            txtColumn3 = v.findViewById(R.id.locationName);
        }
    }
    /**
     * @param position : position of List
     * @param item     : item name of the position
     * */
    public void add(int position, String item) {
        values.add(position, item);
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
    public Adapter(List<String> myDataset) {
        values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.discover_item_box, parent, false);
        /** set the view's size, margins, paddings and layout parameters */
        //ViewHolder vh = new ViewHolder(v);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String name = values.get(position);
        holder.txtColumn1.setText(name);
        holder.txtColumn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });

        holder.txtColumn2.setText("Item  Name : " + name);
        holder.txtColumn3.setText("Price : " + name);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}