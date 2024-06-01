package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.Equipment;
import sg.edu.np.mad.pocketchef.R;

public class InstructionsEquipmentsAdapter extends RecyclerView.Adapter<InstructionsEquipmentsViewHolder> {
    final Context context;
    final List<Equipment> list;

    public InstructionsEquipmentsAdapter(Context context, List<Equipment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionsEquipmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionsEquipmentsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_step_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsEquipmentsViewHolder holder, int position) {
        if (list.isEmpty()) {
            holder.textView_instructions_step_item.setText("No Equipments");
            holder.imageView_instructions_step_items.setVisibility(View.GONE); // Optionally hide the ImageView
        } else {
            Equipment equipment = list.get(position);
            holder.textView_instructions_step_item.setText(equipment.name);
            holder.textView_instructions_step_item.setSelected(true);
            // Check if the image URL is not null
            if (equipment.image != null && !equipment.image.isEmpty()) {
                holder.imageView_instructions_step_items.setVisibility(View.VISIBLE); // Ensure ImageView is visible
                Picasso.get().load(equipment.image)
                        .fit()
                        .centerCrop()
                        .into(holder.imageView_instructions_step_items);
            } else {
                holder.imageView_instructions_step_items.setVisibility(View.GONE); // Hide ImageView if image URL is null
                // Optionally, set a placeholder image if you want to show something instead of hiding
                // holder.imageView_instructions_step_items.setImageResource(R.drawable.placeholder_image); as an example
            }
        }
    }

    @Override
    public int getItemCount() {
        return Math.max(1, list.size());
    }
}

class InstructionsEquipmentsViewHolder extends RecyclerView.ViewHolder {
    final ImageView imageView_instructions_step_items;
    final TextView textView_instructions_step_item;
    public InstructionsEquipmentsViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView_instructions_step_items = itemView.findViewById(R.id.imageView_instructions_step_items);
        textView_instructions_step_item = itemView.findViewById(R.id.textView_instructions_step_item);
    }
}
