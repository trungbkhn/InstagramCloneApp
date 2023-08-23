package com.example.instagramclone.Data

class User {
    private var username: String = ""
    private var fullname: String = ""
    private var bio:      String = ""
    private var image:    String = ""
    private var uid:      String = ""
    private var email:      String = ""

    constructor()
    constructor(username: String, fullname: String, bio: String, image: String, email: String, uid: String)
    {
        this.username = username
        this.fullname = fullname
        this.bio      = bio
        this.image    = image
        this.uid      = uid
        this.email = email
    }

    fun getUserName(): String
    {
        return username
    }

    fun setUserName(username: String)
    {
        this.username = username
    }

    fun getFullname(): String
    {
        return fullname
    }

    fun setFullname(fullname: String)
    {
        this.fullname = fullname
    }

    fun getBio(): String
    {
        return bio
    }

    fun setBio(bio: String)
    {
        this.bio = bio
    }

    fun getImage(): String
    {
        return image
    }

    fun setImage(image: String)
    {
        this.image = image
    }

    fun getUID(): String
    {
        return uid
    }

    fun setUID(uid: String)
    {
        this.uid = uid
    }

    fun getEmail(): String
    {
        return email
    }

    fun setEmail(uid: String)
    {
        this.email = email
    }


}


