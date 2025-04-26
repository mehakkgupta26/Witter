package com.example.witter.models

data class post(
    val text:String="",
    val createdBy: User=User(),
    val createdAt:Long=0L,
    val likedBy:ArrayList<String> =ArrayList()

)