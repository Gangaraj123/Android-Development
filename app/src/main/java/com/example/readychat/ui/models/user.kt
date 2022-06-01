package com.example.readychat.ui.models

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var about:String="Hey there! I am using Ready chat"
    var profile_pic_url: String? = null
     constructor()
    constructor(name: String, email: String, uid: String, ab:String?=null,profile_pic_url: String? = null) {
        this.name = name
        this.email = email
        this.uid = uid

        if (ab != null) {
            about=ab
        }
        if (profile_pic_url != null) this.profile_pic_url = profile_pic_url
    }

}