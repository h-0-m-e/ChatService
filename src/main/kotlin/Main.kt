data class Message(val id: Int, var read: Boolean = false, var text: String)

data class Chat(val recipientId: Int, var messages: MutableList<Message>)

class ChatNotFoundException(message: String) : RuntimeException(message)

class MessageNotFoundException(message: String) : RuntimeException(message)


object ChatService {

    private var chats = mutableMapOf<Int, Chat>()

    private var messageIdCounter = 1

    private fun <E> MutableMap<Int, E>.throwIfMessageNotFound(desiredToFindId: Int) {
        chats.forEach { it ->
            it.value.messages.forEach {
                if (it.id == desiredToFindId) {
                    return
                }
            }
        }
        throw MessageNotFoundException("Message with id $desiredToFindId not found!")
    }

    private fun <E> MutableMap<Int, E>.throwIfChatNotFound(desiredToFindId: Int) {
        this.forEach {
            if (it.key == desiredToFindId) {
                return
            }
        }
        throw ChatNotFoundException("Chat with id $desiredToFindId not found!")
    }

    private fun <E> MutableMap<Int, E>.deleteChatIfEmpty(chatId: Int) {
        if (chats[chatId]!!.messages.isEmpty()) {
            deleteChat(chatId)
        }
    }

    fun addMessage(recipientId: Int, message: Message): Message {
        chats.getOrPut(recipientId) { Chat(recipientId, mutableListOf()) }
            .messages.add(message.copy(id = messageIdCounter++))
        return message
    }

    fun createChat(recipientId: Int) = chats.putIfAbsent(recipientId, Chat(recipientId, mutableListOf()))

    fun printChats() {
        println(chats)
    }

    fun getChats(): Map<Int, Chat> {
        val emptyMessage = Message(0, true, "No messages yet")
        val map = chats
        map.values.forEach {
            if (it.messages.isEmpty()) {
                it.messages.add(emptyMessage)
            } else {
                it.messages = mutableListOf(it.messages.last())
            }
        }
        return map
    }

    fun getMessages(recipientId: Int, messagesCount: Int): List<Message> {
        chats.throwIfChatNotFound(recipientId)
        val chat = chats[recipientId]
        return chat!!.messages.takeLast(messagesCount).onEach { it.read = true }
    }

    fun unreadChatsCount() = chats.values.count { chat -> chat.messages.any { !it.read } }

    fun editMessage(recipientId: Int, editedMessage: Message): Message {
        chats.throwIfMessageNotFound(editedMessage.id)
        chats[recipientId]?.messages?.removeIf { it.id == editedMessage.id }
        chats[recipientId]?.messages?.add(editedMessage)
        return editedMessage
    }

    fun deleteMessage(recipientId: Int, messageId: Int): Boolean {
        chats.throwIfMessageNotFound(messageId)
        chats[recipientId]?.messages?.removeIf { it.id == messageId }
        chats.deleteChatIfEmpty(recipientId)
        return true
    }

    fun deleteChat(recipientId: Int): Boolean {
        chats.throwIfChatNotFound(recipientId)
        chats.remove(recipientId)
        return true
    }

    fun clear() {
        chats.clear()
        messageIdCounter = 1
    }
}

fun main() {
    ChatService.editMessage(1, Message(1, false, "Hi man"))
}