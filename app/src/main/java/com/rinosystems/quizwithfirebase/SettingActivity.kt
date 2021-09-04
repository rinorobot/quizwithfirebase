package com.rinosystems.quizwithfirebase

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    private val Gallery_Pick = 1
    private lateinit var SettingsUserRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUserId: String
    lateinit var loadingBar: ProgressDialog
    lateinit var UserProfileImageRef: StorageReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        loadingBar = ProgressDialog(this)


        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser!!.uid
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile images")



        SettingsUserRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        val myProfileImage = snapshot.child("profileimage").getValue().toString()
                        val myCountry = snapshot.child("country").getValue().toString()
                        val myDOP = snapshot.child("dop").getValue().toString()
                        val myFullname = snapshot.child("fullname").getValue().toString()
                        val myGender = snapshot.child("gender").getValue().toString()
                        val myStatus = snapshot.child("status").getValue().toString()
                        val myUsername = snapshot.child("username").getValue().toString()

                        Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_launcher_foreground).into(settings_profile_image)

                        settings_username.setText(myUsername)
                        settings_dob.setText(myDOP)
                        settings_numero_cuenta.setText(myCountry)
                        settings_fullname.setText(myFullname)
                        settings_status.setText(myStatus)

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }



            })



        update_account_settings_buttons.setOnClickListener {
           UpdateAccountSettingsButton()
        }
        
        settings_profile_image.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT)
            galleryIntent.setType("image/*")
            startActivityForResult(galleryIntent, Gallery_Pick)
        }
        
    }

    private fun UpdateAccountSettingsButton() {
        val username = settings_username.text.toString()
        val fullname = settings_fullname.text.toString()
        val status = settings_status.text.toString()
        val cuenta = settings_numero_cuenta.text.toString()
        val dop = settings_dob.text.toString()

        if (username.isEmpty()){
            Toast.makeText(this,"Escribe tu username",Toast.LENGTH_LONG).show()
        }
        else if (fullname.isEmpty()){
            Toast.makeText(this,"Escribe tu fullname",Toast.LENGTH_LONG).show()
        }
        else if (status.isEmpty()){
            Toast.makeText(this,"Escribe tu status",Toast.LENGTH_LONG).show()
        }
        else if (cuenta.isEmpty()){
            Toast.makeText(this,"Escribe tu cuenta",Toast.LENGTH_LONG).show()
        }
        else if (dop.isEmpty()){
            Toast.makeText(this,"Escribe tu dop",Toast.LENGTH_LONG).show()
        }
        else
        {
            loadingBar.setTitle("Foto de perfil")
            loadingBar.setMessage("Por favor espera un momento en lo que se actualiza tu foto de perfil")
            loadingBar.setCanceledOnTouchOutside(true)
            loadingBar.show()
            UpdateAccountInfo(username,fullname,status,cuenta,dop)
        }


    }

    private fun UpdateAccountInfo(username: String, fullname: String, status: String, cuenta: String, dop: String) {

        val userMap = HashMap<String,Any>()
        userMap.put("username",username)
        userMap.put("fullname",fullname)
        userMap.put("status",status)
        userMap.put("cuenta",cuenta)
        userMap.put("dop",dop)

        SettingsUserRef.updateChildren(userMap).addOnCompleteListener {

            if (it.isSuccessful){
                SendUserToMainActivity()
                Toast.makeText(this,"Cuenta actualizada con éxito", Toast.LENGTH_LONG).show()
                loadingBar.dismiss()
            }else{
                Toast.makeText(this,"Ocurrió un error al intentar actulizar la cuenta", Toast.LENGTH_LONG).show()
                loadingBar.dismiss()
            }


        }



    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this,PrincipalActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==Gallery_Pick && resultCode == Activity.RESULT_OK && data!=null){
            var ImageUri = data.data


            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this)
        }

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            val result = CropImage.getActivityResult(data)




            if (resultCode == Activity.RESULT_OK){
                loadingBar.setTitle("Foto de perfil")
                loadingBar.setMessage("Por favor espera un momento en lo que se actualiza tu foto de perfil")
                loadingBar.setCanceledOnTouchOutside(true)
                loadingBar.show()


                val reultUri = result.uri

                val filePath = UserProfileImageRef.child(currentUserId+".jpg")

                filePath.putFile(reultUri).addOnCompleteListener{it ->

                    if (it.isSuccessful){
                        Toast.makeText(this,"La imagen de tu perfil ha sido guardada correctamente",Toast.LENGTH_LONG).show()

                        it.result!!.storage.downloadUrl.addOnSuccessListener{
                            val downloadUrl = it.toString()
                            SettingsUserRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener {task ->
                                if (task.isSuccessful){

                                    val selfIntent = Intent(this,SettingActivity::class.java)
                                    startActivity(selfIntent)

                                    Toast.makeText(this,"La imagen de tu perfil ha sido guardada correctamente en la base de datos",Toast.LENGTH_LONG).show()
                                    loadingBar.dismiss()
                                }else{
                                    val message = task.exception!!.message
                                    Toast.makeText(this,"Ocurrió un error: "+message,Toast.LENGTH_LONG).show()
                                    loadingBar.dismiss()
                                }

                            }

                        }


                    }

                }
            }
        }else{
            Toast.makeText(this,"Ocurrió un error con el recorte de la imagen. Intenta de nuevo",Toast.LENGTH_LONG).show()
            loadingBar.dismiss()
        }


    }



}
