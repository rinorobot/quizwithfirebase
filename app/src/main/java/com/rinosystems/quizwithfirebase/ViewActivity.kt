package com.rinosystems.quizwithfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.single_view.*

class ViewActivity : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var refPreguntas: DatabaseReference
    val preguntas = mutableListOf<Pregunta>()

    var pregunta: Pregunta = Pregunta("","","","","","")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        ref = FirebaseDatabase.getInstance().getReference().child("Examen")
        refPreguntas = FirebaseDatabase.getInstance().getReference().child("Examen")


        val examenKey = intent.getStringExtra("ExamenKey")


        ref.child(examenKey!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre_examen = snapshot.child("nombre_examen").getValue().toString()
              //  val preguntas = snapshot.child("preguntas").getValue().toString()

               // Toast.makeText(this@ViewActivity,"Conexión exitosa",Toast.LENGTH_LONG).show()



                image_single_view_Activity.text  = nombre_examen

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        refPreguntas.child(examenKey!!).child("preguntas").addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                Toast.makeText(this@ViewActivity,"Conexión exitosa",Toast.LENGTH_LONG).show()

                for (data in snapshot.children){
                    pregunta = data.getValue(Pregunta::class.java)!!
                    preguntas.add(pregunta)
                }

                pregunta_view_Activity.text = preguntas[0].getPregunta()+", "+preguntas[1].getPregunta()+", "+preguntas[2].getPregunta()
            }
            override fun onCancelled(error: DatabaseError) {
              //  Toast.makeText(this@ViewActivity,"No se encontró conexión",Toast.LENGTH_LONG).show()
            }

        })









    }
}
