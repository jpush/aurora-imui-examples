package cn.jiguang.imuisample.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

import java.util.HashMap
import java.util.UUID

import cn.jiguang.imui.commons.models.IMessage
import cn.jiguang.imui.commons.models.IUser

@Entity(tableName = "messages")
class MyMessage(@field:ColumnInfo(name = "msgId")
                private var mMsgId: String, private var text: String?, private var timeString: String?,
                private var type: Int,
                @field:Embedded
                var user: DefaultUser?, private var mediaFilePath: String?, private var duration: Long,
                private var progress: String?) : IMessage {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entryId")
    var id: Int = 0

    @Ignore
    constructor(text: String, type: Int)
            : this(UUID.randomUUID().toString(), text, "", type, null, "", 0, "") {
    }

    @Ignore
    constructor(text: String, type: Int, user: DefaultUser)
            : this(UUID.randomUUID().toString(), text, "", type, user, "", 0, "") {
    }

    override fun getMsgId(): String {
        return this.mMsgId
    }

    override fun getFromUser(): IUser {
        return if (user == null) {
            DefaultUser("0", "user1", "")
        } else user!!
    }

    fun setMediaFilePath(path: String) {
        this.mediaFilePath = path
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    override fun getDuration(): Long {
        return duration
    }

    fun setProgress(progress: String) {
        this.progress = progress
    }

    override fun getProgress(): String? {
        return progress
    }

    override fun getExtras(): HashMap<String, String>? {
        return null
    }

    fun setTimeString(timeString: String) {
        this.timeString = timeString
    }

    override fun getTimeString(): String? {
        return timeString
    }


    fun setType(type: Int) {
        this.type = type
    }

    override fun getType(): Int {
        return type
    }

    override fun getMessageStatus(): IMessage.MessageStatus {
        return IMessage.MessageStatus.SEND_SUCCEED
    }

    override fun getText(): String? {
        return text
    }

    fun setText(text: String) {
        this.text = text
    }

    override fun getMediaFilePath(): String? {
        return mediaFilePath
    }
}

