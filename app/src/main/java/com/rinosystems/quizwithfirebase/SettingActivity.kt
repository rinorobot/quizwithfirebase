package com.rinosystems.quizwithfirebase

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
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

                        val myFullname = snapshot.child("fullname").getValue().toString()
                        val myCuenta = snapshot.child("cuenta").getValue().toString()
                        val myCellphone = snapshot.child("cellphone").getValue().toString()
                        val myOcupation = snapshot.child("ocupation").getValue().toString()



                        Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_launcher_foreground).into(settings_profile_image)

                        settings_fullname.setText(myFullname)
                        settings_cuenta.setText(myCuenta)
                        settings_cellphone.setText(myCellphone)
                        settings_ocupation.setText(myOcupation)

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

        val fullname = settings_fullname.text.toString()
        val cuenta = settings_cuenta.text.toString()
        val ocupation = settings_ocupation.text.toString()
        val cellphone = settings_cellphone.text.toString()



        if (fullname.isEmpty()){
            Toast.makeText(this,"Escribe tu nombre completo",Toast.LENGTH_LONG).show()
        }
        else if (cuenta.isEmpty()){
            Toast.makeText(this,"Escribe tu número de cuenta",Toast.LENGTH_LONG).show()
        }
        else if (ocupation.isEmpty()){
            Toast.makeText(this,"Escribe tu ocupación",Toast.LENGTH_LONG).show()
        }
        else if (cellphone.isEmpty()){
            Toast.makeText(this,"Escribe tu número de celular",Toast.LENGTH_LONG).show()
        }

        else
        {
            loadingBar.setTitle("Foto de perfil")
            loadingBar.setMessage("Por favor espera un momento en lo que se actualiza tu foto de perfil")
            loadingBar.setCanceledOnTouchOutside(true)
            loadingBar.show()
            UpdateAccountInfo(fullname,cuenta,ocupation,cellphone)
        }


    }

    private fun UpdateAccountInfo(fullname: String, cuenta: String, ocupation: String, cellphone: String) {

        val userMap = HashMap<String,Any>()
        userMap.put("fullname",fullname)
        userMap.put("cuenta",cuenta)
        userMap.put("ocupation",ocupation)
        userMap.put("cellphone",cellphone)

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
