package com.example.wap

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wap.databinding.FragmentGameBinding
import com.example.wap.viewModel.GameViewModel
import com.example.wap.viewModel.TamagoViewModel

class GameFragment : Fragment() {
    private lateinit var gameBinding: FragmentGameBinding
    private var drawable: AnimationDrawable? = null // 펫 애니메이션 제어변수
    private var curPosX: Float = 0f // 애니메이션 방향을 위해 x위치 저장변수

    var handler = Handler(Looper.getMainLooper())
    var runnable = Runnable {}

    lateinit var gameViewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        gameBinding = FragmentGameBinding.inflate(inflater, container, false)

        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        gameViewModel.level.observe(viewLifecycleOwner, Observer {
            gameBinding.petLevelTextView.text = "Lv ${it.level}"
            gameBinding.gameProgressbar.progress = it.exp
        })

        return gameBinding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("GameFragment", "GameFragment시작")
    }

    override fun onResume() {
        super.onResume()
        gameViewModel.loadLevel()
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPet(gameBinding.petImageView)
        gameBinding.petImageView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("GameFragment", "touch")
                }
            }
            return@setOnTouchListener true
        }
    }

    //펫 반복 움직임 제어 함수
    private fun initPet(pet: ImageView) {
        handler.removeCallbacks(runnable) // 기존에 실행 중이던 함수 취소
        runnable = object : Runnable {
            override fun run() {
                //1~10초마다 함수 실행
                val delay = (0..10000).random().toLong()
                movePet(pet)
                handler.postDelayed(runnable, delay)
            }
        }
        handler.post(runnable)
    }

    //펫의 위치를 이동하는 함수
    private fun movePet(pet: ImageView) {
        val nextPosX = (-300..300).random().toFloat() //X축으로 랜덤위치로 움직임

        ObjectAnimator.ofFloat(pet, "translationX", nextPosX).apply {
            duration = 2000
            // 리스너를 붙여 위치이동의 시작과 끝에 반응
            addListener(onStart = { changePetAnimation("walk", nextPosX) },
                onEnd = { changePetAnimation("stop") })
            start()
        }
    }

    //펫의 애니메이션을 바꾸는 함수
    private fun changePetAnimation(state: String, nextPosX: Float = 0f) {
        drawable?.stop()
        when (state) {
            "walk" -> {
                // 애니메이션을 변경하고 (현재포지션-다음포지션)으로 이동방향을 받아 이미지 반전
                gameBinding.petImageView.setBackgroundResource(R.drawable.pet_walk_animation)
                gameBinding.petImageView.scaleX = if (curPosX - nextPosX > 0) 1f else -1f
                curPosX = nextPosX // 다음포지션값을 현재포지션으로
            }
            "stop" -> gameBinding.petImageView.setBackgroundResource(R.drawable.pet_idle_animation)
        }
        drawable = gameBinding.petImageView.background as AnimationDrawable
        drawable?.start()
    }
}