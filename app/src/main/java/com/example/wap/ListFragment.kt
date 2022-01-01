package com.example.wap

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wap.Notification.AlarmReceiver
import com.example.wap.Notification.Constants.Companion.NOTIFICATION_ID
import com.example.wap.databinding.FragmentListBinding
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
    // Datasource 인스턴스를 만들고 이 인스턴스에서 loadMyToDoList() 메서드를 호출
    // 반환된 확인 목록을 myDataset라는 val에 저장
    private val myDataset: MutableList<MyToDoList> = mutableListOf()

    private val todoCollectionRef = Firebase.firestore.collection("todo")

    private val gameCollectionRef = Firebase.firestore.collection("game")

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadTodo()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
    }

    val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, ItemTouchHelper.RIGHT){
        // 리스트 아이템 위치 이동하기
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // drag and drop feature
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Collections.swap(myDataset, fromPosition, toPosition)

            // adaptor에게 item이 이동함을 알려줌
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

            return false
        }

        // 리스트 아이템 오른쪽으로 밀어 삭제하기
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // 리스트 position
            val position = viewHolder.adapterPosition
            //DB에서 삭제
            deleteTodo(myDataset[position])
            // 리스트 지우기
            myDataset.remove(myDataset[position])

            // adaptor에게 item이 removed 되었음을 알려줌
            recyclerView.adapter?.notifyItemRemoved(position)
        }
    }
    private fun saveTodo(todo: MyToDoList) = CoroutineScope(Dispatchers.IO).launch {
        Log.d("tag","?")
        try{
            todoCollectionRef.add(todo).await()
            withContext(Dispatchers.Main) {
                myDataset.add(todo)
                Toast.makeText(requireContext(), "할 일이 추가되었습니다!", Toast.LENGTH_LONG).show()
                recyclerView.adapter?.notifyDataSetChanged()
                binding.addDeadline.text = null
                binding.addTodo.text = null
            }
        } catch(e: Exception){
            Log.d("tag",e.message.toString())
        }
    }
    private fun loadTodo() = CoroutineScope(Dispatchers.IO).launch {
        try{
            val querySnapshot = todoCollectionRef.get().await()
            for(document in querySnapshot.documents){
                val todo = document.toObject<MyToDoList>()
                todo?.let { myDataset.add(it) }
            }
            withContext(Dispatchers.Main){
                recyclerView = binding.recyclerView
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = ListAdapter(this@ListFragment,myDataset)
                val itemTouchHelper = ItemTouchHelper(simpleCallback)
                itemTouchHelper.attachToRecyclerView(recyclerView)
                recyclerView.setHasFixedSize(true)
            }
        } catch(e: Exception){
            Log.d("Tag",e.message.toString())
        }
    }
    //checkBox check
    override fun onCheck(position: Int, isChecked: Boolean, todo: MyToDoList) {

        val alarmManager = mainActivity.getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(mainActivity, AlarmReceiver::class.java)
        intent.let{
            it.putExtra("todo",todo.toDo)
            it.putExtra("deadline",todo.deadline)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            mainActivity, NOTIFICATION_ID , intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val toastMessage = if(isChecked){

            updateLevel()

            val time = 10000
            val triggerTime = (SystemClock.elapsedRealtime() + time) //두가지 시간중 지금은 경과시간
            alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, //절전모드에서도 알람 울림
                triggerTime,
                pendingIntent
            )
            "${time/1000}후 알람 울려요 "

        } else{
            alarmManager.cancel(pendingIntent)
            Toast.makeText(mainActivity, "알람이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(mainActivity, toastMessage.toString(), Toast.LENGTH_SHORT).show()
    }
    //todo 삭제
    private fun deleteTodo(todo: MyToDoList) = CoroutineScope(Dispatchers.IO).launch {
        val todoQuery = todoCollectionRef
            .whereEqualTo("deadline", todo.deadline)
            .whereEqualTo("toDo", todo.toDo)
            .get()
            .await()
        if(todoQuery.documents.isNotEmpty()){
            for(document in todoQuery){
                try{
                    todoCollectionRef.document(document.id).delete().await()
            }catch(e: Exception){
                Log.d("Tag","delete error")
                }
            }
        }
    }
    private fun updateLevel() = CoroutineScope(Dispatchers.IO).launch{

        try{
            val querySnapshot = gameCollectionRef.get().await()
            for(document in querySnapshot.documents){
                val game = document.toObject<GameData>()
                game?.let {
                    var nextProgress = it.exp + 50
                    var nextLevel = it.level
                    if(nextProgress >= 100){
                        nextProgress -= 100
                        nextLevel += 1
                    }
                    val gameQuery = gameCollectionRef
                        .whereEqualTo("level", game.level)
                        .whereEqualTo("progress", game.exp)
                        .get()
                        .await()
                    if(gameQuery.documents.isNotEmpty()){
                        for(document in gameQuery){
                            try{
                                gameCollectionRef.document(document.id).update("level", nextLevel).await()
                                gameCollectionRef.document(document.id).update("progress", nextProgress).await()
                            }
                            catch(e: Exception){
                                e.printStackTrace()
                                Log.d("tag","update error")
                            }
                        }
                    }
                }
            }
        }catch(e: Exception){
            e.printStackTrace()
            Log.d("tag", "load data error")
        }
    }
}