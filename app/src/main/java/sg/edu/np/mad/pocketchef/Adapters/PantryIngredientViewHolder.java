package sg.edu.np.mad.pocketchef.Adapters;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.pocketchef.R;

public class PantryIngredientViewHolder extends RecyclerView.ViewHolder{
    CheckBox ingredientCheckbox;
    public PantryIngredientViewHolder(View ingredientView) {
        super(ingredientView);
        ingredientCheckbox = ingredientView.findViewById(R.id.ingredientCheckbox);

    }
}
