package com.rinosystems.quizwithfirebase

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_clik_report.*

class ClikReportActivity : AppCompatActivity() {

    private var ReportKey: String? = null
    private lateinit var ClickReportRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUserID: String
    private lateinit var databaseUserID: String
    private lateinit var description: String
    private lateinit var ubication: String
    private lateinit var image: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clik_report)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser!!.uid

        ReportKey = intent.getStringExtra("ReportKey")
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

                click_report_description.text = description
                click_report_ubication.text = ubication
                Picasso.get().load(image).into(click_report_image)

                if (currentUserID.equals(databaseUserID)){
                    delete_report_button.visibility = View.VISIBLE
                    edit_repot_button.visibility = View.VISIBLE
                }

                edit_repot_button.setOnClickListener {
                    EditCurrentReport(description,ubication)
                }

            }

            }
            override fun onCancelled(error: DatabaseError) {

            }

        })

        delete_report_button.setOnClickListener {
            DeteleCurrentReport()
        }

    }

    private fun EditCurrentReport(description: String,ubication:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar reporte")

        val twoBoxes = LinearLayout(this)
        twoBoxes.orientation = LinearLayout.VERTICAL

        val inputField = EditText(this)
        inputField.setText(description)

        val inputField2 = EditText(this)
        inputField2.setText(ubication)

        twoBoxes.addView(inputField)
        twoBoxes.addView(inputField2)



        builder.setView(twoBoxes)


        builder.setPositiveButton("Actualizar",object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                ClickReportRef.child("description").setValue(inputField.text.toString())
                ClickReportRef.child("ubicacion").setValue(inputField2.text.toString())
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
