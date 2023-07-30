package com.example.in_id

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Showuploads : AppCompatActivity() {

    private var userlist : MutableList<Users> = mutableListOf()
    private lateinit var recycler : RecyclerView
    private var realtimefirebasedb : FirebaseDatabase =FirebaseDatabase.getInstance()
    private var realdbfirebase = realtimefirebasedb.reference.child("Users")
    private lateinit var useradapter : userAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showuploads)
        recycler = findViewById(R.id.recyclerView)
        createData()
    }

    private fun createData() {
        realdbfirebase.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist.clear()
                for(eachuser in snapshot.children){
                    val user = eachuser.getValue(Users::class.java)
                    if(user!=null){
                        userlist.add(user)
                        Log.d("println",user.id.toString())
                    }
                    useradapter = userAdapter(this@Showuploads,userlist)
                    recycler.layoutManager = LinearLayoutManager(this@Showuploads)
                    recycler.adapter = useradapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}