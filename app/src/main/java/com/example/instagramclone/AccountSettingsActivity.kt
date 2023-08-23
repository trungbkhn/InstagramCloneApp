package com.example.instagramclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.instagramclone.Data.User
import com.example.instagramclone.databinding.ActivityAccountSettingsBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URL

private lateinit var binding: ActivityAccountSettingsBinding
private lateinit var firebaseUser: FirebaseUser
private lateinit var profileId: String
private var check = ""
private var imageUri: Uri? = null
private var myUrl = ""
private var storageProfilePicRef: StorageReference? = null


class AccountSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")





        binding.btnLogoutAccount.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        binding.imgProfileInfor.setOnClickListener {0
            if (check == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfoOnly()
            }
        }
        binding.txtChangeImage.setOnClickListener {
            check = "clicked"
            CropImage.activity().setAspectRatio(1, 1)
                .start(this@AccountSettingsActivity)
        }
        userInfo(binding.edtUsername, binding.edtFullname, binding.edtBio, binding.cirImgVProfile)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        val cirImgV_profile = findViewById<CircleImageView>(R.id.cirImgV_profile)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            cirImgV_profile.setImageURI(imageUri)
        } else {

        }
    }


    private fun updateUserInfoOnly() {
        when {
            TextUtils.isEmpty(binding.edtFullname.text.toString()) -> {
                Toast.makeText(
                    this@AccountSettingsActivity,
                    "Please write full name first",
                    Toast.LENGTH_SHORT
                )
            }

            binding.edtUsername.text.toString() == "" -> {
                Toast.makeText(
                    this@AccountSettingsActivity,
                    "Please write username first",
                    Toast.LENGTH_SHORT
                )
            }

            binding.edtBio.text.toString() == "" -> {
                Toast.makeText(
                    this@AccountSettingsActivity,
                    "Please write your bio first",
                    Toast.LENGTH_SHORT
                )
            }

            else -> {
                val usersRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("Users")
                val userMap = HashMap<String, Any>()
                userMap["fullname"] = binding.edtFullname.text.toString().toLowerCase()
                userMap["username"] = binding.edtUsername.text.toString().toLowerCase()
                userMap["bio"] = binding.edtBio.text.toString().toLowerCase()
                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(
                    this@AccountSettingsActivity,
                    "Update successfully",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
    }

    private fun uploadImageAndUpdateInfo() {

        when {
            imageUri == null -> Toast.makeText(
                this,
                "Please select image first.",
                Toast.LENGTH_SHORT
            )

            TextUtils.isEmpty(binding.edtFullname.text.toString()) -> {
                Toast.makeText(
                    this@AccountSettingsActivity,
                    "Please write full name first",
                    Toast.LENGTH_SHORT
                )
            }

            binding.edtUsername.text.toString() == "" -> {
                Toast.makeText(
                    this@AccountSettingsActivity,
                    "Please write username first",
                    Toast.LENGTH_SHORT
                )
            }

            binding.edtBio.text.toString() == "" -> {
                Toast.makeText(
                    this@AccountSettingsActivity,
                    "Please write your bio first",
                    Toast.LENGTH_SHORT
                )
            }

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile...")
                progressDialog.show()
                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (
                    OnCompleteListener<Uri> {
                        if (it.isSuccessful) {
                            val downloadUrl = it.result
                            myUrl = downloadUrl.toString()
                            val ref = FirebaseDatabase.getInstance().reference.child("Users")
                            val userMap = HashMap<String, Any>()
                            userMap["fullname"] = binding.edtFullname.text.toString().toLowerCase()
                            userMap["username"] = binding.edtUsername.text.toString().toLowerCase()
                            userMap["bio"] = binding.edtBio.text.toString().toLowerCase()
                            userMap["image"] = myUrl

                            ref.child(firebaseUser.uid).updateChildren(userMap)
                            Toast.makeText(
                                this,
                                "Account Information has been updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(this@AccountSettingsActivity, MainActivity::class.java)
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

    private fun userInfo(
        txtFragmentUsername: EditText?,
        txtFullnameProfileFrag: EditText?,
        txtBioProfile: EditText?,
        cimgvprofile: CircleImageView?
    ) {
        val userRef2 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        userRef2.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                val user = dataSnapshot.getValue<User>(User::class.java)
                Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                    .into(cimgvprofile)
                txtFragmentUsername?.setText(user!!.getUserName())
                txtFullnameProfileFrag?.setText(user!!.getFullname())
                txtBioProfile?.setText(user!!.getBio())
            } else {
                val error = task.exception
            }
        }

    }
}