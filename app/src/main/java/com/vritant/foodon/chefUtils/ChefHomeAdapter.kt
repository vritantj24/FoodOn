package com.vritant.foodon.chefUtils

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vritant.foodon.DishModel
import com.vritant.foodon.R

class ChefHomeAdapter(context: Context,dishModelList: ArrayList<DishModel>) : RecyclerView.Adapter<ChefHomeViewHolder>() {

    private val items = dishModelList
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChefHomeViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.chef_menu_delete, parent, false)
        return ChefHomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChefHomeViewHolder, position: Int) {
        val dish = items[position]
        holder.dishes.text = dish.Dishes
        dish.RandomUID
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext,UpdateDeleteDish::class.java)
            intent.putExtra("updateDeleteDish",dish.RandomUID)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ChefHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dishes: TextView = itemView.findViewById(R.id.dish_Name)
}