package com.barryzea.connectfour.common

import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.barryzea.connectfour.R
import com.barryzea.connectfour.databinding.WinnerDialogLayoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * Project ConnectFour
 * Created by Barry Zea H. on 30/5/23.
 * Copyright (c)  All rights reserved.
 **/

fun Activity.showToast(msg:String, duration:Int=Toast.LENGTH_SHORT){
 Toast.makeText(this, msg, duration).show()
}
fun Activity.showToast(msgRes:Int, duration:Int=Toast.LENGTH_SHORT){
  Toast.makeText(this,this.getString(msgRes) , duration).show()
}

fun Activity.createImageViewPiece(x:Int, y:Int, onClickListener:(String)->Unit): ImageView {
    val params= ViewGroup.LayoutParams(
        resources.getDimensionPixelSize(R.dimen.width),
        resources.getDimensionPixelSize(R.dimen.height))

    val boxImg= ImageView(this)
    boxImg.layoutParams=params
    boxImg.tag="$x,$y"
    boxImg.setPadding(4,4,4,4)
    boxImg.setBackgroundResource(R.drawable.circle_box)
    boxImg.setOnClickListener {onClickListener(boxImg.tag.toString())}

    return boxImg
}
fun Activity.createLinearLayout():LinearLayout{
    val  linearLayoutRow = LinearLayout(this)
    linearLayoutRow.id="1".toInt()
    linearLayoutRow.orientation=LinearLayout.VERTICAL
    linearLayoutRow.gravity = Gravity.CENTER
    return linearLayoutRow
}

fun Activity.showGameOverDialog(colorWinner:Int,victories:Int, onClickDialog:()->Unit){
    val bind = WinnerDialogLayoutBinding.inflate(layoutInflater)
    var pieceDrawable=0
    when(colorWinner){
        Constants.MY_PLAYER_COLOR->{pieceDrawable=R.drawable.green_circle}
        Constants.OTHER_PLAYER_COLOR->{pieceDrawable=R.drawable.yellow_circle}
    }
    val materialDialog = MaterialAlertDialogBuilder(this)
        .setView(bind.root)
        .setPositiveButton(R.string.accept){d,_->
            onClickDialog()
            d.dismiss()
        }
        .setCancelable(false)
        .show()
    bind.ivColorWinner.setImageResource(pieceDrawable)
    bind.tvVictories.text=victories.toString()
}