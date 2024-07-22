package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.R;

public class PostInfoAdapter extends RecyclerView.Adapter<PostInfoViewHolder> {
    Context context;
    List<String> info = new ArrayList<>();
    String type;

    public PostInfoAdapter(Context context, List<String> info, String type) {
        this.context = context;
        this.info = info;
        this.type = type;
    }

    @NonNull
    @Override
    public PostInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostInfoViewHolder(LayoutInflater.from(context).inflate(R.layout.list_post_info, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostInfoViewHolder holder, int position) {
        String infoText = String.valueOf(info.get(position));
        holder.info.setText(infoText); // Set the text of the TextView

        if (type.equals("instructions")){
            holder.numberCount.setText("Step " + (position + 1) + ":");
        }
        else{
            holder.numberCount.setText(String.valueOf(position + 1));
        }
    }

    public int getItemCount() {
        return info.size();
    }
}

class PostInfoViewHolder extends RecyclerView.ViewHolder {
    TextView info, numberCount;
    public PostInfoViewHolder(View itemView) {
        super(itemView);
        info = itemView.findViewById(R.id.textView_ingredients_name);
        numberCount = itemView.findViewById(R.id.number_count);
    }
}