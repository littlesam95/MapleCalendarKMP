package com.sixclassguys.maplecalendar

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    // override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val name: String = "IOS"
}

actual fun getPlatform(): Platform = IOSPlatform()