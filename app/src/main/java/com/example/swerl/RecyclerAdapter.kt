package com.example.swerl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val mList: List<ItemsViewModel>)  : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post, parent, false)

        return ViewHolder(view)
    }
    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        // sets the text to the textview from our itemHolder class
        holder.imageView.setImageResource(ItemsViewModel.post)
        holder.profilePic.setImageResource(ItemsViewModel.profile)
        holder.textView.text = ItemsViewModel.username

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.user_post)
        val textView: TextView = itemView.findViewById(R.id.username)
        val  profilePic:ImageView=itemView.findViewById(R.id.profile_pic)

    }
}