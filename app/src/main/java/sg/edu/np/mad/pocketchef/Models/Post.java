package sg.edu.np.mad.pocketchef.Models;

import com.google.firebase.database.ServerValue;

import java.util.List;

public class Post {
    private String postKey;
    private String title;
    private String recipeImage;
    private float protein;
    private float fat;
    private float calories;
    private float servings;
    private float prepTime;
    private float costPerServing;
    private List<String> instructions;
    private List<String> ingredients;
    private List<String> equipment;
    private String userId;
    private String username;
    private String userPhoto;
    private Object timeStamp;
    private List<Comment> comments;
    private int likes;

    public Post(String title, String recipeImage, float protein, float fat, float calories, float servings, float prepTime, float costPerServing, List<String> instructions, List<String> ingredients, List<String> equipment, String userId, String username, String userPhoto, List<Comment> comments, int likes) {
        this.title = title;
        this.recipeImage = recipeImage;
        this.protein = protein;
        this.fat = fat;
        this.calories = calories;
        this.servings = servings;
        this.prepTime = prepTime;
        this.costPerServing = costPerServing;
        this.instructions = instructions;
        this.ingredients = ingredients;
        this.equipment = equipment;
        this.userId = userId;
        this.username = username;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.comments = comments;
        this.likes = 0;
    }

    public Post() {
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getServings() {
        return servings;
    }

    public void setServings(float servings) {
        this.servings = servings;
    }

    public float getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(float prepTime) {
        this.prepTime = prepTime;
    }

    public float getCostPerServing() {
        return costPerServing;
    }

    public void setCostPerServing(float costPerServing) {
        this.costPerServing = costPerServing;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment;
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

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
