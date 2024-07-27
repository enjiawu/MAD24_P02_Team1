package sg.edu.np.mad.pocketchef.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.CartItem;
import sg.edu.np.mad.pocketchef.Models.ShoppingCart;

@Dao
public interface ShoppingCartDao {

    @Insert
    void insert(ShoppingCart item);

    @Delete
    void delete(ShoppingCart item);

    @Query("SELECT * FROM ShoppingCart WHERE id = :id")
    ShoppingCart getAllShoppingCartsForId(int id);

    // 可以添加更多查询方法
    @Query("SELECT * FROM ShoppingCart WHERE user = :user")
    List<ShoppingCart> getAllShoppingCartsForUser(String user);

    @Update
    void update(ShoppingCart shoppingCart);
}
