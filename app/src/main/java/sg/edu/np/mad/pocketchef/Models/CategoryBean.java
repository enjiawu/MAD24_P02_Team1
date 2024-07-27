package sg.edu.np.mad.pocketchef.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.checkerframework.common.aliasing.qual.Unique;

@Entity
public class CategoryBean {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String imagePath;
    @Unique
    public String text;

    public CategoryBean(String username,String imagePath, String text) {
        this.imagePath = imagePath;
        this.text = text;
    }
}
