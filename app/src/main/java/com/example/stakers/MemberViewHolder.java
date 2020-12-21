package com.example.stakers;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MemberViewHolder extends RecyclerView.ViewHolder {
    ImageView senderPicNoti;
    ImageButton acceptBtn, denyBtn;
    TextView timestampTextNoti, senderEmail;

    public MemberViewHolder(@NonNull View itemView) {
        super(itemView);

        senderPicNoti = itemView.findViewById(R.id.senderPicNoti);
        timestampTextNoti = itemView.findViewById(R.id.timestampTextNoti);
        senderEmail = itemView.findViewById(R.id.senderEmail);
        acceptBtn = itemView.findViewById(R.id.acceptBtn);
        denyBtn = itemView.findViewById(R.id.denyBtn);
    }
}
