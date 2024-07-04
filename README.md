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

#### Community page using SQLite with app notifications and recipe sharing to WhatsApp - Enjia - [ ]
- Successfully implement Community Page User Interface - [ ]
- Successfully implement functionality to let users add, delete and edit posts - [ ]
- Successfully format recipe details to be displayed - [ ]
- Successfully implement app notifications for posts - [ ]
- Successfully implement recipe sharing using WhatsApp API - [ ]
#### Calorie Counter - Ggwendolynn - [ ]
#### Virtual Pantry - Timothy - [ ]
- Successfully implement Nested Recycler view - [ ]
- Successfully parse ingredients through API to fetch images - [ ]
- Successfully fetch and display recipes from API - [ ]
- Successfully store pantry items in database - [ ]
#### Generated Shopping List - Wenya - [ ]
### Limitations of API:
#### Advanced Searching - [recipes/complexSearch](https://spoonacular.com/food-api/docs#Search-Recipes-Complex)
- Some inputs do not generate the correct recipe details when the query and excludeIngredients parameters are both entered. For example, when the user inputs "apple" for the query and excludeIngredients parameters, the API still returns recipes with apples. However, all other food options work, like entering cheese or chicken for both parameters. 
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
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Menu1.png)
- Starting menu for application. Blank space left for Stage 2 implementation/usage
- To proceed to buttons, User has to swipe downwards to trigger animation to load buttons

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
- Recipes are listed similar to "Random Recipes"
- Users can click on recipes to view recipe details

#### 9) Favourite:
![image](https://github.com/enjiawu/MAD24_P02_Team1/blob/main/Images/Favourites.png)
- Users can view their categories for favourited recipes
- User can click on category to view recipes added to a specific category

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
------------------------------------------------
### Dependencies Utilised:
- Picasso dependency for image loading from API:
https://github.com/square/picasso
- Retrofit dependency for API calling:
https://github.com/square/retrofit
- Retrofit gson converter dependency:
https://github.com/square/retrofit/tree/trunk/retrofit-converters/gson
- Material 3 open-source Google design system:
https://m3.material.io/
- Glide an image loading and cache library
https://github.com/bumptech/glide
- Jsoup a java to html parser 
  https://jsoup.org/download
- Firebase for database
https://firebase.google.com/
- CircleImageView
  https://github.com/hdodenhof/CircleImageView
- Android Splashscreen
https://developer.android.com/jetpack/androidx/releases/core
- Android AppCompat
  https://developer.android.com/jetpack/androidx/releases/appcompat
- Android Room
  https://developer.android.com/jetpack/androidx/releases/room
- Kongzue DialogX
  https://github.com/kongzue/DialogX
- Kongzue DialogX Album Dialog and File Dialog
  https://github.com/kongzue/DialogXSample

