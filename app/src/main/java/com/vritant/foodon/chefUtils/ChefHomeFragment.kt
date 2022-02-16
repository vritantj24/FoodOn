package com.vritant.foodon.chefUtils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vritant.foodon.DishModel
import com.vritant.foodon.R
import kotlinx.android.synthetic.main.fragment_chef_home.*

class ChefHomeFragment : Fragment() {

    private val dishModelList = ArrayList<DishModel>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var state : String
    private lateinit var city : String
    private lateinit var area : String
    private lateinit var chefHomeAdapter: ChefHomeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_chef_home, container, false)
        activity?.title = "Home"

        recycle_Menu.setHasFixedSize(true)
        recycle_Menu.layoutManager = LinearLayoutManager(requireContext())
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance().getReference("Chef").child(userId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val chef = snapshot.getValue(Chef::class.java)
                if (chef!=null)
                {
                    state = chef.State.toString()
                    city = chef.City.toString()
                    area = chef.Area.toString()
                }
                chefDishes()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return view
    }

    private fun chefDishes()
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val data = FirebaseDatabase.getInstance().getReference("FoodDetails").child(state).child(city).child(area).child(userId)
        data.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dishModelList.clear()
                for (snapshot1 in snapshot.children)
                {
                    val dishModel = snapshot1.getValue(DishModel::class.java)
                    if (dishModel != null) {
                        dishModelList.add(dishModel)
                    }
                }
                chefHomeAdapter = ChefHomeAdapter(requireContext(),dishModelList)
                recycle_Menu.adapter = chefHomeAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}