package com.krzdabrowski.airpurrr.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    private lateinit var loginViewModel: LoginViewModel

    @get:Rule
    val aacSyncRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel()
    }

    @Test
    fun `on init, when login and password are valid, no errors are set`() {
        setLogin()
        setPassword()

        assertThat(loginViewModel.isEmailError.get()).isFalse()
        assertThat(loginViewModel.isPasswordError.get()).isFalse()
    }

    @Test
    fun `on login button clicked, when login is valid & password is invalid, login error is not set`() {
        setLogin()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isEmailError.get()).isFalse()
    }

    @Test
    fun `on login button clicked, when login is valid & password is invalid, password error is set`() {
        setLogin()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isPasswordError.get()).isTrue()
    }

    @Test
    fun `on login button clicked, when login is valid & password is invalid, form is invalid`() {
        setLogin()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isFormValid.value).isFalse()
    }

    @Test
    fun `on login button clicked, when login is invalid & password is valid, login error is set`() {
        setPassword()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isEmailError.get()).isTrue()
    }

    @Test
    fun `on login button clicked, when login is invalid & password is valid, password error is not set`() {
        setPassword()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isPasswordError.get()).isFalse()
    }

    @Test
    fun `on login button clicked, when login is invalid & password is valid, form is invalid`() {
        setPassword()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isFormValid.value).isFalse()
    }

    @Test
    fun `on login button clicked, when login and password are valid, form is valid`() {
        setLogin()
        setPassword()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isFormValid.value).isTrue()
    }

    private fun setLogin() {
        loginViewModel.email.value = "some@login.com"
    }

    private fun setPassword() {
        loginViewModel.password.value = "password"
    }
}