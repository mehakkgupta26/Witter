package com.example.witter.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



class UsersDao {
    private val db= FirebaseFirestore.getInstance()
    private val usersCollections=db.collection("users")

    fun addUser(user: com.example.witter.models.User){
        user?.let {
            GlobalScope.launch(Dispatchers.IO){
                usersCollections.document(user.uid).set(it)
            }
        }
    }
    fun getUserbyId(uID:String):Task<DocumentSnapshot>{
        return usersCollections.document(uID).get()
    }
}