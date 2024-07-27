package sg.edu.np.mad.pocketchef.Listener;


import sg.edu.np.mad.pocketchef.Models.SearchedRecipeQueryApiResponse;

//For Searched Query Recipes API response

public interface SearchRecipeQueryListener {
    void didFetch(SearchedRecipeQueryApiResponse response, String message);

    void didError(String message);
}
