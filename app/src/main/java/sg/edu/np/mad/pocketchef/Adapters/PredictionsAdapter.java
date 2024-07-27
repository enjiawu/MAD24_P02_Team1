package sg.edu.np.mad.pocketchef.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

import sg.edu.np.mad.pocketchef.MapActivity;
import sg.edu.np.mad.pocketchef.R;


public class PredictionsAdapter extends RecyclerView.Adapter<PredictionsAdapter.PredictionViewHolder> {
    private List<Place> placesList;
    Context context;

    public PredictionsAdapter(List<Place> placesList, Context context) {
        this.placesList = placesList;
        this.context = context;
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place, parent, false);
        return new PredictionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Place place = placesList.get(position);
        holder.placeNameTv.setText(place.getName());
        holder.placeAddressTv.setText(place.getAddress());
        holder.directionIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapActivity.class);

                intent.putExtra("latitude", placesList.get(position).getLatLng().latitude);
                intent.putExtra("longitude", placesList.get(position).getLatLng().longitude);
                intent.putExtra("Name", placesList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public void updatePredictions(List<Place> newPredictions) {
        placesList.clear();
        placesList.addAll(newPredictions);
        notifyDataSetChanged();
    }

    class PredictionViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTv;
        TextView placeAddressTv;
        ImageView directionIv;


        PredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            placeNameTv = itemView.findViewById(R.id.placeNameTv);
            placeAddressTv = itemView.findViewById(R.id.placeAddressTv);
            directionIv = itemView.findViewById(R.id.directionIv);
        }
    }
}
