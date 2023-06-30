package com.example.application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.application.dataClasses.sort_header_list_data_class


class Sort_header_recycler_Adpater(private val mList: ArrayList<sort_header_list_data_class>) :
RecyclerView.Adapter<Sort_header_recycler_Adpater.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int, ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.filter_items_list
            , parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mList[position]
        holder.textFilterHeader.text = currentItem.sort_headings
    }
    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var textFilterHeader : TextView



        init {
            textFilterHeader = itemView.findViewById(R.id.text_filter_header)
        }
    }


}