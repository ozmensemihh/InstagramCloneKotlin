package com.semihozmen.instagramclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.semihozmen.instagramclonekotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if(auth.currentUser != null){
            val intent = Intent(this@MainActivity,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    fun signIn(view:View){

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if(email.equals("") || password.equals("")){
            Toast.makeText(this@MainActivity,"Enter email and password",Toast.LENGTH_LONG).show()
        }else{
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    val intent = Intent (this@MainActivity,FeedActivity::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
        }

    }

    fun signUp(view: View){

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if(email.equals("") || password.equals("")){
            Toast.makeText(this@MainActivity,"Enter email and password",Toast.LENGTH_LONG).show()
        }else{
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {

                    val intent = Intent(this@MainActivity,FeedActivity::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
        }

    }
}