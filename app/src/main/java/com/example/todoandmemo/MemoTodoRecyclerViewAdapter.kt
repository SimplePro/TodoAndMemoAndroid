package com.example.todoandmemo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

//새로운 다른 itemLayout을 만들었다. (MemoRecyclerView 안에서 Dialog 쪽에서 사용됨. Dialog 안에 있는 RecyclerView의 아이디는 memoPlanRecyclerViewDialog이다.)
class MemoTodoRecyclerViewAdapter(val todoList: ArrayList<TodoForm>) : RecyclerView.Adapter<MemoTodoRecyclerViewAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memo_todo_list_item, parent, false)
        Log.d("TAG", "onCreateViewHolder LayoutInflater")
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val Dialog = LayoutInflater.from(parent.context).inflate(R.layout.memo_add_dialog, parent, false)
                val memoListPlanTextDialog = Dialog.findViewById<TextView>(R.id.memoListPlanTextViewDialog)
                //RecyclerView 에서 아이템을 클릭했을 때 이벤트를 어떻게 구현할지 생각해야함.
//                val memoListConstraintLayoutDialog = Dialog.findViewById<ConstraintLayout>(R.id.memoListLayoutDialog)
                memoListPlanTextDialog.text = todoTitleText.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.todoTitleText.text = todoList.get(position).todo
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitleText = itemView.findViewById<TextView>(R.id.memoTodoListTextView)
    }


}