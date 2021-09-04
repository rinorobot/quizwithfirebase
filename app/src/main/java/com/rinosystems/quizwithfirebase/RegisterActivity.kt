package com.rinosystems.quizwithfirebase

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var loadingBar: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

       loadingBar = ProgressDialog(this)



        register_create_account.setOnClickListener {
            CreateNewAccount()
        }
    }

    private fun SendEmailVerificationMessage(){
        val user = mAuth.currentUser
        if (user != null){
            user.sendEmailVerification().addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(this,"Registro exitoso, revisa tu correo para activar tu cuenta.",Toast.LENGTH_LONG).show()
                    SendUserToLoginActivity()
                    mAuth.signOut()


                }else{
                    val message = it.exception!!.message.toString()
                    Toast.makeText(this,"Error: "+message,Toast.LENGTH_LONG).show()
                    mAuth.signOut()

                }
            }
        }
    }
    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = mAuth.currentUser

        if (currentUser != null){
            SendUserToMainActivity()
        }

    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this,PrincipalActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }

    private fun CreateNewAccount() {
        val email = register_email.text.toString()
        val password = register_password.text.toString()
        val confirmPassword = register_confirm_password.text.toString()

        if (email.isEmpty()){
            Toast.makeText(this,"Por favor escribe tu email",Toast.LENGTH_LONG).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this,"Por favor escribe tu contraseña",Toast.LENGTH_LONG).show()
        }
        else if (confirmPassword.isEmpty()){
            Toast.makeText(this,"Por favor confirma tu contraseña",Toast.LENGTH_LONG).show()

        }
        else if (!password.equals(confirmPassword)){
            Toast.makeText(this,"Tu contraseña no coincide con tu contraseña confirmada",Toast.LENGTH_LONG).show()
        }else{
            loadingBar.setTitle("Creando una nueva cuenta")
            loadingBar.setMessage("Por favor espera un momento en lo que se crea la cuenta")
            loadingBar.show()
            loadingBar.setCanceledOnTouchOutside(true)


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful){

                        SendEmailVerificationMessage()
                        loadingBar.dismiss()
                    }else{
                        val mensaje: String? = task.exception?.message
                        Toast.makeText(this@RegisterActivity,"Ocurrió un error: "+mensaje,Toast.LENGTH_LONG).show()
                    }
                }

            })
          }
    }

    private fun SendUserToLoginActivity() {
        val loginIntent = Intent(this,LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
        finish()

    }
}
