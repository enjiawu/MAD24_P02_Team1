package sg.edu.np.mad.pocketchef.Listener;

import java.util.List;

import sg.edu.np.mad.pocketchef.Models.IngredientsRecipesResponse;

public interface IngredientsRecipesListener {
    void didFetch(List<IngredientsRecipesResponse> response, String message);

    void didError(String message);
}
