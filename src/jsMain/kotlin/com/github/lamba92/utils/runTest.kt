package com.github.lamba92.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.CoroutineScope

actual fun runTest(function: suspend CoroutineScope.() -> Unit) =
    GlobalScope.promise { function() }.asDynamic()