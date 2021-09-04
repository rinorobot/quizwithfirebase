package com.rinosystems.quizwithfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        mAuth = FirebaseAuth.getInstance()

        reset_password_email_button.setOnClickListener {
            val userEmail = reset_password_email.text.trim().toString()
            if (userEmail.isEmpty()){
                Toast.makeText(this,"Por favor escribe primero tu email.",Toast.LENGTH_LONG).show()
            }else{
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Revisa tu cuenta de email para cambiar la contraseña.",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this,LoginActivity::class.java))
                    }else{
                        val message = it.exception!!.message.toString()
                        Toast.makeText(this,"Ocurrió un error: "+message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }



    }
}
