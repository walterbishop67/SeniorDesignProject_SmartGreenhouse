package com.group22.smartgreenhouse.ui.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.repository.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationActivity(
    onNavigateToLogin: () -> Unit = { },
    onLoginClick: () -> Unit = { },
    authRepository: AuthRepository
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var passwordMatch by remember { mutableStateOf(true) }
    var emailError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }

    // Initialize ViewModel with factory
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(authRepository)
    )

    val registerState by viewModel.registerState.collectAsState()
    val shouldNavigate by viewModel.navigateToLogin.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToLogin.collect {
            if (it) {
                onNavigateToLogin()
                viewModel.resetNavigation()
            }
        }
    }

    // Handle registration state changes
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterViewModel.RegisterState.Success -> {
                Toast.makeText(
                    context,
                    "Registration successful! Please check your email to confirm your account.",
                    Toast.LENGTH_LONG
                ).show()
                // Clear form after successful registration
                firstName = ""
                lastName = ""
                email = ""
                userName = ""
                password = ""
                confirmPassword = ""
            }
            is RegisterViewModel.RegisterState.Error -> {
                if (state.message.contains("already registered")) {
                    emailError = true
                    emailErrorMessage = state.message
                } else {
                    Toast.makeText(
                        context,
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(32.dp))

        // First Name Field
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.green),
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = firstName.isEmpty() && registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(Modifier.height(16.dp))

        // Last Name Field
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.green),
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = lastName.isEmpty() && registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(Modifier.height(16.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.green),
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            isError = emailError || (email.isEmpty() && registerState is RegisterViewModel.RegisterState.Error),
            supportingText = {
                if (emailError) {
                    Text(text = emailErrorMessage, color = Color.Red)
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        // Username Field
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.green),
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = userName.isEmpty() && registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.green),
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            isError = password.isEmpty() && registerState is RegisterViewModel.RegisterState.Error
        )

        Spacer(Modifier.height(16.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordMatch = password == confirmPassword
            },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.green),
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isError = !passwordMatch || (confirmPassword.isEmpty() && registerState is RegisterViewModel.RegisterState.Error),
            supportingText = {
                if (!passwordMatch) {
                    Text(text = "Passwords don't match", color = Color.Red)
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = {
                if (password != confirmPassword) {
                    passwordMatch = false
                    return@Button
                }

                viewModel.register(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    userName = userName,
                    password = password,
                    confirmPassword = confirmPassword
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.green),
                contentColor = Color.White
            ),
            enabled = registerState !is RegisterViewModel.RegisterState.Loading &&
                    firstName.isNotEmpty() &&
                    lastName.isNotEmpty() &&
                    email.isNotEmpty() &&
                    userName.isNotEmpty() &&
                    password.isNotEmpty() &&
                    confirmPassword.isNotEmpty() &&
                    passwordMatch
        ) {
            if (registerState is RegisterViewModel.RegisterState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Register", fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Login Prompt
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "Already have an account? ",
                color = Color.Gray
            )
            Text(
                text = "Login here",
                color = colorResource(R.color.green),
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationActivityPreview() {
    // Mock implementation for preview
}