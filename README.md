# LitList
The democratic, crowd-sourced playlist manager.

Team Wubalubadubdub: Josiah Putman, Lessley Hernandez, Sia Peng 

## Project Idea
Our idea is to create an app that allows users to collaboratively create and manage playlist with other users in their area. This app can be used as an easy way to simultaneously listen to music with others and manage playlists live. Music has always been a way to express emotion and communicate feelings. Through this app we can allow people to share a piece of themselves through music. Users have the ability to see all public playlists in a map view, and joining, participating and giving your vote for the next song are all just a tap away. LitList allows users to use playlists as a liaison for understanding the musical atmosphere on campus.

LitList @Dartmouth?

Dartmouth has many barriers between the ability to be vulnerable on campus and expressing yourself. However, as much as we try to hide ourselves, our music taste and playlist can say a lot about us. Consider when people go through a heartbreak, trying to get hyped, or just want to focus. This form of expressing what we would like to project to ourselves through music doesn’t have to be an individual experience.

And finally...
DEMOCRACY! Something that has been tragically lost in 2k17 will be revived through the LitList! All members of a playlist will have the ability to upvote, downvote and add songs!


### Why people should care
- Its a better way to experience and share music
- Understanding the musical environment on campus
- If you care about what people are thinking you care about what people are listening to

## User experience
- Login page - uses Spotify login, after successful login opens main view
- Main view - sliding tab layout
    - Map view
        - Google maps fragment appears, users can select playlist or create their own
    - Playlist view
        - When a playlist is selected, this shows the current songs in the playlist. Users can upvote, downvote and add their own songs
           - Shows the current song playing at the top
           - Lists songs in order of being added initially
        - If not selected, users can manage their song collection.
    - Rank view
        - Ranks playlists based on number of members listening and subscribed
    - Settings view
        - Notification settings, etc.


	Songs get upvoted and downvoted by users, which sorts the playing order

Please see `litlist.png` for a diagram of the user experience.

## Implementation Notes

### APIs
We will be using the following APIs. We already have the keys.

### APIs

#### Spotify
We plan on using Spotify for playing songs and login.

#### Google maps
We plan on using Google maps for the map view.

### Sensors
Accelerometers, GPS
Shaking the phone auto-joins the nearest session.

### Server development
This project will require a server. We want to program in Python on a Raspberry Pi.
The server either needs to be able to stream audio simultaneously to the users or find some way to sync up the users' audio stream with Spotify.

The server must also handle requests for playlist creation, adding/removing songs from the playlist, upvoting/downvoting songs, etc.

### Platform / Languages

This project will be developed in Java and Kotlin using Android studio. I prefer using Kotlin because it helps me make less null pointer exceptions, but feel free to use as much Java as you want. I will not be converting any Java to Kotlin and am comfortable using both languages. My only request is that all classes for holding JSON objects be defined as Kotlin data classes.
 -- Josiah

## UI Design

Develop the UI via Figma here
https://www.figma.com/file/TadFx7fYTgYNrsfTsq5YclMd/LitListMockup

