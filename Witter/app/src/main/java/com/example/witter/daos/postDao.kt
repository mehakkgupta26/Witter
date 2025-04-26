package com.example.witter.daos

import com.example.witter.models.User
import com.example.witter.models.post
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class postDao {
    val db=FirebaseFirestore.getInstance()
    val postcollection=db.collection("post")
    val auth=Firebase.auth

    fun addpost(text: String){
        val currentUserId=auth.currentUser!!.uid
        GlobalScope.launch {
            val usersDao=UsersDao()
            val user=usersDao.getUserbyId(currentUserId).await().toObject(User::class.java)!!
            val currentTime=System.currentTimeMillis()
            val post=post(text,user,currentTime)

            postcollection.document().set(post)
        }
    }

    fun getPostById(postId: String):Task<DocumentSnapshot>{
        return postcollection.document(postId).get()

    }
    fun updateLikes(postId:String){
        GlobalScope.launch {
            val currentUserId=auth.currentUser!!.uid
            val post=getPostById(postId).await().toObject(post::class.java)
            val isLiked= post!!.likedBy!!.contains(currentUserId)

            if(isLiked == true){
                post.likedBy.remove(currentUserId)
            }
            else{
                post.likedBy.add(currentUserId)
            }

            postcollection.document(postId).set(post)

        }
    }
}