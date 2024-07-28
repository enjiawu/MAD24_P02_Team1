![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Pocketchef_logo.svg)
# MAD24_P02_Team1
MAD 2024 Assignment
## Students:
- S10258457C **Yeo Jin Rong**
- S10256978E **Wu Enjia**  
- S10256132A **Timothy Chai Weijie** 
- S10262576D **Ggwendolynn Lee Rasni**  
- S10262410A **Xue Wenya**  

## Welcome to Pocket Chef
### Introduction
We are Group 1 from P02 taking the MAD Module for Software Engineering Specialisation under Information Technology (N54).   
This repository is for our assignment.    
Our application is a food recipe app called Pocket Chef.      
> Pocket Chef is the ultimate companion for making informed dietary decisions and aiding with effortlessly planning meals. Whether it's a health-conscious individual or someone simply looking to streamline their culinary journey, our app is designed to cater to their needs.

### Category
Food & Drink

### Objective
Pocket Chef is designed to empower users to take control of their diet by providing them with a convenient platform to access a wide range of recipes, plan meals, and track nutritional information, all at the tip of their fingers. We aim to make healthy eating more accessible and enjoyable for everyone.


This will be achieved through 3 main goals:
1) Providing recipes with information on ingredients required to prepare them, leading to users being eased into cooking their meals rather than opting for takeout.
2) Providing avenues for users to plan their meals, enabling them to structure their meals according to their needs and dietary requirements (such as specific dietary restrictions or intolerances).
3) Providing systems for users to manage and track their ingredients to be able to conveniently plan out meals, further leading to a paradigm shift in their lifestyle to incorporate meal planning into their daily routine.

