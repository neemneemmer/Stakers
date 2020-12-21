package com.example.stakers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

public class GroupViewHolder extends RecyclerView.ViewHolder {
                ImageView image_single_view, isMaster, isMember, isPending;
                TextView groupNameText, groupSizeText, groupMemText, groupTypeText, groupPriceText, groupOwnerText, timestampText, isFull;
                CircularImageView userIV;
                View view;

                public GroupViewHolder(@NonNull View itemView) {
                                super(itemView);

                                isMaster = itemView.findViewById(R.id.isMaster);
                                isMember = itemView.findViewById(R.id.isMember);
                                isPending = itemView.findViewById(R.id.isPending);
                                isFull = itemView.findViewById(R.id.isFull);
                                isMaster.setVisibility(View.GONE);
                                isMember.setVisibility(View.GONE);
                                isPending.setVisibility(View.GONE);
                                isFull.setVisibility(View.GONE);

                                image_single_view = itemView.findViewById(R.id.image_single_view);
                                groupNameText = itemView.findViewById(R.id.groupNameText);
                                groupSizeText = itemView.findViewById(R.id.groupSizeText);
                                groupMemText = itemView.findViewById(R.id.groupMemText);
                                groupTypeText = itemView.findViewById(R.id.groupTypeText);
                                groupPriceText = itemView.findViewById(R.id.groupPriceText);
                                userIV = itemView.findViewById(R.id.userIV);
                                groupOwnerText = itemView.findViewById(R.id.groupOwnerText);
                                timestampText = itemView.findViewById(R.id.timestampText);
                                view = itemView;
                }
}
