package sg.edu.np.mad.pocketchef.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.RecipeDetailsC;

@Dao
public interface RecipeDetailsCDao {
    @Insert
    void insert(RecipeDetailsC recipeDetailsC);


    @Update
    void update(RecipeDetailsC recipeDetailsC);

    @Delete
    void delete(RecipeDetailsC recipeDetailsC);

    @Query("DELETE FROM RecipeDetailsC WHERE categoryBeanId = :categoryBeanId")
    void deleteByCategoryBeanId(String categoryBeanId);

    @Query("SELECT * FROM RecipeDetailsC WHERE recipeDetailsResponseId = :responseId")
    RecipeDetailsC getByRecipeDetailsResponseId(int responseId);
    @Query("SELECT * FROM RecipeDetailsC WHERE categoryBeanId = :categoryBeanId")
    List<RecipeDetailsC> getByCategoryBeanId(String categoryBeanId);

    @Query("SELECT * FROM RecipeDetailsC")
    List<RecipeDetailsC> getAll();
}