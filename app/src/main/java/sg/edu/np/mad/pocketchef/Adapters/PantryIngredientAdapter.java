package sg.edu.np.mad.pocketchef.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.pocketchef.R;

public class PantryIngredientAdapter extends RecyclerView.Adapter<PantryIngredientViewHolder>{
    ArrayList<String> data;

    public PantryIngredientAdapter(ArrayList<String> input) {
        data = input;
    }

    public PantryIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ingredient = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pantry, parent, false);
        return new PantryIngredientViewHolder(ingredient);
    }

    public void onBindViewHolder(PantryIngredientViewHolder holder, int position) {
        String s = data.get(position);
        holder.ingredientCheckbox.setText(s);
    }

    public int getItemCount() {
        return data.size();
    }
}
