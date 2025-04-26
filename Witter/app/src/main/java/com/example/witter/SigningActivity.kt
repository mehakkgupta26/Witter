package com.example.witter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.witter.daos.UsersDao
import com.example.witter.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext



class SigningActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int=123
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG="SigningActivity Tag"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signing)

        val Buttongoogle = findViewById<SignInButton>(R.id.signingbutton)



        auth= Firebase.auth
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        Buttongoogle.setOnClickListener{
            signIn()
        }

        }
    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        updateUI(currentUser)



    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,"ffailed",Toast.LENGTH_LONG).show();
                Toast.makeText(this,e.message,Toast.LENGTH_LONG).show();
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential=GoogleAuthProvider.getCredential(idToken,null)
        signingbutton.visibility=View.GONE
        progressbar.visibility=View.VISIBLE
        text.visibility=View.GONE
        GlobalScope.launch(Dispatchers.IO){
            val auth=auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }
    }



    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser!=null){
            val user= User(firebaseUser.uid,firebaseUser.displayName,firebaseUser.photoUrl.toString())
            val usersdao=UsersDao()
            usersdao.addUser(user)

            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            signingbutton.visibility=View.VISIBLE
            progressbar.visibility=View.GONE
            text.visibility=View.VISIBLE
        }

    }


}

