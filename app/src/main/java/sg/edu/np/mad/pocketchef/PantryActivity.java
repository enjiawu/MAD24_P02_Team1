package sg.edu.np.mad.pocketchef;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.pocketchef.Adapters.PantryIngredientAdapter;

public class PantryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayList<String> ingredientList = new ArrayList<>();
        ingredientList.add("Salt");
        ingredientList.add("Sugar");
        ingredientList.add("Butter");
        ingredientList.add("Honey");

        RecyclerView pantryRecyclerView = findViewById(R.id.pantryRecyclerView);
        PantryIngredientAdapter pantryAdapter = new PantryIngredientAdapter(ingredientList);

        LinearLayoutManager pantryLayoutManager = new LinearLayoutManager(this);

        pantryRecyclerView.setLayoutManager(pantryLayoutManager);
        pantryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pantryRecyclerView.setAdapter(pantryAdapter);
    }
}