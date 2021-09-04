package com.rinosystems.quizwithfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_mis_reportes.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MisReportesActivity : AppCompatActivity() {


    private lateinit var ReportsRef: DatabaseReference
    private lateinit var UsersRef: DatabaseReference
    private lateinit var currentUserID: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var options: FirebaseRecyclerOptions<Reportes>

      var ocupation: String? = null






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_reportes)


        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser!!.uid
        ReportsRef = FirebaseDatabase.getInstance().getReference().child("Reportes")
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users")




        UsersRef.child(currentUserID).addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    ocupation = snapshot.child("ocupation").getValue().toString()
                    Toast.makeText(this@MisReportesActivity,ocupation,Toast.LENGTH_LONG).show()
                    if (ocupation.equals("Administrador de app")){
                        DisplayAdminReports()
                    }else{
                        DisplayAllReports()
                    }

          }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




        //Configurando

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        misreportes_list.layoutManager = linearLayoutManager

        misreportes_list.setHasFixedSize(true)


        add_new_reporte.setOnClickListener {
            SendUserToPostActivity()
        }




    }

    private fun DisplayAllReports() {



            options = FirebaseRecyclerOptions.Builder<Reportes>()
                .setQuery(ReportsRef, Reportes::class.java)
                .build()



        val firebaseRecyclerAdapter = object :FirebaseRecyclerAdapter<Reportes,ReportsViewHolder>(options){
         override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {

             val v = LayoutInflater.from(parent.context).inflate(R.layout.all_reports_layout,parent,false)

             return ReportsViewHolder(v)
         }

         override fun onBindViewHolder(holder: ReportsViewHolder, position: Int, model: Reportes) {

             val ReportKey = getRef(position).key

             holder.setFullname(model.getFullname())
             holder.setTime(model.getTime())
             holder.setDate(model.getDate())
             holder.setDescription(model.getDescription())
             holder.setProfileimage(model.getProfileimage())
             holder.setReportimage(model.getReporteImage())
             holder.setStatus(model.getStatus())


             holder.mView.setOnClickListener {
                 val clickReportIntent = Intent(this@MisReportesActivity,ClikReportActivity::class.java)
                 clickReportIntent.putExtra("ReportKey",ReportKey)
                 startActivity(clickReportIntent)
             }

         }

     }


        firebaseRecyclerAdapter.startListening()
        misreportes_list.adapter = firebaseRecyclerAdapter


    }


    private fun DisplayAdminReports() {



        val ref = ReportsRef.orderByChild("uid").startAt(currentUserID).endAt(currentUserID+"\uf8ff")




        options = FirebaseRecyclerOptions.Builder<Reportes>()
            .setQuery(ref, Reportes::class.java)
            .build()



        val firebaseRecyclerAdapter = object :FirebaseRecyclerAdapter<Reportes,ReportsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {

                val v = LayoutInflater.from(parent.context).inflate(R.layout.all_reports_layout,parent,false)

                return ReportsViewHolder(v)
            }

            override fun onBindViewHolder(holder: ReportsViewHolder, position: Int, model: Reportes) {

                val ReportKey = getRef(position).key

                holder.setFullname(model.getFullname())
                holder.setTime(model.getTime())
                holder.setDate(model.getDate())
                holder.setDescription(model.getDescription())
                holder.setProfileimage(model.getProfileimage())
                holder.setReportimage(model.getReporteImage())
                holder.setStatus(model.getStatus())

                holder.mView.setOnClickListener {
                    val clickReportIntent = Intent(this@MisReportesActivity,ClikReportActivity::class.java)
                    clickReportIntent.putExtra("ReportKey",ReportKey)
                    startActivity(clickReportIntent)
                }

            }

        }


        firebaseRecyclerAdapter.startListening()
        misreportes_list.adapter = firebaseRecyclerAdapter


    }






    class ReportsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mView = itemView

        fun setFullname(fullname: String){
            val username = mView.findViewById<TextView>(R.id.report_user_name)
            username.setText(fullname)
        }
        fun setProfileimage(profileimage: String){
            val image = mView.findViewById<CircleImageView>(R.id.report_profile_image)
            Picasso.get().load(profileimage).into(image)
        }
        fun setTime(time: String){
            val ReportTime = mView.findViewById<TextView>(R.id.report_time)
            ReportTime.setText("   "+time)
        }
        fun setDate(date: String){
            val ReportDate = mView.findViewById<TextView>(R.id.report_date)
            ReportDate.setText("   "+date)
        }
        fun setDescription(description: String){
            val ReportDescription = mView.findViewById<TextView>(R.id.click_report_description)
            ReportDescription.setText(description)
        }
        fun setReportimage(reportimage: String){
            val ReportImage = mView.findViewById<ImageView>(R.id.click_report_image)
            Picasso.get().load(reportimage).into(ReportImage)
        }

        fun setStatus(status: String){
            val ReportStatus = mView.findViewById<TextView>(R.id.report_status)
            ReportStatus.setText(status)
        }


    }

    private fun SendUserToPostActivity() {
        val intent = Intent(this,ReporteActivity::class.java)
        startActivity(intent)

    }


}
