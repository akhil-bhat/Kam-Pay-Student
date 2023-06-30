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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.application.R
import com.example.application.dataClasses.AppliedJob
import com.example.application.job_details_data_class
import com.google.android.material.imageview.ShapeableImageView

class AppliedAdapter(private val context: Activity, private val appliedJobsList: List<AppliedJob>,
                     val jobDetailsDataClass: List<job_details_data_class>) :
    RecyclerView.Adapter<AppliedAdapter.MyViewHolder>() {






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(
            R.layout.applied_card
            , parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return jobDetailsDataClass.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = jobDetailsDataClass[position]

        holder.bookmark_icon.setOnClickListener {
            holder.bookmark_icon.visibility = View.GONE
            holder.bookmark_icon_filled.visibility = View.VISIBLE
        }

        holder.bookmark_icon_filled.setOnClickListener {
            holder.bookmark_icon.visibility = View.VISIBLE
            holder.bookmark_icon_filled.visibility = View.GONE
        }

        holder.brand_name.text = currentItem.businessName

        if(currentItem.jobTitle.length > 9){
            holder.job_name.text = "${currentItem.jobTitle.substring(0,8)}..."
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

        if (currentItem.Address.length > 27) {
            holder.address.text = "${currentItem.Address.substring(0, 27)}..."
        }
        else {
            holder.address.text = currentItem.Address
        }

        Glide.with(context)
            .load(currentItem.businessLogo)
            .placeholder(R.drawable.default_business_logo) // Optional: Placeholder image to be displayed while the image is being loaded
            .into(holder.brand_image)

    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var job_name : TextView
        var brand_image : ShapeableImageView
        val ratings : TextView
        val rates : TextView
        val address : TextView
        val brand_name: TextView
        val rating_star_image_view : ImageView
        val bookmark_icon : ImageView
        val bookmark_icon_filled : ImageView


        init {
            job_name = itemView.findViewById(R.id.job_name_a)
            brand_image = itemView.findViewById(R.id.brand_image_a)
            ratings = itemView.findViewById(R.id.ratings_a)
            rates = itemView.findViewById(R.id.rates_a)
            address= itemView.findViewById(R.id.address_a)
            brand_name = itemView.findViewById(R.id.brand_name_a)
            rating_star_image_view = itemView.findViewById(R.id.star_image_view_ratings_a)
            bookmark_icon = itemView.findViewById(R.id.bookMarkIcon_a)
            bookmark_icon_filled = itemView.findViewById(R.id.bookMarkIcon_filled_a)

        }
    }
}