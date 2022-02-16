package com.vritant.foodon.customerUtils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vritant.foodon.R


class CustomerCartFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "Cart"
        return inflater.inflate(R.layout.fragment_customer_cart, container, false)
    }


}