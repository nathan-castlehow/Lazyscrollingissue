package com.example.lazyscrollingissue

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazyscrollingissue.ui.theme.LazyscrollingissueTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

const val COLUMN_ITEM_COUNT = 25
const val ROW_ITEM_COUNT = 25

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyscrollingissueTheme { ScrollingLayoutWithAnimateToScroll() }
        }
    }

    @Composable
    private fun ScrollingLayoutWithAnimateToScroll() {
        val lazyColumnListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LazyColumn(state = lazyColumnListState) {
            items(COLUMN_ITEM_COUNT) { columnIndex ->
                var hasFocus by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .onFocusChanged {
                            // first time we land inside the focus container
                            if (it.hasFocus && !hasFocus) {
                                coroutineScope.launch {

                                    // remove me to see scroll issue
                                    delay(50)
                                    scrollEffect(lazyColumnListState, columnIndex)
                                }
                            }
                            hasFocus = it.hasFocus
                            Log.i("ScrollingLayoutWithAnimateToScroll", "hasFocus:${it.hasFocus}")
                        }
                ) {
                    Text(
                        "I am section $columnIndex"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(ROW_ITEM_COUNT) { rowIndex ->
                            val intSource = remember { MutableInteractionSource() }
                            val isFocused by intSource.collectIsFocusedAsState()

                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 2.dp,
                                        color = if (isFocused) Color.Red else Color.Black
                                    )
                                    .size(100.dp)
                                    .focusable(interactionSource = intSource),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Location $columnIndex:$rowIndex"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun BaseScrollingLayout() {
        val lazyColumnListState = rememberLazyListState()

        LazyColumn(state = lazyColumnListState) {
            items(COLUMN_ITEM_COUNT) { columnIndex ->
                Column(
                    modifier = Modifier
                ) {
                    Text(
                        "I am section $columnIndex"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(ROW_ITEM_COUNT) { rowIndex ->
                            val intSource = remember { MutableInteractionSource() }
                            val isFocused by intSource.collectIsFocusedAsState()

                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 2.dp,
                                        color = if (isFocused) Color.Red else Color.Black
                                    )
                                    .size(100.dp)
                                    .focusable(interactionSource = intSource),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Location $columnIndex:$rowIndex"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


private suspend fun scrollEffect(state: LazyListState, rowIndex: Int) {
    Log.i("HomeScreen", "start scrollEffect $rowIndex")

    try {
        val index = if (rowIndex > 0) rowIndex - 1 else rowIndex
        val offset = if (rowIndex > 0) 0 else 0
        state.animateScrollToItem(
            index, offset
        )
        Log.i("HomeScreen", "end scrollEffect $rowIndex")
    } catch (ex: CancellationException) {
        Log.e("HomeScreen", "cancelled scrollEffect $rowIndex", ex)
    }
}