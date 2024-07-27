package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;
import java.util.List;

import sg.edu.np.mad.pocketchef.Models.Step;
import sg.edu.np.mad.pocketchef.R;

public class InstructionStepAdapter extends RecyclerView.Adapter<InstructionStepViewHolder> {
    final Context context;
    final List<Step> list;

    public InstructionStepAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionStepViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_steps, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionStepViewHolder holder, int position) {
        Step step = list.get(position);

        holder.textView_instructions_step_number.setText(MessageFormat.format("Step {0}", step.number));
        // Format the step text with new lines and bullet points
        holder.textView_instructions_step_title.setText(step.formatStepText());

        // Ingredients RecyclerView
        holder.recycler_instruction_ingredients.setHasFixedSize(true);
        holder.recycler_instruction_ingredients.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        InstructionsIngredientsAdapter instructionsIngredientsAdapter = new InstructionsIngredientsAdapter(context, step.ingredients);
        holder.recycler_instruction_ingredients.setAdapter(instructionsIngredientsAdapter);

        // Equipment RecyclerView
        holder.recycler_instruction_equipments.setHasFixedSize(true);
        holder.recycler_instruction_equipments.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        InstructionsEquipmentsAdapter instructionsEquipmentsAdapter = new InstructionsEquipmentsAdapter(context, step.equipment);
        holder.recycler_instruction_equipments.setAdapter(instructionsEquipmentsAdapter);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

class InstructionStepViewHolder extends RecyclerView.ViewHolder {
    final TextView textView_instructions_step_number;
    final TextView textView_instructions_step_title;
    final RecyclerView recycler_instruction_equipments;
    final RecyclerView recycler_instruction_ingredients;

    public InstructionStepViewHolder(@NonNull View itemView) {
        super(itemView);
        textView_instructions_step_number = itemView.findViewById(R.id.textView_instructions_step_number);
        textView_instructions_step_title = itemView.findViewById(R.id.textView_instructions_step_title);
        recycler_instruction_equipments = itemView.findViewById(R.id.recycler_instruction_equipments);
        recycler_instruction_ingredients = itemView.findViewById(R.id.recycler_instruction_ingredients);
    }
}
