package com.github.lamba92.utils

import kotlinx.coroutines.CoroutineScope

expect fun runTest(function: suspend CoroutineScope.() -> Unit)