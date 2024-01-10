package com.example.madcamp_week2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PostItem(val _id: Number,val content: String,val date1: String, val date2: String, val imgfilename: String, val location: String, val title: String, val writerid: String) : Parcelable
class ReviewItem(val title: String, val content: String)

val BASEURL: String = "http://143.248.191.38:5000/"