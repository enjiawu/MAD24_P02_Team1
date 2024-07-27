package sg.edu.np.mad.pocketchef.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.pocketchef.PantryActivity;
import sg.edu.np.mad.pocketchef.R;

// Timothy - Stage 2
public class PantryIngredientAdapter extends RecyclerView.Adapter<PantryIngredientViewHolder>{
    PantryActivity pantryActivity;
    ArrayList<String> data;

    public PantryIngredientAdapter(ArrayList<String> input, PantryActivity activity) {
        data = input;
        pantryActivity = activity;
    }

    public PantryIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ingredient = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pantry, parent, false);
        return new PantryIngredientViewHolder(ingredient);
    }

    public void onBindViewHolder(PantryIngredientViewHolder holder, int position) {
        String s = data.get(position);
        holder.ingredientCheckbox.setText(s);
        holder.ingredientCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.d("CHECKED", (String) holder.ingredientCheckbox.getText());
                    pantryActivity.SelectIngredient((String) holder.ingredientCheckbox.getText());
                } else if (!isChecked) {
                    Log.d("UNCHECKED", (String) holder.ingredientCheckbox.getText());
                    pantryActivity.RemoveIngredient((String) holder.ingredientCheckbox.getText());
                }
            }
        });
    }

    public int getItemCount() {
        return data.size();
    }
}
