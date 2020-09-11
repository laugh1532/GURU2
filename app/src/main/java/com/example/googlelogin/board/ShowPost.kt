package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.example.googlelogin.PostDTO
import com.example.googlelogin.R
import com.example.googlelogin.ReplyDTO
import com.example.googlelogin.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_show_post.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */

class ShowPost : Fragment() {
    //firestore 연결 준비
    private var firestore = FirebaseFirestore.getInstance()

    //PostName 받아오는 함수 정의
    fun newInstance2(boardPostName: String): Fragment {
        var fragment = ShowPost()
        var bundle = Bundle()

        bundle.putString("PostName", boardPostName)

        fragment.setArguments(bundle)

        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /***** 자기가 쓴 글 수정하는 기능 구현 *****/

        //db에서 댓글 받을 객체를 담은 리스트 초기화
        var replyDTOList:ArrayList<ReplyDTO> = ArrayList<ReplyDTO>()

        //view 생성
        var view = inflater.inflate(R.layout.fragment_show_post, container, false)
        var postView = inflater.inflate(R.layout.show_post_item_in_listview, null, false)

        //textView 생성
        var title_v = postView.findViewById<TextView>(R.id.title)
        var writer_v = postView.findViewById<TextView>(R.id.writer)
        var date_v = postView.findViewById<TextView>(R.id.date)
        var content_v = postView.findViewById<TextView>(R.id.content)

        //버튼 생성
        var btn_back: ImageButton = postView.findViewById(R.id.btn_back)
        var button9: Button = view.findViewById(R.id.button9) //댓글 저장 버튼

        //listView 생성
        var replyListView = view.findViewById<ListView>(R.id.replyListView)

        //listView header에 추가
        replyListView.addHeaderView(postView)

        //editText 생성
        var editText4: EditText = view.findViewById(R.id.editText4) //댓글 내용
        editText4.requestFocus()

        //imageView 생성
        var imageView2 = postView.findViewById<ImageView>(R.id.imageView2)

        //뒤로가기 버튼 이벤트
        btn_back.setOnClickListener { view ->
            fragmentManager!!.popBackStack()
        }

        //collection과 document 정보 받아오기
        val postName = getArguments()?.getString("PostName")
        //var boardMajor = getArguments()?.getString("boardMajor")
        var post_documentName = ""
        var fileName = ""

        //post 내용 DB에서 불러우기
        firestore?.collection("Board")
            ?.whereEqualTo("title", postName)
            ?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                for (document in documentSnapshot) {
                    var postDTO = document.toObject(PostDTO::class.java)

                    title_v.text = postDTO?.title.toString()
                    writer_v.text = postDTO?.writer.toString()
                    date_v.text = postDTO?.date.toString()
                    content_v.text = postDTO?.content.toString()
                    post_documentName = postDTO?.documentName.toString()
                    fileName = postDTO?.fileName.toString()
                }

                //사진 불러오기
                if(fileName != "" || fileName == null){
                    var storage = FirebaseStorage.getInstance()
                    var storageRef = storage.reference
                    var imageRef = storageRef.child(fileName)

                    imageRef.getDownloadUrl().addOnCompleteListener { task ->
                        if (task.isSuccessful()) {
                            // Glide 이용하여 이미지뷰에 로딩
                            Glide.with(this)
                                .load(task.getResult())
                                .override(1024, 980)
                                .into(imageView2)
                        } else {
                            // URL을 가져오지 못하면 토스트 메세지
                            Toast.makeText(
                                this!!.activity!!,
                                task.getException().toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }

                //댓글 불러오기
                firestore?.collection("Board")
                    ?.document(post_documentName)
                    ?.collection("reply")
                    .orderBy("date")
                    ?.get()
                    ?.addOnSuccessListener { result ->
                        //댓글 하나 받아올 replyDTO
                        var replyDTO: ReplyDTO?

                        for (document in result) {
                            replyDTO = document.toObject(ReplyDTO::class.java) //db에서 받아옴
                            replyDTOList.add(replyDTO) //replyDTOList에 저장
                        }
                        //listView에 adapter 연결 (처음)
                        var adapter = MyAdapter(this!!.activity!!, replyDTOList)
                        replyListView.adapter = adapter
                    }
            }

        //listView에 adapter 연결 (다시)
        var adapter = MyAdapter(this!!.activity!!, replyDTOList)
        replyListView.adapter = adapter

        //댓글 저장 버튼 이벤트
        button9.setOnClickListener { view ->
            setReply(post_documentName, replyDTOList)
        }

        return view
    }

    //댓글 저장 함수
    private fun setReply(post_documentName: String, replyDTOList:ArrayList<ReplyDTO>) {
        val user = FirebaseAuth.getInstance().currentUser //댓글 사용자

        //댓글 내용 저장할 db 공간 생성
        val replyContent = editText4.text.toString()
        val dateAndtime = LocalDateTime.now()
        val replyDate = dateAndtime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS"))
        val replyWriter = user?.email.toString()

        //댓글 작성자 전공 가져오기
        firestore.collection("userInfo")
            ?.document(user?.email.toString())
            ?.get()
            ?.addOnSuccessListener { document->
                var userDTO: UserDTO? = document.toObject(UserDTO::class.java)
                var replyMajor:String? = userDTO!!.major

                val replyDTO = ReplyDTO(
                    replyContent,
                    replyDate,
                    replyWriter,
                    replyMajor
                )

                //댓글 갱신하기
                firestore?.collection("Board")
                    ?.document(post_documentName)
                    ?.collection("reply")
                    ?.add(replyDTO)
                    ?.addOnCompleteListener { task ->
                        firestore?.collection("Board")
                            ?.document(post_documentName)
                            ?.collection("reply")
                            .orderBy("date", Query.Direction.DESCENDING).limit(1)
                            ?.get()
                            ?.addOnSuccessListener { result ->
                                var replyDTO: ReplyDTO?
                                for (document in result) {
                                    replyDTO = document.toObject(ReplyDTO::class.java)
                                    replyDTOList.add(replyDTO)

                                    //listView에 adapter 연결 (갱신)
                                    var adapter = MyAdapter(this!!.activity!!, replyDTOList)
                                    replyListView.adapter = adapter
                                }
                            }
                    }
            }

        //editText에 내용 없애고 키보드 숨기기
        editText4.setText("")
        editText4.clearFocus()
        val searchView = this.activity!!.getCurrentFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView?.windowToken, 0)
    }

    inner class MyAdapter(context: Context, replyDTOList:ArrayList<ReplyDTO>) : BaseAdapter() {
        var context = context
        var replyDTOList = replyDTOList

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val view: View = LayoutInflater.from(context).inflate(R.layout.post_reply_item, null)

            var writer = view.findViewById<TextView>(R.id.writer)
            var date = view.findViewById<TextView>(R.id.date)
            var content = view.findViewById<TextView>(R.id.content)
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