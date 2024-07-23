package sg.edu.np.mad.pocketchef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

import sg.edu.np.mad.pocketchef.Adapters.PantryIngredientAdapter;

public class PantryActivity extends AppCompatActivity {

    RecyclerView pantryRecyclerView;
    ExtendedFloatingActionButton availableRecipesButton;

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

        FindViews();
        SetUpListeners();

        ArrayList<String> ingredientList = new ArrayList<>();
        ingredientList.add("Salt");
        ingredientList.add("Sugar");
        ingredientList.add("Butter");
        ingredientList.add("Honey");

        PantryIngredientAdapter pantryAdapter = new PantryIngredientAdapter(ingredientList);

        LinearLayoutManager pantryLayoutManager = new LinearLayoutManager(this);

        pantryRecyclerView.setLayoutManager(pantryLayoutManager);
        pantryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pantryRecyclerView.setAdapter(pantryAdapter);
    }

    private void FindViews() {
        pantryRecyclerView = findViewById(R.id.pantryRecyclerView);
        availableRecipesButton = findViewById(R.id.availableRecipesButton);
    }

    private void SetUpListeners() {
        availableRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent availableRecipes = new Intent(PantryActivity.this, PantryRecipesActivity.class);
                startActivity(availableRecipes);
            }
        });
    }
}