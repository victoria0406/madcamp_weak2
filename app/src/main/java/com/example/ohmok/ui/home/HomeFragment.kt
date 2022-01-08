package com.example.ohmok.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ohmok.*
import com.example.ohmok.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText
import io.socket.emitter.Emitter
import java.net.Socket
import com.example.ohmok.MyGameRoomRecyclerViewAdapter


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    lateinit var rooms: List<String>


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var user_name = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("create","hihi")
        var main_room = main_room()


        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        root.findViewById<Button>(R.id.new_room).setOnClickListener{ view ->
            //소켓에 새로운 방 집어넣기
            var room_name = root.findViewById<TextInputEditText>(R.id.room_name).text.toString()
            val room_intent = Intent(view.context, waiting_room::class.java)
            room_intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            room_intent.putExtra("room_name",room_name)

            room_intent.putExtra("user_name",user_name)
            var par_num = "";
            //par_num = args[0].toString()
            //room_intent.putExtra("par_num",par_num)
            //mSocket.close()
            startActivity(room_intent)
            //소켓 크리에이트 전송
            //mSocket.emit("join",room_name)
        }




        // Set the adapter




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun setUsername(name:String){
        user_name = name
    }
    fun setRoomlist(setted_rooms:List<String>){
        rooms = setted_rooms
    }


}