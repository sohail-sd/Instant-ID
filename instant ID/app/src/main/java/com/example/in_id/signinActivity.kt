package com.example.in_id

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class signinActivity : AppCompatActivity() {

    lateinit var firebaseauth : FirebaseAuth

    lateinit var emailsignin : EditText
    lateinit var passsignin : EditText
    lateinit var signin : Button
    lateinit var signuptxt : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        emailsignin = findViewById(R.id.emailEt)
        passsignin = findViewById(R.id.passET)
        signin = findViewById(R.id.signinbutton)
        signuptxt = findViewById(R.id.textView)
        firebaseauth = FirebaseAuth.getInstance()

        signin.setOnClickListener {
            verifyuser()
        }

        signuptxt.setOnClickListener {
            val intent = Intent(applicationContext,SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun verifyuser() {
        val email = emailsignin.text.toString()
        val pass = passsignin.text.toString()
        if(email.isNotEmpty() && pass.isNotEmpty()){
            firebaseauth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {

                if(it.isSuccessful){
                    val intent = Intent(applicationContext,MainActivity::class.java)
                    startActivity(intent)
                }else{
                 Toast.makeText(applicationContext,"Authentication Failed",Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(applicationContext,"please fill all fields",Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseauth.currentUser!=null){
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
        }
    }
}