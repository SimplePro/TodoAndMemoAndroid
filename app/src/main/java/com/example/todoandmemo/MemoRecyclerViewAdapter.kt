package com.example.todoandmemo

import android.content.Context
import android.media.Image
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
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
class MemoRecyclerViewAdapter (val memoList: ArrayList<MemoForm>, val DoneTodoList: ArrayList<TodoForm>, private val listener : memoItemClickListener) : RecyclerView.Adapter<MemoRecyclerViewAdapter.CustomViewHolder>(){

    interface memoItemClickListener {
        fun memoOnItemClick(view: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val lottieAnimationAlphaAnimation = AnimationUtils.loadAnimation(parent.context, R.anim.lottie_animation_alpha_animation2)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memo_list_item, parent, false)
        return CustomViewHolder(view).apply {
                memoReplaceButton.setOnClickListener {
                    val dialog = AlertDialog.Builder(parent.context)
                    val edialog: LayoutInflater = LayoutInflater.from(parent.context)
                    val mView: View = edialog.inflate(R.layout.memo_add_dialog, null)
                    val builder: AlertDialog = dialog.create()

                    val memoTitleTextDialog = mView.findViewById<EditText>(R.id.memoTitleEditTextDialog)
                    val memoContentTextDialog = mView.findViewById<EditText>(R.id.memoContentEditTextDialog)
                    val memoListLayoutDialog = mView.findViewById<ConstraintLayout>(R.id.memoListLayoutDialog)
                    val memoPlanConstraintLayoutDialog = mView.findViewById<ConstraintLayout>(R.id.memoPlanLayoutDialog)
                    val memoPlanRecyclerViewLayoutDialog = mView.findViewById<RecyclerView>(R.id.memoPlanRecyclerViewDialog)
                    val memoPlanCancelButtonDialog = mView.findViewById<ImageView>(R.id.memoPlanCancelImageViewDialog)
                    val memoPlanTextDialog = mView.findViewById<TextView>(R.id.memoListPlanTextViewDialog)
                    val memoSaveButtonDialog = mView.findViewById<Button>(R.id.memoSaveButtonDialog)
                    val memoCancelButtonDialog = mView.findViewById<Button>(R.id.memoCancelButtonDialog)
                    var currentTime: Date = Calendar.getInstance().getTime()
                    val date_text: String = SimpleDateFormat("yyyy년 MM월 dd일 EE요일", Locale.getDefault()).format(currentTime)

                    memoTitleTextDialog.setText("${memoList.get(adapterPosition).memoTitle}")
                    memoContentTextDialog.setText("${memoList.get(adapterPosition).memoContent}")


                    builder.setView(mView)
                    builder.show()

                    //memoList 저장 버튼
                    memoSaveButtonDialog.setOnClickListener {
                        Log.d("TAG", "memoButton is pressed")
//                      date_text = SimpleDateFormat("yyyy년 MM월 dd일 EE요일", Locale.getDefault()).format()
                        memoList.set(adapterPosition, MemoForm(memoTitleTextDialog.text.toString(), memoContentTextDialog.text.toString(), date_text, "계획"))
                        notifyItemChanged(adapterPosition, memoList.size)
                        Log.d("TAG", "memoList of size : ${memoList.size}")
                        builder.dismiss()
                    }
                    //Dialog 닫기 버튼
                    memoCancelButtonDialog.setOnClickListener {
                        Log.d("TAG", "memoCancelButton is pressed")
                            builder.dismiss()
                        }
                    //RecyclerView와 같이 나타나는 닫기 버튼 (X 버튼)
                    memoPlanCancelButtonDialog.setOnClickListener {
                        memoListLayoutDialog.visibility = View.VISIBLE
                        memoPlanConstraintLayoutDialog.visibility = View.GONE
                    }
                    // 무슨 계획을 한 후에 쓰는 메모인가요? (선택)
                    memoPlanTextDialog.setOnClickListener {
                        memoListLayoutDialog.visibility = View.INVISIBLE
                        memoPlanConstraintLayoutDialog.visibility = View.VISIBLE
                        memoPlanRecyclerViewLayoutDialog.adapter = MemoTodoRecyclerViewAdapter(DoneTodoList)
                        memoPlanRecyclerViewLayoutDialog.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
                        memoPlanRecyclerViewLayoutDialog.setHasFixedSize(true)
                    }
                    //Dialog 안에 있는 RecyclerView
                    memoPlanRecyclerViewLayoutDialog.setOnClickListener {
                        //RecyclerView 에서 아이템을 클릭했을 때 이벤트를 어떻게 구현할지 생각해야함.
                        memoListLayoutDialog.visibility = View.VISIBLE
                        memoPlanConstraintLayoutDialog.visibility = View.GONE
                    }
                }
            memoRemoveButton.setOnClickListener {
//                if((memoList.size -  1) == 0)
//                {
//                    val mainView = LayoutInflater.from(parent.context).inflate(R.layout.activity_main, parent, false)
//                    val memoLottieAnimationView = mainView.findViewById<LinearLayout>(R.id.memoLottieAnimationLayout)
//                    memoLottieAnimationView.visibility = View.VISIBLE
//                    android.os.Handler().postDelayed({
//                        memoLottieAnimationView.startAnimation(lottieAnimationAlphaAnimation)
//                        Log.d("TAG", "memoRemoveButton is onClicked, log in handler and started lottieAnimationAlphaAnimation")
//                    }, 500)
//                }
                memoList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemChanged(adapterPosition, memoList.size)
                listener.memoOnItemClick(it, adapterPosition)
            }
            }
        }

    override fun getItemCount(): Int {
        return memoList.size
    }


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.memoTitleText.text = memoList.get(position).memoTitle
        holder.memoContentText.text = memoList.get(position).memoContent
        holder.memoCalendarText.text = memoList.get(position).memoCalendar
        holder.memoPlanText.text = "(${memoList.get(position).memoPlan} 후)"
    }
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoTitleText = itemView.findViewById<TextView>(R.id.memoListTitleTextView)
        val memoContentText = itemView.findViewById<TextView>(R.id.memoListContentTextView)
        val memoCalendarText = itemView.findViewById<TextView>(R.id.memoListCalendarTextView)
        val memoPlanText = itemView.findViewById<TextView>(R.id.memoListPlanTextView)
        val memoReplaceButton = itemView.findViewById<ImageView>(R.id.memoListReplaceButton)
        val memoRemoveButton = itemView.findViewById<ImageView>(R.id.memoListRemoveButton)
    }
}