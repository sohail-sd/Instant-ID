package com.example.in_id

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var pass: EditText
    lateinit var repass : EditText
    lateinit var signin : TextView
    lateinit var signup : Button
    lateinit var firebaseauth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        email= findViewById(R.id.emailEt)
        pass = findViewById(R.id.passET)
        repass = findViewById(R.id.confirmPassEt)
        signin = findViewById(R.id.textView)
        signup = findViewById(R.id.button)
        firebaseauth = FirebaseAuth.getInstance()
        signin.setOnClickListener {
            val intent = Intent(applicationContext,signinActivity::class.java)
            startActivity(intent)
        }
        signup.setOnClickListener {
            createUser()
        }

    }

    private fun createUser() {
        val signup_email = email.text.toString()
        val signup_password = pass.text.toString()
        if(signup_email.isNotEmpty() && signup_password.isNotEmpty() && signup_password == repass.text.toString()){

            firebaseauth.createUserWithEmailAndPassword(signup_email,signup_password).addOnCompleteListener {

                if(it.isSuccessful){
                    val intent = Intent(applicationContext,signinActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(applicationContext,"please fill all fields",Toast.LENGTH_LONG)
        }
    }
}