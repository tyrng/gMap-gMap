package com.example.ongty.gmap;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

class ViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    public TextView txtTitle;
    public TextView txtCat;
    public TextView txtPrice;
    public TextView txtLocation;

    public ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        txtTitle = itemView.findViewById(R.id.list_title);
        txtCat = itemView.findViewById(R.id.list_category);
        txtPrice = itemView.findViewById(R.id.list_price);
        txtLocation = itemView.findViewById(R.id.list_location);
    }

    public void setTxtTitle(String string){txtTitle.setText(string);}

    public void setTxtCat(String string) { txtCat.setText(string); }

    public void setTxtPrice(String string) {txtPrice.setText(string);}

    public void setTxtLocation(String string) {txtLocation.setText(string);}
}
