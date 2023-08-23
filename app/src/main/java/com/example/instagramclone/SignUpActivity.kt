package com.example.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.example.instagramclone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var binding: ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSigninAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            CreateAccount()
        }

    }

    private fun CreateAccount() {
        val fullName = binding.edtFullnameSingup.text.toString()
        val userName = binding.edtUsernameSingup.text.toString()
        val email = binding.edtEmailSingup.text.toString()
        val passWord = binding.edtPasswordSingup.text.toString()
        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(
                this,
                "Full Name is required.",
                Toast.LENGTH_SHORT
            )

            TextUtils.isEmpty(userName) -> Toast.makeText(
                this,
                "UserName is required.",
                Toast.LENGTH_SHORT
            )

            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "Email is required.",
                Toast.LENGTH_SHORT
            )

            TextUtils.isEmpty(passWord) -> Toast.makeText(
                this,
                "PassWord is required.",
                Toast.LENGTH_SHORT
            )

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, passWord)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserInfo1(fullName, userName, email, progressDialog)
                            progressDialog.dismiss()
                            Toast.makeText(
                                this,
                                "Account has been created successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG)
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo1(
        fullName: String,
        userName: String,
        email: String,
        progressDialog: ProgressDialog
    ) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email.toLowerCase()
        userMap["bio"] = "Using Instagram Clone App."
        userMap["image"] = "gs://instagram-clone-2f4ba.appspot.com/Default Images/profile.png"

        usersRef.child(currentUserID).setValue(userMap).addOnCompleteListener {
            if (it.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(this, "Account has been created successfully.", Toast.LENGTH_SHORT)

                FirebaseDatabase.getInstance().reference
                    .child("Follow")
                    .child(currentUserID)
                    .child("Following").child(currentUserID).setValue(true)


                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            } else {
                val message = it.exception!!.toString()
                Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }
        }
    }
}