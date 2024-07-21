package sg.edu.np.mad.pocketchef.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.PostClickListener;
import sg.edu.np.mad.pocketchef.Listener.PostLikeClickListener;
import sg.edu.np.mad.pocketchef.Models.Post;
import sg.edu.np.mad.pocketchef.Models.SearchedRecipe;
import sg.edu.np.mad.pocketchef.R;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityViewHolder>{
    Context context;
    List<Post> posts; // List to store posts
    PostClickListener postListener; //Listener for when they click on the post
    PostLikeClickListener likesListener;
    private DatabaseReference postRef;

    public CommunityAdapter(Context context, List<Post> posts, PostClickListener postListener, PostLikeClickListener likesListener) {
        this.context = context;
        this.posts = posts;
        this.postListener = postListener;
        this.likesListener = likesListener;
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

        if (post.getComments() != null) {
            holder.comments_text.setText(String.valueOf(post.getComments().size()));
        } else {
            holder.comments_text.setText("0"); // or some other default value
        }


        holder.dateUpdated.setText(String.valueOf(post.formatDate()));
        holder.likes_text.setText(String.valueOf(post.getLikes()));

        holder.username.setText("@" + post.getUsername());

        // Load post image
        Picasso.get().load(post.getRecipeImage()).into(holder.imageView_food);

        // Load profile picture
        if (post.getProfilePicture() != null) {
            Picasso.get().load(post.getProfilePicture()).into(holder.profilePicture);
        } else {
            holder.profilePicture.setImageResource(R.drawable.pocketchef_logo);
        }

        //Check if user clicks on post, if they click then return the Id
        holder.post_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postListener.onPostClicked(String.valueOf(posts.get(holder.getAdapterPosition()).getPostKey()));
            }
        });

        holder.likesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likesListener.onLikeClicked(String.valueOf(posts.get(position).getPostKey()), position);
            }
        });
    }
    public void updatePost(Post post, int position) {
        if (position != -1) {
            posts.set(position, post);
        }
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
    ImageView imageView_food, likesImage, profilePicture;
    LinearLayout likesLayout;


    // Assigning xml elements to variables in adapter
    public CommunityViewHolder(@NonNull View itemView) {
        super(itemView);
        post_container = itemView.findViewById(R.id.post_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        username = itemView.findViewById(R.id.username);
        dateUpdated = itemView.findViewById(R.id.dateUpdated);
        likes_text = itemView.findViewById(R.id.likes_text);
        comments_text = itemView.findViewById(R.id.comments_text);
        imageView_food = itemView.findViewById(R.id.imageView_food);
        likesLayout = itemView.findViewById(R.id.post_likes);
        likesImage = itemView.findViewById(R.id.likes_image);
        profilePicture = itemView.findViewById(R.id.profile_picture);
    }
}
