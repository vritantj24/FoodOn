package com.vritant.foodon.customerUtils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vritant.foodon.DishModel
import com.vritant.foodon.R
import kotlinx.android.synthetic.main.fragment_customer_home.*


class CustomerHomeFragment : Fragment(),SwipeRefreshLayout.OnRefreshListener {

    private var dishModelList = ArrayList<DishModel>()
    private lateinit var customerHomeAdapter : CustomerHomeAdapter
    private lateinit var state: String
    private lateinit var city : String
    private lateinit var area : String
    private lateinit var data: DatabaseReference
    private lateinit var databaseReference:DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_customer_home,container,false)
        activity?.title = "Home"
        recycle_menu.setHasFixedSize(true)
        recycle_menu.layoutManager = LinearLayoutManager(context)
        swipeLayout.setOnRefreshListener(this)
        swipeLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.Red)

        swipeLayout.post(Runnable {
            swipeLayout.isRefreshing = true
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            data = FirebaseDatabase.getInstance().getReference("Customer").child(userId)
            data.addListenerForSingleValueEvent(object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val customer = snapshot.getValue(Customer::class.java)
                    if (customer!=null)
                    {
                        state = customer.State.toString()
                        city = customer.city.toString()
                        area = customer.Area.toString()
                    }
                    customerMenu()
                }

                override fun onCancelled(error: DatabaseError) {
                    swipeLayout.isRefreshing = false
                }

            })
        })
        return view
    }

    override fun onRefresh() {
            customerMenu()
    }

    private fun customerMenu()
    {
        swipeLayout.isRefreshing = true
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodDetails").child(state).child(city).child(area)
        databaseReference.addValueEventListener(object  : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snapshot1 in snapshot.children) {
                    for (snapshot2 in snapshot1.children) {
                        val dishModel = snapshot2.getValue(DishModel::class.java)
                        if (dishModel != null) {
                            dishModelList.add(dishModel)
                        }
                    }
                }
                customerHomeAdapter = CustomerHomeAdapter(requireContext(),dishModelList)
                recycle_menu.adapter = customerHomeAdapter
                swipeLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
                swipeLayout.isRefreshing = false
            }

        })
    }

}