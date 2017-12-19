package cn.jiguang.imuisample.data

import cn.jiguang.imui.commons.models.IUser


class DefaultUser(private val id: String, private val displayName: String, val avatar: String) : IUser {

    override fun getId(): String {
        return id
    }

    override fun getDisplayName(): String {
        return displayName
    }

    override fun getAvatarFilePath(): String {
        return avatar
    }
}
