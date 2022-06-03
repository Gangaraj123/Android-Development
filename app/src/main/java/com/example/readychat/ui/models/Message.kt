package com.example.readychat.ui.models

import com.google.firebase.Timestamp
import java.util.Date

class Message {
    var message: String? = null
    var senderId: String? = null
    var ImageUrl: String? = null
    var timestamp: Long=0

    constructor()
    constructor(message: String? = null, sid: String, imgurl: String? = null ) {
        this.message = message
        this.senderId = sid
        this.ImageUrl = imgurl
        timestamp=System.currentTimeMillis()
    }
}