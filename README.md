# City Slicker App Design
# Trip Planner App for Final Project @ FBU - Engineering
===

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#ProductSpec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview / Description / App Evaluation
   - **Category**: Travel (Travel and itinerary planner)
   - **Mobile first** web app as secondary
   - **Story:** This trip planner lets you create trips together by adding popular restaurants/attractions to trips. Travellers can add locations from a map, view a sorted list of recommended places based on their preferences and budget, add collaborators, and even order the places in which they wish to visit down to the minute! The app will also be able to recommend users places to go depending on the user's preferences. 
   - **Market:** College students travelling for break, families on vacation, travellers, vacationers, backpackers, young adults planning out their outings 
   - **Habit:** Travellers will use the app in order to make trips they wish to go to and be recommended places to go to based on preferences.
   - **Scope:** MVP: users will be able to make a trip, add collaborators, and get recommended places based on their user preferences. V2: Users will be able to copy, like, save others' trips and see a list of friends' most recent trips in a feed. V3: Users can add places to a general wishlist and upload pictures to albums organized by trip as they check off places they've been to. 
   
## Product Spec

### 1. User Stories (Required and Optional)

**FBU APP EXPECTATIONS**
* [x] Your app has multiple views
* [x] Your app interacts with a database (e.g. Parse)
* [x] You can log in/log out of your app as a user
* [x] You can sign up with a new user profile
* [x] Your app integrates with at least one SDK or new API [Places SDK, Maps SDK, Places web service API]
* [x] Your app uses at least one gesture [pinch to scale map, swipe up to expand bottom sheet]
* [x] Your app uses at least one animation (e.g. fade in/out, animating a view growing and shrinking, cardview carousel, a location on the google maps expands when the marker is tapped) 
* [x] Your app incorporates at least one external library to add visual polish [https://github.com/DanielMartinus/konfetti, https://github.com/daimajia/AndroidViewAnimations, https://github.com/tyrantgit/ExplosionField]
* [x] Your app provides opportunities for you to overcome difficult/ambiguous technical problems [Quicksort algorithm and sorting based on budget/preference weight]

**Required Must-have Stories**
* [x] Authentication: Users will be able to log in, log out, and sign up for accounts.
* [x] Profile: Users will be able to upload profile pictures, see a list of their trips, and a list of friends
* [x] Collaborative trip creation: users can add collaborators to their trips
* [x] Trip creation: Users can see a map with embedded destinations, search for specific locations, view recommended destinations, and add places to a new trip that they are creating
* [x] Editing trips: Users can remove places from trips, organize places by day, create itineraries by ordering the places within a single day by time
* [x] Landing page map: Users can freely search a map to add trips to an existing or new trip. By default this pans to their home location.
* [x] Quick compose a trip: users will be prompted to fill out a short form (ie. regions(s), from-to date, collaborators, trip name, and preferences for recommended activities/restaurants/attractions)
* [x] Map search bars: users can search up specific locations, when in trip creation mode the search bar is limited to the city
* [x] Creating a trip: multiple regions in the form, once they're done adding places to one region, go to the next region
* [x] Places: users can see the website link, photo, rating, number of rating, approximate price point, and address of each place
* [x] Users will be recommended places to go based on a recommendation algorithm
* [x] Swipe up to see list of recommended places on the map

**Optional Nice-to-have Stories**
* [ ] Users can add places from Explore Map to new or exisiting trips
* [ ] Users can add more places from the map to already existing trips in the Trip Details page
* [ ] Users can view and edit the details of a trip by clicking on any trip in the list on their Profile page
* [ ] Users can upload pictures to albums organized by trip as they check off places they've been to. 
* [ ] Google calendar integration so that the user can easily add a trip and all of it's places as events on their GCAL
* [ ] Feed: Users can see a feed of their friends' most recent trip, user can like/copy a trip
* [ ] Friends profile page: users can view all their friends' trips in the friend's profile page and view each trip's details
* [ ] Users can check off places they go to and rate the places


### 2. Screen Archetypes

* Login, Sign Up
* User's Profile
* Explore Map
* Quick Compose Map
* Quick Compose Form
* Trip Details Screen

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Map landing page
* Profile
* Quick Compose

**Flow Navigation** (Screen to Screen)

* Login / sign up:
   * Login: username, password, toggle password visibility, login button
   * Sign up: username, password, toggle password visibility, signup button
* Profile:
    * Upload photo (optional)
    * User's list of trips and friends
* Main Activity bottom nav options:
    * Explore Map landing page:
        * autocomplete search bar
        * leads to location details fragment
            * can lead to quick compose
            * can add location to existing trip
    * profile:
        * list of friends
        * cardviews of user created trips
            * leads to trip details page for that trip 
    * quick compose trip:
        * form: choose dates, choose regions, choose a starting location to start adding to, once finished adding places to one place prompts user to go to next region
            * user will be shown a filterable and searchable map with the attractions and restaurants
            * user can swipe up to see a list of recommended places
                * trip details dialog fragment: user can add it to the trip, close out of it, hide it
                    * user will be led to the Edit Trip Details screen               

* Friend's profile page:
    * list of trips
    * top rated trips show up at the top
* Friend's Trip Details screen:
    * User can like the trip
    * User can copy the trip to their list of trips
* User's Edit Trip Details Screen:
    * User can see a list of places (sorted by chronology and city) and assign a date/time to each place
    * User can remove a place
    * User can add places to their trip

## Digital Wireframes
<img src="https://github.com/flaurencyac/CitySlicker/blob/master/City%20Slicker.png" width=600>
https://www.figma.com/file/tug03NeN4NqirYlgbBSF0h/City-Slicker?node-id=0%3A1

### [BONUS] Interactive Prototype: https://www.figma.com/proto/tug03NeN4NqirYlgbBSF0h/City-Slicker?node-id=1%3A3 

## Schema 
### Models

#### User Class
| Property | Required | Type     | Description |
| -------- | -------- | -------- | ----------- |
| username | *        | String   |             |
| password | *        | String   |             |
| profilePicture    |          | ParseFile|             |
| friends  |          | Array    |             |
| trips    |          | Array    |             |

#### Trip Class
| Property | Required | Type | Description |
| -------- | -------- | ---- | ----------- |
| owner | * | Pointer | Pointer to the user object that owns the trip |
| collaborators | | Array | List of user objects |
| places |  | Array | List of string place objects in a set ordered by city |
| startDate | * | datetime | |
| endDate | * | datetime | |
| budget | | Int | default is 0, max = 4 |
| regions | | Array | list of geographical locations the trip is split into, this is an array of placeId strings |
| cityNames | | Array | list of strings of city names |
| tripName | * | String | |
| foodPref, familyPred, adultPred, shoppingPref, attractionsPref | | Integers | numbers between 0 and 4 that indicates preference weight/rank | 

#### Spot Class
| Property | Required | Type | Description |
| -------- | -------- | ---- | ----------- |
| trip | * | pointer | points to the trip that it belongs to |
| name | * | String | name of the place |
| date | | datetime | day the spot takes place |
| placeId | * | String | placeId string from Places API/SDK |
| regionId | * | String | cityId string from Places API/SDK | 
| time | | datetime | date of when the place will be visited | 

### Networking
* Login / sign up screens:
    * Read/GET user and all of the user's information
    * Create/POST signUp 
    * signIn background
* Create Profile screen:
    * Update/PUT profile image, user's preferences, friends, home location, etc.
    * Save
* Main Activity:
    * map landing fragment:
        * search/filter locations Google Maps SDK call
        * Location Details modal: Create/POST a trip, create/POST a place, put a place into a trip
    * profile fragment:
        * Read/GET all of the user's trip objects and friends list 
        * Update/PUT a friend obj
    * quick compose trip fragment:
        * Create/POST a new trip object, Update/PUT location/date/name onto the trip      
    * feed:
        * Read/GET query all friends' most recent trip
* Friend's profile page: 
    * Read/GET list of trips from friend
* Friend's Trip Details screen: 
    * Update/PUT like/like count/trip obj into user's class
    * Create/POST a copy of the trip object and add it to the current user's trips (for copying a trip). Save user object afterwards.
* User's Trip Details Screen: 
    * Update/PUT rating
    * Read/GET list of places, cost estimate in a trip
* Edit Trip Screen:
    * Read/GET all places associated with this trip, 
    * Update/PUT rating, date/time, and give cost estimates to every place obj
    * Save trip
    * Delete a place obj from the trip

- [OPTIONAL: List endpoints if using existing API such as Yelp]
