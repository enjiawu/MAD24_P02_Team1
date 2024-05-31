package sg.edu.np.mad.pocketchef.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.checkerframework.common.aliasing.qual.Unique;

@Entity
public class CategoryBean {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public CategoryBean(String imagePath, String text) {
        this.imagePath = imagePath;
        this.text = text;
    }

    public String imagePath;
    @Unique
    public String text;
}
