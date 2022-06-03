package com.example.readychat.ui.models

class Friend_Request {
      var senderName:String=""
      var receiverName:String=""
    var senderId:String=""
    var senderMail:String=""
    var receiverMail:String=""
    var receiverId:String=""
    var  request_message=""
     constructor()
    constructor(sname:String?=null,rname:String?=null,sId:String?=null,rId:String?=null
    ,rmessage:String?=null,smail:String?=null,rmail:String?=null)
    {
        if (smail != null) {
            senderMail=smail
        }
        if (rmail != null) {
            receiverMail=rmail
        }
        if (sname != null) {
            senderName=sname
        }
        if (rname != null) {
            receiverName=rname
        }
        if (sId != null) {
            senderId=sId
        }
        if (rId != null) {
            receiverId=rId
        }
        if (rmessage != null) {
            request_message=rmessage
        }
    }
}