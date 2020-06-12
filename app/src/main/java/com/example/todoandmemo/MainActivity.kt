package com.example.todoandmemo

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.memo_add_dialog.*
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.prefs.PreferenceChangeEvent
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), TodoRecyclerViewAdapter.todoItemClickListener, MemoRecyclerViewAdapter.memoItemClickListener,
    MemoRecyclerViewAdapter.memoItemReplaceClickListener, MemoTodoRecyclerViewAdapter.memoItemViewOnClickListener
{

    //변수 선언
    var todoList: MutableList<TodoForm> = mutableListOf()
    var memoList: MutableList<MemoForm> = mutableListOf()
    var DoneTodoList: MutableList<TodoForm> = mutableListOf()
    //LottieAnimation의 VISIBLE을 정해주기 위해서 선언하는 변수 (false면 VISIBLE true 면 GONE)
    var todoLottieAnimationVisibleForm = false
    var memoLottieAnimationVisibleForm = false
    //중복 LottieAnimation을 방지하기 위함. 또는 다른 여러가지 클릭 이벤트에서 사용됨.
    var tabMenuBoolean = "TODO"

    //메모 리스트에 들어갈 날짜 항목
    var currentTime: Date = Calendar.getInstance().getTime()
    var date_text: String = "null"

    var lottieAnimationAlphaAnimation : Animation? = null
    var startLottieAnimationAlphaAnimation: Animation? = null

    var position: Int = 0

    lateinit var memoDialog: AlertDialog.Builder
    lateinit var memoEdialog: LayoutInflater
    lateinit var memoMView: View
    lateinit var memoBuilder: AlertDialog

    lateinit var memoTitleTextDialog: EditText
    lateinit var memoContentTextDialog: EditText
    lateinit var memoListLayoutDialog: ConstraintLayout
    lateinit var memoPlanConstraintLayoutDialog : ConstraintLayout
    lateinit var memoPlanRecyclerViewLayoutDialog : RecyclerView
    lateinit var memoPlanCancelButtonDialog : ImageView
    lateinit var memoPlanTextDialog : TextView
    lateinit var memoSaveButtonDialog : Button
    lateinit var memoCancelButtonDialog : Button

    var memoPlanText : String = ""

    //역할 : 액티비티가 생성되었을 때.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lottieAnimationAlphaAnimation = AnimationUtils.loadAnimation(this, R.anim.lottie_animation_alpha_animation)
        startLottieAnimationAlphaAnimation = AnimationUtils.loadAnimation(this, R.anim.lottie_animation_alpha_animation2)

        //만일 todoList 의 사이즈가 1이면 GONE 으로 되는 todoLottieAnimationVisibleForm 을 true 로 바꾸어 LottieAnimationView 를 GONE 형태로 바꾸어 줘야함.
        if(todoList.size == 1) {
            todoLottieAnimationVisibleForm = true
        }
        if(todoLottieAnimationVisibleForm == true) {
            todoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
            Handler().postDelayed({
                todoLottieAnimationLayout.visibility = View.GONE
                todoLottieAnimationVisibleForm = false
            }, 500)
        }


        //여기는 위에 부분과 똑같음. 위에는 todoLottieAnimationVisibleForm 이였지만 여기는 memoLottieAnimationVisibleForm 이다.
        if(memoList.size == 1) {
            memoLottieAnimationVisibleForm = true
        }
        if(memoLottieAnimationVisibleForm == true) {
            memoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
            Handler().postDelayed({
                memoLottieAnimationLayout.visibility = View.GONE
                memoLottieAnimationVisibleForm = false
            }, 500)
        }

        //추가 버튼이 클릭되었을 때.
        addButton.setOnClickListener {

            //만일 이용자가 TODO를 클릭한 상태라면
            if(tabMenuBoolean == "TODO") {
                //Dialog 띄어줌
                todoDialogDeclaration()
            }

            //만일 사용자가 MEMO 버튼을 누른 상태라면
            else if(tabMenuBoolean == "MEMO") {
                //Dialog 띄어줌.
                memoDialogDeclaration()
            }
        }

        //Menu 에 있는 todo버튼이 눌렸을 때
        tabMenuTodoLayout.setOnClickListener {

            //이미 TODO버튼이 눌린 상태라면
            if (tabMenuBoolean == "TODO") {
                Log.d("TAG", "tabMenuBoolean is TODO")
            }

            //tabMenuBoolean 이 TODO가 아니고, todoList의 사이즈가 0이라면
            else if (todoList.size == 0) {
                //todoRecyclerView 는 보여주고, memoRecyclerView 는 안 보여준다.
                todoRecyclerView.visibility = View.VISIBLE
                memoRecyclerView.visibility = View.GONE
                //tabMenuBoolean 의 값을 TODO로 만들어주어 TODO가 클릭 됬음을 표시한다.
                tabMenuBoolean = "TODO"
                //TODO가 선택됬음을 사용자에게 알리기 위해 보기 좋게 Background 를 변경한다.
                tabMenuTodoLayout.setBackgroundResource(R.drawable.selected_tab_menu_background)
                tabMenuMemoLayout.setBackgroundResource(R.drawable.rectangle_tab_menu_background)
                stateTextView.text = "TODO"
                //todoLottieAnimationLayout 을 애니메이션과 함께 자연스럽게 보여준다.
                todoLottieAnimationLayout.visibility = View.VISIBLE
                todoLottieAnimationLayout.startAnimation(startLottieAnimationAlphaAnimation)

                //만일 memoList 의 사이즈가 0이라면
                if (memoList.size == 0) {
                    //memoLottieAnimationView 를 애니메이션과 함께 자연스럽게 GONE 으로 바꿈.
                    memoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                    Handler().postDelayed({
                        memoLottieAnimationLayout.visibility = View.GONE
                    }, 500)
                }
            }
            //만일 todoList 의 사이즈가 0보다 크다면
            else {
                //todoRecyclerView는 보여주고 memoRecyclerView는 안 보여준다.
                todoRecyclerView.visibility = View.VISIBLE
                memoRecyclerView.visibility = View.GONE
                tabMenuBoolean = "TODO"
                tabMenuTodoLayout.setBackgroundResource(R.drawable.selected_tab_menu_background)
                tabMenuMemoLayout.setBackgroundResource(R.drawable.rectangle_tab_menu_background)
                stateTextView.text = "TODO"
                if (memoList.size == 0) {
                    memoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                    Handler().postDelayed({
                        memoLottieAnimationLayout.visibility = View.GONE
                    }, 500)
                }
            }
        }

        //Menu 에 있는 MEMO 가 눌렸다면
        tabMenuMemoLayout.setOnClickListener {
            //만일 이미 MEMO 가 눌린 상태라면
            if(tabMenuBoolean == "MEMO") {
                Log.d("TAG", "tabMenuBoolean is MEMO")
            }

            //memoList 의 사이즈가 0이라면
            else if(memoList.size == 0) {
                //todoRecyclerView 는 안보여주고, memoRecyclerView 는 보여준다.
                todoRecyclerView.visibility = View.GONE
                memoRecyclerView.visibility = View.VISIBLE
                tabMenuBoolean = "MEMO"
                tabMenuMemoLayout.setBackgroundResource(R.drawable.selected_tab_menu_background)
                tabMenuTodoLayout.setBackgroundResource(R.drawable.rectangle_tab_menu_background)
                stateTextView.text = "MEMO"
                //memoLottieAnimationView 를 애니메이션과 함께 보여준다.
                memoLottieAnimationLayout.visibility = View.VISIBLE
                memoLottieAnimationLayout.startAnimation(startLottieAnimationAlphaAnimation)
                //todoList 의 사이즈가 0이라면
                if(todoList.size == 0)
                {
                    //todoLottieAnimationView 를 애니메이션과 함께 안 보여준다.
                    todoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                    Handler().postDelayed({
                        todoLottieAnimationLayout.visibility = View.GONE
                    }, 500)
                }
            }

            //memoList 의 사이즈가 0보다 크다면
            else {
                //memoRecyclerView 를 보여주고, todoRecyclerView 는 안 보여준다.
                memoRecyclerView.visibility = View.VISIBLE
                todoRecyclerView.visibility = View.GONE
                tabMenuBoolean = "MEMO"
                tabMenuTodoLayout.setBackgroundResource(R.drawable.rectangle_tab_menu_background)
                tabMenuMemoLayout.setBackgroundResource(R.drawable.selected_tab_menu_background)
                stateTextView.text = "MEMO"
                //만일 todoList 의 사이즈가 0이라면
                if(todoList.size == 0)
                {
                    //todoLottieAnimationView 를 애니메이션과 함께 안 보여준다.
                    todoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                    Handler().postDelayed({
                        todoLottieAnimationLayout.visibility = View.GONE
                    }, 500)
                }
            }
        }

        //todoRecyclerView adapter 연결 & RecyclerView 세팅
        todoRecyclerView.apply{
            adapter = TodoRecyclerViewAdapter(todoList as ArrayList<TodoForm>, DoneTodoList as ArrayList<TodoForm>, this@MainActivity)
            Log.d("TAG", "todoRecyclerView adapter ")
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        //memoRecyclerView adapter 연결 & RecyclerView 세팅
        memoRecyclerView.apply {
            adapter = MemoRecyclerViewAdapter(memoList as ArrayList<MemoForm>, todoList as ArrayList<TodoForm>, this@MainActivity, this@MainActivity)
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }


    }

    //todoItem 이 remove 되었을 때 todoLottieAnimation 의 visibility 를 조정하는 콜백 함수
    override fun todoOnItemClick(view: View, position: Int) {
        Log.d("TAG", "todoOnItemClick")
        if(todoList.size == 0)
        {
            Log.d("TAG", "todoList size is 0")
            todoLottieAnimationLayout.visibility = View.VISIBLE
            todoLottieAnimationLayout.startAnimation(startLottieAnimationAlphaAnimation)
        }
    }

    //memoItem 이 remove 되었을 때 memoLottieAnimation 의 visibility 를 조정하는 콜백 함수
    override fun memoOnItemClick(view: View, position: Int) {
        Log.d("TAG", "memoonItemClick")
        if(memoList.size == 0)
        {
            Log.d("TAG", "memoList size is 0")
            memoLottieAnimationLayout.visibility = View.VISIBLE
            memoLottieAnimationLayout.startAnimation(startLottieAnimationAlphaAnimation)
        }
    }

    //memoPlanText 를 쉐어드로 저장했었는데 그 값을 받아와서 조정하는 함수
    private fun loadMemoPlanTextData(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val memoPlanTextShared = pref.getString("memoPlanText", "")

        if(memoPlanTextShared != "")
        {
            memoPlanText = memoPlanTextShared.toString()
        }
    }

    //memoItem 들의 정보를 수정해주는 콜백 함수
    override fun memoItemReplaceClick(view: View, position: Int) {
        //변수 선언
        memoDialog = AlertDialog.Builder(this)
        memoEdialog = LayoutInflater.from(this)
        memoMView = memoEdialog.inflate(R.layout.memo_add_dialog, null)
        memoBuilder = memoDialog.create()

        memoTitleTextDialog = memoMView.findViewById<EditText>(R.id.memoTitleEditTextDialog)
        memoContentTextDialog = memoMView.findViewById<EditText>(R.id.memoContentEditTextDialog)
        memoListLayoutDialog = memoMView.findViewById<ConstraintLayout>(R.id.memoListLayoutDialog)
        memoPlanConstraintLayoutDialog = memoMView.findViewById<ConstraintLayout>(R.id.memoPlanLayoutDialog)
        memoPlanRecyclerViewLayoutDialog = memoMView.findViewById<RecyclerView>(R.id.memoPlanRecyclerViewDialog)
        memoPlanCancelButtonDialog = memoMView.findViewById<ImageView>(R.id.memoPlanCancelImageViewDialog)
        memoPlanTextDialog = memoMView.findViewById<TextView>(R.id.memoListPlanTextViewDialog)
        memoSaveButtonDialog = memoMView.findViewById<Button>(R.id.memoSaveButtonDialog)
        memoCancelButtonDialog = memoMView.findViewById<Button>(R.id.memoCancelButtonDialog)
        var currentTime: Date = Calendar.getInstance().getTime()
        val date_text: String = SimpleDateFormat("yyyy년 MM월 dd일 EE요일", Locale.getDefault()).format(currentTime)
        memoTitleTextDialog.setText("${memoList.get(position).memoTitle}")
        memoContentTextDialog.setText("${memoList.get(position).memoContent}")


        if(memoPlanText != "")
        {
            memoPlanTextDialog.setText("${memoPlanText}")
        }

        memoBuilder.setView(memoMView)
        memoBuilder.show()

        //memoList 저장 버튼
        memoSaveButtonDialog.setOnClickListener {
            Log.d("TAG", "memoButton is pressed")
            memoList.set(position, MemoForm(memoTitleTextDialog.text.toString(), memoContentTextDialog.text.toString(), date_text, "${memoPlanText}"))
            memoRecyclerView.adapter = MemoRecyclerViewAdapter(memoList as ArrayList<MemoForm>, DoneTodoList as ArrayList<TodoForm>, this, this)
            Log.d("TAG", "memoList of size : ${memoList.size}")
            memoBuilder.dismiss()
        }

        //Dialog 닫기 버튼
        memoCancelButtonDialog.setOnClickListener {
            Log.d("TAG", "memoCancelButton is pressed")
            memoBuilder.dismiss()
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
            memoPlanRecyclerViewLayoutDialog.adapter = MemoTodoRecyclerViewAdapter(DoneTodoList as ArrayList<TodoForm>, this, this)
            memoPlanRecyclerViewLayoutDialog.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            memoPlanRecyclerViewLayoutDialog.setHasFixedSize(true)
        }

        //Dialog 안에 있는 RecyclerView
        memoPlanRecyclerViewLayoutDialog.setOnClickListener {
            //RecyclerView 에서 아이템을 클릭했을 때 이벤트를 어떻게 구현할지 생각해야함.
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }
    }

    //memoPlanText 를 조정해주는 콜백 함수
    override fun memoItemViewOnClick(view: View, position: Int) {
        memoListLayoutDialog.visibility = View.VISIBLE
        memoPlanConstraintLayoutDialog.visibility = View.GONE
        loadMemoPlanTextData()
        if(memoPlanText != "")
        {
            memoPlanTextDialog.setText("${memoPlanText}")
        }
        Log.d("TAG", "memoPlanText is ${memoPlanText}")
    }

    //todoDialog 함수
    private fun todoDialogDeclaration() {
        val todoDialog = AlertDialog.Builder(this)
        val todoEdialog: LayoutInflater = LayoutInflater.from(this)
        val todoMView: View = todoEdialog.inflate(R.layout.todo_add_dialog, null)
        val todoBuilder: AlertDialog = todoDialog.create()

        val todoText = todoMView.findViewById<EditText>(R.id.todoEditTextDialog)
        val contentText = todoMView.findViewById<EditText>(R.id.contentEditTextDialog)
        val todoButton = todoMView.findViewById<Button>(R.id.todoButtonDialog)
        val cancelTodoButton = todoMView.findViewById<Button>(R.id.CancelTodoButtonDialog)

        todoBuilder.setView(todoMView)
        todoBuilder.show()

        todoButton.setOnClickListener {
            Log.d("TAG", "todoButton is pressed")
            todoList.add(TodoForm(todoText.text.toString(), contentText.text.toString()))
            Log.d("TAG", "todoList of size : ${todoList.size}")
            todoRecyclerView.adapter = TodoRecyclerViewAdapter(todoList as ArrayList<TodoForm>, DoneTodoList as ArrayList<TodoForm>, this)
            todoBuilder.dismiss()
            //만일 todoList의 아이템을 추가했을 때 todoList 의 사이즈가 1이면 todoLottieAnimationVisibleForm 을 true 로 바꾸어 주어 LottieAnimation 의 Visible 을 조정해주어야 함.
            if (todoList.size == 1) {
                todoLottieAnimationVisibleForm = true
            }
            //만일 todoLottieAnimationVisibleForm 이 true 이면 todoLottieAnimationView를 애니메이션고 함께 자연스럽게 GONE 으로 바꾸어 줌.
            if (todoLottieAnimationVisibleForm == true) {
                todoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                Handler().postDelayed({
                    todoLottieAnimationLayout.visibility = View.GONE
                    todoLottieAnimationVisibleForm = false
                }, 500)
            }
        }
        //닫기 버튼이 클릭되었을 때
        cancelTodoButton.setOnClickListener {
            Log.d("TAG", "todoCancelButton is pressed")
            todoBuilder.dismiss()
        }
    }

    //memoDialog 함수
    private fun memoDialogDeclaration() {
        //필요한 변수 선언
        memoDialog = AlertDialog.Builder(this)
        memoEdialog = LayoutInflater.from(this)
        memoMView = memoEdialog.inflate(R.layout.memo_add_dialog, null)
        memoBuilder = memoDialog.create()

        memoTitleTextDialog = memoMView.findViewById<EditText>(R.id.memoTitleEditTextDialog)
        memoContentTextDialog = memoMView.findViewById<EditText>(R.id.memoContentEditTextDialog)
        memoListLayoutDialog = memoMView.findViewById<ConstraintLayout>(R.id.memoListLayoutDialog)
        memoPlanConstraintLayoutDialog = memoMView.findViewById<ConstraintLayout>(R.id.memoPlanLayoutDialog)
        memoPlanRecyclerViewLayoutDialog = memoMView.findViewById<RecyclerView>(R.id.memoPlanRecyclerViewDialog)
        memoPlanCancelButtonDialog = memoMView.findViewById<ImageView>(R.id.memoPlanCancelImageViewDialog)
        memoPlanTextDialog = memoMView.findViewById<TextView>(R.id.memoListPlanTextViewDialog)
        memoSaveButtonDialog = memoMView.findViewById<Button>(R.id.memoSaveButtonDialog)
        memoCancelButtonDialog = memoMView.findViewById<Button>(R.id.memoCancelButtonDialog)

        memoBuilder.setView(memoMView)
        memoBuilder.show()

        //저장하기 버튼을 눌렀을 때
        memoSaveButtonDialog.setOnClickListener {
            Log.d("TAG", "memoButton is pressed")
            date_text = SimpleDateFormat("yyyy년 MM월 dd일 EE요일", Locale.getDefault()).format(currentTime)
            memoList.add(0, MemoForm(memoTitleTextDialog.text.toString(), memoContentTextDialog.text.toString(), date_text, "${memoPlanText}"))
            Log.d("TAG", "memoList of size : ${memoList.size}")
            memoRecyclerView.adapter = MemoRecyclerViewAdapter(memoList as ArrayList<MemoForm>, DoneTodoList as ArrayList<TodoForm>, this, this)
            memoBuilder.dismiss()
            //만일 memoList 의 사이즈가 1이라면 memoLottieAnimationVisibleForm 을 true 로 바꾸어 주어 memoLottieAnimationView 를 GONE 으로 바꾸어 주어야 함.
            if (memoList.size == 1) {
                memoLottieAnimationVisibleForm = true
            }
            //만일 memoLottieAnimationVisibleForm 이 true 이면 애니메이션과 함께 memoLottieAnimationView 를 GONE 으로 바꾸어 줌.
            if (memoLottieAnimationVisibleForm == true) {
                memoLottieAnimationLayout.startAnimation(lottieAnimationAlphaAnimation)
                Handler().postDelayed({
                    memoLottieAnimationLayout.visibility = View.GONE
                    memoLottieAnimationVisibleForm = false
                }, 500)
            }
        }

        //닫기 버튼이 눌렸을 때
        memoCancelButtonDialog.setOnClickListener {
            Log.d("TAG", "memoCancelButton is pressed")
            memoBuilder.dismiss()
        }

        //RecyclerView 와 같이 나오는 닫기 버튼 (X 버튼)
        memoPlanCancelButtonDialog.setOnClickListener {
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }

        //무슨 계획을 한 후에 쓰는 메모인가요? (선택) 이 눌렸을 때
        memoPlanTextDialog.setOnClickListener {
            memoListLayoutDialog.visibility = View.INVISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.VISIBLE
            memoPlanRecyclerViewLayoutDialog.adapter = MemoTodoRecyclerViewAdapter(DoneTodoList as ArrayList<TodoForm>, this, this)
            memoPlanRecyclerViewLayoutDialog.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            memoPlanRecyclerViewLayoutDialog.setHasFixedSize(true)
        }

        //메모 Dialog 안에 있는 RecylerView 가 눌렸을 때.
        memoPlanRecyclerViewLayoutDialog.setOnClickListener {
            memoListLayoutDialog.visibility = View.VISIBLE
            memoPlanConstraintLayoutDialog.visibility = View.GONE
        }
    }
}
