package com.rinosystems.quizwithfirebase

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var option: FirebaseRecyclerOptions<Examen>
    lateinit var adapter: FirebaseRecyclerAdapter<Examen,MyViewHolder>
    lateinit var dataRef: DatabaseReference

    lateinit var loadingBar : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dataRef = FirebaseDatabase.getInstance().getReference().child("Examen")

        loadingBar = ProgressDialog(this)
        loadingBar.setMessage("Cargando lista de examenes")
        loadingBar.show()


        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)

        loadData()




    }

    private fun loadData() {


        option = FirebaseRecyclerOptions.Builder<Examen>().setQuery(dataRef, object : SnapshotParser<Examen> {
            override fun parseSnapshot(snapshot: DataSnapshot): Examen {
                return Examen(snapshot.child("nombre_examen").getValue().toString())
            }
        }).build()

        adapter = object: FirebaseRecyclerAdapter<Examen,MyViewHolder>(option){
            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Examen) {

                loadingBar.dismiss()

                holder.textView.text = model.getNombreExamen()
                holder.v.setOnClickListener {
                    val intent = Intent(this@HomeActivity,ViewActivity::class.java)
                    intent.putExtra("ExamenKey",getRef(position).key)
                    startActivity(intent)
                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

                var v = LayoutInflater.from(parent.context).inflate(R.layout.single_view,parent,false)

                return MyViewHolder(v)


            }



        }

        adapter.startListening()
        recyclerView.adapter = adapter

    }
}
