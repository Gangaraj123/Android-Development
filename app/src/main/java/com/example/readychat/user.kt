package com.example.readychat

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var profile_pic_uri: String? = null
     constructor()
    constructor(name: String, email: String, uid: String, profile_pic_uri: String? = null) {
        this.name = name
        this.email = email
        this.uid = uid
        if (profile_pic_uri != null) this.profile_pic_uri = profile_pic_uri
    }

}