package sg.edu.np.mad.pocketchef.base;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import sg.edu.np.mad.pocketchef.Dao.CategoryDao;
import sg.edu.np.mad.pocketchef.Dao.RecipeDetailsCDao;
import sg.edu.np.mad.pocketchef.Models.CategoryBean;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsC;

@Database(entities = {CategoryBean.class, RecipeDetailsC.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();
    public abstract RecipeDetailsCDao RecipeDetailsCDao();
    private static volatile AppDatabase instance;
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "app.db"
                    ).build();
                }
            }
        }
        return instance;
    }
}
