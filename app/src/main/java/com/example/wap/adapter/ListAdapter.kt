package com.example.wap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.wap.R
import com.example.wap.data.MyToDoList
import com.example.wap.databinding.ListViewBinding

class ListAdapter(
    private val listener: onCheckedChangeListener,
    //MyToDoList List를 받을 변수 dataset과
    //각종 앱 관련 정보를 받을 context 객체를 생성자에 선언
    private val dataset: List<MyToDoList>,
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    //RecyclerView의 새 뷰 홀더를 만들기 위해 레이아웃 관리자가 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
       val binding =
           ListViewBinding.inflate(LayoutInflater.from(parent.context),parent, false)

        return ListViewHolder(binding)
    }

    //목록 항목 뷰의 콘텐츠를 바꾸기 위해 레이아웃 관리자가 호출
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = dataset[position]
        holder.setTodo(list)
    }

    //데이터 세트의 크기를 반환
    override fun getItemCount(): Int {
        return dataset.size
    }
    inner class ListViewHolder(val binding: ListViewBinding) : RecyclerView.ViewHolder(binding.root),
    CompoundButton.OnCheckedChangeListener{
        lateinit var currentTodo: MyToDoList
        fun setTodo(todo: MyToDoList){
            binding.toDo.text = todo.toDo
            binding.deadline.text = todo.deadline
            this.currentTodo = todo
        }
        //val checking: SwitchCompat = binding.checking
        init{
            binding.checking.setOnCheckedChangeListener(this)
        }

        override fun onCheckedChanged(view: CompoundButton?, isChecked: Boolean) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                when(view?.id){
                    R.id.checking -> listener.onCheck(position, isChecked, currentTodo)
                }
            }
        }
    }
    interface onCheckedChangeListener{
        fun onCheck(position: Int, isChecked: Boolean, todo: MyToDoList)
    }
}