### Motivation
Due to rising obesity in Singapore, from 10.5% in 2019-2020 to 11.6% in 2021-2022 done by the [National Population Health Survey](https://www.healthhub.sg/live-healthy/its-not-a-small-world-after-all#:~:text=Based%20on%20the%202021%2F2022,increase%20in%20obesity%20rates%20observed). With 11.9% to 13.1% in males and 9.3% to 10.2%, with the highest disparity amongst elders from aged 40-49 at 15.0%. This underscores the critical need for intervention to combat this trend.


Among the various factors contributing to this rise, the two predominant factors are exercise and nutrition. Recognizing the significance of nutrition in combating obesity, our group has decided to address this aspect. By focusing on nutrition, we aim to empower users to take control of their dietary choices, allowing them to properly plan out meals and select recipes, thereby reducing the barrier to home cooking.


Through our platform, users will gain access to tools and resources to plan meals effectively and select recipes aligned with their nutritional goals. By promoting healthier eating habits and providing support for home cooking, we hope to contribute to the collective efforts in tackling the obesity epidemic in Singapore.

## Features
### Stage 1
#### Splash Screen on App Launch - Jin Rong - [x]
#### Drawable Menu implemented in various pages - Jin Rong & Enjia - [x]
#### Main Menu with motion layout functionality - Jin Rong - [x]
#### Random Recipe Browsing, filtered by tags, provided by Spoonacular API - Jin Rong - [x]
##### Tasks (Recipe Browser):
- Successfully implement API requests for GetRecipes, SimilarRecipes and Ingredients - [x]
- Successfully incorporate recycler views with vertical scrolling - [x]
- Successfully incorporate onClickListener for recipes to load a new activity - [x]
- Successfully incorporate recycler views with horizontal scrolling for ingredients and similar recipes - [x]
- Successfully incorporate instructions recycler viewholder - [x]
  
#### User login system using a database - Timothy - [x]
##### Tasks (User login system using a database):
- Successfully implement login and signup page in a single activity using ViewAnimator - [x]
- Successfully implement gathering of more complex data types such as date and image - [x]
- Successfully connect to database to add new account details and recognise user to log in - [x]
- Successfully implement input validation and relevant error toasts for account details - [x]

#### User profile - Ggwendolynn - [X]
##### Tasks (User profile):
- Successfully designed profile UI with Name, Email, DOB, and Password = [x]
- Successfully implement the profile page with user data - [X]
- Successfully implement Update Profile feature to database - [X]
- Successfully connected to database to retrieve user's profile details - [X]
- Successfully implement functioning connection upon user login, the profile page seamlessly retrieves data from the database - [X]

#### Advanced Food Recipe Search - Enjia - [x]
##### Tasks (Meal Planner):
- Successfully implement Advanced Searching UI with options to search for recipes by name, ingredients and nutrients - [x]
- Successfully implement API requests for Search for Recipe - [x]
- Successfully display searched Recipes in UI - [x]
- Successfully implement sorting of searched recipes - [x]

#### Favorite List - Wenya - [x]
##### Tasks (Favorite List):
- Successfully designed favorite list UI with category image, category name and adding or removing category- [x]
- Successfully implement Data Model for favorite category and favorite recipes - [x]
- Successfully implement favorite functionality, able to favorite or unfavorite recipes into different categories and store them in the database - [x]
- Successfully use DOA (Data Access Object) to store and retrieve favorite recipes data - [x]
---
### Stage 2
#### Image Classification utilising TFLite model and Voice Recognition, recommend recipes based on input- Jin Rong - [X]
- Successfully implement tensorflow lite model - [X]
- Successfully implement camera intent to capture image (take photo or use gallery) - [X]
- Successfully utilise tensorflow lite model to predict image with high accuracy (sufficiently high confidence at 100 epoch) - [X]
- Successfully implement voice recognition using Android Native Speech Recogniser - [X]
- Successfully parse voice into a string that correlates with a library - [X]
- Create new activity SearchedRecipeQueryOutput - [X]
- Obtain Recipes successfully from spoonacular api, prompting with query pasesed from intent, and adapt into recycler view - [X]
- Implement onClickListener for recipes leading to RecipeDetailsActivity - [X]
Optional:
- Consolidate both activities into one activity - [X]
- UI/UX improvements, such as button animation and transitions - []

##### Menu Update to accomodate new features - Jin Rong []
- Incorporate new buttons for new features in Main Menu and Drawable Menu - []
- Revamp menu to utilise clickable cardViews instead of buttons for UI - []
- Use constraintHeight and constraintWeight for mobile responsiveness - []
- Add logo svgs for both menus - []

#### Community page with app notifications and recipe sharing using Firebase Dynamic Links - Enjia - [x]
- Successfully implement Community Page User Interface - [x]
- Successfully implement functionality to let users add, delete and edit posts - [x]
- Successfully format recipe details to be displayed - [x]
- Successfully implement comments handling for post details -[x]
- Successfully implement in-app notifications for post interactions - [x]
- Successfully implement recipe sharing using Firebase Dynamic Links - [x]
- Successfully implement dashboard statistics with the most popular post and the newest post - [x]
- Successfully implement a notifications page for users to view notifications and delete them - [x]
- Successfully implement push notifications whenever a user adds a new post - [x]
#### Market Locator - Ggwendolynn - [X]
- Successfully implement Market Locator User Interface - [X]
- Successfully implement Market Distance User Interface - [X]
- Successfully integrate Google Maps API - [X]
- Successfully display market location details on the map - [X]
- Successfully implement search functionality to locate markets - [X]
- Successfully implement functionality to show distance of select market location - [X]
- Successfully handle user interactions (e.g., clicks on map direction) - [X]
- Successfully implement permissions for user's location - [X]
#### Virtual Pantry - Timothy - [ ]
- Successfully implement Recycler view to display ingredients- [x]
- Successfully implement Recycler view to display recipes - [x]
- Successfully implement drag and drop and swiping to re-order, edit and delete ingredients = [x]
- Successfully fetch and display recipes from API - [x]
- Successfully store, load and update pantry items in database - [x]
#### Generated Shopping List - Wenya - [X]
- Successfully implement Shopping List Interface - [X]
- Successfully created custom categories for shopping list - [X]
- Successfully implement functionality to add/remove item to the shopping list manually - [X]
- Successfully used DAO to store and retrieve shopping list data - [X]
- Successfully implement the ability to add all ingredients from a selected recipe to the shopping list - [X]
- Successfully include quantities and units for each ingredient, based on selected recipes - [X]
- Successfully implement QR code sharing options - [X]
- Successfully implement barcode scanning to add products to the Virtual Pantry - [X]
- Successfully implement auto-ticking of matched items in the shopping list from the Virtual Pantry - [X]

### Limitations of API:
#### Advanced Searching - [recipes/complexSearch](https://spoonacular.com/food-api/docs#Search-Recipes-Complex)
- Some inputs do not generate the correct recipe details when the query and excludeIngredients parameters are both entered. For example, when the user inputs "apple" for the query and excludeIngredients parameters, the API still returns recipes with apples. However, all other food options work, like entering cheese or chicken for both parameters.
- API calls for equipment and ingredient might be changed due to API Provider changing their output. For example, ingredients utilise "ingredient.image" which is the parameter itself such as "/banana.jpg", however for equipment, it is the full url "https://img.spoonacular.com/equipments_100x100/pan.png". Undocumented changes for this API have happened, so if images do not show up in the recyclerview for "RecipeDetailsActivity", please check through postman.

### Limtiations of App:
#### Complex Search
- Food Images limited to 101 types of food, as per labels.txt in assets folder
- Voice Recognition limited to 6300+ food names, as per ingredients to label.txt in raw folder
#### Favourites & Shopping List:
- IllegalStateException occurs when attempting to store data in SQLite once database has exceed capacity, to regain functionality, clear cache of the app
---------------------------------------------
### Screenshots of Application:
#### 1) Login: 
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Login.png)
- Users will key in their user name and email to log in. 
- Users are able to opt to sign up and create a new account

