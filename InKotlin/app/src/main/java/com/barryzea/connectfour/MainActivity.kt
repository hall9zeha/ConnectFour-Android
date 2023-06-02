package com.barryzea.connectfour

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.barryzea.connectfour.common.Constants
import com.barryzea.connectfour.common.createImageViewPiece
import com.barryzea.connectfour.common.createLinearLayout
import com.barryzea.connectfour.common.showGameOverDialog
import com.barryzea.connectfour.databinding.ActivityMainBinding
import com.barryzea.connectfour.entities.Piece

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var arrayPieces:Array<Array<Piece>>
    private var linearLayoutColumn:LinearLayout?=null
    private var switchTurn:Boolean=true
    private val ROWS=6
    private val COLUMNS=7
    private var currentColor=0
    private var MY_PLAYER=0
    private var OTHER_PLAYER=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        populateGameBoard()
        setUpListeners()

    }
    private fun populateGameBoard(){
        bind.lnContent.removeAllViews()
        linearLayoutColumn?.removeAllViews()
        arrayPieces= Array(COLUMNS){ Array(ROWS){Piece()} }

        for(column in  0  until COLUMNS){
           linearLayoutColumn = createLinearLayout()
            for(row in 0 until ROWS){
                val imageView = createImageViewPiece(column,row,::setPieceOnCLickListener)
                linearLayoutColumn?.addView(imageView)
                addImageViewToArray(column,row,imageView)
            }
            bind.lnContent.addView(linearLayoutColumn)
        }
    }
    private fun setUpListeners(){
        bind.btnReset.setOnClickListener {
            showGameOverDialog(currentColor,4) {
                Toast.makeText(this, "Cerrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showGameOverDialog(colorWinn:Int, victories:Int){
        Handler(Looper.getMainLooper()).postDelayed({
            showGameOverDialog(colorWinn,victories) {
                populateGameBoard()
                currentColor=0
                switchTurn=true
            }
        },500)
    }
    private fun updateGameBoard(column:Int){
        val lnCol= bind.lnContent[column] as LinearLayout
        var count =1L
        val colorRes = if(!switchTurn)R.drawable.green_circle else R.drawable.yellow_circle

        for (i in 0 until ROWS){
            val img = lnCol[i] as ImageView
            img.setPadding(8,8,8,8)
            val color = arrayPieces[column][i].color

            Handler(Looper.getMainLooper()).postDelayed({
                if(arrayPieces[column][i].color == Constants.WITHOUT_COLOR)img.setImageResource(colorRes)
            },count * 90)
            Handler(Looper.getMainLooper()).postDelayed({
                if(arrayPieces[column][i].color == Constants.WITHOUT_COLOR)img.setImageResource(0)
            },count * 110)
            Handler(Looper.getMainLooper()).postDelayed({
                when(color){
                    Constants.MY_PLAYER_COLOR->img.setImageResource(R.drawable.green_circle)
                    Constants.OTHER_PLAYER_COLOR->img.setImageResource(R.drawable.yellow_circle)
                }
            },count * 110)
            count++
        }
    }
    private fun addImageViewToArray(column:Int, row:Int,  iv:ImageView){
        arrayPieces[column][row]= Piece(iv)
    }
    private fun setPieceOnCLickListener(tagCoordinates:String){
        val column= tagCoordinates.substringBeforeLast(",").toInt()
        val row=tagCoordinates.substringAfterLast(",").toInt()
        for(i in 5 downTo 0){
            if(arrayPieces[column][i].color==Constants.WITHOUT_COLOR){
                setColorPiece(column,i)
                checkWinner(column,i)
                break
            }
        }
       updateGameBoard(column)
   }
    private fun setColorPiece(column:Int, row:Int){
        if(switchTurn){ arrayPieces[column][row].color = Constants.MY_PLAYER_COLOR;changeTurn()}
        else { arrayPieces[column][row].color = Constants.OTHER_PLAYER_COLOR; changeTurn()}

    }
    private fun changeTurn(){
        currentColor = if(switchTurn)Constants.MY_PLAYER_COLOR else Constants.OTHER_PLAYER_COLOR
        switchTurn = !switchTurn

    }
    private fun checkWinner(col:Int, row:Int):Boolean{
        var countPieces=0;
            for(j in 1 until 4){
                if(checkIfOutIndexRange(col+j,row) && arrayPieces[col+j][row].color==currentColor) countPieces++//horizontal derecha
                else if(checkIfOutIndexRange(col-j,row) && arrayPieces[(col -j)][row].color==currentColor)countPieces++//horizontal izquierda
                else if(checkIfOutIndexRange(col,row+j) && arrayPieces[col][row+j].color==currentColor)countPieces++//vertical superior
                else if(checkIfOutIndexRange(col,row-j) && arrayPieces[col][row-j].color==currentColor) countPieces++//vertical inferior
                else if(checkIfOutIndexRange(col+j ,row+j) && arrayPieces[col+j][row+j].color==currentColor)countPieces++//diagonal superior derecha
                else if(checkIfOutIndexRange(col-j, row+j) && arrayPieces[col-j][row+j].color==currentColor)countPieces++//diagonal superior izquierda
                else if(checkIfOutIndexRange(col+j, row-j) && arrayPieces[col+j][row+j].color==currentColor)countPieces++//diagonal inferior derecha
                else if(checkIfOutIndexRange(col-j, row-j) && arrayPieces[col-j][row-j].color==currentColor)countPieces++//diagonal inferior izquierda
                if(countPieces == 3) {
                    when (currentColor) {
                        Constants.MY_PLAYER_COLOR -> {MY_PLAYER++;showGameOverDialog(currentColor,MY_PLAYER)}
                        Constants.OTHER_PLAYER_COLOR -> {OTHER_PLAYER++;showGameOverDialog(currentColor,OTHER_PLAYER)}
                    }
                }
            }
        return false
    }
   private fun checkIfOutIndexRange(col:Int,row:Int):Boolean{
       return try {
           arrayPieces[col][row].color==currentColor
       }catch(e:ArrayIndexOutOfBoundsException){
           false
       }
   }
}