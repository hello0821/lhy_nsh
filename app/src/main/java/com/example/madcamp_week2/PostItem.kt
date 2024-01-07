package com.example.madcamp_week2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PostItem(val id: Number, val title: String, val writer: String, val date: String, val loc: String, val content: String) : Parcelable