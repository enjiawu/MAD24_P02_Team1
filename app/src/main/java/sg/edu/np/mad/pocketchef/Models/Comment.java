package sg.edu.np.mad.pocketchef.Models;

public class Comment {
    private String commentId;
    private String comment;
    private String userId;
    private String username;
    private String userProfilePicture;

    public Comment(String commentId, String comment, String userId, String username, String userProfilePicture) {
        this.commentId = commentId;
        this.comment = comment;
        this.userId = userId;
        this.username = username;
        this.userProfilePicture = userProfilePicture;
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

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }
}
