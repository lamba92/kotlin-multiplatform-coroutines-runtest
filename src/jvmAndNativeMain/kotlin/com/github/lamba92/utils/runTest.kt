package com.github.lamba92.utils

import kotlinx.coroutines.runBlocking

expect fun <T> runTest(function: suspend () -> T) =
    runBlocking(function)