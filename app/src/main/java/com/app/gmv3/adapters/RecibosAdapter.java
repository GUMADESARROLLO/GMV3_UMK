package com.app.gmv3.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.gmv3.activities.ActivityImageDetail;
import com.app.gmv3.models.ItemRecibosAttach;
import com.app.gmv3.utilities.ImageTransformation;
import com.squareup.picasso.Picasso;
import com.app.gmv3.R;

import java.io.File;
import java.util.ArrayList;


public class RecibosAdapter extends RecyclerView.Adapter<RecibosAdapter.AttachmentListViewHolder> {
    public ArrayList<ItemRecibosAttach> newAttachmentList;
    public Activity mActivity;


    public RecibosAdapter(ArrayList<ItemRecibosAttach> list, Activity activity) {
        newAttachmentList = list;
        mActivity = activity;
    }

    @Override
    public AttachmentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attach_thumblaine, parent, false);

        AttachmentListViewHolder holder = new AttachmentListViewHolder(view, mActivity, newAttachmentList);

        return holder;
    }

    @Override
    public void onBindViewHolder(final AttachmentListViewHolder holder, int position) {
        holder.attachedImageName.setText((newAttachmentList.get(position).getImageName()));

        holder.cancelAttachment.setVisibility((newAttachmentList.get(position).ismDelete()) ? View.VISIBLE: View.GONE);

        final String userImage = newAttachmentList.get(position).getImageID();

        Log.e("TAG_error", "onBindViewHolder: " + userImage );

        if (userImage.isEmpty()||userImage.equals(null)||userImage.equals("")) {

        } else {


            Picasso.with(mActivity).load(userImage)
                    .transform(ImageTransformation.getTransformation(holder.attachedImageId))
                    .error(R.drawable.logo_round)
                    .placeholder(R.drawable.logo_round)
                    .into(holder.attachedImageId);

        }

    }

    @Override
    public int getItemCount() {
        return newAttachmentList.size();
    }

    class AttachmentListViewHolder extends RecyclerView.ViewHolder {

        ImageView attachedImageId;
        TextView attachedImageName;
        ImageView cancelAttachment;



        public AttachmentListViewHolder(View view, final Activity activity, final ArrayList<ItemRecibosAttach> attachmentList) {
            super(view);
            attachedImageId     = view.findViewById(R.id.attachedImageId);
            attachedImageName   = view.findViewById(R.id.attachedImageName);
            cancelAttachment    = view.findViewById(R.id.cancelAttachment);

            cancelAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    attachmentList.remove(pos);
                    notifyDataSetChanged();
                }
            });
        }
    }
}