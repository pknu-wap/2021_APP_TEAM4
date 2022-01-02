package com.example.wap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wap.databinding.ActivityMainBinding
import com.example.wap.ui.game.GameFragment
import com.example.wap.ui.todo_list.ListFragment

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val list= listOf(ListFragment(), GameFragment())
        val pagerAdapter=FragmentPagerAdapter(list, this)

        with(binding) {
            viewPager.adapter = pagerAdapter
            val titles = listOf("to-do", "game")
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = titles.get(position)
            }.attach()
        }
    }
}

class FragmentPagerAdapter(val fragmentList:List<Fragment>,fragmentActivity: FragmentActivity)
                            : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList.get(position)
}