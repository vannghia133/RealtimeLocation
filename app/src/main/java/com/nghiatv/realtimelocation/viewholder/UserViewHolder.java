package com.nghiatv.realtimelocation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nghiatv.realtimelocation.IRecyclerItemClickListener;
import com.nghiatv.realtimelocation.R;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtUserEmail;
    IRecyclerItemClickListener iRecyclerItemClickListener;

    public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
        this.iRecyclerItemClickListener = iRecyclerItemClickListener;
    }

    public UserViewHolder(View itemView) {
        super(itemView);
        txtUserEmail = (TextView) itemView.findViewById(R.id.txtUserEmail);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        iRecyclerItemClickListener.onItemClickListener(view, getAdapterPosition());
    }
}
