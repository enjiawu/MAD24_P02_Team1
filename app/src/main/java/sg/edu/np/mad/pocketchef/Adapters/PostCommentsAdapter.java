package sg.edu.np.mad.pocketchef.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.pocketchef.Models.Comment;
import sg.edu.np.mad.pocketchef.R;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsViewHolder> {
    Context context;
    List<Comment> comments = new ArrayList<>();

    public PostCommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public PostCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostCommentsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_post_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentsViewHolder holder, int position) {
        Comment comment = comments.get(position);

        // Setting all the comment information
        holder.username.setText(comment.getUsername());
        holder.datePosted.setText(comment.formatDate());
        holder.comment.setText(comment.getComment());

        // Load profile picture
        if (comment.getUserProfilePicture() != null) {
            Picasso.get().load(comment.getUserProfilePicture()).into(holder.profilePicture);
        } else {
            holder.profilePicture.setImageResource(R.drawable.pocketchef_logo);
        }
    }

    public int getItemCount() {
        return comments.size();
    }
}

class PostCommentsViewHolder extends RecyclerView.ViewHolder {
    TextView username, datePosted, comment;
    ImageView profilePicture;
    public PostCommentsViewHolder(View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
        datePosted = itemView.findViewById(R.id.datePosted);
        comment = itemView.findViewById(R.id.comments_text);
        profilePicture = itemView.findViewById(R.id.profile_picture);
    }
}