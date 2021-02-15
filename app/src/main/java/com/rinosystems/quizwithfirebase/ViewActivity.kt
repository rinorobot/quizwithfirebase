package com.rinosystems.quizwithfirebase

import android.app.ProgressDialog
import android.content.DialogInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.single_view.*
import java.net.URI

class ViewActivity : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var refPreguntas: DatabaseReference
    var preguntas = ArrayList<Pregunta>()


    var pregunta: Pregunta = Pregunta("","","","","","","","","","","")

    //Variablres para evualuar cuestionario
    var index_question = 0
    private val ids_answers = listOf(R.id.answer1,R.id.answer2,R.id.answer3,R.id.answer4)
    private lateinit var answer_is_correct : BooleanArray

    private lateinit var answer : IntArray
    var preguntas_correctas : ArrayList<Int> = ArrayList()
    var preguntas_incorrectas : ArrayList<Int> = ArrayList()
    var preguntas_no_contestadas : ArrayList<Int> = ArrayList()
    var correctas : Int= 0
    var incorrectas : Int = 0
    var nocontestadas : Int = 0
    var calif = 0.0





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        ref = FirebaseDatabase.getInstance().getReference().child("Examen")
        refPreguntas = FirebaseDatabase.getInstance().getReference().child("Examen")


        val examenKey = intent.getStringExtra("ExamenKey")


        ref.child(examenKey!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre_examen = snapshot.child("nombre_examen").getValue().toString()
                label_question.text = nombre_examen




            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        refPreguntas.child(examenKey).child("preguntas").addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (data in snapshot.children){
                    pregunta = data.getValue(Pregunta::class.java)!!
                    preguntas.add(pregunta)

                }

               startOver()

        btn_check.setOnClickListener {
                    checkAnswer(preguntas[index_question].getCorrecta().toInt())
                     if (index_question<preguntas.size-1){
                        index_question++
                        showQuestion()
                    }else{
                        checkResults()

                    }

       }
                btn_previus.setOnClickListener {
                    checkAnswer(preguntas[index_question].getCorrecta().toInt())
                    if (index_question>0){
                        index_question--
                        showQuestion()
                    }


                }


            }
        override fun onCancelled(error: DatabaseError) {
  }

        })
}



    private fun checkResults() {

        correctas = 0
        incorrectas = 0
        nocontestadas = 0



        for (i in 0..preguntas.size-1) {
             if (answer_is_correct[i])    correctas++
             else if (answer[i] == -1)    nocontestadas++
                else incorrectas++

        }

        calif = (correctas.toDouble()/preguntas.size.toDouble())*10

       val builder = AlertDialog.Builder(this)

        if (calif>=8) {
            builder.setIcon(R.drawable.icon_muy_bien)
            builder.setTitle("Muy bien")
        }
        else if (calif<8&&calif>6){
            builder.setIcon(R.drawable.icon_regular)
            builder.setTitle("Puedes mejorar")
        }else{
            builder.setIcon(R.drawable.icon_mal)
            builder.setTitle("Estudiando puedes pasarlo")
        }

        for (i in 0..preguntas.size-1){
            var respuesta = i
            if (answer_is_correct[i]==true){

                preguntas_correctas.add(++respuesta)
            }


            if (answer_is_correct[i]==false){

                preguntas_incorrectas.add(++respuesta)
            }

            if (answer[i]== -1){
                preguntas_no_contestadas.add(respuesta)
            }


        }




        builder.setMessage("Calificación: ${String.format("%.1f",calif)}"+"\nPreguntas correctas: ${preguntas_correctas} \nPreguntas incorrectas: ${preguntas_incorrectas} \nPreguntas no contestadas: ${preguntas_no_contestadas}")

        builder.setCancelable(false)//tal vez sea mejor quitarlo

        builder.setPositiveButton("Ver respuestas",DialogInterface.OnClickListener { dialog, which ->
            finish()

        })

        builder?.setNegativeButton("Volver a empezar", DialogInterface.OnClickListener { dialog, which ->

            startOver()//Borra todas las preguntas

        })


        builder.show()
    }



    private fun startOver() {
        answer_is_correct = BooleanArray(preguntas.size) //Poner en overstar
        answer = IntArray(preguntas.size)//Poner en overstar

        for (i in 0..answer.size-1){
            answer[i] = -1
        }
        index_question = 0

        preguntas_correctas.clear()
        preguntas_incorrectas.clear()
        preguntas_no_contestadas.clear()
        showQuestion()
    }

    private fun checkAnswer(correcta: Int) {


        val id = answer_group.checkedRadioButtonId
        var ans = -1

        for (i in 0..ids_answers.size-1){
            if (ids_answers[i] == id){
                ans = i+1
            }
        }


        answer_is_correct[index_question] = (ans==correcta)
        answer[index_question] = ans


    }

    fun showQuestion(){
        answer_group.clearCheck()
        text_question.text = preguntas[index_question].getPregunta()
        ivQuestion.visibility = View.GONE
        IVanswer1.visibility = View.GONE

        if (preguntas[index_question].getRespuestaA()==""){
            answer1.text = preguntas[index_question].getRespuestaA()
            IVanswer1.visibility = View.VISIBLE
            Picasso.get().load(preguntas[index_question].getLinkA()).into(IVanswer1)


        }else{
            answer1.text = preguntas[index_question].getRespuestaA()
        }




        answer2.text = preguntas[index_question].getRespuestaB()
        answer3.text = preguntas[index_question].getRespuestaC()
        answer4.text = preguntas[index_question].getRespuestaD()



        if (preguntas[index_question].getLinkPregunta()!=""){
            ivQuestion.visibility = View.VISIBLE
            Picasso.get().load(preguntas[index_question].getLinkPregunta()).into(ivQuestion)

        }else{
            ivQuestion.visibility = View.GONE
        }




        for (i in 0..ids_answers.size-1){
            val rb = findViewById<RadioButton>(ids_answers[i])

            if (answer[index_question]==i){
                rb.isChecked=true
            }
        }



        if (index_question==0){
            btn_previus.visibility=View.GONE
        }else{
            btn_previus.visibility=View.VISIBLE
        }
        if (index_question == preguntas.size-1){
            btn_check.setText("Finalizar")
        }else{
            btn_check.setText("Siguiente")
        }


    }

}
