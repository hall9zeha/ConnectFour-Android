package com.barryzea.connectfour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.barryzea.connectfour.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        populateGameView()
    }
    private fun populateGameView(){
        val params=ViewGroup.LayoutParams(resources.getDimensionPixelSize(R.dimen.width),resources.getDimensionPixelSize(R.dimen.height))
       val rows=6
        val columns=7
        for(i in  0  until rows){
            val linearLayoutRow = LinearLayout(this)
            linearLayoutRow.orientation=LinearLayout.HORIZONTAL
            linearLayoutRow.gravity = Gravity.CENTER
            for(j in 0 until columns){
                val boxIv= ImageView(this)

                boxIv.layoutParams=params
                boxIv.setPadding(4,4,4,4)
                boxIv.setBackgroundResource(R.drawable.circle_box)
                linearLayoutRow.addView(boxIv)
            }
            bind.lnContent.addView(linearLayoutRow)
        }
    }
}