package com.teme.addtorv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teme.addtorv.room.User
import kotlinx.android.synthetic.main.user_view.view.*

class Adapter(private val users : ArrayList<User>) : RecyclerView.Adapter<Adapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.user_view, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(users[position])
    }

    inner class CustomViewHolder(private val view: View) : RecyclerView.ViewHolder(view){

        fun bind(user: User){
            view.tv_last_name.text = "Last Name: " + user.lastName
            view.tv_first_name.text = "First Name: " + user.firstName
        }

    }
}

