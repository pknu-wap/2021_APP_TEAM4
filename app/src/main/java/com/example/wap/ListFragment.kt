package com.example.wap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wap.databinding.FragmentListBinding
import java.util.*

class ListFragment : Fragment() {
    // 뷰 바인딩 구현을 위해 null을 허용하는 FragmentListBinding 참조를 가져와야 한다.
    private var _binding: FragmentListBinding? = null
    // get()은 이 속성이 'get-only'임을 나타낸다. 즉 값을 가져올 수 있지만 할당되고 나면 다른 것에 할당할 수 없다.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    // Datasource 인스턴스를 만들고 이 인스턴스에서 loadMyToDoList() 메서드를 호출
    // 반환된 확인 목록을 myDataset라는 val에 저장
    private lateinit var myDataset: MutableList<MyToDoList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        myDataset = Datasource().loadMyToDoList()
        recyclerView = binding.recyclerView
        // 세로 레이아웃
        recyclerView.layoutManager = LinearLayoutManager(context)
        // requireContext()와 myDataset를 매개변수로 새 ListAdapter 인스턴스를 만들어
        // ListAdapter 객체를 recyclerView의 adapter 속성에 할당

        recyclerView.adapter = ListAdapter(myDataset)

        // 할 일 목록 추가
        binding.addButton.setOnClickListener{
            // 문자열이 입력되었을 경우 추가
            if(binding.addTodo.text.length != 0 && binding.addDeadline.text.length != 0){
                myDataset.add(MyToDoList(binding.addTodo.text.toString(), binding.addDeadline.text.toString()))
                recyclerView.adapter?.notifyDataSetChanged()
                binding.addTodo.text = null
                binding.addDeadline.text = null
                Toast.makeText(requireContext(), "할 일이 추가되었습니다!", Toast.LENGTH_LONG).show()
            }
        }

        // 리사이클러뷰에 drag and drop과 swipe 기능 추가
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        // attach recycler view to item touch helper
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.setHasFixedSize(true)
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
            // 리스트 지우기
            myDataset.remove(myDataset[position])
            // adaptor에게 item이 removed 되었음을 알려줌
            recyclerView.adapter?.notifyItemRemoved(position)
        }
    }
}