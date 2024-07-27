package sg.edu.np.mad.pocketchef.Models;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class IngredientsRecipesResponse {
    public int id;
    public String image;
    public int likes;
    public ArrayList<Ingredient> missedIngredients;
    public ArrayList<Ingredient> usedIngredients;
    public String title;
}
