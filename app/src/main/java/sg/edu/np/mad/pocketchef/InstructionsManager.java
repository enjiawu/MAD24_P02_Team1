package sg.edu.np.mad.pocketchef;

import android.os.Handler;

import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.InstructionsListener;
import sg.edu.np.mad.pocketchef.Listener.RecipeDetailsListener;
import sg.edu.np.mad.pocketchef.Listener.SimilarRecipesListener;
import sg.edu.np.mad.pocketchef.Models.InstructionsResponse;

public class InstructionsManager {
    private final Handler handler = new Handler();
    private final int DELAY_BETWEEN_CALLS = 200; // Delay in milliseconds (200ms = 5 requests per second)

    public void processInstructions(List<InstructionsResponse> instructionsList, InstructionsListener listener) {
        // Execute the instructions with a delay
        handler.postDelayed(() -> listener.didFetch(instructionsList, "Success"), DELAY_BETWEEN_CALLS);
    }

    public void fetchRecipeDetailsWithDelay(RequestManager manager, RecipeDetailsListener listener, int id, long delay) {
        handler.postDelayed(() -> manager.getRecipeDetails(listener, id), delay);
    }

    public void fetchSimilarRecipesWithDelay(RequestManager manager, SimilarRecipesListener listener, int id, long delay) {
        handler.postDelayed(() -> manager.getSimilarRecipes(listener, id), delay);
    }

    public void fetchInstructionsWithDelay(RequestManager manager, InstructionsListener listener, int id, long delay) {
        handler.postDelayed(() -> manager.getInstructions(listener, id), delay);
    }
    public void QuerDataBase(RequestManager manager, InstructionsListener listener, int id, long delay){
        handler.postDelayed(() -> manager.getInstructions(listener, id), delay);
    }
}
