package sg.edu.np.mad.pocketchef.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.Date;

@Entity
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String productName;
    public int quantity;
    public String productIcon;
    public String addedTime;

    public String owner;

    // Getters and setters
}