package com.example.stakers;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
                CircularImageView senderPicNoti;
                TextView timestampTextNoti, senderEmailNoti, notifyTypeTV;
                ImageButton denyBtn, acceptBtn;
                Button toGroupBtn;
                View view;

                public NotificationViewHolder(@NonNull View itemView) {
                                super(itemView);

                                notifyTypeTV = itemView.findViewById(R.id.notifyTypeTV);
                                senderPicNoti = itemView.findViewById(R.id.senderPicNoti);
                                timestampTextNoti = itemView.findViewById(R.id.timestampTextNoti);
                                senderEmailNoti = itemView.findViewById(R.id.senderEmail);
                                acceptBtn = itemView.findViewById(R.id.acceptBtn);
                                denyBtn = itemView.findViewById(R.id.denyBtn);
                                toGroupBtn = itemView.findViewById(R.id.toGroupBtn);
                                view = itemView;
                }
}
