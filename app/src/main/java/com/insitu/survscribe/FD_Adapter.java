package com.insitu.survscribe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.survscribe.R;

import java.util.List;

public class FD_Adapter extends RecyclerView.Adapter<FD_Adapter.MyHorizontalViewHolder> {
    private List<FD_Data> dataList;
    private RowDataClickListener listener;
    public FD_Adapter(List<FD_Data> dataList, RowDataClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    class MyHorizontalViewHolder extends RecyclerView.ViewHolder {
        // ... Your TextViews:

        TextView subHeaderTextView;
        TextView timeOfSandPouringTextView;
        TextView timeStartedTextView;
        TextView timeFinishedTextView;
        TextView soilTypesTextView;
        TextView massOfDrySoilFromHoleTextView;
        TextView initialMassOfSandAndJarTextView;
        TextView finalMassOfSandAndJarTextView;
        TextView massOfSandFillTheHoleTextView;
        TextView remarksTextView;

        public MyHorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            // ... Initialize your TextViews here:
            subHeaderTextView = itemView.findViewById(R.id.subHeaderTextView);
            timeOfSandPouringTextView = itemView.findViewById(R.id.timeOfSandPouringTextView);
            timeStartedTextView = itemView.findViewById(R.id.timeStartedTextView);
            timeFinishedTextView = itemView.findViewById(R.id.timeFinishedTextView);
            soilTypesTextView = itemView.findViewById(R.id.soilTypesTextView);
            massOfDrySoilFromHoleTextView = itemView.findViewById(R.id.massDrySoilTextView);
            initialMassOfSandAndJarTextView = itemView.findViewById(R.id.initialMassTextView);
            finalMassOfSandAndJarTextView = itemView.findViewById(R.id.finalMassTextView);
            massOfSandFillTheHoleTextView = itemView.findViewById(R.id.massSandFillHoleTextView);
            remarksTextView = itemView.findViewById(R.id.remarksTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    FD_Data fdData = dataList.get(position);
                    if (position != 0 && listener != null &&
                            !(fdData.getTimeStarted().isEmpty() ||
                                    fdData.getTimeFinished().isEmpty() ||
                                    fdData.getSoilTypes().isEmpty() ||
                                    fdData.getMassOfDrySoilFromHole().isEmpty() ||
                                    fdData.getInitialMassOfSandAndJar().isEmpty() ||
                                    fdData.getFinalMassOfSandAndJar().isEmpty()
                                    )
                    ) {
                        listener.onRowDataClick(position, fdData);
                    }
                }
            });
        }
    }
    public interface ChangeListener {
        void onTableDataChanged();
    }

    private ChangeListener changeListener;

    public void setChangeListenerListener(ChangeListener listener) {
        this.changeListener = listener;
    }

    public void updateRow(int position, String timeOfSandPouring, String timeStarted, String timeFinished, String soilTypes,
                          String massOfDrySoil, String initialMass, String finalMass) {
        FD_Data fdDataToUpdate = dataList.get(position);
        fdDataToUpdate.setTimeOfSandPouring(timeOfSandPouring);
        fdDataToUpdate.setTimeStarted(timeStarted);
        fdDataToUpdate.setTimeFinished(timeFinished);
        fdDataToUpdate.setSoilTypes(soilTypes);
        fdDataToUpdate.setMassOfDrySoilFromHole(massOfDrySoil);
        fdDataToUpdate.setInitialMassOfSandAndJar(initialMass);
        fdDataToUpdate.setFinalMassOfSandAndJar(finalMass);
        fdDataToUpdate.setMassOfSandFillTheHole(String.valueOf(Double.parseDouble(initialMass) - Double.parseDouble(finalMass)));
        notifyItemChanged(position);

        if (changeListener != null) {
            changeListener.onTableDataChanged();
        }
    }
    public void updateRemarks(int position, String remarks) {
        FD_Data fdDataToUpdate = dataList.get(position);

        fdDataToUpdate.setRemarks(remarks);
        if (changeListener != null) {
            changeListener.onTableDataChanged();
        }
    }

    @NonNull
    @Override
    public MyHorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.fd_row_layout, parent, false);
        return new MyHorizontalViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHorizontalViewHolder holder, int position) {
        FD_Data dataItem = dataList.get(position);

        if(position > 0) {
            holder.remarksTextView.setText("");
            holder.subHeaderTextView.setText(String.valueOf(position));
        }
        if(position==0)
            holder.remarksTextView.setText("Remarks");
        else if(position == 1)
            holder.remarksTextView.setText(dataItem.getRemarks());
        else
            holder.remarksTextView.setVisibility(View.GONE);
        holder.timeOfSandPouringTextView.setText(dataItem.getTimeOfSandPouring());
        holder.timeStartedTextView.setText(dataItem.getTimeStarted());
        holder.timeFinishedTextView.setText(dataItem.getTimeFinished());
        holder.soilTypesTextView.setText(dataItem.getSoilTypes());
        holder.massOfDrySoilFromHoleTextView.setText(String.valueOf(dataItem.getMassOfDrySoilFromHole()));
        holder.initialMassOfSandAndJarTextView.setText(String.valueOf(dataItem.getInitialMassOfSandAndJar()));
        holder.finalMassOfSandAndJarTextView.setText(String.valueOf(dataItem.getFinalMassOfSandAndJar()));
        holder.massOfSandFillTheHoleTextView.setText(String.valueOf(dataItem.getMassOfSandFillTheHole()));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
