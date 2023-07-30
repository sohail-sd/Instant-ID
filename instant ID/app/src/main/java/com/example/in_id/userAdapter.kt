package com.example.in_id

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class userAdapter(var context : Context,var userlist : MutableList<Users>) : RecyclerView.Adapter<userAdapter.useritem>() {

    inner class useritem(item : View): RecyclerView.ViewHolder(item){
        val img : ImageView = item.findViewById(R.id.adaptimage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): useritem {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false)
        return useritem(view)
    }

    override fun getItemCount() = userlist.size

    override fun onBindViewHolder(holder: useritem, position: Int) {
        val curr = userlist[position].id
        Picasso.get().load(curr).into(holder.img)
    }
}