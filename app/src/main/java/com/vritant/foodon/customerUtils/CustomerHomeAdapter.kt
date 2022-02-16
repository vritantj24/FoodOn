package com.vritant.foodon.customerUtils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.vritant.foodon.DishModel
import com.vritant.foodon.R

class CustomerHomeAdapter(context: Context , dishModelList : ArrayList<DishModel>) : RecyclerView.Adapter<CustomerHomeViewHolder>() {

    private val items = dishModelList
    private val mContext = context
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerHomeViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.cutomer_menudish_item, parent, false)

        return CustomerHomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerHomeViewHolder, position: Int) {
        val dish = items[position]
        Glide.with(mContext).load(dish.ImageURL).into(holder.image)
        holder.dishName.text = dish.Price
        dish.RandomUID
        dish.ChefId
        holder.dishPrice.text = "Price: "+dish.Price + " Rs"
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class CustomerHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dishName: TextView = itemView.findViewById(R.id.dish_name)
    val image: ImageView = itemView.findViewById(R.id.menu_image)
    val dishPrice : TextView = itemView.findViewById(R.id.dish_price)
}