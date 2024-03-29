package com.rinosystems.quizwithfirebase

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_reporte.*
import org.json.JSONException
import org.json.JSONObject

class ReporteActivity : AppCompatActivity() {




    lateinit var loadingBar: ProgressDialog

    val  Gallery_Pick = 1
    lateinit var ImageUri: Uri
    lateinit var description: String
    lateinit var ubicacion: String
    lateinit var ReportesImagesReference: StorageReference
    lateinit var UsersRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var ReportesRef: DatabaseReference


    lateinit var saveCurrentDate: String
    lateinit var  saveCurrentTime: String
    lateinit var  reporteRandomName: String
    lateinit var downloadURL: String
    lateinit var current_user_id: String

    //Para la notificación
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=" + "AAAAO2HotNs:APA91bEwkUSlMrkFMNijyAcwJf4zAWwdVXciHhqzKlORF5ALWJsEtRHOmY0el5yTpFwe3reBYMRmiXA4167pWlUdnFnQvxnOhyF9HiGJnx4-M5KTguO-mFXw1KJrqTaTircNM3SJx1YN"
    private val contentType = "application/json"





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte)

        mAuth = FirebaseAuth.getInstance()
        current_user_id = mAuth.currentUser!!.uid

        ReportesImagesReference = FirebaseStorage.getInstance().getReference()
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        ReportesRef = FirebaseDatabase.getInstance().getReference().child("Reportes")

        loadingBar = ProgressDialog(this)

        select_post_image.setOnClickListener {
            OpenGallery()
        }

        update_post_button.setOnClickListener {
            ValidatePostInfo()
            EnviandoNotifiacionAAdministradores()

        }


    }

    private fun EnviandoNotifiacionAAdministradores() {

        val topic = "/topics/admins"
        val notification = JSONObject()
        val notificationBody = JSONObject()

        try {
            val mensaje =""+report_description.text.toString().subSequence(0,27) + "...En "+ reporte_lugar.text.toString().subSequence(0,15)
            notificationBody.put("title","Nuevo reporte")
            notificationBody.put("message",mensaje)
            notification.put("to",topic)
            notification.put("data",notificationBody)
            Log.e("TAG", "try")
        }catch (e: JSONException){
            Log.e("TAG", "onCreate: " + e.message)
        }

        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->


            },
            Response.ErrorListener {
                Toast.makeText(this, "Request error", Toast.LENGTH_LONG).show()

            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)



    }

    private fun ValidatePostInfo() {
         description = report_description.text.toString()
        ubicacion = reporte_lugar.text.toString()

        if (ImageUri == null){
            Toast.makeText(this,"Por favor selecciona una imagen", Toast.LENGTH_LONG).show()
        }else if(description.isEmpty()){
            Toast.makeText(this,"Por favor, describe tu reporte", Toast.LENGTH_LONG).show()
        }else if (ubicacion.isEmpty()){
            Toast.makeText(this,"Por favor, escribe la ubicación de tu reporte", Toast.LENGTH_LONG).show()
        }
        else{
            loadingBar.setTitle("Creando nuevo reporte")
            loadingBar.setMessage("Por favor espera un mientras se registra el reporte")
            loadingBar.show()
            loadingBar.setCanceledOnTouchOutside(true)

            StoringImageToFirebaseStore()
        }
    }

    private fun StoringImageToFirebaseStore() {
       val calForDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd-MMMM-yyyy")
        saveCurrentDate = currentDate.format(calForDate.time)

        val calForTime = Calendar.getInstance()
        val currentTime = SimpleDateFormat("HH:mm")
        saveCurrentTime = currentTime.format(calForTime.time)

        reporteRandomName = saveCurrentDate+saveCurrentTime



        val filePath = ReportesImagesReference.child("Reportes Images").child(ImageUri.lastPathSegment!!+reporteRandomName+".jpg")

        filePath.putFile(ImageUri).addOnCompleteListener{ task->
           if (task.isSuccessful){
               Toast.makeText(this,"Imagen guardada correctamente",Toast.LENGTH_LONG).show()

               task.result!!.storage.downloadUrl.addOnSuccessListener {
                    downloadURL = it.toString()




                    SavingPostInformationToDatabase()
                }




            }else{
                val message = task.exception!!.message
                Toast.makeText(this,"Ocurrió un error: "+message,Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun SavingPostInformationToDatabase() {
        UsersRef.child(current_user_id).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userFullName = snapshot.child("fullname").value.toString()
                    val userProfileImage = snapshot.child("profileimage").value.toString()

                    val reporteMap = HashMap<String,Any>()
                    reporteMap.put("uid",current_user_id)
                    reporteMap.put("date",saveCurrentDate)
                    reporteMap.put("time",saveCurrentTime)
                    reporteMap.put("description",description)
                    reporteMap.put("reporteImage",downloadURL)
                    reporteMap.put("ubicacion",ubicacion)
                    reporteMap.put("profileimage",userProfileImage)
                    reporteMap.put("fullname",userFullName)
                    reporteMap.put("status","Pendiente")

                    ReportesRef.child(current_user_id+reporteRandomName).updateChildren(reporteMap).addOnCompleteListener {
                        if (it.isSuccessful){
                            SendUserToMainActivity()
                            Toast.makeText(this@ReporteActivity,"El reporte se guardó con éxito", Toast.LENGTH_LONG).show()
                            loadingBar.dismiss()

                        }else{
                            val message = it.exception!!.message.toString()
                            Toast.makeText(this@ReporteActivity,"Ocurrió un error: "+message, Toast.LENGTH_LONG).show()
                            loadingBar.dismiss()
                        }
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }



        })

    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this,PrincipalActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }

    private fun OpenGallery() {
        val galleryIntent = Intent()
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT)
        galleryIntent.setType("image/*")
        startActivityForResult(galleryIntent, Gallery_Pick)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==Gallery_Pick && resultCode == Activity.RESULT_OK && data != null){

            ImageUri = data.data!!
            select_post_image.setImageURI(ImageUri)
        }

    }
}
