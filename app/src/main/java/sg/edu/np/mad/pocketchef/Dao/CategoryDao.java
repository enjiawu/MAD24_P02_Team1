package sg.edu.np.mad.pocketchef.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.CategoryBean;

// define the Data Access Object interface for CategoryBean
@Dao
public interface CategoryDao {
    // method to insert new category into the database
    @Insert
    void insertCategory(CategoryBean category);

    // method to update existing category in the database
    @Update
    void updateCategory(CategoryBean category);

    // method to retrieve all categories from database
    @Query("SELECT * FROM CategoryBean")
    List<CategoryBean> getAllCategories();

    // method to delete category from database
    @Delete
    void deleteCategory(CategoryBean category);


    @Query("UPDATE RecipeDetailsC SET categoryBeanId = :newText WHERE categoryBeanId = :oldText")
    void updateRecipeDetailsCategoryId(String oldText, String newText);

    @Transaction
    default void updateCategoryAndRelatedRecipeDetails(CategoryBean categoryBean, String oldText) {
        updateCategory(categoryBean);
        updateRecipeDetailsCategoryId(oldText, categoryBean.text);
    }
}