package com.insitu.survscribe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.survscribe.R;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {
    private List<String> members;

    public MembersAdapter(List<String> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_item_layout, parent, false); // Use your member_item_layout.xml here
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String memberName = members.get(position);
        holder.memberTextView.setText(memberName);

        holder.removeButton.setOnClickListener(v -> {
            members.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberTextView;
        ImageButton removeButton;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberTextView = itemView.findViewById(R.id.memberName); // IDs from your member_item_layout.xml
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
