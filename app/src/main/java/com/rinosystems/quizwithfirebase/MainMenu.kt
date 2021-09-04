package com.rinosystems.quizwithfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        btnExas.setOnClickListener {
            val intent =  Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }
        btnCuenta.setOnClickListener {
            val intent =  Intent(this,PrincipalActivity::class.java)
            startActivity(intent)
        }
    }
}
