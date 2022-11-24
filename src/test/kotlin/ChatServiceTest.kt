import org.junit.Test
import org.junit.Before
import kotlin.test.assertEquals

class ChatServiceTest {

    @Before
    fun clearBeforeTest(){
        ChatService.clear()
    }

    @Test
    fun addMessage_NoChat() {
        val message = Message(0,false,"First msg")

        val result = ChatService.addMessage(1,message)

        assertEquals(message,result)
    }

    @Test
    fun addMessage_ChatExist() {
        val service = ChatService
        val message = Message(0,false,"First msg")

        service.createChat(1)

        val result = service.addMessage(1,message)

        assertEquals(message,result)
    }

    @Test
    fun getMessages() {
        val service = ChatService
        val message = Message(0,false,"First msg")
        val list = listOf(Message(1,true,"First msg"))

        service.addMessage(1,message)
        val result = service.getMessages(1,1)

        assertEquals(list, result)
    }

    @Test(expected = ChatNotFoundException::class)
    fun getMessages_ShouldThrow() {
        val service = ChatService

        service.getMessages(1,1)
    }

    @Test
    fun getChats() {
        val service = ChatService
        val chat1 = Chat(1, mutableListOf())
        val chat2 = Chat(2, mutableListOf())
        val message = Message(1, false, "Hi")
        val emptyMessage = Message(0, true, "No messages yet")
        val map = mapOf(Pair(1,chat1),Pair(2,chat2))

        chat1.messages.add(message)
        chat2.messages.add(emptyMessage)

        service.createChat(1)
        service.createChat(2)
        service.addMessage(1,message)

        val result = service.getChats()

        assertEquals(map,result)
    }

    @Test
    fun editMessage() {
        val service = ChatService
        val message = Message(1,false,"Hi")
        val message2 = Message(2,false,"Hello")
        val editedMessage = Message(1,true,"Hi man")

        service.addMessage(1,message)
        service.addMessage(1,message2)
        service.getMessages(1,1)


        val result = service.editMessage(1,editedMessage)

        assertEquals(editedMessage,result)
    }

    @Test(expected = MessageNotFoundException::class)
    fun editMessage_ShouldThrow() {
        val service = ChatService
        val editedMessage = Message(1,true,"Hi man")

        service.editMessage(1,editedMessage)
    }

    @Test
    fun deleteMessage() {
        val service = ChatService
        val message = Message(0, false, "Hi")

        service.addMessage(1,message)

        val result = service.deleteMessage(1,1)

        assertEquals(true,result)

    }

    @Test(expected = MessageNotFoundException::class)
    fun deleteMessage_ShouldThrow() {
        val service = ChatService

        service.deleteMessage(1,1)
    }

    @Test
    fun deleteChat() {
        val service = ChatService

        service.createChat(1)

        val result = service.deleteChat(1)

        assertEquals(true, result)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChat_ShouldThrow() {
        val service = ChatService

        service.deleteChat(1)
    }
}