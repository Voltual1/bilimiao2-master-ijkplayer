package com.a10miaomiao.bilimiao.comm.player

import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory

object BilimiaoPlayerManager {

    fun initConfig() {
        PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
    }

}