package cn.jiguang.imuisample

import android.content.Context
import cn.jiguang.imuisample.data.MessageRepository
import cn.jiguang.imuisample.data.source.MessageLocalDataSource
import cn.jiguang.imuisample.data.source.MyDatabase
import cn.jiguang.imuisample.util.AppExecutors

class Injection {

    companion object {
        fun provideMsgsRepository(context: Context): MessageRepository {
            val database = MyDatabase.getInstance(context)
            return MessageRepository.getInstance(MessageLocalDataSource.getInstance(AppExecutors(), database.messageDao()))
        }
    }
}