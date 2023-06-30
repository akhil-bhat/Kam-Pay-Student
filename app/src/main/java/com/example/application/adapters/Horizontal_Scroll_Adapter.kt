package com.example.application.adapters

import android.app.Activity
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.application.R
import com.example.application.job_details_data_class
import com.google.android.material.imageview.ShapeableImageView

class Horizontal_Scroll_Adapter (val context : Activity,
                                 val jobDetailsList1: List<job_details_data_class>) :
      RecyclerView.Adapter<Horizontal_Scroll_Adapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.job_description_card_horizontal_scroll
            , parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
    ) {
        val currentItem = jobDetailsList1[position]

        holder.bookmark_icon.setOnClickListener {
            holder.bookmark_icon.visibility = View.GONE
            holder.bookmark_icon_filled.visibility = View.VISIBLE
        }
        holder.bookmark_icon_filled.setOnClickListener {
            holder.bookmark_icon.visibility = View.VISIBLE
            holder.bookmark_icon_filled.visibility = View.GONE
        }

        if(currentItem.jobTitle.length > 9){
            holder.job_name.text = "${currentItem.jobTitle.substring(0,7)}..."
        }

        else{
            holder.job_name.text = currentItem.jobTitle
        }

        holder.ratings.text = currentItem.ratings.toString()

        if (currentItem.ratings >= 4.5) {
            holder.ratings.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_green))
        }
        else if (currentItem.ratings >= 4.0) {
            holder.ratings.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
        }
        else if(currentItem.ratings >= 3.5 ) {
            holder.ratings.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_yellow))
        }
        else if(currentItem.ratings >= 3.0 ) {
            holder.ratings.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_yellow))
        }
        else if(currentItem.ratings >= 2.5 ) {
            holder.ratings.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_orange))
        }
        else if(currentItem.ratings >= 2.0 ) {
            holder.ratings.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_orange))
        }
        else {
            holder.ratings.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.dark_red))
        }

        val color = when {
            currentItem.ratings >= 4.5 -> R.color.dark_green
            currentItem.ratings >= 4.0 ->R.color.light_green
            currentItem.ratings >= 3.5 -> R.color.dark_yellow
            currentItem.ratings >= 3.0 -> R.color.light_yellow
            currentItem.ratings >= 2.5 -> R.color.dark_orange
            currentItem.ratings >= 2.0 -> R.color.light_orange
            else ->R.color.dark_red
        }

        holder.rating_star_image_view.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, color),
            PorterDuff.Mode.MULTIPLY)

        holder.rates.text = currentItem.payPerDay.toString()

        Glide.with(context)
            .load(currentItem.businessLogo)
            .placeholder(R.drawable.baskin_robbins_logo)
            .into(holder.brand_image)

        val durationInSeconds = currentItem.durationInSeconds
        val hours = durationInSeconds / 3600
        holder.duration.text = hours.toString()
        holder.apply_button.setOnClickListener {
            val dialogFragment = PopUpFragmernt(position, jobDetailsList1)
            val fragmentManager = (holder.itemView.context as FragmentActivity).supportFragmentManager
            dialogFragment.show(fragmentManager, "MyDialogFragment")
        }

    }

    override fun getItemCount(): Int {
        return jobDetailsList1.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var job_name : TextView
        var brand_image : ShapeableImageView
        val ratings : TextView
        val rates : TextView
        val rating_star_image_view : ImageView
        val duration : TextView
        val apply_button: CardView
        val bookmark_icon :ImageView
        val bookmark_icon_filled : ImageView


        init {
            job_name = itemView.findViewById(R.id.job_title_h)
            brand_image = itemView.findViewById(R.id.brand_image_H)
            ratings = itemView.findViewById(R.id.ratings_H)
            rates = itemView.findViewById(R.id.rates_H)
            rating_star_image_view = itemView.findViewById(R.id.star_image_view_ratings_H)
            duration = itemView.findViewById(R.id.text_view_duration)
            apply_button = itemView.findViewById(R.id.apply_button_H)
            bookmark_icon = itemView.findViewById(R.id.bookmark_icon_H)
            bookmark_icon_filled = itemView.findViewById(R.id.filled_bookmar_H)

        }
    }



}