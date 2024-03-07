package com.insitu.survscribe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.survscribe.R;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.List;

public class SP_Adapter extends RecyclerView.Adapter<SP_Adapter.RowViewHolder> {
    private List<SP_Data> SPDataList;
    private RowDataClickListener listener;
    private static final int HEADER_TYPE = 0;
    private static final int DATA_TYPE = 1;


    public SP_Adapter(List<SP_Data> SPDataList, RowDataClickListener listener) {
        this.SPDataList = SPDataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SP_Adapter.RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_row_layout, parent, false); // Use your header_layout.xml
        return new RowViewHolder(view, parent.getContext());
    }


    @Override
    public void onBindViewHolder(@NonNull SP_Adapter.RowViewHolder holder, int position) {
        SP_Data SPData = SPDataList.get(position);
        holder.depthOfRefusalTextView.setText(SPData.getDepthOfRefusal());
        holder.nValuesTextView.setText(SPData.getnValues());

        if (SPData.isHeader) {
            holder.depthOfRefusalTextView.setTypeface(null, Typeface.BOLD);
            holder.nValuesTextView.setTypeface(null, Typeface.BOLD);
            holder.imageHeaderTextview.setTypeface(null, Typeface.BOLD);
            holder.imageHeaderTextview.setText(SPData.getImagePath());
            holder.imageHeaderTextview.setVisibility(View.VISIBLE);
            holder.soilImage.setVisibility(View.GONE);
        } else {
            holder.imageHeaderTextview.setVisibility(View.GONE);
            holder.soilImage.setVisibility(View.VISIBLE);
            if (SPData.getImagePath() != null && !SPData.getImagePath().isEmpty()) {
                Picasso.get()
                        .load(new File(SPData.getImagePath()))
                        .placeholder(R.drawable.loading_image)
                        .error(R.drawable.file_not_found)
                        .fit()
                        .into(holder.soilImage);
            } else {
                holder.soilImage.setImageResource(R.drawable.add_image_icon);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (SPDataList.get(position).isHeader) {
            return HEADER_TYPE;
        } else {
            return DATA_TYPE;
        }
    }

    public interface ChangeListener {
        void onTableDataChanged();
    }

    private ChangeListener changeListener;
    public void updateRow(int position, String depthOfRefusal, String nValues) {
        SP_Data SPDataToUpdate = SPDataList.get(position);
        SPDataToUpdate.setDepthOfRefusal(depthOfRefusal);
        SPDataToUpdate.setnValues(nValues);
        notifyItemChanged(position);

        if (changeListener != null) {
            changeListener.onTableDataChanged();
        }
    }

    @Override
    public int getItemCount() {
        return SPDataList.size();
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        TextView depthOfRefusalTextView;
        TextView nValuesTextView;
        ImageView soilImage;
        TextView imageHeaderTextview;

        public RowViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            depthOfRefusalTextView = itemView.findViewById(R.id.depthOfRefusalTextView);
            nValuesTextView = itemView.findViewById(R.id.nValuesTextView);
            soilImage = itemView.findViewById(R.id.soilImage);
            imageHeaderTextview = itemView.findViewById(R.id.imageHeaderTextView);
            this.context = context;
            setupClickListener(depthOfRefusalTextView);
            setupClickListener(nValuesTextView);

            soilImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    ((sp_test_layout) itemView.getContext()).clickedImagePosition = position; // Store
                    promptImageSelection();
                }
            });
        }
        private void promptImageSelection() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            ((Activity) itemView.getContext()).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }



        private void setupClickListener(TextView textView) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    SP_Data spData = SPDataList.get(position);
                    if (position != 0 || !spData.getnValues().isEmpty() || !spData.getDepthOfRefusal().isEmpty()) {
                        if (listener != null) {
                            listener.onRowDataClick(position, spData);
                        }
                    }
                }
            });
        }
        private static final int PICK_IMAGE_REQUEST = 1;



    }
}
