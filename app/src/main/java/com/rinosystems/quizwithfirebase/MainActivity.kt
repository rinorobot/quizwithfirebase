package com.rinosystems.quizwithfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var myDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myDatabaseReference = FirebaseDatabase.getInstance().getReference().child("examenes")

        myDatabaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

               Toast.makeText(this@MainActivity,"Conexión exitosa",Toast.LENGTH_LONG).show()
            }
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity,"Fallo en la conexión",Toast.LENGTH_LONG).show()
            }




        })








    }
}
