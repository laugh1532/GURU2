package com.example.googlelogin.Study


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.googlelogin.*
import com.example.myapplication.PostList
import com.example.myapplication.ShowPost
import com.example.myapplication.WritePost
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_show_post.*
import kotlinx.android.synthetic.main.fragment_show_study.*
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class StudyShow : Fragment() {
    private var firestore = FirebaseFirestore.getInstance()

    fun newInstance3(studyPostName: String): Fragment {
        var fragment = StudyShow()
        var bundle = Bundle()

        bundle.putString("StudyPostName", studyPostName)
        fragment.setArguments(bundle)

        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //댓글 리스트 받을 arraylist
        var replyDTOList : ArrayList<ReplyDTO> = ArrayList<ReplyDTO>()

        //view 생성
        var view = inflater.inflate(R.layout.fragment_show_study, container, false)
        var studypostView = inflater.inflate(R.layout.show_study_item_in_listview, null, false)

        //textView 생성, 글 내용
        var title_v = studypostView.findViewById<TextView>(R.id.study_title)
        var writer_v = studypostView.findViewById<TextView>(R.id.study_writer)
        var date_v = studypostView.findViewById<TextView>(R.id.study_date)
        var content_v = studypostView.findViewById<TextView>(R.id.study_content)
        var location_V = studypostView.findViewById<TextView>(R.id.study_location)
        var theme_v = studypostView.findViewById<TextView>(R.id.study_theme)

        //button 생성, 글 내용
        var btn_back = studypostView.findViewById<ImageButton>(R.id.btn_back)
        var btn_studyroom = studypostView.findViewById<Button>(R.id.btn_studyroom)

        //listView 생성
        var replyListView = view.findViewById<ListView>(R.id.study_replyListView)

        //listView header에 추가
        replyListView.addHeaderView(studypostView)

        //button
        var btn_study_reply_save = view.findViewById<Button>(R.id.btn_study_reply_save)

        //editText 생성
        var et_study_reply: EditText = view.findViewById(R.id.et_study_reply) //댓글 내용
        et_study_reply.requestFocus()

        //뒤로가기 버튼
        btn_back.setOnClickListener { view ->
            fragmentManager!!.popBackStack()
        }

        //collection과 document 정보 받아오기
        val studyPostName = getArguments()?.getString("StudyPostName")
        var study_documentName = ""


        //post 내용 DB에서 불러우기
        firestore?.collection("Study")
            ?.whereEqualTo("studyTitle", studyPostName)
            ?.get()
            ?.addOnSuccessListener { result ->
                for (document in result) {
                    var studypostDTO = document.toObject(StudyPostDTO::class.java)
                    title_v.text = studypostDTO?.studyTitle.toString()
                    writer_v.text = studypostDTO?.studyWriter.toString()
                    date_v.text = studypostDTO?.studyDate.toString()
                    content_v.text = studypostDTO?.studyContent.toString()
                    location_V.text = studypostDTO?.studyLocation.toString()
                    theme_v.text = studypostDTO?.studyTheme.toString()
                    study_documentName = studypostDTO?.studyDocumentName.toString()
                }

                firestore?.collection("Study")
                    ?.document(study_documentName)
                    ?.collection("reply")
                    .orderBy("date")
                    ?.get()
                    ?.addOnSuccessListener { result ->
                        var replyDTO : ReplyDTO

                        for (document in result) {
                            replyDTO = document.toObject(ReplyDTO::class.java)

                            replyDTOList.add(replyDTO)
                        }

                        //listView에 adapter 연결 처음
                        var adapter = MyAdapter(this!!.activity!!, replyDTOList)
                        replyListView.adapter = adapter
                    }
            }

        //listView에 adapter 연결 다시
        var adapter = MyAdapter(this!!.activity!!, replyDTOList)
        replyListView.adapter = adapter

        //댓글 저장 버튼 이벤트
        btn_study_reply_save.setOnClickListener { view ->
            setReply(study_documentName, replyDTOList)
        }

//        //스터디룸 추천 버튼
//        btn_studyroom.setOnClickListener { view ->
//            fragmentManager!!.beginTransaction()
//                .replace(R.id.nav_host_fragment, StudyRoom().newInstance4(location_V.text.toString()))
//                .addToBackStack(null)
//                .commit()
//        }

        return view
    }

    //댓글 저장 함수
    private fun setReply(study_documentName: String, replyDTOList:ArrayList<ReplyDTO>) {
        val user = FirebaseAuth.getInstance().currentUser //댓글 사용자

        //댓글 저장 시 필요한 정보 저장
        val replyContent = et_study_reply.text.toString()
        val dateAndtime = LocalDateTime.now()
        val replyDate = dateAndtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS"))
        val replyWriter = user?.email.toString()

        //댓글 작성자 전공 가져오기
        firestore.collection("userInfo")
            ?.document(user?.email.toString())
            ?.get()
            ?.addOnSuccessListener { document ->
                var userDTO: UserDTO? = document.toObject(UserDTO::class.java)
                var replyMajor: String? = userDTO!!.major

                val replyDTO = ReplyDTO(
                    replyContent,
                    replyDate,
                    replyWriter,
                    replyMajor
                )

                //댓글 갱신하기
                firestore?.collection("Study")
                    ?.document(study_documentName)
                    ?.collection("reply")
                    ?.add(replyDTO)
                    ?.addOnCompleteListener { task ->

                        firestore?.collection("Study")
                            ?.document(study_documentName)
                            ?.collection("reply")
                            .orderBy("date", Query.Direction.DESCENDING).limit(1)
                            ?.get()
                            ?.addOnSuccessListener { result ->
                                var replyDTO : ReplyDTO

                                for (document in result) {
                                    replyDTO = document.toObject(ReplyDTO::class.java)
                                    replyDTOList.add(replyDTO)
                                }

                                //listView에 adapter 연결 (갱신)
                                var adapter = MyAdapter(this!!.activity!!, replyDTOList)
                                replyListView.adapter = adapter
                            }
                    }
            }


        //editText에 내용 없애고 키보드 숨기기
        et_study_reply.setText("")
        et_study_reply.clearFocus()
        val searchView = this.activity!!.getCurrentFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView?.windowToken, 0)
    }

    inner class MyAdapter(context: Context, replyDTOList:ArrayList<ReplyDTO>) : BaseAdapter(){
        var context = context
        var replyDTOList = replyDTOList

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.post_reply_item, null)

            var writer = view.findViewById<TextView>(R.id.writer)
            var date= view.findViewById<TextView>(R.id.date)
            var content= view.findViewById<TextView>(R.id.content)
            var major = view.findViewById<TextView>(R.id.major)

            writer.text = replyDTOList[position].writer
            date.text = replyDTOList[position].date
            content.text = replyDTOList[position].content
            major.text = replyDTOList[position].major

            return view
        }

        override fun getItem(position: Int): Any {
            return replyDTOList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return replyDTOList.size
        }
    }
}
