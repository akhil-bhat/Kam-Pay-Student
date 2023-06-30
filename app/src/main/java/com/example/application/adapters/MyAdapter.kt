package com.example.application

import android.annotation.SuppressLint
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
import com.example.application.adapters.PopUpFragmernt
import com.google.android.material.imageview.ShapeableImageView

class MyAdapter(val context : Activity, val jobDetailsDataClass: List<job_details_data_class>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.job_description_card_vertical_scrolls
            , parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return jobDetailsDataClass.size
    }

    @SuppressLint("SetTextI18n")

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

        holder.rating_star_image_view.setColorFilter(ContextCompat.getColor(holder.itemView.context, color),
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
            .placeholder(R.drawable.baskin_robbins_logo) // Optional: Placeholder image to be displayed while the image is being loaded
            .into(holder.brand_image)


        holder.apply_button.setOnClickListener {
            val dialogFragment = PopUpFragmernt(position, jobDetailsDataClass)
            val fragmentManager = (holder.itemView.context as FragmentActivity).supportFragmentManager
            dialogFragment.show(fragmentManager, "MyDialogFragment")
        }

    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var job_name : TextView
        var brand_image : ShapeableImageView
        val ratings : TextView
        val rates : TextView
        val address :TextView
        val brand_name:TextView
        val rating_star_image_view : ImageView
        val apply_button : CardView
        val bookmark_icon :ImageView
        val bookmark_icon_filled : ImageView


       init {
            job_name = itemView.findViewById(R.id.job_name)
            brand_image = itemView.findViewById(R.id.brand_image)
            ratings = itemView.findViewById(R.id.ratings)
           rates = itemView.findViewById(R.id.rates)
           address= itemView.findViewById(R.id.address)
           brand_name = itemView.findViewById(R.id.brand_name)
           rating_star_image_view = itemView.findViewById(R.id.star_image_view_ratings)
           apply_button = itemView.findViewById(R.id.apply_button)
           bookmark_icon = itemView.findViewById(R.id.bookMarkIcon)
           bookmark_icon_filled = itemView.findViewById(R.id.bookMarkIcon_filled)

        }
    }

}