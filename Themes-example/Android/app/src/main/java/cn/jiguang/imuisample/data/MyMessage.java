package cn.jiguang.imuisample.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.commons.models.IUser;

@Entity(tableName = "messages")
public class MyMessage implements IMessage {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entryId")
    private int mId;
    private String mMsgId;
    private String text;
    private String timeString;
    private MessageType type;
    @Embedded
    private DefaultUser user;
    private String mediaFilePath;
    private long duration;
    private String progress;

    @Ignore
    public MyMessage(String text, MessageType type) {
        this(UUID.randomUUID().toString(), text, "", type, null, "", 0, "");
    }

    @Ignore
    public MyMessage(String text, MessageType type, DefaultUser user) {
        this(UUID.randomUUID().toString(), text, "", type, user, "", 0, "");
    }

    public MyMessage(String msgId, String text, String timeString, @NonNull MessageType type,
                     @Nullable DefaultUser user, String mediaFilePath, long duration, String progress) {
        this.text = text;
        this.type = type;
        this.mMsgId = msgId;
        this.timeString = timeString;
        this.mediaFilePath = mediaFilePath;
        this.user = user;
        this.duration = duration;
        this.progress = progress;
    }

    public DefaultUser getUser() {
        return user;
    }

    public void setUser(DefaultUser user) {
        this.user = user;
    }


    public void setId(int id) {
        this.mId = id;
    }

    public int getId() {
        return this.mId;
    }

    @Override
    public String getMsgId() {
        return this.mMsgId;
    }

    @Override
    public IUser getFromUser() {
        if (user == null) {
            return new DefaultUser("0", "user1", null);
        }
        return user;
    }

    public void setMediaFilePath(String path) {
        this.mediaFilePath = path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String getProgress() {
        return progress;
    }

    @Override
    public HashMap<String, String> getExtras() {
        return null;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    @Override
    public String getTimeString() {
        return timeString;
    }

    public void setCustomType(int type) {
        this.type.setCustomType(type);
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public MessageStatus getMessageStatus() {
        return MessageStatus.SEND_SUCCEED;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getMediaFilePath() {
        return mediaFilePath;
    }
}

