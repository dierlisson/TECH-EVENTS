package com.dierlisson.techevents.presentation.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `splash timer should trigger navigation after 1200ms delay`() {
        val viewModel = SplashViewModel()

        assertNull(viewModel.navigateToEventsList.value)

        testDispatcher.scheduler.advanceTimeBy(1201L)

        assertTrue(viewModel.navigateToEventsList.value == true)
    }

    @Test
    fun `onNavigationHandled should reset navigation state`() {
        val viewModel = SplashViewModel()

        testDispatcher.scheduler.advanceTimeBy(1201L)
        assertTrue(viewModel.navigateToEventsList.value == true)

        viewModel.onNavigationHandled()
        assertEquals(false, viewModel.navigateToEventsList.value)
    }
}
