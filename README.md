Smart Pantry Manager

About:
Smart Pantry Manager is an Android app that can assist users keep track of food ingredients that currently have at home.
The main purpose of the app is to reduce food waste by showing recipes that can be made using ingredients that are currently in the pantry, additionally it advises ingredients that will be expiring soon or have already expired.
The app is made using Java and Android Studio.

Main Features:
- Add ingredients
- Edit ingredients
- Delete ingredients
- Add quantity and unit of ingredients
- Add expiry dates
- View all items
- View recipes in collection
- View details and preparation steps
- Get suggestions for recipes with current ingredients
- Strict recipe matching
- View "Almost there" (showing recipes with 1 ingredient missing from pantry)
- Expiry alerts
- Settings with toggle to turn expiry alerts on or off

Recipe Matching:
A recipe will only appear under "Suggested Recipes" if the pantry has all the ingredients listed, as well as enough quantity of each ingredient.
Recipes that are missing one ingredient will appear under "Almost there".
Recipes that are missing more than one ingredient will not be shown - but can be viewed in "Recipe Collection".

Database:
The app makes use of SQLite database, and stores:
- Pantry ingredients
- Qunatity
- Units
- Expiry dates
- Recipes
- Recipe ingredients
The pantry information remains saved when the app is closed and opened.

Main Screens
Home:
The home screen gives access to the main parts of the app, such as:
- My Pantry
- Suggested Recipes
- Recipe Collection
- Settings

My Pantry - this is where ingredients can be created, read, updated and deleted.
Suggested Recipes - this is where recipes are shown that can be made with the ingredients currently in the pantry.
Almost There - this is where recipes with one ingredient missing is shown, additionally the missing ingredient is also displayed here.
Recipe Collection - this is where all the recipes are stored in the app.
Recipe Details - this shows the ingredients as well as a detailed description of the preparation instructions for each recipe.
Settings - this is where expiry alerts can be turned on and off

Expiry Alerts - if expiry alerts are enabled, the app shows an alert for:
- Have already expired
- Will expire within 3 days

Technologies used:
- Java
- Android Studio
- SQLite
- SQLiteOpenHelper
- RecyclerView
- SharedPreferences
- GitHub

GitHub Repository:
The project source code and commit history may be found here
https://github.com/MatthewMaistry/Smart-Pantry-Manager

Project
Author: Matthew Seelan Maistry
Student no: 401902212
Project name: Smart Pantry Manager
Year: 2026 Semester 2
Course name: Mobile App Development 700