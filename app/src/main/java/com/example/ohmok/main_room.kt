package com.example.ohmok

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ohmok.databinding.ActivityMainRoomBinding
import com.example.ohmok.ui.home.HomeFragment
import com.kakao.sdk.user.UserApiClient
import io.socket.emitter.Emitter

class main_room : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainRoomBinding

    //lateinit var mSocket: Socket// oncreate될 때, 소켓을 만들어서 받아온다.
    var room_name = ""
    //var mSocket = SocketApplication.get()
    var user_name = ""


    var roomInfo = listOf<room_info>(room_info("a1",setOf("b","c")),room_info("a2",setOf("a")),room_info("a3",setOf("d")))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.v("2nd","create")
        val home = HomeFragment()

        //mSocket.connect()

        //home.setSocket(mSocket)

        binding = ActivityMainRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMainRoom.toolbar)
        //mSocket = SocketApplication.get()



        //방 정보 들어오는 과정 -> 새로 고침 버튼으로 업데이트 하던지
        //Log.v("hi1","hihi")
        //mSocket = SocketApplication.get()
        //mSocket.connect() // close 되면 새 소켓 열기
        //Log.v("s0c", mSocket.isActive.toString())
        //Log.v("hi3","hihi")


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main_room)




        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("nav", "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                var bundle = Bundle(1)


                user_name = "${user.kakaoAccount?.profile?.nickname}"


                home.setUsername(user_name)

                bundle.putString("user_name",user_name)
                navView.findViewById<TextView>(R.id.user_name).text =  user_name
                var profile_image = navView.findViewById<ImageView>(R.id.profile_image)

                var imageUrl = "${user.kakaoAccount?.profile?.thumbnailImageUrl}"

                Glide.with(navView).load(imageUrl).into(profile_image);
                Log.i("nav", "사용자 정보 요청 성공" +
                        //"\n회원번호: ${user.id}" +
                        //"\n이메일: ${user.kakaoAccount?.email}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



    }
    var open_room = Emitter.Listener { args ->
        val room_intent = Intent(this, waiting_room::class.java)
        Log.v("open room",args[0].toString())
        room_intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        room_intent.putExtra("room_name",room_name)
        room_intent.putExtra("my_name",user_name)
        var par_num = "";
        par_num = args[0].toString()
        room_intent.putExtra("par_num",par_num)
        //mSocket.close()
        startActivity(room_intent)
     }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_room, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_room)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}


