package com.insitu.survscribe;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.survscribe.R;

import java.util.List;

public class DCP_Adapter extends RecyclerView.Adapter<DCP_Adapter.RowViewHolder> {
    private List<DCP_RowData> DCPRowDataList;

    private static final int HEADER_TYPE = 0;
    private static final int DATA_TYPE = 1;
    private RowDataClickListener listener;

    public DCP_Adapter(List<DCP_RowData> DCPRowDataList, RowDataClickListener listener) {
        this.DCPRowDataList = DCPRowDataList;
        this.listener = listener;
    }
    public interface ChangeListener {
        void onTableDataChanged();
    }

    private ChangeListener changeListener;

    public void setChangeListenerListener(ChangeListener listener) {
        this.changeListener = listener;
    }

    public void updateRow(int position, String blows, String penetration) {
        DCP_RowData DCPRowDataToUpdate = DCPRowDataList.get(position);
        DCPRowDataToUpdate.setBlowsValue(blows);
        DCPRowDataToUpdate.setPenetrationValue(penetration);
        notifyItemChanged(position);

        if (changeListener != null) {
            changeListener.onTableDataChanged();
        }
    }
    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dcp_row_layout, parent, false); // Use your header_layout.xml
            return new RowViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dcp_row_layout, parent, false);
            return new RowViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        DCP_RowData DCPRowData = DCPRowDataList.get(position);
        holder.blowsTextView.setText(DCPRowData.getBlowsValue());
        holder.penetrationTextView.setText(DCPRowData.getPenetrationValue());
        holder.differenceTextView.setText(DCPRowData.getDifferenceValue());
        if (DCPRowData.isHeader) {
            holder.blowsTextView.setTypeface(null, Typeface.BOLD);
            holder.penetrationTextView.setTypeface(null, Typeface.BOLD);
            holder.differenceTextView.setTypeface(null, Typeface.BOLD);
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return DCPRowDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (DCPRowDataList.get(position).isHeader) {
            return HEADER_TYPE;
        } else {
            return DATA_TYPE;
        }
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {
        TextView blowsTextView;
        TextView penetrationTextView;
        TextView differenceTextView;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            blowsTextView = itemView.findViewById(R.id.blowsTextView);
            penetrationTextView = itemView.findViewById(R.id.penetrationTextView);
            differenceTextView = itemView.findViewById(R.id.differenceTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    DCP_RowData DCPRowData = DCPRowDataList.get(position);
                    if (position != 0 && (!DCPRowData.getBlowsValue().isEmpty() || !DCPRowData.getPenetrationValue().isEmpty())) {
                        if (listener != null) {
                            listener.onRowDataClick(position, DCPRowData);
                        }
                    }
                }
            });
        }
    }

}
