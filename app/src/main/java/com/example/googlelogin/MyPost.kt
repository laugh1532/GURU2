package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.googlelogin.PostDTO
import com.example.googlelogin.R
import com.example.googlelogin.Study.StudyShow
import com.example.googlelogin.StudyListDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */

class MyPost : Fragment() {
    //fragment
    var showPost = ShowPost()
    var studyShow = StudyShow()

    //firestore 연결 준비
    val firestore = FirebaseFirestore.getInstance()

    //실제 listView 클릭시 사용할 데이터
    var tmpPost:ArrayList<PostDTO> = ArrayList<PostDTO>()
    var tmpStudy:ArrayList<StudyListDTO> = ArrayList<StudyListDTO>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //db에서 받아올 데이터
        var postList:ArrayList<PostDTO> = ArrayList<PostDTO>()
        var studyList:ArrayList<StudyListDTO> = ArrayList<StudyListDTO>()

        // view 생성
        var view = inflater.inflate(R.layout.fragment_my_post, container, false)

        //listView 생성
        var postListView: ListView = view.findViewById<ListView>(R.id.postListView)

        //button 생성
        var btn_back : ImageButton = view.findViewById(R.id.btn_back)

        //editText 생성
        var editText:EditText = view.findViewById(R.id.editText)

        //textView 가져오기
        var textView:TextView = view.findViewById(R.id.textView)
        var boardName = view.findViewById<TextView>(R.id.textView2)

        //switch 가져오기
        var switch:Switch = view.findViewById(R.id.switch1)

        //사용자 정보 가져오기
        val user = FirebaseAuth.getInstance().currentUser

        //게시판 이름 가져오기
        textView.text = user?.email.toString()

        //학과 게시판 글 가져오기
        firestore?.collection("Board")
            ?.whereEqualTo("writer", user?.email.toString())
            ?.get()
            ?.addOnSuccessListener { result ->
                var postDTO:PostDTO
                for (document in result) {
                    postDTO = document.toObject(PostDTO::class.java)
                    postList.add(postDTO)
                }

                //내림차순 정렬
                Collections.sort(postList, PostDTO())

                //실제 데이터에 사용
                tmpPost = postList

                var myPostAdapter = MyPostAdapter(this@MyPost!!.activity!!, postList)
                postListView.adapter = myPostAdapter
            }

        //스터디 게시판 글 가져오기
        firestore?.collection("Study")
            ?.whereEqualTo("studyWriter", user?.email.toString())
            ?.get()
            ?.addOnSuccessListener { result ->
                var studyListDTO:StudyListDTO
                for (document in result) {
                    studyListDTO = document.toObject(StudyListDTO::class.java)
                    studyList.add(studyListDTO)
                }

                //내림차순 정렬
                Collections.sort(studyList, StudyListDTO())

                tmpStudy = studyList
            }

