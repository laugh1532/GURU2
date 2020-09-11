package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.widget.BaseAdapter
import com.example.googlelogin.R

/**
 * A simple [Fragment] subclass.
 */
class BoardList : Fragment() {
    //fragment
    var postList = PostList()

    //실제 사용할 게시판 목록
    var boardList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //게시판 목록
        var boards_name:ArrayList<String> = ArrayList<String>()
        boards_name = arrayListOf(
            "국어국문학과","영어영문학과","중어중문학과", "수학과",
            "화학과","물리학과","기계공학과","전자공학과","컴퓨터공학과",
            "정보보호학과", "소프트웨어융합학과", "디지털미디어학과", "언론영상학부","사학과","심리학과")

        boardList = boards_name

        //view 생성
        var view = inflater.inflate(R.layout.fragment_board_list, container, false)

        //listView 생성
        var boardListView:ListView = view.findViewById<ListView>(R.id.boardListView)

        //editText 생성
        var editText:EditText = view.findViewById(R.id.editText)

        //listView에 adapter 연결
        var adapter = MyAdapter(this!!.activity!!, boards_name)
        boardListView.adapter = adapter

        //listView 클릭시 해당 postlist로 이동
        boardListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            fragmentManager!!.beginTransaction()
                .replace(R.id.nav_host_fragment, postList.newInstance(boardList[position]))
                .addToBackStack(null)
                .commit()
        }

        //검색기능 구현
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val search = s.toString()
                (boardListView.getAdapter() as MyAdapter).getFilter()?.filter(search)
            }
        })

        return view
    }

    inner class MyAdapter(context: Context, boards_name: ArrayList<String>) : BaseAdapter(), Filterable{
        var context = context

        var listViewItemList = boards_name //원본 arrayList
        var filteredItemList: ArrayList<String> = listViewItemList //검색했을 때 보여줄 arrayList

        var listFilter: Filter? = null //리스트 필터

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = LayoutInflater.from(context).inflate(R.layout.board_list_item, null)

            var title = view.findViewById<TextView>(R.id.title)

            title.text = filteredItemList[position]

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
                    result.values = listViewItemList
                    result.count = listViewItemList.size
                }
                else{
                    var itemList : ArrayList<String> = ArrayList<String>()

                    //검색어와 게시판 이름 비교
                    for(item:String in listViewItemList){
                        if(item.toUpperCase().contains(constraint.toString().toUpperCase())){
                            itemList.add(item)
                        }
                    }
                    result.values = itemList
                    result.count = itemList.size

                    boardList = itemList
                }
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as ArrayList<String>

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