package com.example.readychat.ui.models

class Message {
    var message: String? = null
    var senderId: String? = null
    var ImageUrl:String?=null
    constructor()
    constructor(message: String?=null,sid:String,imgurl:String?=null)
    {
        this.message=message
        this.senderId=sid
        this.ImageUrl=imgurl
    }
}