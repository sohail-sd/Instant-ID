package com.example.in_id

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class inidapplication : AppCompatActivity() {

    private var imageuri : Uri? = null


    private var realtimedb = FirebaseDatabase.getInstance()
    private var realdbrefarence = realtimedb.reference.child("Registrations")

    private var storage = FirebaseStorage.getInstance()
    private var storagereferance = storage.reference


    private lateinit var activityresultlauncher : ActivityResultLauncher<Intent>


    private lateinit var img : ImageView
    private lateinit var fname : EditText
    private lateinit var regd : EditText
    private lateinit var course : EditText
    private lateinit var year : EditText
    private lateinit var dept : EditText
    private lateinit var aadhar :EditText
    private lateinit var address : EditText
    private lateinit var submit : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inidapplication)

        registeractivitylauncher()
        img = findViewById(R.id.shapeableImageView)
        fname=findViewById(R.id.NameET)
        regd = findViewById(R.id.registerET)
        course = findViewById(R.id.CourseET)
        year = findViewById(R.id.yearET)
        dept = findViewById(R.id.DeptET)
        aadhar = findViewById(R.id.raadharET)
        address = findViewById(R.id.addressET)
        submit = findViewById(R.id.submitbtn)

        img.setOnClickListener {
            selectAndShowImage()
        }
        submit.setOnClickListener {
            uploadtodb()

        }


    }

    private fun uploadtodb() {

        val aadhardata = aadhar.text.toString()
        val namedata = fname.text.toString()
        val regddata = regd.text.toString()
        val branchdata = course.text.toString()
        val yeardata = year.text.toString()
        val deptdata = dept.text.toString()
        val fnamedata = fname.text.toString()
        val addressdata = address.text.toString()
        val registerreference = storagereferance.child("Registrations/").child(aadhardata)

        imageuri?.let{

            registerreference.putFile(it).addOnSuccessListener {

                val uploadref = registerreference
                uploadref.downloadUrl.addOnSuccessListener {

                    val imageurl = it.toString()
                    addtodb(fnamedata,aadhardata,namedata,regddata,branchdata,yeardata,deptdata,addressdata,imageurl)
                }
            }

        }

    }

    private fun addtodb(fname:String,aadhardata: String, namedata: String, regddata: String,
                        branchdata: String, yeardata: String, deptdata: String,
                        addressdata: String, imageurl: String) {
        val newregistration = registernew(fname,regddata,branchdata,yeardata,deptdata,aadhardata,addressdata,imageurl)
        val id = realdbrefarence.push().key.toString()
           realdbrefarence.child(id).setValue(newregistration).addOnCompleteListener {

               if(it.isSuccessful){
                   Toast.makeText(applicationContext,"submitted successfully",Toast.LENGTH_LONG).show()
                   val intent = Intent(applicationContext,MainActivity::class.java)
                   startActivity(intent)
               }
           }
    }

    fun registeractivitylauncher(){

        activityresultlauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                val rescode = it.resultCode
                val imgdata = it.data
                if(imgdata!=null && rescode== RESULT_OK){

                    imageuri = imgdata.data

                    Picasso.get().load(imageuri).into(img)
                }

            })

    }

    private fun selectAndShowImage() {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            val intent = Intent()
            intent.type="image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityresultlauncher.launch(intent)
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
            intent.type="image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityresultlauncher.launch(intent)
        }
    }
}