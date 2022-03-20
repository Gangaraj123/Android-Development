package com.example.readychat

class Message {
    var message: String? = null
    var senderId: String? = null
    constructor()
    constructor(message: String,sid:String)
    {
        this.message=message
        this.senderId=sid
    }
}