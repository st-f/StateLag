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
import androidx.recyclerview.widget.RecyclerView
import com.example.statelag.CardViewModel.CardState.Content
import com.example.statelag.CardViewModel.CardState.None
import com.example.statelag.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainAdapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.adapter = mainAdapter
        mainAdapter.itemsList = (1..10).map {
            CardViewModel.CardModel(
                text = "[${it.toString().padStart(2, '0')}]",
                duration = 0
            )
        }
        mainAdapter.notifyDataSetChanged()

    }

    class MainAdapter : RecyclerView.Adapter<MainViewHolder>() {

        lateinit var itemsList: List<CardViewModel.CardModel>

        override fun onViewRecycled(holder: MainViewHolder) {
            (holder.itemView as? ComposeView)?.disposeComposition()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.composeview, parent, false)
            return MainViewHolder(view)
        }

        override fun getItemCount(): Int = itemsList.count()

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            holder.bind(itemsList[position])
        }
    }

    class MainViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val videoCardViewModel = CardViewModel()

        init {
            setViewCompositionStrategy(view)
        }

        fun bind(data: CardViewModel.CardModel) {
            (view as ComposeView).setContent {
                videoCardViewModel.init(data)
                val videoCardState by videoCardViewModel.state.collectAsState(None)
                if (videoCardState is Content) {
                    val content = videoCardState as Content
                    println("setContent: ${content.data}")
                    val text = "${content.data.text}  ${content.data.duration}"
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(text)
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