package com.zybooks.weighttrackerproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DataGridAdapter extends RecyclerView.Adapter<DataGridAdapter.ViewHolder> {

    private final List<Weight> weightEntries;
    private final LayoutInflater inflater;

    // Constructor
    public DataGridAdapter(Context context, List<Weight> weightEntries) {
        this.inflater = LayoutInflater.from(context);
        this.weightEntries = weightEntries;
    }

    // Create a ViewHolder for each weight entry
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater, parent);
    }

    // Bind the weight entry to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull DataGridAdapter.ViewHolder holder, int position) {
        Weight weight = weightEntries.get(position);
        holder.bind(weight);
        // This tag will be used to identify the Weight object for each button for clicking and deletion
        holder.gridButton.setTag(position);
    }

    @Override
    public int getItemCount() {
        return weightEntries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final Button gridButton;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_grid_button, parent, false));
            gridButton = itemView.findViewById(R.id.grid_button);

            // Set a clickListener for the button
            gridButton.setOnClickListener((view) -> {
                // Get the Weight object from the entry's tag
                Weight weight = weightEntries.get((int) view.getTag());

                // Navigate to the EditWeight Activity and pass the weight + date as extras
                Intent intent = new Intent(view.getContext(), EditWeightActivity.class);

                intent.putExtra("weight", weight.getWeight());
                intent.putExtra("date", weight.getDate().toString());
                intent.putExtra("id", weight.getId());

                // Start the EditWeight Activity
                view.getContext().startActivity(intent);
            });
        }

        // Set the text of the button to the weight entry
        public void bind(Weight weight) {
            gridButton.setText(weight.toString());
        }
    }
}
