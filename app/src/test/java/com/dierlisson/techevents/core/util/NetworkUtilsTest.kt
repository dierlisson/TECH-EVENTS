package com.dierlisson.techevents.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NetworkUtilsTest {

    private val context: Context = mock()
    private val connectivityManager: ConnectivityManager = mock()
    private val networkCapabilities: NetworkCapabilities = mock()

    @Test
    fun `isNetworkAvailable should return false when ConnectivityManager is null`() {
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null)

        val result = NetworkUtils.isNetworkAvailable(context)

        assertFalse(result)
    }

    @Test
    fun `isNetworkAvailable should return true when network has WIFI transport`() {
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        val activeNetwork = mock<android.net.Network>()
        whenever(connectivityManager.activeNetwork).thenReturn(activeNetwork)
        whenever(connectivityManager.getNetworkCapabilities(activeNetwork)).thenReturn(networkCapabilities)
        whenever(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        whenever(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true)

        val result = NetworkUtils.isNetworkAvailable(context)

        assertTrue(result)
    }
}
