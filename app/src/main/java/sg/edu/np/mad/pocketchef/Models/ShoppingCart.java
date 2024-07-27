package sg.edu.np.mad.pocketchef.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import java.util.List;

@Entity
public class ShoppingCart {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String imagurl;
    public String user;
    public String items;
    public String createTime;

}
