package com.example.googlelogin.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.googlelogin.R
import com.example.googlelogin.Study.StudyPostList
import com.example.myapplication.BoardList
import com.example.myapplication.PostList

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    /////

    ): View? {
        var view = inflater.inflate(R.layout.fragment_study_post_list, container, false)

        fragmentManager!!.beginTransaction()
            .replace(R.id.nav_host_fragment, StudyPostList())
            .addToBackStack(null)
            .commit()

        return view
    }
}