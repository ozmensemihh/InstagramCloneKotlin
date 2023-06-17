package com.semihozmen.instagramclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.semihozmen.instagramclonekotlin.databinding.ActivityFeedBinding
import kotlinx.coroutines.flow.combineTransform

class FeedActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database :FirebaseFirestore
    private lateinit var postList: ArrayList<Post>
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        auth = Firebase.auth
        database = Firebase.firestore
        postList = ArrayList<Post>()
        getData()

        binding.rv.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(postList)
        binding.rv.adapter = postAdapter


        addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == R.id.action_add){
                    val intent = Intent(this@FeedActivity,UploadActivity::class.java)
                    startActivity(intent)

                }else if(menuItem.itemId == R.id.action_signout){
                    auth.signOut()
                    val intent = Intent(this@FeedActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                return true
            }

        })
    }

    private fun getData(){

        database.collection("Post").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(this@FeedActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents
                        postList.clear()
                        for (document in documents){
                            val comment = document.get("comment") as String
                            val email = document.get("email") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val post = Post(email,comment, downloadUrl)
                            postList.add(post)
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }



    }

}