package chat.sphinx.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.toAnnotatedString
import chat.sphinx.wrapper.message.isMediaAttachmentAvailable
import chat.sphinx.wrapper.message.retrieveTextToShow
import com.example.compose.badge_red
import chat.sphinx.wrapper.message.*

@Composable
actual fun MessageMenu(
    chatMessage: ChatMessage,
    isVisible: MutableState<Boolean>,
    chatViewModel: ChatViewModel
) {
    val dismissKebab = {
        isVisible.value = false
    }

    val clipboardManager = LocalClipboardManager.current

    CursorDropdownMenu(
        expanded = isVisible.value,
        onDismissRequest = dismissKebab, modifier = Modifier.background(MaterialTheme.colorScheme.onSecondaryContainer).clip(
            RoundedCornerShape(16.dp)
        )
    ) {
        val messageText = chatMessage.message.messageContentDecrypted?.value ?: ""

        if (chatMessage.message.isBoostAllowed) {
            DropdownMenuItem(onClick = {
                chatMessage.boostMessage()
                dismissKebab()
            }) {
                OptionItem("Boost",Res.drawable.ic_boost_green)
            }
        }
        if (chatMessage.message.isCopyAllowed) {
            DropdownMenuItem(onClick = {
                clipboardManager.setText(
                    messageText.toAnnotatedString()
                )
                dismissKebab()
            }) {
                OptionItem("Copy text", imageVector = Icons.Default.ContentCopy)
            }
        }

        if (chatMessage.message.isCopyLinkAllowed) {
            DropdownMenuItem(onClick = {
                clipboardManager.setText(
                    messageText.toAnnotatedString()
                )
                dismissKebab()
            }) {
                OptionItem("Copy Call Link", imageVector = Icons.Default.Link)
            }
        }

        if (chatMessage.message.isReplyAllowed) {
            DropdownMenuItem(onClick = {
                chatMessage.setAsReplyToMessage(chatViewModel.editMessageState)
                dismissKebab()
            }) {
                OptionItem("Reply", imageVector = Icons.Default.Reply)
            }
        }
        if (chatMessage.message.isSaveAllowed) {
            DropdownMenuItem(onClick = {
                // TODO: Save attachment...
                chatViewModel.editMessageState
                dismissKebab()
            }) {
                OptionItem("Save attachment", imageVector = Icons.Default.Save)
            }
        }

        if (chatMessage.message.isDeleteAllowed(
            chatMessage.chat,
            chatMessage.accountOwner().nodePubKey
        )) {
            DropdownMenuItem(onClick = {
                // TODO: Confirm action...
                chatMessage.deleteMessage()
                dismissKebab()
            }) {
                OptionItem("Delete", imageVector = Icons.Default.Delete, color = badge_red)
            }
        }
    }
}
@Composable
fun OptionItem(
    optionText: String,
    iconPath: String? = null,
    imageVector: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.tertiary
){
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        iconPath?.let {
            Image(
                painter = imageResource(it),
                contentDescription = "Sphinx Background",
                modifier = Modifier.size(18.dp),
                contentScale = ContentScale.FillBounds
            )
        }
        imageVector?.let {
            Icon(it, contentDescription = null, modifier = Modifier.size(18.dp), tint = color)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(optionText, color = color, fontSize = 11.sp)
    }
}