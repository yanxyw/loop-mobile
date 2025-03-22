package com.loop.mobile.presentation

import kotlinx.coroutines.CoroutineScope

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect open class BaseViewModel() {
    var scope: CoroutineScope

    fun showLoading()
    fun hideLoading()
    fun showDialog(title: String, message: String)
    fun hideDialog()
}