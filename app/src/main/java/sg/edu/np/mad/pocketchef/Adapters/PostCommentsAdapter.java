package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.CommentOnHoldListener;
import sg.edu.np.mad.pocketchef.Models.Comment;
import sg.edu.np.mad.pocketchef.R;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsViewHolder> {
    Context context;
    List<Comment> comments = new ArrayList<>();
    CommentOnHoldListener commentOnHoldListener;

    // To load user data for posts
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference mUserRef;
    StorageReference storageReference;
    FirebaseUser currentUser;

    public PostCommentsAdapter(Context context, List<Comment> comments, CommentOnHoldListener commentOnHoldListener) {
        this.context = context;
        this.comments = comments;
        this.commentOnHoldListener = commentOnHoldListener;

        // Set up database for post user data
        //Firebase database setup
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");
        database = FirebaseDatabase.getInstance("https://pocket-chef-cd59c-default-rtdb.asia-southeast1.firebasedatabase.app/");
        // Get current user
        currentUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public PostCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostCommentsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_post_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentsViewHolder holder, int position) {
        Comment comment = comments.get(position);

        if (comment != null) { // Must check if comment is null because we deleting  based on position from the database
            mUserRef.child(comment.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        String commentUsername = userSnapshot.child("username").getValue(String.class);
                        String Image = userSnapshot.child("Image").getValue(String.class);

                        // Setting all the comment information
                        holder.username.setText("@" + commentUsername);
                        holder.datePosted.setText(comment.formatDate());
                        holder.comment.setText(comment.getComment());

                        // Load profile picture
                        if (Image != null) {
                            Picasso.get().load(Image).into(holder.profilePicture);
                        } else {
                            holder.profilePicture.setImageResource(R.drawable.pocketchef_logo);
                        }

                        holder.commentContainer.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                commentOnHoldListener.onCommentHold(position);
                                return true;
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    Log.e("CommunityAdapter", "Error loading post details", error.toException());
                }
            });
        }
    }
    public void updateComments(List<Comment> comment){
        this.comments = comments;
    }

    public int getItemCount() {
        return comments.size();
    }
}

class PostCommentsViewHolder extends RecyclerView.ViewHolder {
    CardView commentContainer;
    TextView username, datePosted, comment;
    ImageView profilePicture;
    public PostCommentsViewHolder(View itemView) {
        super(itemView);
        commentContainer = itemView.findViewById(R.id.comment_container);
        username = itemView.findViewById(R.id.username);
        datePosted = itemView.findViewById(R.id.datePosted);
        comment = itemView.findViewById(R.id.comments_text);
        profilePicture = itemView.findViewById(R.id.profile_picture);
    }
}