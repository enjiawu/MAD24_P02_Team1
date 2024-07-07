package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.PostClickListener;
import sg.edu.np.mad.pocketchef.Models.Post;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipe;
import sg.edu.np.mad.pocketchef.R;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityViewHolder>{
    Context context;
    List<Post> posts; // List to store posts
    PostClickListener listener; //Listener for when they click on the post

    public CommunityAdapter(Context context, List<Post> posts, PostClickListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommunityViewHolder(LayoutInflater.from(context).inflate(R.layout.community_post, parent, false));
    }
    //Override method to input items from api to SearchedRecipesViewHolder
    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.textView_title.setText(post.getTitle());
        holder.textView_title.setSelected(true);

        if (post.getComments().isEmpty() || post.getComments() == null){
            holder.comments_text.setText(0);
        }
        else{
            holder.comments_text.setText(post.getComments().size());
        }

        holder.dateUpdated.setText(post.getTimeStamp().toString());
        holder.likes_text.setText(post.getLikes());

        // Load user profile picture
        Picasso.get().load(post.getUserPhoto()).into(holder.userProfilePicture);
        holder.username.setText(post.getUsername());

        // Load post image
        Picasso.get().load(post.getRecipeImage()).into(holder.imageView_food);

        //Check if user clicks on post, if they click then return the Id
        holder.post_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPostClicked(String.valueOf(posts.get(holder.getAdapterPosition()).getPostKey()));
            }
        });
    }
    @Override
    public int getItemCount() {
        return posts.size();
    }
}

// Preparing view holder
class CommunityViewHolder extends RecyclerView.ViewHolder {
    CardView post_container;
    TextView textView_title, username, dateUpdated, likes_text, comments_text;
    ImageView userProfilePicture, imageView_food;


    // Assigning xml elements to variables in adapter
    public CommunityViewHolder(@NonNull View itemView) {
        super(itemView);
        post_container = itemView.findViewById(R.id.post_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        username = itemView.findViewById(R.id.username);
        dateUpdated = itemView.findViewById(R.id.dateUpdated);
        likes_text = itemView.findViewById(R.id.likes_text);
        comments_text = itemView.findViewById(R.id.comments_text);
        userProfilePicture = itemView.findViewById(R.id.userProfilePicture);
        imageView_food = itemView.findViewById(R.id.imageView_food);
    }
}

