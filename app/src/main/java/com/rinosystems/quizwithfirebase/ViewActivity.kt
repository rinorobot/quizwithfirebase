package com.rinosystems.quizwithfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.single_view.*

class ViewActivity : AppCompatActivity() {

    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        ref = FirebaseDatabase.getInstance().getReference().child("Examen")

        val examenKey = intent.getStringExtra("ExamenKey")


        ref.child(examenKey!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre_examen = snapshot.child("nombre_examen").getValue().toString()
                val preguntas = snapshot.child("preguntas").getValue().toString()

                image_single_view_Activity.text  = nombre_examen
                pregunta_view_Activity.text = preguntas
            }


            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@ViewActivity,"No se encontró conexión",Toast.LENGTH_LONG).show()

            }



        })









    }
}
