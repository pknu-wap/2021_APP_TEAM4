package com.example.wap.ui.todo_list

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wap.adapter.ListAdapter
import com.example.wap.MainActivity
import com.example.wap.data.MyToDoList
import com.example.wap.databinding.FragmentListBinding
import com.example.wap.notification.AlarmReceiver
import com.example.wap.notification.Constants.Companion.NOTIFICATION_ID
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class ListFragment : Fragment(), ListAdapter.onCheckedChangeListener {
    // 뷰 바인딩 구현을 위해 null을 허용하는 FragmentListBinding 참조를 가져와야 한다.
    private val binding by lazy{ FragmentListBinding.inflate(layoutInflater)}
    // get()은 이 속성이 'get-only'임을 나타낸다. 즉 값을 가져올 수 있지만 할당되고 나면 다른 것에 할당할 수 없다.
    private lateinit var recyclerView: RecyclerView

     lateinit var tamagoViewModel: TamagoViewModel

     lateinit var todoListViewModel: TodoListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tamagoViewModel = ViewModelProvider(this)[TamagoViewModel::class.java]

        todoListViewModel = ViewModelProvider(this)[TodoListViewModel::class.java]

        todoListViewModel.todoList.observe(this){ value ->
            connectUi(value)
        }

        todoListViewModel.loadTodo()

        // Inflate the layout for this fragment
        // 할 일 목록 추가
        binding.addButton.setOnClickListener{
            // 문자열이 입력되었을 경우 추가
            if(binding.addTodo.text.isNotEmpty() && binding.addDeadline.text.isNotEmpty()){
                val deadLine = binding.addDeadline.text.toString()
                val todoText = binding.addTodo.text.toString()
                val todoList = MyToDoList(todoText, deadLine)
                saveTodo(todoList)
            }
        }
        return binding.root
    }

    val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN
            or ItemTouchHelper.START or ItemTouchHelper.END, ItemTouchHelper.RIGHT){
        // 리스트 아이템 위치 이동하기
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean { return false }

        // 리스트 아이템 오른쪽으로 밀어 삭제하기
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // 리스트 position
            val position = viewHolder.adapterPosition
            //DB에서 삭제
            todoListViewModel.deleteTodo(position)
            // adapter에게 item이 removed 되었음을 알려줌
            recyclerView.adapter?.notifyItemRemoved(position)
        }
    }
    private fun saveTodo(todo: MyToDoList) {
        todoListViewModel.saveTodo(todo)
        Toast.makeText(requireContext(), "할 일이 추가되었습니다!", Toast.LENGTH_LONG).show()
        binding.addDeadline.text = null
        binding.addTodo.text = null
    }
    //checkBox check
    override fun onCheck(position: Int, isChecked: Boolean, todo: MyToDoList) {
        tamagoViewModel.updateLevel()
    }

    private fun connectUi(value: List<MyToDoList>) {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ListAdapter(this@ListFragment,value)
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
    }
}