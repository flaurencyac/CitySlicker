# City Slicker App Design
# Collaborative Trip Planner App for Final Project @ FBU - Engineering
===

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#ProductSpec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview / Description / App Evaluation
   - **Category**: Travel (Roadtrip coordinator / travel planner)
   - **Mobile first** though web would be useful too!
   - **Story:** This trip planner lets you create trips together by adding popular restaurants/attractions to a trip together with your friends. Travellers can view their trips on a map, plan out their trips by day, and even order the places in which they wish to visit down to the minute! You can even watch your feed where you can see/copy/like your friends' most recent trips. The app will also be able to recommend users places to go depending on the user's preferences. 
   - **Market:** College students travelling for break, families on vacation, travellers, vacationers, backpackers, young adults planning out their outings 
   - **Habit:** Travellers will use the app in order to make trips they wish to go to, be recommended places to go to based on preferences, and will be able to check things off (and document their pictures as they do) by uploading pictures to an album tied to their trips
   - **Scope:** MVP: collaborative trip making & recommended places based on user preferences. Users will be able to copy, like, save others' trips and see a list of friends' most recent trips in a feed. Users will be able to see how much trips are estimated to cost as well as rate the places they go to and rate entire trips. V2: Users can add places to a general wishlist and upload pictures to albums organized by trip as they check off places they've been to. 
   
## Product Spec

### 1. User Stories (Required and Optional)

