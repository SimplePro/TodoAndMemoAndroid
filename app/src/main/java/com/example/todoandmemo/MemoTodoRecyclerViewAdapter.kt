package com.example.todoandmemo

import android.content.Context
import android.media.Image
import android.preference.PreferenceManager
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
class MemoTodoRecyclerViewAdapter(val todoList: ArrayList<TodoForm>, val context: Context, private val memoOnClick : memoItemViewOnClickListener)
    : RecyclerView.Adapter<MemoTodoRecyclerViewAdapter.CustomViewHolder>() {

    interface memoItemViewOnClickListener {
        fun memoItemViewOnClick(view: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memo_todo_list_item, parent, false)
        Log.d("TAG", "onCreateViewHolder LayoutInflater")
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                saveData(todoList.get(adapterPosition).todo)
                memoOnClick.memoItemViewOnClick(it, adapterPosition)
                Log.d("TAG", "saveData ${todoList.get(adapterPosition).todo}")
            }
            DoneTodoListRemoveButton.setOnClickListener {
                todoList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemChanged(adapterPosition, todoList.size)
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
        val DoneTodoListRemoveButton = itemView.findViewById<ImageView>(R.id.memoTodoListRemoveButton)
    }

    private fun saveData(memoPlanText: String){
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()

        editor.putString("memoPlanText", memoPlanText)
            .apply()

    }


}