package sg.edu.np.mad.pocketchef.Listener;


import sg.edu.np.mad.pocketchef.Models.SearchedRecipeApiResponse;

//For Searched Recipes API response

public interface SearchRecipeListener {
    void didFetch(SearchedRecipeApiResponse response, String message);

    void didError(String message);
}
