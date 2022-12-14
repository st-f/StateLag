package com.example.statelag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.example.statelag.CardViewModel.CardState.Content
import com.example.statelag.CardViewModel.CardState.None
import com.example.statelag.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val useLegacy = false
    private val mainAdapter = MainAdapter(useLegacy)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainAdapter.itemsList = (1..10).map {
            CardViewModel.CardModel(0)
        }
        binding.recyclerView.adapter = mainAdapter
        mainAdapter.notifyDataSetChanged()
    }

    class MainAdapter(private val useLegacy: Boolean) : RecyclerView.Adapter<MainViewHolder>() {

        lateinit var itemsList: List<CardViewModel.CardModel>

        override fun onViewRecycled(holder: MainViewHolder) {
            (holder.itemView as? ComposeView)?.disposeComposition()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val layout = if (useLegacy) R.layout.android_view else R.layout.compose_view
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return MainViewHolder(view, useLegacy)
        }

        override fun getItemCount(): Int = itemsList.count()

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            holder.bind(itemsList[position])
        }
    }

    class MainViewHolder(private val view: View, private val useLegacy: Boolean) : RecyclerView.ViewHolder(view) {

        private val videoCardViewModel = CardViewModel()

        init {
            if (!useLegacy) {
                setViewCompositionStrategy(view)
            }
        }

        fun bind(data: CardViewModel.CardModel) {
            if (useLegacy) bindLegacy(data) else bindCompose(data)
        }

        private fun bindLegacy(data: CardViewModel.CardModel) {
            GlobalScope.launch {
                videoCardViewModel.init(data)
                videoCardViewModel.state.collectLatest { videoCardState ->
                    if (videoCardState is Content) {
                        val textView = view.findViewById<TextView>(R.id.textView)
                        textView.text = videoCardState.data.number.toString()
                    }
                }
            }
        }

        private fun bindCompose(data: CardViewModel.CardModel) {
            (view as ComposeView).setContent {
                videoCardViewModel.init(data)
                val videoCardState by videoCardViewModel.state.collectAsState(None)
                if (videoCardState is Content) {
                    val content = videoCardState as Content
                    val text = "${content.data.number}"
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(text = text, style = TextStyle(fontSize = 24.sp))
                    }
                }
            }
        }

        private fun setViewCompositionStrategy(view: View) {
            (view as? ComposeView)?.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
    }

}