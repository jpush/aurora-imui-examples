package cn.jiguang.imuisample.data

import android.annotation.SuppressLint
import cn.jiguang.imuisample.data.source.MessageDao
import cn.jiguang.imuisample.data.source.MessageDataSource
import cn.jiguang.imuisample.data.source.MessageLocalDataSource


class MessageRepository(private var source: MessageLocalDataSource): MessageDataSource {

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: MessageRepository? = null

        fun getInstance(dataSource: MessageLocalDataSource): MessageRepository {
            if (INSTANCE == null) {
                synchronized(MessageRepository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MessageRepository(dataSource)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getMessage(id: String, callback: MessageDataSource.GetMessageCallback) {
        source.getMessage(id, callback)
    }

    override fun getMessageByPrimaryKey(id: Int, callback: MessageDataSource.GetMessageCallback) {
        source.getMessageByPrimaryKey(id, callback)
    }

    override fun getHistoryMessages(from: Int, to: Int, callback: MessageDataSource.LoadHistoryMessages) {
        source.getHistoryMessages(from, to, callback)
    }


}