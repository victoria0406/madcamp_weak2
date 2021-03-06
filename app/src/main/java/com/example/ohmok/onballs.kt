package com.example.ohmok;

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import org.w3c.dom.Text
import java.util.*
import kotlin.math.round

class onballs: View {
    val for_black = mutableListOf<ball>()
    val for_white = mutableListOf<ball>()
    var distance :Float = 0.toFloat()
    var ball_array = Array<IntArray>(15, {IntArray(15)})

    var turn = true;

    var signal = true;

    var chating =false
    var count = 0;

    lateinit var mSocket: io.socket.client.Socket;

    var my_color = "black"
    var my_room = ""
    //mSocket.connect() // close 되면 새 소켓 열기

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onDraw(canvas: Canvas) {
        //val ma = MainActivity()
        //turn = ma.turn
        Log.v("h2",turn.toString())

        signal = false
        distance = canvas.width.toFloat()/16
        super.onDraw(canvas)
        val ball_black_paint = Paint()
        ball_black_paint.color = Color.BLACK
        val ball_white_paint = Paint()
        ball_white_paint.color = Color.WHITE

        for (b in for_black) {
            canvas.drawCircle(b.get_x()*distance, b.get_y()*distance, 30F, ball_black_paint)
        }
        for (b in for_white) {
            canvas.drawCircle(b.get_x()*distance, b.get_y()*distance, 30F, ball_white_paint)
        }
        var check = check_win_or_fall()
        Log.v("winner",check.toString())
        if(check==1){
            mSocket.emit("game end", my_room, my_color)
        }
        else if(check == 2) { // if draw
            mSocket.emit("game end", my_room, "draw")
        }
        //turn = !turn
        //random_loc()

        var sec = 0
    }

    /*fun random_loc(){
        val random = Random()
        var x = random.nextInt(16)
        var y = random.nextInt(16)
        while(ball_array[x][y]!=0){
            x = random.nextInt(16)
            y = random.nextInt(16)
        }
        var setball = ball((x+1)*distance,(y+1)*distance)
        ball_array[x][y]=-1
        if (!turn) {
            for_white.add(setball)
            turn = true
            invalidate()
        }else{
            //for_black.add(setball)
            //turn = false
        }


    }*/
    //서버 요청 받고 white를 채우는 코드 추가, 이때, while을 통해 터치 이벤트 작동을 막아둔다.
    //소켓 받는건 이 위치에서 실행
    //시간 설정 후 턴 돌리는건 어케 하지?

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //Log.v("click0",for_black.size.toString())
        Log.d("test", chating.toString())
        if (!turn || chating){
            return false

        }else{
            //터치 가능 위치 재설정(이상하게 밀려서 클릭이 안되는 문제 발생
            var xAxis : Int =0
            var yAxis : Int = 0
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    //Log.v("click",for_black.size.toString())
                    if (round(event.x/distance).toInt()<1 || round(event.x/distance).toInt()>15|| round(event.y/distance).toInt()<1 || round(event.y/distance).toInt()>15){
                        return false
                    }
                    if(ball_array[round(event.x/distance).toInt()-1][ round(event.y/distance).toInt()-1]==0){
                        xAxis = round(event.x/distance).toInt()
                        yAxis = round(event.y/distance).toInt()
                        var setball = ball(xAxis,yAxis)
                        //ball_array[xAxis-1][yAxis-1]=1
                        //for_black.add(setball)
                        //var args = arrayListOf<String>(my_color, xAxis.toString(), yAxis.toString())
                        mSocket.emit("set go", my_room, my_color, xAxis.toString(), yAxis.toString())
                        //turn = false
                        //signal = true

                        //소켓 쓰면 소켓에다가 send 후 onDraw에 리스너 열어두기
                        //invalidate()
                        
                        //소켓으로 전송하고 동시에 받아와보자
                    }


                }
            }
        }

        return true
    }


    fun add_ball(one_ball:ball,color_is_black:Boolean){
        count++;
        Log.d("test1", count.toString())
        if(color_is_black){
            for_black.add(one_ball)
            var getter = -1
            if(my_color=="black"){
                getter = 1
            }
            ball_array[one_ball.get_x()-1][one_ball.get_y()-1]=getter
        }else{
            for_white.add(one_ball)
            var getter = -1
            if(my_color=="white"){
                getter = 1
            }
            ball_array[one_ball.get_x()-1][one_ball.get_y()-1]=getter
        }
        Log.v("ball",color_is_black.toString())
        turn = !turn
        invalidate()

    }

    fun check_win_or_fall():Int{
        //세로
        if(count == 225) {
            return 2;
        }
        var winner = 0;
        for(i in 0..14){
            for(j in 0..10){
                if(ball_array[i][j]==1){
                    if(ball_array[i][j+1]==1&&ball_array[i][j+2]==1&&ball_array[i][j+3]==1&&ball_array[i][j+4]==1){
                        winner = 1
                    }
                }
                if(ball_array[i][j]==-1){
                    if(ball_array[i][j+1]==-1&&ball_array[i][j+2]==-1&&ball_array[i][j+3]==-1&&ball_array[i][j+4]==-1){
                        winner = -1
                    }
                }
            }
        }
        //가로
        for(i in 0..10){
            for(j in 0..14){
                if(ball_array[i][j]==1){
                    if(ball_array[i+1][j]==1&&ball_array[i+2][j]==1&&ball_array[i+3][j]==1&&ball_array[i+4][j]==1){
                        winner = 1
                    }
                }
                if(ball_array[i][j]==-1){
                    if(ball_array[i+1][j]==-1&&ball_array[i+2][j]==-1&&ball_array[i+3][j]==-1&&ball_array[i+4][j]==-1){
                        winner = -1
                    }
                }
            }
        }
        //왼쪽 대각
        for(i in 0..10){
            for(j in 0..10){
                if(ball_array[i][j]==1){
                    if(ball_array[i+1][j+1]==1&&ball_array[i+2][j+2]==1&&ball_array[i+3][j+3]==1&&ball_array[i+4][j+4]==1){
                        winner = 1
                    }
                }
                if(ball_array[i][j]==-1){
                    if(ball_array[i+1][j+1]==-1&&ball_array[i+2][j+2]==-1&&ball_array[i+3][j+3]==-1&&ball_array[i+4][j+4]==-1){
                        winner = -1
                    }
                }
            }
        }
        ///오른쪽 대각
        for(i in 5..14){
            for(j in 0..9){
                if(ball_array[i][j]==1){
                    if(ball_array[i-1][j+1]==1&&ball_array[i-2][j+2]==1&&ball_array[i-3][j+3]==1&&ball_array[i-4][j+4]==1){
                        winner = 1
                    }
                }
                if(ball_array[i][j]==-1){
                    if(ball_array[i-1][j+1]==-1&&ball_array[i-2][j+2]==-1&&ball_array[i-3][j+3]==-1&&ball_array[i-4][j+4]==-1){
                        winner = -1
                    }
                }
            }
        }
        return winner
    }
    @JvmName("setTurn1")
    fun setTurn(t:Boolean, room_name:String){
        turn = t
        my_room = room_name
        if (t!=true){
            my_color ="white"
        }
    }
    fun setSocket(soc:io.socket.client.Socket){
        mSocket = soc
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(size, size)
    }


}
