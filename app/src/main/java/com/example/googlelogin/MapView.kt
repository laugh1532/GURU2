package com.example.googlelogin2


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.NaverCloudPlatformClient
import kotlinx.android.synthetic.main.activity_third.*


/**
 * A simple [Fragment] subclass.
 */
class MapView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        var view = inflater.inflate(com.example.googlelogin.R.layout.fragment_map_view, container, false)

        //NaverMapSdk.getInstance(this!!.activity!!).client = NaverCloudPlatformClient("845ikjfhhf")

        return view

    }


}
