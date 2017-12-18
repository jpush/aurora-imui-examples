package cn.jiguang.imuisample.data.source

import android.annotation.SuppressLint
import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imuisample.data.DefaultUser
import cn.jiguang.imuisample.data.MyMessage
import cn.jiguang.imuisample.util.AppExecutors


class MessageLocalDataSource(private var appExcutors: AppExecutors, private var msgDao: MessageDao): MessageDataSource {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: MessageLocalDataSource? = null

        fun getInstance(appExcutors: AppExecutors, msgDao: MessageDao): MessageLocalDataSource {
            if (null == INSTANCE) {
                INSTANCE = MessageLocalDataSource(appExcutors, msgDao)
            }
            return INSTANCE!!
        }
    }

    override fun getMessage(id: String, callback: MessageDataSource.GetMessageCallback) {
    }

    override fun getHistoryMessages(from: Int, to: Int, callback: MessageDataSource.LoadHistoryMessages) {
        val runnable = Runnable {
            val messages: List<MyMessage> = msgDao.getHistoryMessages(from, to)

            if (messages.isNotEmpty()) {
                callback.onResult(messages)
            } else {
                callback.onResult(fakeData())
            }
        }
        appExcutors.diskIO().execute(runnable)
    }

    fun fakeData(): List<MyMessage> {
        val list = ArrayList<MyMessage>(10)
        print("Returning fake data")
        for (i: Int in 0..10) {
            print("Message $i is creating")
            if (i % 2 == 0) {
                val user = DefaultUser("0", "user1", "R.drawable.ironman")
                list.add(MyMessage("Hello", IMessage.MessageType.SEND_TEXT, user))
            } else {
                val user = DefaultUser("1", "user2", "R.drawable.deadpool")
                list.add(MyMessage("Hi", IMessage.MessageType.RECEIVE_TEXT, user))
            }
        }
        return list
    }
}