        //스위치 이벤트
        switch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                //스터디 게시판
                if(isChecked) {
                    //체크된 상태로 만들 시 코드
                    boardName.text = "      스터디 게시판"
                    postListView.adapter = MyStudyAdapter(this@MyPost!!.activity!!, studyList)
                }
                //학과 게시판
                else {
                    //체크된 상태 취소 시 코드
                    boardName.text = "        학과 게시판"
                    postListView.adapter = MyPostAdapter(this@MyPost!!.activity!!, postList)
                }
            }
        })

        //listView 클릭 이벤트
        postListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            //스터디 게시판
            if(switch.isChecked){
                fragmentManager!!.beginTransaction()
                    .replace(R.id.nav_host_fragment, studyShow.newInstance3(tmpStudy[position].studyTitle!!))
                    .addToBackStack(null)
                    .commit()
            }
            //학과 게시판
            else{
                fragmentManager!!.beginTransaction()
                    .replace(R.id.nav_host_fragment, showPost.newInstance2(tmpPost[position].title!!))
                    .addToBackStack(null)
                    .commit()
            }
        }

        //검색기능
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val search = s.toString()
                //스터디 게시판
                if(switch.isChecked){
                    (postListView.getAdapter() as MyStudyAdapter?)?.getFilter()?.filter(search)
                }
                //학과 게시판
                else{
                    (postListView.getAdapter() as MyPostAdapter?)?.getFilter()?.filter(search)
                }

            }
        })

        //뒤로가기 button 이벤트
        btn_back.setOnClickListener { view ->
            fragmentManager!!.popBackStack()
        }

        return view
    }

    //StudyListView 어댑터
    inner class MyStudyAdapter(context: Context, studyListDTOList:ArrayList<StudyListDTO>) : BaseAdapter(), Filterable{
        var context = context

        var studyList = studyListDTOList //원본 arrayList
        var filteredItemList = studyList //필터링 arrayList

        var listFilter: Filter? = null

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.study_list_item, null)

            var title = view.findViewById<TextView>(R.id.title)
            var location = view.findViewById<TextView>(R.id.location)
            var theme = view.findViewById<TextView>(R.id.theme)

            title.text = filteredItemList[position].studyTitle
            location.text = filteredItemList[position].studyLocation
            theme.text = filteredItemList[position].studyTheme

            return view
        }

        //검색기능 구현, filterable 오버라이딩
        override fun getFilter(): Filter? {
            if(listFilter == null){
                listFilter = ListFilter()
            }

            return listFilter
        }

        inner class ListFilter() : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var result : FilterResults = FilterResults()

                if(constraint == null || constraint.length == 0){
                    result.values = studyList
                    result.count = studyList.size
                }
                else{
                    var itemList : ArrayList<StudyListDTO> = ArrayList<StudyListDTO>()

                    for(item:StudyListDTO in studyList){
                        if(item.studyTitle!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }
                        else if(item.studyTheme!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }
                    }

                    result.values = itemList
                    result.count = itemList.size

                    tmpStudy = itemList
                }
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as ArrayList<StudyListDTO>

                if(results.count > 0) {
                    notifyDataSetChanged()
                }
                else{
                    notifyDataSetInvalidated()
                }
            }
        }

        override fun getItem(position: Int): Any {
            return filteredItemList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return filteredItemList.size
        }
    }


    //postListView 어댑터
    inner class MyPostAdapter(context: Context, postDTOList:ArrayList<PostDTO>) : BaseAdapter(), Filterable{
        var context = context

        var postList = postDTOList

        //        var listViewItemList  = posts_name //원본 listView
//        var listViewItemList2 = post_boardName
        var filteredItemList = postList //검색했을 때 보여줄 listView

        var listFilter: Filter? = null

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.post_list_item, null)

            //view에서 textView 받아오기
            var title = view.findViewById<TextView>(R.id.title)
            var content = view.findViewById<TextView>(R.id.content)
            var major = view.findViewById<TextView>(R.id.major)
            var date = view.findViewById<TextView>(R.id.date)

            major.visibility = View.VISIBLE //major textView 보이게 하기

            //textView에 정보 저장
            title.text = filteredItemList[position].title
            content.text = filteredItemList[position].content
            date.text = filteredItemList[position].date
            major.text = filteredItemList[position].boardMajor

            return view
        }

        //검색기능 구현, filterable 오버라이딩
        override fun getFilter(): Filter? {
            if(listFilter == null){
                listFilter = ListFilter()
            }

            return listFilter
        }

        inner class ListFilter() : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var result : FilterResults = FilterResults()

                if(constraint == null || constraint.length == 0){
                    result.values = postList
                    result.count = postList.size
                }
                else{
                    var itemList : ArrayList<PostDTO> = ArrayList<PostDTO>()

                    for(item:PostDTO in postList){
                        if(item.title!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }
                        else if(item.content!!.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }
                    }

                    result.values = itemList
                    result.count = itemList.size

                    tmpPost = itemList
                }
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as ArrayList<PostDTO>

                if(results.count > 0) {
                    notifyDataSetChanged()
                }
                else{
                    notifyDataSetInvalidated()
                }
            }
        }

        override fun getItem(position: Int): Any {
            return filteredItemList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return filteredItemList.size
        }
    }























}