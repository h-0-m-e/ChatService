data class Message(val id: Int, var read: Boolean = false, var text: String)

data class Chat(val recipientId: Int, var messages: MutableList<Message>)

class ChatNotFoundException(message: String) : RuntimeException(message)

class MessageNotFoundException(message: String) : RuntimeException(message)


object ChatService {

    private var chats = mutableMapOf<Int, Chat>()

    private var messageIdCounter = 1

    private fun <E> MutableMap<Int,E>.throwIfMessageNotFound(chatId: Int, desiredToFindId: Int): MutableList<Message> {
        chats[chatId]?.messages?.forEach {
                if (it.id == desiredToFindId) {
                    return chats[chatId]!!.messages
                }
            }
        throw MessageNotFoundException("Message with id $desiredToFindId not found!")
    }

    private fun <Chat> MutableMap<Int, Chat>.throwIfChatNotFound(desiredToFindId: Int): MutableMap<Int, Chat> {
        if (this.containsKey(desiredToFindId)) {
            return this
        }
        throw ChatNotFoundException("Chat with id $desiredToFindId not found!")
    }

    private fun <E> MutableMap<Int, E>.deleteChatIfEmpty(chatId: Int) {
        if (chats[chatId]!!.messages.isEmpty()) {
            chats.remove(chatId)
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
            when (it.messages.isEmpty()) {
              true ->  it.messages.add(emptyMessage)
              false ->  it.messages = mutableListOf(it.messages.last())
            }
        }
        return map
    }

    fun getMessages(recipientId: Int, messagesCount: Int): List<Message> {
        chats.throwIfChatNotFound(recipientId)
        return chats[recipientId]!!.messages.takeLast(messagesCount).onEach { it.read = true }
    }

    fun unreadChatsCount() = chats.values.count { chat -> chat.messages.any { !it.read } }

    fun editMessage(recipientId: Int, editedMessage: Message): Message {
        chats.throwIfMessageNotFound(recipientId,editedMessage.id)
            .apply { removeIf {it.id == editedMessage.id}}
            .apply { add(editedMessage) }
        return editedMessage
    }

    fun deleteMessage(recipientId: Int, messageId: Int): Boolean {
        chats.throwIfMessageNotFound(recipientId,messageId)
            .apply { removeIf { it.id == messageId } }
        chats.deleteChatIfEmpty(recipientId)
        return true
    }

    fun deleteChat(recipientId: Int): Boolean {
        chats.throwIfChatNotFound(recipientId)
            .apply { remove(recipientId) }
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