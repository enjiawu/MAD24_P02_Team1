package sg.edu.np.mad.pocketchef;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sg.edu.np.mad.pocketchef.Listener.InstructionsListener;
import sg.edu.np.mad.pocketchef.Listener.RdmRecipeRespListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeDetailsListener;
import sg.edu.np.mad.pocketchef.Listener.SimilarRecipesListener;
import sg.edu.np.mad.pocketchef.Models.InstructionsResponse;
import sg.edu.np.mad.pocketchef.Models.RandomRecipeApiResponse;
import sg.edu.np.mad.pocketchef.Models.RecipeDetailsResponse;
import sg.edu.np.mad.pocketchef.Models.SimilarRecipeResponse;

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

    // Method to call API for similar recipes
    public void getSimilarRecipes(SimilarRecipesListener listener, int id) {
        CallSimilarRecipes callSimilarRecipes = retrofit.create(CallSimilarRecipes.class);
        Call<List<SimilarRecipeResponse>> call = callSimilarRecipes.callSimilarRecipe(id, "4", context.getString(R.string.api_key));
        call.enqueue(new Callback<List<SimilarRecipeResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<SimilarRecipeResponse>> call, @NonNull Response<List<SimilarRecipeResponse>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }
            @Override
            public void onFailure(@NonNull Call<List<SimilarRecipeResponse>> call, @NonNull Throwable throwable) {
                listener.didError(throwable.getMessage());
            }
        });
    }

    // Method to call instructions
    public void getInstructions(InstructionsListener listener, int id) {
        CallInstructions callInstructions = retrofit.create(CallInstructions.class);
        Call<List<InstructionsResponse>> call = callInstructions.callInstructions(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<List<InstructionsResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<InstructionsResponse>> call, @NonNull Response<List<InstructionsResponse>> response) {
                if (!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(@NonNull Call<List<InstructionsResponse>> call, @NonNull Throwable throwable) {
                listener.didError(throwable.getMessage());
            }
        });
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

    // Method to GET similar recipes from API
    private interface CallSimilarRecipes {
        @GET("recipes/{id}/similar")
        Call<List<SimilarRecipeResponse>> callSimilarRecipe(
                @Path("id") int id,
                @Query("number") String number,
                @Query("apiKey") String apiKey
        );
    }

    // Method to GET Instructions from API
    private interface CallInstructions {
        @GET("recipes/{id}/analyzedInstructions")
        Call<List<InstructionsResponse>> callInstructions (
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }
}

