package com.example.witter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.witter.daos.postDao
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    private lateinit var postdao:postDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        postdao= postDao()

        postb.setOnClickListener {
            val input=postinput.text.toString().trim()
            if(input.isNotEmpty()){
                postdao.addpost(input)
                finish()
            }
        }

    }
}