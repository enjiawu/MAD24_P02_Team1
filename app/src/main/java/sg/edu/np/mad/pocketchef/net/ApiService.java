package sg.edu.np.mad.pocketchef.net;







import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import sg.edu.np.mad.pocketchef.Models.ProductResponse;

public interface ApiService {

        @GET("api/v0/product/{barcode}.json")
        Call<ProductResponse> getProductByBarcode(@Path("barcode") String barcode);
}
