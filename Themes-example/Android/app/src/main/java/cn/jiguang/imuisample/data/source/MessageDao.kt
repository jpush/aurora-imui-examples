package cn.jiguang.imuisample.data.source

import android.arch.persistence.room.*
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imuisample.data.MyMessage

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE entryId >= :from AND entryId <= :to")
    fun getHistoryMessages(from:Int, to: Int):List<MyMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: MyMessage)

    @Update
    fun updateMessage(message: MyMessage)

    @Query("DELETE FROM messages WHERE entryId = :id")
    fun deleteMessageById(id: String)

    @Query("DELETE FROM messages")
    fun deleteAllMessages()

    class MessageConverter {
        @TypeConverter
        fun storedIntToType(type: Int): IMessage.MessageType {
            when (type) {
                IMessage.MessageType.SEND_TEXT.ordinal -> {
                    return IMessage.MessageType.SEND_TEXT
                }
                IMessage.MessageType.SEND_IMAGE.ordinal -> {
                    return IMessage.MessageType.SEND_IMAGE
                }
                IMessage.MessageType.SEND_VOICE.ordinal -> {
                    return IMessage.MessageType.SEND_VOICE
                }
                IMessage.MessageType.SEND_VIDEO.ordinal -> {
                    return IMessage.MessageType.SEND_VIDEO
                }
                IMessage.MessageType.SEND_LOCATION.ordinal -> {
                    return IMessage.MessageType.SEND_LOCATION
                }
                IMessage.MessageType.RECEIVE_TEXT.ordinal -> {
                    return IMessage.MessageType.RECEIVE_TEXT
                }
                IMessage.MessageType.RECEIVE_IMAGE.ordinal -> {
                    return IMessage.MessageType.RECEIVE_IMAGE
                }
                IMessage.MessageType.RECEIVE_VOICE.ordinal -> {
                    return IMessage.MessageType.RECEIVE_VOICE
                }
                IMessage.MessageType.RECEIVE_VIDEO.ordinal -> {
                    return IMessage.MessageType.RECEIVE_VIDEO
                }
                IMessage.MessageType.RECEIVE_LOCATION.ordinal -> {
                    return IMessage.MessageType.RECEIVE_LOCATION
                }

            }
            val msgType = IMessage.MessageType.SEND_CUSTOM
            msgType.customType = type
            return msgType
        }

        @TypeConverter
        fun typeToStoredInt(type: IMessage.MessageType): Int {
            if (type == IMessage.MessageType.SEND_CUSTOM || type == IMessage.MessageType.RECEIVE_CUSTOM) {
                return type.customType
            }
            return type.ordinal
        }
    }
}