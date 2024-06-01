package sg.edu.np.mad.pocketchef.Models;

import android.app.Application;

import com.kongzue.dialogx.DialogX;

public class Favorite extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DialogX.init(this);
    }
}
