package com.rinosystems.quizwithfirebase

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_principal.*
import org.json.JSONException
import org.json.JSONObject

class PrincipalActivity : AppCompatActivity() {



    //Bandera para eliminar cuenta
    var flagUser = false
    lateinit var mAuth: FirebaseAuth
    lateinit var UsersRef: DatabaseReference
    lateinit var ReportsRef: DatabaseReference
    var userID =  ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        mAuth = FirebaseAuth.getInstance()
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users")

        val currentUser = mAuth.currentUser?.uid
        userID =  currentUser.toString()


        if (currentUser!=null) {


            UsersRef.child(currentUser).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChild("fullname")) {
                            val fullname = snapshot.child("fullname").value.toString()
                            principal_fullname.text = fullname
                        }
                        if (snapshot.hasChild("profileimage")) {
                            val image = snapshot.child("profileimage").value.toString()
                            Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground)
                                .into(principal_profile_image)
                        }

                    }
                    else {
                        Toast.makeText(this@PrincipalActivity, "El usuario no existe", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })

        }


        principal_button_logout.setOnClickListener {
            mAuth.signOut()
            SendUserToLoginActivity()
        }
        principal_btn_misreportes.setOnClickListener {
            val intent = Intent(this,MisReportesActivity::class.java)
            startActivity(intent)
        }

        button_settings.setOnClickListener {
            SendUserToSettingsActivity()
        }


        ReportsRef = FirebaseDatabase.getInstance().getReference("Reportes")

        principal_button_delete.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("¿Estás seguro que deseas eliminar tu cuenta?")
            builder.setMessage("Los reportes y otros datos también se eliminarán")

            builder.setPositiveButton("Sí",DialogInterface.OnClickListener { dialogInterface, i ->



                ReportsRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children){
                         for (dsw in ds.children){
                             if (dsw.key!!.contentEquals("uid")){
                                 if (dsw.getValue().toString().contentEquals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                     ReportsRef.child(ds.key!!).setValue(null)
                                 }
                             }
                         }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

                UsersRef.child(userID).removeValue()


                FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener {

                    if (it.isSuccessful){
                        FirebaseMessaging.getInstance().deleteToken()
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/admins")







                        Toast.makeText(this,"Cuenta eliminada con éxito",Toast.LENGTH_LONG).show()
                        SendUserToLoginActivity()


                    }else{
                        Toast.makeText(this,"Para eliminar cuenta, vuelve a iniciar sesión. "+it.exception!!.message,Toast.LENGTH_LONG).show()

                    }

                }



            })




            builder.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->

            })

            builder.show()



        }





    }




    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = mAuth.currentUser

        if (currentUser == null){
            SendUserToLoginActivity()
        }else{


            CheckUserExistence()
        }



    }

    private fun CheckUserExistence() {
        val current_user_id = mAuth.currentUser!!.uid

        UsersRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.hasChild(current_user_id)){
                    SendUserToSetupActivity()

                }

            }
            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun SendUserToSetupActivity() {
        val setupIntent = Intent(this,SetupActivity::class.java)
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(setupIntent)
        finish()
    }

    private fun SendUserToLoginActivity() {
        val loginIntent = Intent(this,LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
        finish()

    }

    private fun SendUserToSettingsActivity() {
        val settingIntent = Intent(this,SettingActivity::class.java)
        startActivity(settingIntent)


    }
}
