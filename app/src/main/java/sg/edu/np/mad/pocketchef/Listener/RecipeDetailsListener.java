package sg.edu.np.mad.pocketchef.Listener;

import sg.edu.np.mad.pocketchef.Models.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponse response, String message);
    void didError(String message);
}
