package sg.edu.np.mad.pocketchef;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import sg.edu.np.mad.pocketchef.Dao.CategoryDao;
import sg.edu.np.mad.pocketchef.Dao.HomeShopItemDao;
import sg.edu.np.mad.pocketchef.Dao.RecipeDetailsCDao;
import sg.edu.np.mad.pocketchef.Dao.ShoppingCartDao;
import sg.edu.np.mad.pocketchef.Models.CartItem;
import sg.edu.np.mad.pocketchef.Models.CategoryBean;
import sg.edu.np.mad.pocketchef.Models.HomeShopItem;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsC;
import sg.edu.np.mad.pocketchef.Models.ShoppingCart;

@Database(entities = {CategoryBean.class, RecipeDetailsC.class, CartItem.class,
        ShoppingCart.class, HomeShopItem.class}, version = 1)
public abstract class FavoriteDatabase extends RoomDatabase {
    // define abstract method for DAOs
    public abstract CategoryDao categoryDao();
    public abstract ShoppingCartDao shoppingCartDao();
    public abstract RecipeDetailsCDao RecipeDetailsCDao();
    public abstract HomeShopItemDao homeShopItemDao();

    // singleton instance of the database
    private static volatile FavoriteDatabase instance;

    // get instance of database (singleton pattern)
    public static FavoriteDatabase getInstance(Context context) {
        if (instance == null) {
            // synchronize to ensure thread safety
            synchronized (FavoriteDatabase.class) {
                if (instance == null) {
                    // build data instance
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(), //context
                            FavoriteDatabase.class, // database class
                            "Favorite.db" // database name
                    ).build();
                }
            }
        }
        return instance;
    }
}