#### 2) Signup: 
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Signup.png)
- Users will input their Username, email, password to create their account

#### 3) Menu (Start): 
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Menu1.jpg)
- Starting menu for application.
- Users can view their dashboard statistics including new notifications and the number of posts they have made.
- Users can view the current most popular post and newest post. Clicking on any will redirect them to that post.
- To proceed to buttons, the User has to swipe downwards to trigger animation to load buttons

#### 4) Menu (End): 
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Menu2.png)
- Menu with buttons displayed after swiping. 
- To go back to previous menu, user has to swipe upwards to trigger animation to hide buttons

#### 4) Drawable Menu:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/DrawMenu.png)
- Drawable menu in various pages, able to redirect to respective pages
- Drawer opens on click of hamburger menu icon

#### 5) Random Recipes: 
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/RandomRecipes.png)
- Random Recipes features. 10 random recipes are displayed for the user to browse. 
- Upon clicking on the recipe, Users are redirected to Recipe Details

#### 6) Recipe Details: 
  ![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/RecipeDetails.png)

  ![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/RecipeDetails2.png)

  ![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/RecipeDetails3.png)
- Details for the Recipe given, displaying instructions, ingredients, equipment and similar recipes required. 
- Users can select the "Star" icon to favourite a recipe, adding to SQLite database

#### 7) Advanced Search:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/AdvancedSearch.png)
- Advanced search feature for users to input recipe name, ingredients excluded, ranges for carbs, proteins and fats, diet and intolerances.
- "Search" button redirects to Advanced Search Output

#### 8) Advanced Search Output:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/AdvancedSearchOutput.png)
- Recipes are listed similarly to "Random Recipes"
- Users can click on recipes to view recipe details

#### 9) Favourite:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Favourites.png)
- Users can view their categories for favourited recipes
- User can click on a category to view recipes added to a specific category

#### 10) Favourite Recipes:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/FavouriteRecipes.png)
- Users can view their favourite recipes
- User can click on recipes to view recipe details

#### 11) View Profile:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/ViewProfile.png)
- Users can view their profile information
- Users can click the "Edit" profile icon to edit their profile information

#### 12) Edit Profile:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/EditProfile.png)
- Users can edit their profile
- Users can click the "Save" profile icon to save their new profile information

#### 13) Community Page:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Community.png)
- Users can view recipes made by other users
- Users can sort posts based on popularity, date made or filter to get their own posts.
- Users can search for posts based on the post title
- Users can click the recipes to view post details

#### 14) Post Details:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/PostDetails1.png)

![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/PostDetails2.png)

![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/PostDetails3.png)
- Users can view the post details, including nutrition, ingredients, equipment and instructions.
- Users can leave comments and likes on the post
- Users can share the post with others in external apps like mail or messages.

#### 15) Add Post/Edit Post:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/AddPost.png)

![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/UpdatePost.png)

- Users can create a new post by filling in all the necessary input boxes, creating a new post will send a push notification to everyone.
- Users can edit their posts by holding the selected and clicking on the edit button.
  
#### 16) Notifications:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Notifications.png)
- Users can view notifications received and delete them by swiping to the left.
- Users can click on the notifications to view the post related to the notificaiton.

#### 17) Complex Search:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/ComplexSearch.png)
- Users can search via Voice Recognition and Image Classification
- Upon pressing Open Camera, user will be prompted for camera permission and an image will be taken
- Upon pressing Open Gallery, user will be prompted for gallery permissions and an image will be selected
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/ComplexSearchImage.png)
- Classifying an image, a food label with associated prediction value will be provided.
- Upon pressing Voice Search, voice recognition will start, and food-related keywords will be filtered out
- Upon pressing Search Recipes, a query will be parsed into intent and recipes will be shown.
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/QueriedRecipes.png)
- Recipes searched based on query from Complex Search will be viewed in a recycler view format.

