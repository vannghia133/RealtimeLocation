package com.nghiatv.realtimelocation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nghiatv.realtimelocation.R;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder  {
    public TextView txtUserEmail;
    public ImageView btnAccept, btnDecline;

    public FriendRequestViewHolder(View itemView) {
        super(itemView);
        txtUserEmail = (TextView) itemView.findViewById(R.id.txtUserEmail);
        btnAccept = (ImageView) itemView.findViewById(R.id.btnAccept);
        btnDecline = (ImageView) itemView.findViewById(R.id.btnDecline);
    }
}