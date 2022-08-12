package com.example.statelag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.RecyclerView
import com.example.statelag.CardViewModel.CardState.*
import com.example.statelag.databinding.ActivityMainBinding
import android.widget.TextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainAdapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainAdapter.itemsList = (1..10).map {
            CardViewModel.CardModel((Math.random() * 200).toLong())
        }
        binding.recyclerView.adapter = mainAdapter
        mainAdapter.notifyDataSetChanged()
    }

    class MainAdapter : RecyclerView.Adapter<MainViewHolder>() {

        lateinit var itemsList: List<CardViewModel.CardModel>

        override fun onViewRecycled(holder: MainViewHolder) {
            (holder.itemView as? ComposeView)?.disposeComposition()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            println("onCreateViewHolder: $parent")
            val view = LayoutInflater.from(parent.context).inflate(R.layout.compose_view, parent, false)
            //val view = LayoutInflater.from(parent.context).inflate(R.layout.android_view, parent, false)
            return MainViewHolder(view)
        }

        override fun getItemCount(): Int = itemsList.count()

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            println("onBindViewHolder: $position")
            holder.bind(itemsList[position])
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }

    class MainViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val videoCardViewModel = CardViewModel()

        init {
            setViewCompositionStrategy(view)
        }

        fun bind(data: CardViewModel.CardModel) {

            /*GlobalScope.launch {
                videoCardViewModel.init(data)
                videoCardViewModel.state.collectLatest { videoCardState ->
                    if(videoCardState is Content) {
                        val textView = view.findViewById<TextView>(R.id.textView)
                        textView.text = videoCardState.data.number.toString()
                    }
                }
            }*/

            (view as ComposeView).setContent {
                videoCardViewModel.init(data)
                val videoCardState by videoCardViewModel.state.collectAsState(None)
                if (videoCardState is Content) {
                    val content = videoCardState as Content
                    println("setContent: ${content.data.number}")
                    val text = "${content.data.number}"
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(text = text, style = TextStyle(fontSize = 24.sp))
                    }
                }
            }
        }

        private fun setViewCompositionStrategy(view: View) {
            (view as? ComposeView)?.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
        }
    }

}