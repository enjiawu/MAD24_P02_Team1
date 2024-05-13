package sg.edu.np.mad.pocketchef.Listener;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.SimilarRecipeResponse;

public interface SimilarRecipesListener {
    void didFetch(List<SimilarRecipeResponse> response, String message);
    void didError(String message);
}
