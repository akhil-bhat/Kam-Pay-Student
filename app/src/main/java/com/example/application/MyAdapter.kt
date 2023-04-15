package com.example.application

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class MyAdapter(val context : Activity, val productArrayList : List<Product>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.job_description_card_hori
            , parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = productArrayList[position]

//        holder.job_name.text = currentItem.title

        if(currentItem.title.length > 11){
            holder.job_name.text = "${currentItem.title.substring(0,11)}..."
        }
        else{
            holder.job_name.text = currentItem.title
        }

        holder.ratings.text = currentItem.rating.toString()
        holder.rates.text = currentItem.price.toString()

        if (currentItem.description.length > 27) {
            holder.address.text = "${currentItem.description.substring(0, 27)}..."
        }
        else {
            holder.address.text = currentItem.description
        }
//        holder.address.text= currentItem.description
        // image view , how to show image in image view if the image is in form of url, 3rd Party Library
        // Picasso
        Picasso.get().load(currentItem.thumbnail).into(holder.brand_image);
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var job_name : TextView
        var brand_image : ShapeableImageView
        val ratings : TextView
        val rates : TextView
        val address :TextView


       init {
            job_name = itemView.findViewById(R.id.job_name)
            brand_image = itemView.findViewById(R.id.brand_image)
            ratings = itemView.findViewById(R.id.ratings)
           rates = itemView.findViewById(R.id.rates)
           address= itemView.findViewById(R.id.address)
        }
    }

}