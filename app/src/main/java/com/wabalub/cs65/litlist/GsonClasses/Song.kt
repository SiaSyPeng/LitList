package com.wabalub.cs65.litlist.GsonClasses

import android.graphics.drawable.Drawable

/**
 * Song data class
 */
data class Song(val name : String, val artist : String, val albumName : String, val albumArt : Drawable)