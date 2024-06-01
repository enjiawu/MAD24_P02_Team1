package sg.edu.np.mad.pocketchef.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.RecipeDetailsC;

// define the data access object interface for recipeDetailC
@Dao
public interface RecipeDetailsCDao {
    // method to insert new recipeDetailC object into database
    @Insert
    void insert(RecipeDetailsC recipeDetailsC);

    // method to update existing recipeDetailC object in database
    @Update
    void update(RecipeDetailsC recipeDetailsC);

    // method to delete recipeDetailC object from database
    @Delete
    void delete(RecipeDetailsC recipeDetailsC);

    // method to delete recipeDetailC associated with specific category
    @Query("DELETE FROM RecipeDetailsC WHERE categoryBeanId = :categoryBeanId")
    void deleteByCategoryBeanId(String categoryBeanId);

    // method to retrieve recipeDetailC object by ID
    @Query("SELECT * FROM RecipeDetailsC WHERE recipeDetailsResponseId = :responseId")
    RecipeDetailsC getByRecipeDetailsResponseId(int responseId);

    // method to retrieve list of recipeDetailC object by their associated category id
    @Query("SELECT * FROM RecipeDetailsC WHERE categoryBeanId = :categoryBeanId")
    List<RecipeDetailsC> getByCategoryBeanId(String categoryBeanId);

    // method to retrieve all recipeDetailC object from database
    @Query("SELECT * FROM RecipeDetailsC")
    List<RecipeDetailsC> getAll();
}