package com.github.lamba92.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.CoroutineScope

actual fun <T> runTest(function: suspend CoroutineScope.() -> Unit) =
    GlobalScope.promise { function() }.asDynamic()