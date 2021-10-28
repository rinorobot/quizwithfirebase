package com.rinosystems.quizwithfirebase

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_setup.*

class SetupActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var UsersRef: DatabaseReference
    lateinit var NipRef: DatabaseReference
    lateinit var UserProfileImageRef: StorageReference
    lateinit var currentUserID: String
    //RadioButton
    var nip = ""
    var nip_pulso = ""
    var nip_status = false


    lateinit var loadingBar: ProgressDialog

    val Gallery_Pick = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        loadingBar = ProgressDialog(this)



        rbAdmin.setOnClickListener({
                      setup_nip.visibility = View.VISIBLE
            nip_status = true

        })
        rbDocente.setOnClickListener({
            setup_nip.visibility = View.GONE
            nip_status = false
        }
        )

        rbEstudiante.setOnClickListener({
            setup_nip.visibility = View.GONE
            nip_status = false
        })









        mAuth = FirebaseAuth.getInstance()

        currentUserID = mAuth.currentUser!!.uid
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID)
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile images")




        setup_information_button.setOnClickListener {
            SaveAccountSetupInformation()
        }

        setup_profile_image.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT)
            galleryIntent.setType("image/*")
            startActivityForResult(galleryIntent, Gallery_Pick)
        }

        UsersRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    if (snapshot.hasChild("profileimage")){
                        val image = snapshot.child("profileimage").value.toString()

                        Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(setup_profile_image)
                    }else{
                        Toast.makeText(this@SetupActivity,"Selecciona primero una imagen",Toast.LENGTH_LONG).show()
                    }




                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        })


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

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            val result = CropImage.getActivityResult(data)




            if (resultCode == Activity.RESULT_OK){
                loadingBar.setTitle("Foto de perfil")
                loadingBar.setMessage("Por favor espera un momento en lo que se actualiza tu foto de perfil")
                loadingBar.setCanceledOnTouchOutside(true)
                loadingBar.show()


                val reultUri = result.uri

                val filePath = UserProfileImageRef.child(currentUserID+".jpg")

                filePath.putFile(reultUri).addOnCompleteListener{it ->

                    if (it.isSuccessful){
                        Toast.makeText(this,"La imagen de tu perfil ha sido guardada correctamente",Toast.LENGTH_LONG).show()

                       it.result!!.storage.downloadUrl.addOnSuccessListener{
                           val downloadUrl = it.toString()
                               UsersRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener {task ->
                                   if (task.isSuccessful){

                                       val selfIntent = Intent(this,SetupActivity::class.java)
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

    private fun SaveAccountSetupInformation() {

        val cuenta = setup_cuenta.text.toString()
        val fullname = setup_fullname.text.toString()
        val cellphone = setup_cellphone.text.toString()

        //RadioButton
        val ocupationID = ocupationSelected.checkedRadioButtonId
        val docenteID = rbDocente.id
        val estudianteID = rbEstudiante.id
        val adminID = rbAdmin.id
        var ocupation = "Sin definir"








        if (cuenta.isEmpty()){
            Toast.makeText(this,"Por favor escribe tu número de cuenta o de trabajador...", Toast.LENGTH_LONG).show()
        }
        else if (fullname.isEmpty()){
            Toast.makeText(this,"Por favor escribe tu nombre completo...", Toast.LENGTH_LONG).show()
        }
        else if (cellphone.isEmpty()){
            Toast.makeText(this,"Por favor escribe tu teléfono celular...", Toast.LENGTH_LONG).show()
        }else if (ocupationID == -1){
            Toast.makeText(this,"Selecciona una ocupación...", Toast.LENGTH_LONG).show()
        }
        else{
            loadingBar.setTitle("Creando una nueva cuenta")
            loadingBar.setMessage("Por favor espera un momento en lo que se crea la cuenta")
            loadingBar.show()
            loadingBar.setCanceledOnTouchOutside(true)

            if (ocupationID==docenteID){
                ocupation = "Docente"
            }
            if (ocupationID==estudianteID){
                ocupation = "Estudiante"
            }
            if (ocupationID==adminID){
                ocupation = "Administrador de app"


            }


            val userMap = HashMap<String,Any>()

            userMap.put("cuenta",cuenta)
            userMap.put("fullname",fullname)
            userMap.put("cellphone",cellphone)
            userMap.put("ocupation",ocupation)
            userMap.put("status","Desconectado")


            if (nip_status==true){

                nip = setup_nip.text.toString()

                if (nip.isEmpty()){
                    Toast.makeText(this,"Escribre la contraseña de administrador de app", Toast.LENGTH_LONG).show()
                }else{

                    NipRef = FirebaseDatabase.getInstance().getReference()
                    NipRef.addValueEventListener(object : ValueEventListener{


                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.exists()) {

                                if (snapshot.hasChild("nip_admin")) {

                                    nip_pulso = snapshot.child("nip_admin").value.toString()

                                    if (nip_pulso.equals(nip)){
                                        //Suscribo al admin a notificaciones
                                        FirebaseMessaging.getInstance().subscribeToTopic("/topics/admins")


                                        UsersRef.updateChildren(userMap).addOnCompleteListener {task ->
                                                if (task.isSuccessful){
                                                    loadingBar.dismiss()
                                                    SendUserToMainActivity()
                                                    Toast.makeText(this@SetupActivity,"Tu cuenta fue creada exitosamente",Toast.LENGTH_LONG).show()
                                                }else{
                                                    val message = task.exception!!.message
                                                    loadingBar.dismiss()
                                                    Toast.makeText(this@SetupActivity,"Ocurrió el siguiente error: "+message,Toast.LENGTH_LONG).show()
                                                }

                                            }


                                    }else{
                                        Toast.makeText(this@SetupActivity,"La contraseña no es la correcta: vuelve a intentarlo",Toast.LENGTH_LONG).show()
                                        loadingBar.dismiss()
                                    }


                                }
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@SetupActivity,"Ocurrió un error: vuelve a intentarlo",Toast.LENGTH_LONG).show()
                            loadingBar.dismiss()

                        }

                    })

                }

            }else{

                UsersRef.updateChildren(userMap).addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        loadingBar.dismiss()
                        SendUserToMainActivity()
                        Toast.makeText(this@SetupActivity,"Tu cuenta fue creada exitosamente",Toast.LENGTH_LONG).show()
                    }else{
                        val message = task.exception!!.message
                        Toast.makeText(this@SetupActivity,"Ocurrió el siguiente error: "+message,Toast.LENGTH_LONG).show()
                    }

                }

            }







        }


    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this,PrincipalActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()

    }










}















