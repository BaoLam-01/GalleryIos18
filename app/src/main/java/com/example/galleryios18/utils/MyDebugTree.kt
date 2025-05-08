package com.example.galleryios18.utils

import timber.log.Timber

class MyDebugTree : Timber.DebugTree() {
    private var fileName: String? = null
    override fun createStackElementTag(element: StackTraceElement): String {
        fileName = element.fileName
        return String.format(
            "(%s:%s)#%s", element.fileName, element.lineNumber, element.methodName
        )
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val mTag: String?
        val mMessage: String
        if (tag != null && tag.length > 35) {
            mTag = fileName
            mMessage = "$message $tag"
        } else {
            mTag = tag
            mMessage = message
        }
        super.log(priority, mTag, mMessage, t)
    }
}