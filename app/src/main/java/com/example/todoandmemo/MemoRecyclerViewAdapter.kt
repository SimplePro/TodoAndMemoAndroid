package com.example.todoandmemo

import android.content.Context
import android.media.Image
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

//todoList까지 받는 이유 : todoList를 Dialog 안에 있는 RecyclerView의 아이템으로 쓰기 위해서이다. 나중에 서버쪽을 작업하게 됬을 때 todoList를 다른 ArrayList형 변수로 바꿔줘야 한다.
class MemoRecyclerViewAdapter (val memoList: ArrayList<MemoForm>, val DoneTodoList: ArrayList<TodoForm>, private val RemoveListener : memoItemClickListener,
                               private val ReplaceListener : memoItemReplaceClickListener)    : RecyclerView.Adapter<MemoRecyclerViewAdapter.CustomViewHolder>(), Filterable{

    var memoSearchList: MutableList<MemoForm> = mutableListOf()

    init {
        memoSearchList = memoList
    }

    //메모의 Remove 버튼이 클릭되었을 때 호출되는 콜백 함수
    interface memoItemClickListener {
        fun memoOnItemClick(view: View, position: Int)
    }

    //메모의 replace 버튼이 클릭되었을 때 호출되는 콜백 함수
    interface memoItemReplaceClickListener {
        fun memoItemReplaceClick(view: View, position: Int)
    }

    //역할 : recyclerView 가 생성되었을 때 실행하는 것.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memo_list_item, parent, false)
        return CustomViewHolder(view).apply {
            //memoItem 의 Replace 버튼이 클릭 되었을 때
            memoReplaceButton.setOnClickListener {
                //Replace 콜백 함수를 호출한다.
                ReplaceListener.memoItemReplaceClick(it, adapterPosition)
            }

            //memoItem 의 Remove 버튼이 클릭 되었을 때
            memoRemoveButton.setOnClickListener {
                //해당 position 의 값을 삭제한다.
                memoList.removeAt(adapterPosition)
                //notify 로 recyclerView 에 반영한다.
                notifyItemRemoved(adapterPosition)
                notifyItemChanged(adapterPosition, memoList.size)
                //Remove 콜백 함수를 호출한다.
                RemoveListener.memoOnItemClick(it, adapterPosition)
            }
            }
        }

    //역할 : recyclerView 에 들어갈 item 의 개수를 반환하는 것.
    override fun getItemCount(): Int {
        return memoSearchList.size
    }


    //역할 : recyclerView 에 데이터를 할당하는 것.
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.memoTitleText.text = memoList.get(position).memoTitle
        holder.memoContentText.text = memoList.get(position).memoContent
        holder.memoCalendarText.text = memoList.get(position).memoCalendar
        memoPlanText(holder, position)
    }

    //역할 : 변수에 findViewById 를 하여 대입하는 것.
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoTitleText = itemView.findViewById<TextView>(R.id.memoListTitleTextView)
        val memoContentText = itemView.findViewById<TextView>(R.id.memoListContentTextView)
        val memoCalendarText = itemView.findViewById<TextView>(R.id.memoListCalendarTextView)
        val memoPlanText = itemView.findViewById<TextView>(R.id.memoListPlanTextView)
        val memoReplaceButton = itemView.findViewById<ImageView>(R.id.memoListReplaceButton)
        val memoRemoveButton = itemView.findViewById<ImageView>(R.id.memoListRemoveButton)
    }

    //역할 : memoPlanText 의 text 형식을 정해주는 것.
    private fun memoPlanText (holder: CustomViewHolder, position : Int) {
        if(memoList.get(position).memoPlan == "") {
            holder.memoPlanText.text = ""
        }
        else if(memoList.get(position).memoPlan != "") {
            holder.memoPlanText.text = "(${memoList.get(position).memoPlan} 후)"
        }
    }

    //역할 : filter 를 이용하여 memoSearchList 를 조정하는 것.
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if(charSearch.isEmpty()) {
                    memoSearchList = memoList
                } else {
                    val resultList = ArrayList<MemoForm>()
                    memoSearchList = memoList
                    for(row in memoList)
                    {
                        if(row.memoContent.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))
                            || row.memoTitle.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))
                            || row.memoPlan.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    memoSearchList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = memoSearchList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                memoSearchList = results?.values as ArrayList<MemoForm>
                notifyDataSetChanged()
            }
        }
    }


}