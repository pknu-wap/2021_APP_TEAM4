package com.example.wap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(
    //MyToDoList List를 받을 변수 dataset과
    //각종 앱 관련 정보를 받을 context 객체를 생성자에 선언
    private val dataset: List<MyToDoList>
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    class ListViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val toDo: TextView = view.findViewById(R.id.toDo)
        val deadline: TextView = view.findViewById(R.id.deadline)
        val checking: SwitchCompat = view.findViewById(R.id.checking)
    }

    //RecyclerView의 새 뷰 홀더를 만들기 위해 레이아웃 관리자가 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_view, parent, false)

        return ListViewHolder(adapterLayout)
    }

    //목록 항목 뷰의 콘텐츠를 바꾸기 위해 레이아웃 관리자가 호출
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val list = dataset[position]
        holder.toDo.text = list.toDo
        holder.deadline.text = list.deadline
    }

    //데이터 세트의 크기를 반환
    override fun getItemCount(): Int {
        return dataset.size
    }

}