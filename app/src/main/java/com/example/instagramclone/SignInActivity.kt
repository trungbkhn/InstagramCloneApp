package com.example.instagramclone

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.viewbinding.ViewBinding
import com.example.instagramclone.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

private lateinit var binding: ActivitySignInBinding
class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSignupNewaccount.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            loginUser()

        }

    }

    private fun loginUser() {
        val email = binding.edtEmail.text.toString()
        val passWord = binding.edtPassword.text.toString()

        when{
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "Email is required.",
                Toast.LENGTH_SHORT
            ).show()

            TextUtils.isEmpty(passWord) -> Toast.makeText(
                this,
                "PassWord is required.",
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("SignIn")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email,passWord).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        progressDialog.dismiss()
                        Toast.makeText(this,"Login successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignInActivity,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val message = it.exception!!.toString()
                        Toast.makeText(this,"Error: $message", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        if(FirebaseAuth.getInstance().currentUser != null)
//        {
//            val intent = Intent(this@SignInActivity, SignInActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            finish()
//        }
//    }
}