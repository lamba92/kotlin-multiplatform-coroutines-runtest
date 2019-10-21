package com.github.lamba92.utils

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CoroutineScope

actual fun runTest(function: suspend CoroutineScope.() -> Unit) =
    runBlocking(block = function)