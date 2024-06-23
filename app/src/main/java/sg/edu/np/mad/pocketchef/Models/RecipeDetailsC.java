package sg.edu.np.mad.pocketchef.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecipeDetailsC {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public  String meal_price;
    public String meal_servings;
    public String meal_ready;
    public String imagPath;
    public int recipeDetailsResponseId;
    public String categoryBeanId;
    public String meal_name;

}
