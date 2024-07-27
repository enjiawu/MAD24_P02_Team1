package sg.edu.np.mad.pocketchef.NearByPlaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private String googlePlacesData;
    private RecyclerView recyclerView;
    String url;
    Context context;


    @Override
    protected String doInBackground(Object... objects) {
        recyclerView = (RecyclerView) objects[0];
        url = (String) objects[1];
        context = (Context) objects[2];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
            Log.d("TestAPi", "doInBackground: " + url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TestAPi", "doInBackground: " + e.getLocalizedMessage().toString());
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlaceList;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        Log.d("nearbyplacesdata", "called parse method");
       // showNearbyPlaces(nearbyPlaceList);
        Toast.makeText(context, ""+nearbyPlaceList.size(), Toast.LENGTH_SHORT).show();
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList) {
        Log.d("TestAPi", "doInBackground: " + nearbyPlaceList.size());


        //add all marker one by one
        for (int i = 0; i < nearbyPlaceList.size(); i++) {

            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            //create marker
            LatLng latLng = new LatLng(lat, lng);



        }
    }


    //create icon that user want to show on nearest place
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
