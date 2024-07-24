package sg.edu.np.mad.pocketchef.Models;

import java.util.ArrayList;

public class Step{
    public int number;
    public String step;
    public ArrayList<Ingredient> ingredients;
    public ArrayList<Equipment> equipment;
    public Length length;

    public String formatStepText() {
        // Format step text here (this example assumes steps are separated by periods)
        return step.replace(". ", ".\n• ")
                .replace(".\n• ", ".\n• ") // Handles cases with multiple sentences
                .replace("\n• ", "\n• "); // Handles leading bullet points
    }
}
