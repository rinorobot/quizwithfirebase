package com.rinosystems.quizwithfirebase

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imageView = itemView.findViewById<ImageView>(R.id.image_single_view)
    var textView = itemView.findViewById<TextView>(R.id.textView_single_view)






}
