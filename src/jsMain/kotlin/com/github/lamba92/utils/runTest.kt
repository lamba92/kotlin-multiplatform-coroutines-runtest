package com.github.lamba92.utils

actual fun <T> runTest(function: suspend () -> T): dynamic =
    GlobalScope.promise { function() }