package com.vritant.foodon.deliveryUtils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vritant.foodon.R


class DeliveryShipOrderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "Ship Orders"
        return inflater.inflate(R.layout.fragment_delivery_ship_order, container, false)
    }

}