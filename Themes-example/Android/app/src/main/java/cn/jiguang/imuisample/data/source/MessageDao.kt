package cn.jiguang.imuisample.data.source

import android.arch.persistence.room.*
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imuisample.data.MyMessage

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE entryId >= :from AND entryId <= :to")
    fun getHistoryMessages(from:Int, to: Int):List<MyMessage>

    @Query("SELECT * FROM messages WHERE msgId = :id")
    fun getMessage(id: String): MyMessage

    @Query("SELECT * FROM messages WHERE entryId = :id")
    fun getMessageByPrimaryKey(id: Int): MyMessage

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: MyMessage)

    @Update
    fun updateMessage(message: MyMessage)

    @Query("DELETE FROM messages WHERE entryId = :id")
    fun deleteMessageById(id: String)

    @Query("DELETE FROM messages")
    fun deleteAllMessages()

}