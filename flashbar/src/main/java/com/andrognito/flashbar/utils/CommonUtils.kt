package com.andrognito.flashbar.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.*
import android.view.Surface.*
import com.andrognito.flashbar.utils.NavigationBarPosition.*
import java.lang.reflect.InvocationTargetException

enum class NavigationBarPosition {
    BOTTOM,
    RIGHT,
    LEFT,
    TOP
}

internal fun getStatusBarHeightInPx(activity: Activity): Int {
    val rectangle = Rect()
    val window = activity.window

    window.decorView.getWindowVisibleDisplayFrame(rectangle)

    val statusBarHeight = rectangle.top
    val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top

    return contentViewTop - statusBarHeight
}

internal fun getNavigationBarPosition(activity: Activity): NavigationBarPosition {
    return when (activity.windowManager.defaultDisplay.rotation) {
        ROTATION_0 -> BOTTOM
        ROTATION_90 -> RIGHT
        ROTATION_270 -> LEFT
        else -> TOP
    }
}

internal fun getNavigationBarSizeInPixels(activity: Activity): Int {
    val realScreenSize = getRealScreenSize(activity)
    val appUsableScreenSize = getAppUsableScreenSize(activity)
    val navigationBarPosition = getNavigationBarPosition(activity)

    return if (navigationBarPosition == LEFT || navigationBarPosition == RIGHT) {
        realScreenSize.x - appUsableScreenSize.x
    } else {
        realScreenSize.y - appUsableScreenSize.y
    }
}

internal fun getActivityRootView(activity: Activity?): ViewGroup? {
    if (activity == null || activity.window == null || activity.window.decorView == null) {
        return null
    }
    return activity.window.decorView as ViewGroup
}

private fun getAppUsableScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val defaultDisplay = windowManager.defaultDisplay
    val size = Point()
    defaultDisplay.getSize(size)
    return size
}

private fun getRealScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val defaultDisplay = windowManager.defaultDisplay
    val size = Point()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        defaultDisplay.getRealSize(size)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        try {
            size.x = Display::class.java.getMethod("getRawWidth").invoke(defaultDisplay) as Int
            size.y = Display::class.java.getMethod("getRawHeight").invoke(defaultDisplay) as Int
        } catch (e: IllegalAccessException) {
        } catch (e: InvocationTargetException) {
        } catch (e: NoSuchMethodException) {
        }
    }
    return size
}