package com.example.googlelogin

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googlelogin.XmlParsingActivity.RecyclerAdapter.ViewHolder
import kotlinx.android.synthetic.main.activity_xml_parsing.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStreamReader
import java.net.URL
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class XmlParsingActivity : AppCompatActivity() {
    lateinit var parser:XmlPullParser
    val datakey = "568d1c1e94d8be2d34f76f9ae539bb87"
    val requestUrl = "http://api.dbpia.co.kr/v2/search/search.xml?target=rated_art&key=568d1c1e94d8be2d34f76f9ae539bb87"
    val list = ArrayList<PaperDTO>()
//    val paper = PaperDTO()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml_parsing)

        parser = XmlPullParserFactory.newInstance().newPullParser()

        var recyclerview: RecyclerView = findViewById(R.id.recyclerview)
        recyclerview.setHasFixedSize(true)

        var layoutManager = LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        recyclerview.setLayoutManager(layoutManager)

        var myAsyncTask = MyAsyncTask()
        myAsyncTask.execute()


//        val adapter = RecyclerAdapter(list)
//        recyclerview.adapter = adapter
    }

 inner class MyAsyncTask : AsyncTask<String, Void, String>() {
     var result = ""


     override fun doInBackground(vararg params: String?): String {

            val requestUrl =  "http://api.dbpia.co.kr/v2/search/search.xml?target=rated_art&key=568d1c1e94d8be2d34f76f9ae539bb87"

            var b_paperTitle = false
            var b_paperAuthor = false
            var b_paperPublisher = false
            var b_paperUrl = false
            var b_paperPages = false
            var b_paperYymm = false

            var b_paperAuthor2 = false

            val url = URL(requestUrl)
            //url.openConnection().getInputStream()
            val openS = url.openStream()
            val factory = XmlPullParserFactory.newInstance()

//            var list = ArrayList<PaperDTO>()
            val parser = factory.newPullParser()
            parser.setInput(InputStreamReader(openS, "UTF-8"))

            val tag = ""
            var eventType = parser.eventType
            var paper = PaperDTO()

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when(eventType) {
                    XmlPullParser.START_DOCUMENT
                    -> {
//                        list = ArrayList<PaperDTO>()
                    }

                    XmlPullParser.END_DOCUMENT
                    -> {}
                    XmlPullParser.END_TAG
                    -> {
                        if (parser.name == "item") {
                        list.add(paper)
                        }
                    }
                    XmlPullParser.START_TAG
                    -> {
                        if (parser.getName().equals("item")) {
                            paper = PaperDTO()
//                          val paper = ClipData.Item()
                        }

                        if (parser.getName().equals("title")) {
                            b_paperTitle = true
                        }

                        if (parser.getName().equals("author")) {
                            b_paperAuthor2 = true

//                            if (parser.getName().equals("name")) {
//                                b_paperAuthor = true
//                            }
                        }

                        if (parser.getName().equals("name")) {
                            b_paperAuthor = true
                        }

                        if (parser.getName().equals("publisher")) {
                            b_paperAuthor = true
//                            if (parser.getName().equals("name")) {
//                                b_paperPublisher = true
//                            }
                        }

                        if (parser.getName().equals("publication") && parser.getName().equals("url")) {
                            b_paperUrl = true
                        }

                        if (parser.getName().equals("pages")) {
                            b_paperPages = true
                        }

                        if (parser.getName().equals("yymm")) {
                            b_paperYymm = true
                        }
                    }

                    XmlPullParser.TEXT -> {
                        if (b_paperTitle){
                            paper.paperTitle = parser.text
                            b_paperTitle = false
                        }
                        if (b_paperAuthor){
                            paper.paperAuthor = parser.text
                            b_paperAuthor = false
                        }
                        if (b_paperPublisher){
                            paper.paperPublisher = parser.text
                            b_paperPublisher = false
                        }
                        if (b_paperUrl){
                            paper.paperUrl = parser.text
                            b_paperUrl = false
                        }
                        if (b_paperPages){
                            paper.paperPages = parser.text
                            b_paperPages = false
                        }
                        if (b_paperYymm){
                            paper.paperYymm = parser.text
                            b_paperYymm = false
                        }
                    }
                }
                eventType = parser.next()
            }
            return result
        }

     override fun onPostExecute(result: String?) {
         super.onPostExecute(result)

         val adapter = RecyclerAdapter(list)
         recyclerview.adapter = adapter
     }
 }

    inner class RecyclerAdapter(private val items: ArrayList<PaperDTO>) :
            RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            val listener = View.OnClickListener { it ->
                Toast.makeText(it.context, "Clicked: ${item.paperTitle}", Toast.LENGTH_LONG).show()
            }
            holder.apply {
                bind(listener, item)
                itemView.tag = item
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val inflatedView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_list_item, parent, false)
            return ViewHolder(inflatedView)
        }

        inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
            private var view:View = view
            fun bind(listener: View.OnClickListener, item: PaperDTO){
                view.findViewById<TextView>(R.id.tv_paperTitle).text = item.paperTitle
                view.findViewById<TextView>(R.id.tv_paperAuthor).text = item.paperAuthor
                view.findViewById<TextView>(R.id.tv_paperPublisher).text = item.paperPublisher
                view.findViewById<TextView>(R.id.tv_paperUrl).text = item.paperUrl
                view.findViewById<TextView>(R.id.tv_paperPages).text = item.paperPages
                view.findViewById<TextView>(R.id.tv_paperYymm).text = item.paperYymm
                view.setOnClickListener(listener)
            }
        }
    }
}
