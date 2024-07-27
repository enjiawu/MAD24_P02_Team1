package sg.edu.np.mad.pocketchef.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

import sg.edu.np.mad.pocketchef.R;

public class SupermarketAdapter extends RecyclerView.Adapter<SupermarketAdapter.SupermarketViewHolder> {
    private List<Place> supermarketList;

    public SupermarketAdapter(List<Place> supermarketList) {
        this.supermarketList = supermarketList;
    }

    @NonNull
    @Override
    public SupermarketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.supermarket_item, parent, false);
        return new SupermarketViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SupermarketViewHolder holder, int position) {
        Place supermarket = supermarketList.get(position);
        holder.nameTextView.setText(supermarket.getName());
        holder.addressTextView.setText(supermarket.getAddress());
    }

    @Override
    public int getItemCount() {
        return supermarketList.size();
    }

    static class SupermarketViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;

        public SupermarketViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            addressTextView = itemView.findViewById(R.id.address);
        }
    }
}
