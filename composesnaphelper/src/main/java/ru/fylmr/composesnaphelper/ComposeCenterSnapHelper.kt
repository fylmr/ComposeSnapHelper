package ru.fylmr.composesnaphelper

import androidx.annotation.Px
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch

/**
 * Creates a [IsSwipingState] that is remembered across compositions.
 */
@Composable
internal fun rememberIsSwipingState(): IsSwipingState {
    return remember {
        IsSwipingState()
    }
}

/**
 * This class maintains the state of the pager snap.
 */
internal class IsSwipingState {

    val isSwiping = mutableStateOf(false)
}

/**
 * [IsSwipingNestedScrollConnection] reacts to the scroll left to right and vice-versa.
 */
internal class IsSwipingNestedScrollConnection(
    private val isSwipingState: IsSwipingState,
    private val onSwiping: () -> Unit
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = when (source) {
        NestedScrollSource.Drag -> onScroll()
        else -> Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = when (source) {
        NestedScrollSource.Drag -> onScroll()
        else -> Offset.Zero
    }

    private fun onScroll(): Offset {
        isSwipingState.isSwiping.value = true
        return Offset.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity = when {
        isSwipingState.isSwiping.value -> {
            onSwiping()
            Velocity.Zero
        }
        else -> Velocity.Zero
    }.also {
        isSwipingState.isSwiping.value = false
    }
}

internal fun LazyListState.getCentralItem(): LazyListItemInfo? {
    val visibleItems = layoutInfo.visibleItemsInfo
    return visibleItems.getOrNull(visibleItems.size / 2)
}

/**
 * [ComposeCenterSnapHelper] decides when to snap to the target view.
 *
 * @param parentViewWidth main dimension size of the container (e.g. width for LazyRow)
 * @param content a block which describes the content. Inside this block, you will have
 * access to [LazyListState].
 */
@Composable
fun ComposeCenterSnapHelper(
    @Px parentViewWidth: Int,
    content: @Composable (LazyListState) -> Unit
) {
    val isSwipingState = rememberIsSwipingState()
    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    val connection = remember(isSwipingState, listState) {
        IsSwipingNestedScrollConnection(isSwipingState) {

            val centralItem = listState.getCentralItem() ?: return@IsSwipingNestedScrollConnection

            scope.launch {
                // We should scroll for the difference between
                // current item start position (centralItem.offset)
                // and it desired start position (difference between half of parent and half of item sizes)

                val halfParent = parentViewWidth / 2
                val desiredStartPosition = halfParent - centralItem.size / 2f
                val actualStartPosition = centralItem.offset
                val offset = actualStartPosition - desiredStartPosition
                listState.animateScrollBy(offset)
            }
        }
    }

    Box(
        modifier = Modifier
            .nestedScroll(connection)
    ) {
        content(listState)
    }
}
