package com.rinosystems.quizwithfirebase

class Reportes {

  private  var date: String = ""
   private var description: String = ""
   private var fullname: String = ""
   private var profileimage: String = ""
   private var reporteImage: String = ""
   private var time: String = ""
   private var uid: String = ""
    private var status: String = ""

    constructor(
        date: String,
        description: String,
        fullname: String,
        profileimage: String,
        reporteImage: String,
        time: String,
        uid: String,
        status: String
    ) {
        this.date = date
        this.description = description
        this.fullname = fullname
        this.profileimage = profileimage
        this.reporteImage = reporteImage
        this.time = time
        this.uid = uid
        this.status = status
    }

    constructor()

    fun getStatus():String{
        return status
    }

    fun getDate():String{
        return date
    }
    fun getDescription():String{
        return description
    }
    fun getFullname():String{
        return fullname
    }
    fun getProfileimage():String{
        return profileimage
    }
    fun getReporteImage():String{
        return reporteImage
    }
    fun getTime():String{
        return time
    }
    fun getUid():String{
        return uid
    }


}