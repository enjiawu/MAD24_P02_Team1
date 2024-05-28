package sg.edu.np.mad.pocketchef.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.CategoryBean;

@Dao
public interface CategoryDao {
    @Insert
    void insertCategory(CategoryBean category);

    @Update
    void updateCategory(CategoryBean category);


    @Query("SELECT * FROM CategoryBean")
    List<CategoryBean> getAllCategories();

    @Delete
    void deleteCategory(CategoryBean category);
}