**FBU APP EXPECTATIONS**
[ ] Your app has multiple views
[ ] Your app interacts with a database (e.g. Parse)
[ ] You can log in/log out of your app as a user
[ ] You can sign up with a new user profile
[ ] Your app integrates with at least one SDK (e.g. Google Maps SDK, Facebook SDK) or API (that you didn’t learn about in CodePath)
[ ] Your app uses at least one gesture (e.g. pinch to scale the map, double tap to like a trip)
[ ] Your app uses at least one animation (e.g. fade in/out, animating a view growing and shrinking, cardview carousel, a location on the google maps expands when the marker is tapped)
[ ] Your app incorporates at least external library to add visual polish (eg. https://droidux.com/ )
[ ] Your app provides opportunities for you to overcome difficult/ambiguous technical problems (e.g combining both the Google Maps and Yelp API, giving users recommended spots based on user preferences)

**Required Must-have Stories**

[ ] Authentication: Users will be able to log in, log out, and sign up for accounts.
[ ] Profile: Users will be able to upload profile pictures, fill out their preferences (mandatory upon creation of an account), and add/view friends
[ ] Collaborative trip creation: users can add collaborators to their trips
[ ] Trip creation: Users can see a map with embedded destinations, search for specific locations, view recommended destinations, add places to an existing or new trip, add places to a general wishlist
[ ] Editing trips: Users can add or remove places from trips, organize places by day, create itineraries by ordering the places within a single day by time
[ ] Landing page map: Users can freely search a map to add trips to an existing or new trip. By default this pans to their most recent trip or if that doesn't exist to their home location.
[ ] Quick Compose map: When searching the map in Quick Compose the user will be adding places exclusively to a new trip
[ ] Quick compose a trip: users will be prompted to fill out a short form (ie. regions(s), from-to date, collaborators, trip name, and preferences for recommended activities/restaurants/attractions)
[ ] Feed: Users can see a feed of their friends' most recent trip, user can like/copy a trip, and user can click a trip to view a trip's details
[ ] Friends profile page: users can view all their friends' trips in the friend's profile page
[ ] Map search bar: users can search up specific locations (optional: whether or not there is a back button to where they were originally placed on the map)
[ ] Creating a trip: multiple regions in the form, once they're done adding places to one region, go to the next region
[ ] Users can see a trip on the map (places are connected in chronological order and color-coded separated by day)
[ ] Users can see how much trips are estimated to cost as well as rate the places they go to and rate entire trips. 
[ ] Users will be recommended places to go based on a recommendation algorithm
[ ] Users can check off places they go to and rate the places

**Optional Nice-to-have Stories**
[ ] Have a general wishlist
[ ] Users can upload pictures to albums organized by trip as they check off places they've been to. 
[ ] Send users notifs when their friends recently made a new trip or added them as a collaborator to 
[ ] Google calendar integration so that the user can easily add a trip and all of it's places as events on their GCAL


### 2. Screen Archetypes

* Login, Sign Up
* User's Profile
* Friend's profile
* Feed
* Landing Map
* Quick Compose Trip
* Compose Trip Map
* Trip Details Screen
* Trip Creation Screen
   

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Map landing page
* Album of trips
* Profile
* Quick Compose
* Feed

**Flow Navigation** (Screen to Screen)

* Login / sign up:
   * Login: username, password, toggle password visibility, login button
   * Sign up: username, password, toggle password visibility, signup button
       * Goes to Create Profile screen (ie. spending $ to $$$ for restaurants, types of activities they tend to enjoy)
* Create Profile:
    * Upload photo (optional)
    * User's preferences
    * Home location for the map to default pan to
* Main Activity bottom nav options:
    * map landing page:
        * search bar
        * leads to location details fragment
            * can lead to quick compose
            * can add location to existing trip
    * profile:
        * list of friends
            * list of their trips
        * cardviews of user created trips
            * leads to trip details page for that trip 
            * higher rated trips show up at the top
    * quick compose trip:
        * form: choose dates, choose regions, choose a starting location to start adding to, once finished adding places to one place prompts user to go to next region
            * user will be shown a filterable and searchable map with the attractions and restaurants
                * trip details dialog fragment: user can add it to the trip, close out of it, hide it
                    * user will be led to the Edit Trip screen               
    * feed:
        * list of friends’ recent trips
            * can double tap to like a trip
            * can copy a trip
            * can view a friend's profile page

* Friend's profile page:
    * list of trips
    * top rated trips show up at the top
* Friend's Trip Details screen:
    * User can like the trip
    * User can copy the trip to their list of trips
* User's Trip Details Screen:
    * User can rate the trip
    * User can see a list of places (chronologically sorted and color coded or separated by day)
    * User can see a cost estimate for the trip
    * User can edit the trip -> goes to Edit Trip Screen
* Edit Trip Screen:
    * Recycler view for every place associated with the trip
    * Ordered by chronology and grouped by day
    * User can add, remove, set date/time, and give cost estimates to every location
    * Items automatically rearrange themselves in chronological order
    * Once saved the user is sent to their list of currently developing trips in the main activity
    * User can also cancel any changes by closing out in which case they will stay in the detail page of the trip

## Digital Wireframes
<img src="https://github.com/flaurencyac/CitySlicker/blob/master/City%20Slicker.png" width=600>

### [BONUS] Interactive Prototype

## Schema 
### Models

#### User Class
| Property | Required | Type     | Description |
| -------- | -------- | -------- | ----------- |
| username | *        | String   |             |
| password | *        | String   |             |
| homeLoc  | *        | String   | a user's home location |
| photo    |          | ParseFile|             |
| friends  |          | Array    |             |
| numFriends | | Int | |
| Trips    |          | Array    |             |
| pricePreference | * | Int     |             |
| activityPreferences | * | Array | Array of 0s and 1s to represent true or false for each item in this list [museums, shopping, tours, animals, sight-seeing] |
| likedTrips | | Array | list of trip objects that the user liked |
| numTrips | | Int | |

#### Trip Class
| Property | Required | Type | Description |
| -------- | -------- | ---- | ----------- |
| user | * | Pointer | |
| collaborators | | Array | List of user objects |
| rating | | Int | Rating out of 10 |
| places | * | Array | List of place objects |
| fromDate | | datetime | |
| toDate | | datetime | |
| length | * | Int | Length of trip in number of days |
| locations | * | Array | list of geographical locations the trip is split into (eg. [locationObj1, locObj2, locObj3])|
| likes | | Int | number of likes |

#### Place Class
| Property | Required | Type | Description |
| -------- | -------- | ---- | ----------- |
| trip | * | pointer | points to the trip that it belongs to |
| image | * | File | thumbnail image provided by Google SDK |
| name | * | String | name of the place |
| description | * | String | description of place from API |
| location | * | String | eg. "Miami, Florida" or is there a location object from Google maps SDK?|
| dayOfTrip | | Int | day of trip the third one |
| regionOfTrip | | Int | every trip has a list of regions (eg. "[Manhattan, Queens, Bronx]") and the regionOfTrip matches one of them (eg. 0 for "Manhattan") |
| rating | | Int | taken from a mixture of Google and Yelp's API |
| numRatings | | Int | Taken from API |
| cost | | Int | Cost estimate taken from SDK from $ to $$$ | 
| time | | datetime | date of when the place will be visited | 
| preferenceTags | | String | "[0,0,1,1,0]" represents true or false for the set list of possible user preferences |


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


- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
