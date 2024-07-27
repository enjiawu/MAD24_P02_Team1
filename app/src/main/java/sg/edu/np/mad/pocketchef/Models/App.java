package sg.edu.np.mad.pocketchef.Models;

import android.app.Application;

import com.kongzue.dialogx.DialogX;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class App extends Application {

    public static String user="wy";

    @Override
    public void onCreate() {
        super.onCreate();
        DialogX.init(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
