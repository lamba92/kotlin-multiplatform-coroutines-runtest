package com.github.lamba92.utils

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CoroutineScope

actual fun <T> runTest(function: suspend CoroutineScope.() -> Unit) =
    runBlocking(block = function)