package com.rinosystems.quizwithfirebase

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_clik_report.*

class ClikReportActivity : AppCompatActivity() {

    private var ReportKey: String? = null
    private lateinit var ClickReportRef: DatabaseReference
    private lateinit var UsersRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUserID: String
    private lateinit var databaseUserID: String
    private lateinit var description: String
    private lateinit var ubication: String
    private lateinit var image: String
    private lateinit var status: String
    var ocupation: String? = null
    var estado = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clik_report)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser!!.uid

        ReportKey = intent.getStringExtra("ReportKey")
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users")

        ClickReportRef = FirebaseDatabase.getInstance().getReference().child("Reportes").child(ReportKey!!)

        delete_report_button.visibility = View.INVISIBLE
        edit_repot_button.visibility = View.INVISIBLE

        ClickReportRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                description = snapshot.child("description").getValue().toString()
                ubication = snapshot.child("ubicacion").getValue().toString()
                image = snapshot.child("reporteImage").getValue().toString()
                databaseUserID = snapshot.child("uid").getValue().toString()
                status = snapshot.child("status").getValue().toString()

                click_report_description.text = description
                click_report_ubication.text = ubication
                Picasso.get().load(image).into(click_report_image)

                if (currentUserID.equals(databaseUserID)||ocupation.equals("Administrador de app")){
                    delete_report_button.visibility = View.VISIBLE
                    edit_repot_button.visibility = View.VISIBLE


                }

                edit_repot_button.setOnClickListener {
                    EditCurrentReport(description,ubication,status)
                }

            }

            }
            override fun onCancelled(error: DatabaseError) {

            }

        })

        delete_report_button.setOnClickListener {
            DeteleCurrentReport()
        }


        //Para mostrar o quitar el status dependiendo de la ocupación
        UsersRef.child(currentUserID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    ocupation = snapshot.child("ocupation").getValue().toString()
                    click_report_status.text = "Status: "+status
                    if (ocupation.equals("Administrador de app")){
                        delete_report_button.visibility = View.VISIBLE
                        edit_repot_button.visibility = View.VISIBLE


                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    private fun EditCurrentReport(description: String,ubication:String,status:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar reporte")

        val twoBoxes = LinearLayout(this)
        twoBoxes.orientation = LinearLayout.VERTICAL

        val inputField = EditText(this)
        inputField.setText(description)

        val inputField2 = EditText(this)
        inputField2.setText(ubication)



        val radioButton = RadioButton(this)
        radioButton.setText("Pendiente")
        val radioButton2 = RadioButton(this)
        radioButton2.setText("En atención")
        val radioButton3 = RadioButton(this)
        radioButton3.setText("Resuelto")
        val radioGroup = RadioGroup(this)

        if (status=="Pendiente"){
            radioButton.isChecked = true
        }else if (status=="En atención"){
            radioButton2.isChecked = true
        }else if (status=="Resuelto"){
            radioButton3.isChecked = true
        }


        radioGroup.addView(radioButton)
        radioGroup.addView(radioButton2)
        radioGroup.addView(radioButton3)

        twoBoxes.addView(inputField)
        twoBoxes.addView(inputField2)
        twoBoxes.addView(radioGroup)



        builder.setView(twoBoxes)



        radioButton.setOnClickListener {
            radioButton2.isChecked = false
            radioButton3.isChecked = false
            estado = "Pendiente"
        }
        radioButton2.setOnClickListener {
            radioButton.isChecked = false
            radioButton3.isChecked = false
            estado = "En atención"
        }
        radioButton3.setOnClickListener {
            radioButton2.isChecked = false
            radioButton.isChecked = false
            estado = "Resuelto"
        }


        builder.setPositiveButton("Actualizar",object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                ClickReportRef.child("description").setValue(inputField.text.toString())
                ClickReportRef.child("ubicacion").setValue(inputField2.text.toString())
                ClickReportRef.child("status").setValue(estado)
               click_report_status.text = estado
                Toast.makeText(this@ClikReportActivity,"Reporte actualizado correctamente...",Toast.LENGTH_LONG).show()
            }

        })

        builder.setNegativeButton("Cancel",object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.cancel()
            }

        })

        val dialog = builder.create()
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.holo_green_dark)



    }


    private fun DeteleCurrentReport() {
        ClickReportRef.removeValue()
        SendUserToMainActivity()
        Toast.makeText(this,"Reporte eliminado",Toast.LENGTH_LONG).show()
    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this,PrincipalActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }
}
