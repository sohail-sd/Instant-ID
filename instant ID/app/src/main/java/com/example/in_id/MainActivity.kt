package com.example.in_id

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*



class MainActivity : AppCompatActivity() {

    lateinit var upload : ImageView
    lateinit var down: ImageView
    lateinit var show : ImageView
    lateinit var inidapp : Button
    private var imageuri : Uri? = null

    private var realtimedatabase = FirebaseDatabase.getInstance()
    private var rdbreferance = realtimedatabase.reference.child("Users")

    private var storagedb = FirebaseStorage.getInstance()
    private var sdbreference = storagedb.reference



    private var firbaseauth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var logout : FloatingActionButton
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        upload = findViewById(R.id.up)
        down = findViewById(R.id.down)
        inidapp = findViewById(R.id.inidapplication)
        show = findViewById(R.id.showimage)
        logout = findViewById(R.id.floatingActionButton)
        registeractivitylauncher()

        upload.setOnClickListener {
            selectImage()
            //createDialog()
        }
        down.setOnClickListener {
            verifyUsertoshow()

        }
        inidapp.setOnClickListener {

            val intent = Intent(applicationContext,inidapplication::class.java)
            startActivity(intent)

        }
        logout.setOnClickListener{
            userlogout()
        }


    }

    private fun userlogout() {
        if(firbaseauth.currentUser != null){
            firbaseauth.signOut()
            val intent = Intent(applicationContext,signinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registeractivitylauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                val resultcode = it.resultCode
                val imagedata = it.data
                if(resultcode== RESULT_OK && imagedata!=null){

                    imageuri = imagedata.data

                    Picasso.get().load(imageuri).into(show)
                }
            })
    }

    private fun selectImage() {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)

        }
        delaysometime()

    }
    private fun uploadImage(){
        val imageName = UUID.randomUUID().toString()
        val imagereference = storagedb.reference.child("images/").child(imageName)

        imageuri?.let {

            imagereference.putFile(it).addOnSuccessListener {

                Toast.makeText(this,"Image Uploaded",Toast.LENGTH_LONG).show()

                val uploadimageref = storagedb.reference.child("images/").child(imageName)
                uploadimageref.downloadUrl.addOnSuccessListener {

                    val imageurl = it.toString()

                    addtodatabase(imageurl)
                }.addOnFailureListener{

                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun addtodatabase(imageurl: String) {
        val id = rdbreferance.push().key.toString()
        val user = Users(imageurl)
        rdbreferance.child(id).setValue(user).addOnCompleteListener {

            if(it.isSuccessful){
                //Toast.makeText(this,"Added to Real Time Db",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == RESULT_OK && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }

    }

    fun createDialog(){
        val dialogview = LayoutInflater.from(this).inflate(R.layout.dialog_layout,null)
        val builder = AlertDialog.Builder(this)
        with(builder){
            setView(dialogview)
        }
        val alertdialog = builder.show()
        val ybtn = alertdialog.findViewById<Button>(R.id.Yesbtn)
        val nbtn = alertdialog.findViewById<Button>(R.id.Nobtn)
        ybtn?.setOnClickListener {
            alertdialog.dismiss()
            uploadImage()
        }
        nbtn?.setOnClickListener {
            alertdialog.dismiss()
        }

    }
    fun delaysometime(){
       Handler(Looper.getMainLooper()).postDelayed({ createDialog() },3000)
    }
     fun verifyUsertoshow(){
        val dialogview = LayoutInflater.from(this).inflate(R.layout.verify_dialog,null)
        val builder = AlertDialog.Builder(this)

        with(builder){
            setView(dialogview)
        }
        val alertd = builder.show()
         val privateKey = alertd.findViewById<EditText>(R.id.privatekeyEt)
         val publicKey = alertd.findViewById<EditText>(R.id.publickeyEt)
         val verify = alertd.findViewById<Button>(R.id.verifybutton)

        verify?.setOnClickListener {
            alertd.dismiss()

            val privatedata = privateKey?.text.toString()
            val publicdata = publicKey?.text.toString()
            val email = firbaseauth.currentUser?.email

            Log.d("println","{$privatedata}  & {$publicdata} & {$email}")
            firbaseauth.signInWithEmailAndPassword(email.toString(),privatedata).addOnCompleteListener {
             if(it.isSuccessful) {
                 if (publicdata == "InId") {
                     val intent = Intent(applicationContext, Showuploads::class.java)
                     startActivity(intent)
                 }
             }
            }.addOnFailureListener {

            }
        }
    }
}