#### 18) Pantry:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Pantry.png)
- Users can add, edit and delete their pantry ingredients here
- Users can select pantry ingredients to search for available recipes

![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Pantry2.png)
- Users can view the possible recipes based on their selected ingredients.
- Users can view the ingredients they are missing for each recipe.
- Users can select any recipe to view recipe details and instructions.

#### 19) Google Maps:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/LocationSearch.png)
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Location.png)
- Users can input a query for an area to search a location.
- API from Google will be called to be utilised to calculate an optimal route.
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Distance.png)
- Users can view their route for requested location.

#### 20) Shopping List:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/ShoppingList.png)
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/ShoppingList-Share.png)
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/ShoppingList-Scan.png)
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/ShoppingList-Add.png)
- Users can view their categories for Shopping list
- Users can share the selected shopping list with others in external apps using a generate QR code
- Users can Scan the QR code to get the shopping list detail
- Users can click on a category to view the ingredients added to a specific category

#### 21) Shopping List Items:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/ShoppingList-Item.png)
- Users can view and delete the ingredients that add in the shopping list
- Users can tick the ingredients that they have and check how many items left in the shooping list
- Users can scan barcode or use the "+" button to add ingredient that they might want to buy
- Users can click on the "house" icon to view the item that they already have in Virtual Pantry

#### 22) Virtual Pantry for Shopping List
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/VirtualPantry.png)
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/VirtualPantry-Scanner.jpg)
- Users can scan barcode to add the products that they already have into the virtual pantry, when the product match with the shopping list item it will auto-ticking the item

---------------------------------------------
### Acknowledgements:
- Video tutorial for motion layout:
https://www.youtube.com/watch?v=o8c1RO3WgBA&list=PLWz5rJ2EKKc-bcyUTIFAr97ZtRkwM7S4y&ab_channel=AndroidDevelopers
https://www.youtube.com/watch?v=pnDgGNKoe4w&ab_channel=JohnWilker
- Tool to convert json to java classes
https://json2csharp.com/
- Video tutorial for Picking image from gallery:
  https://www.youtube.com/watch?v=nOtlFl1aUCw
- Video tutorial for context menu on long press:
  https://www.youtube.com/watch?v=BZ_UrcFOCTc
- Data access Object pattern:
  https://www.tutorialspoint.com/design_pattern/data_access_object_pattern.htm
- Additional Github Repository for Jin Rong's ML Code:
https://github.com/EdricYeo117/MAD_Assignment_Stage2_MLPythonCode
- Video tutorial for barcode scanner
  https://www.youtube.com/watch?v=jtT60yFPelI
------------------------------------------------
### Dependencies Utilised:
- Android AppCompat:
https://developer.android.com/jetpack/androidx/releases/appcompat
- Android Maps Utils:
https://github.com/googlemaps/android-maps-utils
- Android Room:
https://developer.android.com/jetpack/androidx/releases/room
- CircleImageView:
https://github.com/hdodenhof/CircleImageView
- Firebase App Check:
https://firebase.google.com/docs/app-check
- Firebase App Check SafetyNet:
https://firebase.google.com/docs/app-check/android/safetynet-provider
- Firebase for Database:
https://firebase.google.com/
- Glide:
https://github.com/bumptech/glide
- Gson:
https://github.com/google/gson
- Guava:
https://github.com/google/guava
- Jsoup:
https://jsoup.org/download
- Kongzue DialogX:
https://github.com/kongzue/DialogX
- Kongzue DialogX Album Dialog and File Dialog:
https://github.com/kongzue/DialogXSample
- Logger:
https://github.com/orhanobut/logger
- OkHttp:
https://github.com/square/okhttp
- OkHttp Logging Interceptor:
https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
- PermissionX:
https://github.com/guolindev/PermissionX
- Picasso:
https://github.com/square/picasso
- Play Services Location & Maps:
https://developers.google.com/android/guides/setup
- Retrofit:
https://github.com/square/retrofit
- Retrofit Gson Converter:
https://github.com/square/retrofit/tree/trunk/retrofit-converters/gson
- TensorFlow Lite:
https://github.com/tensorflow/tensorflow/tree/master/tensorflow/lite
- ZXing Barcode Scanner:
https://github.com/zxing/zxing
- ZXing Android Embedded:
https://github.com/journeyapps/zxing-android-embedded
- Material 3:
https://m3.material.io/
- Google Places:
https://developers.google.com/maps/documentation/places/android-sdk/overview
