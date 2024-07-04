package sg.edu.np.mad.pocketchef.Listener;

import sg.edu.np.mad.pocketchef.Models.RandomRecipeApiResponse;

public interface RdmRecipeRespListener {
    void didFetch(RandomRecipeApiResponse response, String message);

    void didError(String message);
}
