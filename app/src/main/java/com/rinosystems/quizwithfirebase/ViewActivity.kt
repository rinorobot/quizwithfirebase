package com.rinosystems.quizwithfirebase

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.single_view.*

class ViewActivity : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var refPreguntas: DatabaseReference
  //  var preguntas = mutableListOf<Pregunta>()
    var preguntas = ArrayList<Pregunta>()


    var pregunta: Pregunta = Pregunta("","","","","","","","","","","")

    var index_question = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        ref = FirebaseDatabase.getInstance().getReference().child("Examen")
        refPreguntas = FirebaseDatabase.getInstance().getReference().child("Examen")


        val examenKey = intent.getStringExtra("ExamenKey")


        ref.child(examenKey!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre_examen = snapshot.child("nombre_examen").getValue().toString()
             //   image_single_view_Activity.text  = nombre_examen

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        refPreguntas.child(examenKey!!).child("preguntas").addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {



                for (data in snapshot.children){
                    pregunta = data.getValue(Pregunta::class.java)!!
                    preguntas.add(pregunta)

                }

                text_question.text = preguntas[index_question].getPregunta()
                answer1.text = preguntas[index_question].getRespuestaA()
                answer2.text = preguntas[index_question].getRespuestaB()
                answer3.text = preguntas[index_question].getRespuestaC()
                answer4.text = preguntas[index_question].getRespuestaD()

                btn_check.setOnClickListener {
                    index_question++
                    text_question.text = preguntas[index_question].getPregunta()
                    answer1.text = preguntas[index_question].getRespuestaA()
                    answer2.text = preguntas[index_question].getRespuestaB()
                    answer3.text = preguntas[index_question].getRespuestaC()
                    answer4.text = preguntas[index_question].getRespuestaD()



                }
                btn_previus.setOnClickListener {
                    index_question--
                    text_question.text = preguntas[index_question].getPregunta()
                    answer1.text = preguntas[index_question].getRespuestaA()
                    answer2.text = preguntas[index_question].getRespuestaB()
                    answer3.text = preguntas[index_question].getRespuestaC()
                    answer4.text = preguntas[index_question].getRespuestaD()

                }









            }
            override fun onCancelled(error: DatabaseError) {


            }

        })








 }

}
