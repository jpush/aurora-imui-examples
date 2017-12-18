package cn.jiguang.imuisample.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import cn.jiguang.imuisample.data.MessageRepository
import cn.jiguang.imuisample.data.source.MessageDataSource


class MessageViewModel(private var context: Application, private var repository: MessageRepository) : AndroidViewModel(Application()) {

    fun loadMessages(callback: MessageDataSource.LoadHistoryMessages) {
        this.repository.getHistoryMessages(0, 10, callback)
    }
}