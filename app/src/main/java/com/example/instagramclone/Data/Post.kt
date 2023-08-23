package com.example.instagramclone.Data

class Post {
    private var description: String = ""
    private var postid: String = ""
    private var postimage:      String = ""
    private var publisher:    String = ""

    constructor()
    constructor(description: String, postid: String, publisher: String, postimage: String)
    {
        this.description = description
        this.postid = postid
        this.postimage      = postimage
        this.publisher    = publisher
    }

    fun getDescription(): String
    {
        return description
    }

    fun setDescription(description: String)
    {
        this.description = description
    }

    fun getPostid(): String
    {
        return postid
    }

    fun setPostid(postid: String)
    {
        this.postid = postid
    }

    fun getPostimage(): String
    {
        return postimage
    }

    fun setPostimage(postimage: String)
    {
        this.postimage = postimage
    }

    fun getPublisher(): String
    {
        return publisher
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }



}


