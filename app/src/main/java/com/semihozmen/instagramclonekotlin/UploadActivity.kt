package com.semihozmen.instagramclonekotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.semihozmen.instagramclonekotlin.databinding.ActivityUploadBinding
import java.sql.Timestamp
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<String>
    private var imageUri : Uri? = null
    private lateinit var auth:FirebaseAuth
    private lateinit var database :FirebaseFirestore
    private lateinit var storage:FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLauncher()

        auth = Firebase.auth
        database = Firebase.firestore
        storage = Firebase.storage

    }



    fun selectedImage(view:View){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@UploadActivity,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                    // request permission
                    permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }).show()
            }else{
                // request permission
                permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }else{
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }

    }

    fun share(vies : View){

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if(imageUri != null){
            imageReference.putFile(imageUri!!).addOnSuccessListener {
                // download Url alıp firebaseye kayıt işlemi

                imageReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    val post = hashMapOf<String,Any>()
                    post.put("downloadUrl",downloadUrl)
                    post.put("email",auth.currentUser!!.email!!)
                    post.put("comment",binding.edtYorum.text.toString())
                    post.put("date",com.google.firebase.Timestamp.now())

                    database.collection("Post").add(post).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }

                }



            }.addOnFailureListener{
                Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun registerLauncher() {
        activityResultLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    imageUri = intentFromResult.data
                    imageUri?.let {
                        binding.imgUpload.setImageURI(it)
                    }
                }
            }
        }

        permissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }else{
                Toast.makeText(this@UploadActivity,"Permission Needed",Toast.LENGTH_LONG).show()
            }
        }
    }
}