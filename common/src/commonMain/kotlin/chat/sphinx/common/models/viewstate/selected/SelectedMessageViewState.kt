package chat.sphinx.common.models.viewstate.selected

import chat.sphinx.common.models.viewstate.messageholder.MessageHolderViewState
import chat.sphinx.concepts.views.viewstate.ViewState
import chat.sphinx.wrapper.view.*

internal sealed class SelectedMessageViewState: ViewState<SelectedMessageViewState>() {

    object None: SelectedMessageViewState()

    @Suppress("DataClassPrivateConstructor")
    data class SelectedMessage private constructor(
        val messageHolderViewState: MessageHolderViewState,
        val holderYPos: Px,
        val bubbleCenterXPos: Px,
        val bubbleHeight: Px,
        val recyclerViewWidth: Px,
        val screenHeight: Px,
        val showMenuTop: Boolean,
    ): SelectedMessageViewState() {

        companion object {

            fun instantiate(
                messageHolderViewState: MessageHolderViewState?,

                /* This is the y top position of the holder _within_ the recycler view */
                holderYPosTop: Px,
                holderHeight: Px,
                holderWidth: Px,

                bubbleXPosStart: Px,
                bubbleHeight: Px,
                bubbleWidth: Px,

                headerHeight: Px,

                recyclerViewWidth: Px,

                screenHeight: Px,
            ): SelectedMessage? {
                if (messageHolderViewState == null) {
                    return null
                }

                if (messageHolderViewState.selectionMenuItems.isNullOrEmpty()) {
                    return null
                }

                val spaceTop = holderYPosTop.add(headerHeight)
                val spaceBottom = screenHeight.subtract(spaceTop.add(holderHeight))
                val margin = recyclerViewWidth.subtract(holderWidth).divideBy(2F)

                return SelectedMessage(
                    messageHolderViewState,
                    spaceTop,
                    bubbleWidth.divideBy(2F).add(bubbleXPosStart).add(margin),
                    bubbleHeight,
                    recyclerViewWidth,
                    screenHeight,
                    spaceTop.isGreaterThanOrEqualTo(spaceBottom)
                )
            }

        }
    }
}
