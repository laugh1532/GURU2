package com.example.googlelogin.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.googlelogin.MyPage.MyPage
import com.example.googlelogin.R

class MypageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var root = inflater.inflate(R.layout.fragment_board_list, container, false)

        fragmentManager!!.beginTransaction()
            .replace(R.id.nav_host_fragment, MyPage())
            .addToBackStack(null)
            .commit()

        return root
    }

}