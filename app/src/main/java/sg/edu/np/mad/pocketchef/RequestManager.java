package sg.edu.np.mad.pocketchef;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sg.edu.np.mad.pocketchef.Listener.RdmRecipeRespListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeDetailsListener;
import sg.edu.np.mad.pocketchef.Listener.SearchRecipeListener;
import sg.edu.np.mad.pocketchef.Models.RandomRecipeApiResponse;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsResponse;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipeApiResponse;

public class RequestManager {
    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    // Method to call API for random recipes
    public void getRandomRecipes(RdmRecipeRespListener listener, List<String> tags) {
        CallRandomRecipes callRandomRecipes = retrofit.create(CallRandomRecipes.class);
        Call<RandomRecipeApiResponse> call = callRandomRecipes.callRandomRecipe(context.getString(R.string.api_key), "10", tags);
        call.enqueue(new Callback<RandomRecipeApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<RandomRecipeApiResponse> call, @NonNull Response<RandomRecipeApiResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<RandomRecipeApiResponse> call, @NonNull Throwable throwable) {
                listener.didError(throwable.getMessage());
            }
        });
    }

    // Method to call API for recipe information
    public void getRecipeDetails(RecipeDetailsListener listener, int id) {
        CallRecipeDetails callRecipeDetails = retrofit.create(CallRecipeDetails.class);
        Call<RecipeDetailsResponse> call = callRecipeDetails.callRecipeDetails(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<RecipeDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeDetailsResponse> call, @NonNull Response<RecipeDetailsResponse> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<RecipeDetailsResponse> call, @NonNull Throwable throwable) {
                listener.didError(throwable.getMessage());
            }
        });
    }

    //Method to call API for searched recipes
    public void getSearchedRecipes(SearchRecipeListener listener, String query, String excludeIngredients, int minCarbs, int maxCarbs, int minProtein, int maxProtein, int minCalories, int maxCalories, String diet,  String intolereneces){

        /*
        HashMap<String, String> queryParams = new HashMap<>();

        //Making sure that only queries with user inputs get put into the API calling
        if (query != null && !query.isEmpty()){
            queryParams.put("query", query);
        }

        if (excludeIngredients != null && !excludeIngredients.isEmpty()){
            queryParams.put("query", query);
        }

        if (isValidNumber(minCarbs)){
            queryParams.put("query", query);
        }

        if (isValidNumber(maxCarbs)){
            queryParams.put("query", query);
        }

        if (isValidNumber(minProtein))
        {
            queryParams.put("query", query);
        }

        if (isValidNumber(maxProtein)){
            queryParams.put("query", query);
        }

        if (isValidNumber(minCalories)){
            queryParams.put("query", query);
        }

        if (isValidNumber(maxCalories)){
            queryParams.put("query", query);
        }

        if (diet != null && !diet.isEmpty() && diet!="None"){
            queryParams.put("query", query);
        }

        if (intolereneces != null && !intolereneces.isEmpty() && diet!="None"){
            queryParams.put("query", query);
        }*/

        CallSearchedRecipes callSearchedRecipes = retrofit.create(CallSearchedRecipes.class);
        Call<SearchedRecipeApiResponse> call = callSearchedRecipes.callSearchedRecipes(context.getString(R.string.api_key),
                query != null && !query.isEmpty() ? "query" : null,  // Include query if not empty
                excludeIngredients != null && !excludeIngredients.isEmpty() ? "excludeIngredients" : null,
                minCarbs != null ? "minCarbs" : null,  // Include minCarbs if not null
                maxCarbs != null ? "maxCarbs" : null,  // Include maxCarbs if not null

                );

    }
    private boolean isValidNumber(String num){ //To make sure that number inputs are numbers and not empty
        if (num == null || num.isEmpty()) {
            return false;
        }

        try {
            int intValue = Integer.parseInt(num);
            return intValue >= 0; // Check for non-negative
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method to GET random recipes from API
    private interface CallRandomRecipes {
        @GET("recipes/random")
        Call<RandomRecipeApiResponse> callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") List<String> tags
        );
    }

    // Method to GET recipe details from API, based on ID of recipe
    private interface CallRecipeDetails {
        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponse> callRecipeDetails(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }

    // Method to GET searched recipes from API, based on queries passed entered by user
    private interface CallSearchedRecipes {
        @GET("recipes/complexSearch")
        Call<SearchedRecipeApiResponse> callSearchedRecipes(
                @Query("apiKey") String apiKey,
                @Query("query") String apple
        );
    }
}

