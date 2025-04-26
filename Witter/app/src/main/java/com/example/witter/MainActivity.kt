package com.example.witter

import android.app.DownloadManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.witter.daos.postDao
import com.example.witter.models.post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var postDao: postDao
    private lateinit var adapter: PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        add.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }
        setRecyclerView()
    }

    private fun setRecyclerView() {
        postDao= postDao()
        val postCollections=postDao.postcollection
        val query=postCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions=FirestoreRecyclerOptions.Builder<post>().setQuery(query,post::class.java).build()

        adapter= PostAdapter(recyclerViewOptions,this)

        recycleView.adapter=adapter
        recycleView.layoutManager=LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item ->{
                logOut()
                return true
            }else ->return super.onOptionsItemSelected(item)

        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this, SigningActivity::class.java)
        startActivity(intent)
        finish()
    }
}