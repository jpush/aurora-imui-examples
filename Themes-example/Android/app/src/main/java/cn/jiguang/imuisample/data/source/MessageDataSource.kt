package cn.jiguang.imuisample.data.source

import cn.jiguang.imuisample.data.MyMessage

interface MessageDataSource {
    interface LoadHistoryMessages {
        fun onResult(list: List<MyMessage>)
    }

    interface GetMessageCallback {
        fun onResult(message: MyMessage)
    }

    fun getMessage(id: String, callback: GetMessageCallback)

    fun getHistoryMessages(from: Int, to: Int, callback: LoadHistoryMessages)

}