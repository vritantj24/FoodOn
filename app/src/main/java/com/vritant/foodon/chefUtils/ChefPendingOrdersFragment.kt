package com.vritant.foodon.chefUtils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vritant.foodon.R


class ChefPendingOrdersFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "Pending Orders"
        return inflater.inflate(R.layout.fragment_chef_pending_orders, container, false)
    }


}