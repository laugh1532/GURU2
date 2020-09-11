package com.example.googlelogin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.googlelogin.R
import com.example.myapplication.BoardList
import kotlinx.android.synthetic.main.activity_third.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_board_list, container, false)

        fragmentManager!!.beginTransaction()
            .replace(R.id.nav_host_fragment, BoardList())
            .addToBackStack(null)
            .commit()

        return view
    }
}