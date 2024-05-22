![image](https://github.com/enjiawu/MAD24_P02_Team1/assets/133361144/826a5292-c18b-4f23-97dd-67c8f00aea17)
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
#### Main Menu with motion layout functionality - Jin Rong - [x]
#### Random Recipe Browsing, filtered by tags, provided by Spoonacular API - Jin Rong - [x]
##### Tasks (Recipe Browser):
- Successfully implement API requests for GetRecipes, SimilarRecipes and Ingredients - [x]
- Successfully incorporate recycler views with vertical scrolling - [x]
- Successfully incorporate onClickListener for recipes to load a new activity - [x]
- Successfully incorporate recycler views with horizontal scrolling for ingredients and similar recipes - [x]
- Successfully incorporate instructions recycler viewholder - [x]
  
#### User login system using a database - Timothy - [ ]
##### Tasks (User login system using a database):
- 

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

#### Shopping List - Wenya - [ ]
##### Tasks (Shopping List):
- 
---
### Stage 2
#### Image Classification utilising TFLite model and Voice Recognition, recommend recipes based on input- Jin Rong - [ ]
- Successfully implement tensorflow lite model - [ ]
- Successfully implement camera intent to capture image (take photo or use gallery) - [ ]
- Successfully utilise tensorflow lite model to predict image with high accuracy (sufficiently high confidence at 100 epoch) - [ ]
- Successfully implement voice recognition using Android Native Speech Recogniser - [ ]
- Successfully parse voice into a string that correlates with a library - [ ]
- Obtain Recipe IDs from Spoonacular API, parse into recycler views in new activity - [ ]
- Implement onClickListener for recipes leading to RecipeDetailsActivity - [ ]

#### Community page - Enjia - [ ]
#### Caloric Counter - Ggwendolynn - [ ]
#### Virtual Pantry - Timothy - [ ]
#### Generated Shopping List - Wenya - [ ]
### Acknowledgements:
- Video tutorial for motion layout:
https://www.youtube.com/watch?v=o8c1RO3WgBA&list=PLWz5rJ2EKKc-bcyUTIFAr97ZtRkwM7S4y&ab_channel=AndroidDevelopers
https://www.youtube.com/watch?v=pnDgGNKoe4w&ab_channel=JohnWilker
- Tool to convert json to java classes
https://json2csharp.com/
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
- Jsoup a javal html parser 
- https://github.com/bumptech/glide

