package sg.edu.np.mad.pocketchef.Listener;

import java.util.ArrayList;

import sg.edu.np.mad.pocketchef.Models.IngredientsRecipesResponse;

public interface IngredientsRecipesListener {
    void didFetch(ArrayList<IngredientsRecipesResponse> response, String message);

    void didError(String message);
}
