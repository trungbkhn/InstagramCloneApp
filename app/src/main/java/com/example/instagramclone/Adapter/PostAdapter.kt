package com.example.instagramclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Data.Post
import com.example.instagramclone.Fragments.ProfileFragment
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

class PostAdapter(
    private var mContext: Context,
    private var mPost: List<Post>,
    private var isFragment: Boolean = false,

) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return PostAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val post = mPost[position]
        firebaseUser = FirebaseAuth.getInstance().currentUser
        Picasso.get().load(post!!.getPostimage()).into(holder.img_post_home)
        holder.tv_description.text = post.getDescription()
        publisherInfo(holder.cimgv_profile_post, holder.tv_username_post, holder.tv_publisher, post.getPublisher())
    }

    private fun publisherInfo(cimgvProfilePost: CircleImageView?, tvUsernamePost: TextView?, tvPublisher: TextView?, publisherID: String) {
        val userRef1 = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
//        val userRef2 = FirebaseDatabase.getInstance().reference.child("Posts").child()
        userRef1.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(cimgvProfilePost)
                    tvUsernamePost?.text = user?.getUserName()
                    tvPublisher?.text = user?.getFullname()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }




    override fun getItemCount(): Int {
        return mPost.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cimgv_profile_post = itemView.findViewById<CircleImageView>(R.id.cimgv_profile_post)
        var tv_username_post = itemView.findViewById<TextView>(R.id.tv_username_post)
        var img_post_home = itemView.findViewById<ImageView>(R.id.img_post_home)
        var btn_heartNotLike = itemView.findViewById<ImageButton>(R.id.btn_heartNotLike)
        var btn_comment = itemView.findViewById<ImageButton>(R.id.btn_comment)
        var btn_save_comment = itemView.findViewById<ImageButton>(R.id.btn_save_comment)
        var tv_likes = itemView.findViewById<TextView>(R.id.tv_likes)
        var tv_publisher = itemView.findViewById<TextView>(R.id.tv_publisher)
        var tv_description = itemView.findViewById<TextView>(R.id.tv_description)
        var tv_comment = itemView.findViewById<TextView>(R.id.tv_comment)

    }
}