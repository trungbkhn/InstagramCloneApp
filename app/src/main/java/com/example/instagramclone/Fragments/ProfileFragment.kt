package com.example.instagramclone.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.instagramclone.AccountSettingsActivity
import com.example.instagramclone.R
import com.example.instagramclone.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var  profileId: String = ""
    private var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val tv_total_following = view.findViewById<TextView>(R.id.tv_total_following)
        val tv_total_followers = view.findViewById<TextView>(R.id.tv_total_followers)
        val txt_fragment_username = view.findViewById<TextView>(R.id.txt_fragment_username)
        val txt_fullname_profile_frag = view.findViewById<TextView>(R.id.txt_fullname_profile_frag)
        val txt_bio_profile = view.findViewById<TextView>(R.id.txt_bio_profile)
        val cimgv_profile = view.findViewById<CircleImageView>(R.id.cimgv_profile)
        val btn_account_setting = view.findViewById<Button>(R.id.btn_account_setting)


        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            if (pref.contains("profileId")) {
                this.profileId = pref.getString("profileId", null).toString()
            } else {
                profileId = firebaseUser.uid
            }
        }
        else{
            this.profileId == firebaseUser.uid
        }

        if (profileId == firebaseUser.uid) {
            btn_account_setting.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowingButton(btn_account_setting)
        }


        btn_account_setting.setOnClickListener {
            val getButtonText = btn_account_setting.text.toString()

            when {
                getButtonText == "Edit Profile" -> startActivity(
                    Intent(
                        context,
                        AccountSettingsActivity::class.java
                    )
                )

                getButtonText == "Follow" -> {
                    firebaseUser?.uid.let {
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it.toString())
                            .child("Following").child(profileId).setValue(true)
                    }
                    firebaseUser?.uid.let {
                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                            .child("Followers").child(it.toString()).setValue(true)
                    }
                    btn_account_setting.text = "Following"
                }

                getButtonText == "Following" -> {
                    firebaseUser?.uid.let {
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it.toString())
                            .child("Following").child(profileId).removeValue()
                    }
                    firebaseUser?.uid.let {
                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                            .child("Followers").child(it.toString()).removeValue()
                    }
                    btn_account_setting.text = "Follow"
                }
            }
//
        }
        if (profileId == firebaseUser.uid) {
            userInfo(
                txt_fragment_username,
                txt_fullname_profile_frag,
                txt_bio_profile,
                cimgv_profile
            )
        } else {
            userInfoLate(
                txt_fragment_username,
                txt_fullname_profile_frag,
                txt_bio_profile,
                cimgv_profile
            )
        }
        getFollowers(tv_total_followers)
        getFollowings(tv_total_following)











        return view
    }

    private fun getFollowings(tv_total_following: TextView) {
        val followersRef1 = profileId?.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(it.toString())
                .child("Following")

        }
        followersRef1?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    tv_total_following.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getFollowers(tv_total_followers: TextView) {
        val followersRef = profileId.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow")
                .child(it1.toString())
                .child("Followers")

        }
        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    tv_total_followers.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun userInfo(
        txtFragmentUsername: TextView?,
        txtFullnameProfileFrag: TextView?,
        txtBioProfile: TextView?,
        cimgvprofile: CircleImageView?
    ) {
        val userRef2 = FirebaseDatabase.getInstance().getReference("Users").child(profileId)

        userRef2.get().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val dataSnapshot = task.result
                val user = dataSnapshot.getValue<User>(User::class.java)
                Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                    .into(cimgvprofile)
                txtFragmentUsername?.text = user!!.getUserName()
                txtFullnameProfileFrag?.text = user!!.getFullname()
                txtBioProfile?.text = user!!.getBio()
            } else {
                val error = task.exception
            }
        }

    }

    private fun userInfoLate(
        txtFragmentUsername: TextView,
        txtFullnameProfileFrag: TextView,
        txtBioProfile: TextView,
        cimgvprofile: CircleImageView
    ) {
        val userRef3 = FirebaseDatabase.getInstance().getReference("Users").child(profileId)
        userRef3.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(cimgvprofile)
                    txtFragmentUsername.text = user!!.getUserName()
                    txtFullnameProfileFrag.text = user!!.getFullname()
                    txtBioProfile.text = user!!.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun checkFollowAndFollowingButton(btn_account_setting: Button) {
        val followingRef = profileId?.let {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(firebaseUser.uid).child("Following")
        }
        if (followingRef != null) {
            followingRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()) {
                        btn_account_setting.text = "Following"
                    } else {
                        btn_account_setting.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }


    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()

    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}