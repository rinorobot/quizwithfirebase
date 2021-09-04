package com.rinosystems.quizwithfirebase

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var loadingBar: ProgressDialog
    lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1
    val TAG = "LoginActivity"
    private var emailAddressChecker: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        loadingBar = ProgressDialog(this)


        register_account_link.setOnClickListener {
            SendUserToRegisterActivity()
        }

        login_button.setOnClickListener {
            AllowingUserToLogin()
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        google_signin_button.setOnClickListener {
            signIn()
        }

        forget_password_link.setOnClickListener {

            val intent = Intent(this,ResetPasswordActivity::class.java)
            startActivity(intent)


        }


    }


    private fun VerifyEmailAddress(){
        val user = mAuth.currentUser
        emailAddressChecker = user?.isEmailVerified

        if (emailAddressChecker!!){
            SendUserToMainActivity()

        }else{
            Toast.makeText(this,"Por favor verifica primero tu cuenta.",Toast.LENGTH_LONG).show()
            mAuth.signOut()
        }



    }




    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            loadingBar.setTitle("Inicio de sesión con Google")
            loadingBar.setMessage("Por favor espera un momento en lo que se inicia sesión con Google")
            loadingBar.setCanceledOnTouchOutside(true)
            loadingBar.show()
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result!!.isSuccess){
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!.idToken!!)

                Toast.makeText(this,"Espera un momento en lo que obtenemos los datos de tu cuenta Google",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"No pudimos conectarnos a  tu cuenta Google",Toast.LENGTH_LONG).show()
                loadingBar.dismiss()
            }

        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    SendUserToMainActivity()
                    loadingBar.dismiss()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    val message = task.exception!!.message.toString()
                    SendUserToLoginActivity()
                    Toast.makeText(this,"No pudimos autenticarte. Ocurrió el siguiente error: "+message,Toast.LENGTH_LONG).show()
                    loadingBar.dismiss()


                }

                // ...
            }
    }

    private fun SendUserToLoginActivity() {
        val loginIntent = Intent(this,LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
        finish()
    }


    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = mAuth.currentUser

        if (currentUser != null){
            SendUserToMainActivity()
        }

    }

    private fun AllowingUserToLogin() {
        val email = login_email.text.toString()
        val password = login_password.text.toString()

        if (email.isEmpty()){
            Toast.makeText(this,"Por favor escribe tu mail",Toast.LENGTH_LONG).show()
        }else if (password.isEmpty()){
            Toast.makeText(this,"Por favor escribe tu mail",Toast.LENGTH_LONG).show()
        }else{
            loadingBar.setTitle("Login")
            loadingBar.setMessage("Por favor espera un momento en lo que se inicia sesión")
            loadingBar.setCanceledOnTouchOutside(true)
            loadingBar.show()

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful){
                        VerifyEmailAddress()
                        loadingBar.dismiss()
                    }else{
                        val message = task.exception.toString()
                        Toast.makeText(this@LoginActivity,"Ocurrió un error: "+message,Toast.LENGTH_LONG).show()
                        loadingBar.dismiss()
                    }
                }

            })
        }
    }

    private fun SendUserToMainActivity() {
        val mainIntent = Intent(this,PrincipalActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }

    private fun SendUserToRegisterActivity() {
        val registerIntent = Intent(this,RegisterActivity::class.java)
        startActivity(registerIntent)

    }
}
