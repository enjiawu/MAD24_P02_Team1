package sg.edu.np.mad.pocketchef.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HomeShopItem {
    @PrimaryKey
    public int id;
    public String name;
    public Double amount;
    public String unit;
    public String image;
}
