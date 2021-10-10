package ru.fylmr.composesnaphelper

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCenterSnapHelper(parentSize = getScreenWidth()) { state ->
                LazyRow(state = state, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(count = 15) { i ->
                        Text("Item #$i")
                    }
                }
            }
        }
    }

    private fun getScreenWidth(): Int {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay?.getMetrics(dm)
        return dm.widthPixels
    }
}