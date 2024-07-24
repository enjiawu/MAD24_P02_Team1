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

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Listener.CommentOnHoldListener;
import sg.edu.np.mad.pocketchef.Models.Comment;
import sg.edu.np.mad.pocketchef.R;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsViewHolder> {
    Context context;
    List<Comment> comments = new ArrayList<>();
    CommentOnHoldListener commentOnHoldListener;
    public PostCommentsAdapter(Context context, List<Comment> comments, CommentOnHoldListener commentOnHoldListener) {
        this.context = context;
        this.comments = comments;
        this.commentOnHoldListener = commentOnHoldListener;
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
            // Setting all the comment information
            holder.username.setText("@" + comment.getUsername());
            holder.datePosted.setText(comment.formatDate());
            holder.comment.setText(comment.getComment());

            // Load profile picture
            if (comment.getUserProfilePicture() != null) {
                Picasso.get().load(comment.getUserProfilePicture()).into(holder.profilePicture);
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