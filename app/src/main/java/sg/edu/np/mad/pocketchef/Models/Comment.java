package sg.edu.np.mad.pocketchef.Models;

import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Comment {
    private String commentId, comment, userId, username, userProfilePicture;
    private Object date;

    public Comment(String comment, String userId, String username, String userProfilePicture) {
        this.commentId = commentId;
        this.comment = comment;
        this.userId = userId;
        this.username = username;
        this.userProfilePicture = userProfilePicture;
        this.date = ServerValue.TIMESTAMP;
    }

    public Comment() {
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public String formatDate() {
        long timeStampSeconds = (Long) date / 1000; // Convert milliseconds to seconds
        Date date = new Date(timeStampSeconds * 1000); // Create Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }
}
