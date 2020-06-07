package com.example.todoandmemo

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class TodoRecyclerViewAdapter(val todoList: ArrayList<TodoForm>, val DoneTodoList: ArrayList<TodoForm>, private val listener: todoItemClickListener) : RecyclerView.Adapter<TodoRecyclerViewAdapter.CustomViewHolder>() {

//    interface TodoDoneButtonSetOnClickListener {
//        fun todoDoneButton() 1번째 방법
//    }
//
//    var todoDoneButtonSetOnClick : TodoDoneButtonSetOnClickListener? = null 1번째 방법

//    var listener: (() -> Unit)? = null 2번째 방법

    interface todoItemClickListener {
        fun todoOnItemClick(view: View, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item, parent, false)
        Log.d("TAG", "onCreateViewHolder LayoutInflater")
        return CustomViewHolder(view).apply {
            replaceButton.setOnClickListener {
                val dialog = AlertDialog.Builder(parent.context)
                val edialog : LayoutInflater = LayoutInflater.from(parent.context)
                val mView : View = edialog.inflate(R.layout.todo_add_dialog, null)
                val builder : AlertDialog = dialog.create()

                val todoText = mView.findViewById<EditText>(R.id.todoEditTextDialog)
                val contentText = mView.findViewById<EditText>(R.id.contentEditTextDialog)
                val todoButton = mView.findViewById<Button>(R.id.todoButtonDialog)
                val cancelTodoButton = mView.findViewById<Button>(R.id.CancelTodoButtonDialog)


                todoText.setText("${todoList.get(adapterPosition).todo}")
                contentText.setText("${todoList.get(adapterPosition).content}")

                builder.setView(mView)
                builder.show()

                todoButton.setOnClickListener {
                    todoList.set(adapterPosition, TodoForm(todoText.text.toString(), contentText.text.toString()))
                    notifyItemChanged(adapterPosition, todoList.size)
                    builder.dismiss()
                }

                cancelTodoButton.setOnClickListener {
                    builder.dismiss()
                }
            }

            DoneButton.setOnClickListener {
                DoneTodoList.add(todoList.get(adapterPosition))
                todoList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemChanged(adapterPosition, todoList.size)
//                todoDoneButtonSetOnClick?.todoDoneButton() 1번째 방법
//                listener?.invoke() 2번째 방법
                listener.todoOnItemClick(it, adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.todoText.text = todoList.get(position).todo
        Log.d("TAG", "todoList.get(position).todo : ${todoList.get(position).todo}")
        Log.d("TAG", "todoList.get(position).content : ${todoList.get(position).content}")
        Log.d("TAG", "onBindViewHolder successful set todoText")
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoText = itemView.findViewById<TextView>(R.id.todoListTextView)
        val DoneButton = itemView.findViewById<ImageView>(R.id.todoListDoneButton)
        val replaceButton = itemView.findViewById<ImageView>(R.id.todoListReplaceButton)

//        fun bind(todoList: TodoForm){ 3번째 방법
//            itemView.setOnClickListener {
//                listener.onItemClick(it, adapterPosition)
//            }
//        }
    }



}