package sg.edu.np.mad.pocketchef.Dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import sg.edu.np.mad.pocketchef.Models.HomeShopItem;

@Dao
public interface HomeShopItemDao {
    @Insert
    void insert(HomeShopItem homeShopItem);

    @Query("SELECT * FROM HomeShopItem")
    List<HomeShopItem> getAll();

    @Delete
    void delete(HomeShopItem homeShopItem);

    // 其他数据库操作方法...
}