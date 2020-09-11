package com.example.googlelogin.ui.jobkorea

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JobKoreaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "공채 일정 확인하기"
    }
    val text: LiveData<String> = _text
}