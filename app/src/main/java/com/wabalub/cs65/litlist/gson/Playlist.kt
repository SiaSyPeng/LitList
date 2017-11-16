package com.wabalub.cs65.litlist.gson

/**
 * Data class for a playlist
 */
data class Playlist(val songList : List<Song>, val creator : String, val id : String)