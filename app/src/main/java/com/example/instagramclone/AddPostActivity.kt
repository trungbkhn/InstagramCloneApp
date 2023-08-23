package com.example.instagramclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var myUrl = ""
    private var storageProfilePicRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")

        val btn_saveAddPost = findViewById<ImageButton>(R.id.btn_saveAddPost)
        btn_saveAddPost.setOnClickListener {
            UploadImage()
        }
        CropImage.activity().setAspectRatio(2, 1).start(this@AddPostActivity)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val img_post = findViewById<ImageView>(R.id.img_post)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            img_post.setImageURI(imageUri)
        } else {

        }
    }

    private fun UploadImage() {
        val edt_descriptionPost = findViewById<EditText>(R.id.edt_descriptionPost)
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Adding new post ")
        progressDialog.setMessage("Please wait, we are adding your picture post...")
        progressDialog.show()
        when {
            imageUri == null -> Toast.makeText(
                this,
                "Please select image first.",
                Toast.LENGTH_SHORT
            )

            TextUtils.isEmpty(edt_descriptionPost.text.toString()) -> {
                Toast.makeText(
                    this@AddPostActivity,
                    "Please write the description",
                    Toast.LENGTH_SHORT
                )

            }

            else -> {
                val fileRef =
                    storageProfilePicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri> {
                        if (it.isSuccessful) {
                            val edt_descriptionPost = findViewById<EditText>(R.id.edt_descriptionPost)
                            val downloadUrl = it.result
                            myUrl = downloadUrl.toString()
                            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                            val postId = ref.push().key
                            val postMap = HashMap<String, Any>()
                            postMap["postid"] = postId!!
                            postMap["description"] = edt_descriptionPost.text.toString().toLowerCase()
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["postimage"] = myUrl

                            ref.child(postId).updateChildren(postMap)
                            Toast.makeText(
                                this,
                                "Post uploaded successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(this@AddPostActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            progressDialog.dismiss()
                        } else {
                            progressDialog.dismiss()
                        }
                    }
                )
            }
        }
    }
}