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
    fun `given login and password are valid, when init, then no errors are set`() {
        setLogin()
        setPassword()

        assertThat(loginViewModel.isEmailError.get()).isFalse()
        assertThat(loginViewModel.isPasswordError.get()).isFalse()
    }

    @Test
    fun `given login is valid & password is invalid, when login button clicked, then login error is not set`() {
        setLogin()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isEmailError.get()).isFalse()
    }

    @Test
    fun `given login is valid & password is invalid, when login button clicked, then password error is set`() {
        setLogin()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isPasswordError.get()).isTrue()
    }

    @Test
    fun `given login is valid & password is invalid, when login button clicked, then form is invalid`() {
        setLogin()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isFormValid.value).isFalse()
    }

    @Test
    fun `given login is invalid & password is valid, when login button clicked, then login error is set`() {
        setPassword()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isEmailError.get()).isTrue()
    }

    @Test
    fun `given login is invalid & password is valid, when login button clicked, then password error is not set`() {
        setPassword()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isPasswordError.get()).isFalse()
    }

    @Test
    fun `given login is invalid & password is valid, when login button clicked, then form is invalid`() {
        setPassword()

        loginViewModel.onLoginButtonClick()

        assertThat(loginViewModel.isFormValid.value).isFalse()
    }

    @Test
    fun `given login and password are valid, when login button clicked, then form is valid`() {
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