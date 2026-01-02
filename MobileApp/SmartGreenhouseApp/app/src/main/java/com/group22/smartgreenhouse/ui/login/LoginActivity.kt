import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.UserRole
import com.group22.smartgreenhouse.ui.login.LoginActivityViewModel
import com.group22.smartgreenhouse.ui.login.LoginViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginActivity(
    onLoginClick: (userRole: UserRole) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LoginActivityViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )
    val uiState = viewModel.uiState

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var userRole = UserRole.USER

    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var resetToken by remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    if (showResetPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showResetPasswordDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = resetToken,
                        onValueChange = { resetToken = it },
                        label = { Text("Reset Token") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordOutlinedTextField(
                        password = newPassword,
                        label = "New Password"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordOutlinedTextField(
                        password = confirmPassword,
                        label = "Confirm Password"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetToken.isBlank() || newPassword.value.isBlank() || confirmPassword.value.isBlank()) {
                            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                        } else if (newPassword.value != confirmPassword.value) {
                            Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.resetPassword(email.value, resetToken, newPassword.value, confirmPassword.value)

                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green),
                        contentColor = Color.White
                    )
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        resetToken = ""
                        newPassword.value = ""
                        confirmPassword.value = ""
                        showResetPasswordDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginActivityViewModel.UiState.Error -> {
                val errorMessage = (uiState as LoginActivityViewModel.UiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            is LoginActivityViewModel.UiState.Success -> {
                val response = (uiState as LoginActivityViewModel.UiState.Success).response
                response?.let {
                    val role = if (it.roles.any { role -> role.equals("Admin", ignoreCase = true) }) {
                        UserRole.ADMIN
                    } else {
                        UserRole.USER
                    }
                    onLoginClick(role)
                }
            }
            is LoginActivityViewModel.UiState.ForgotPasswordSuccess -> {
                val message = (uiState as LoginActivityViewModel.UiState.ForgotPasswordSuccess).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                showResetPasswordDialog = true
            }
            is LoginActivityViewModel.UiState.ResetPasswordSuccess -> {
                val message = (uiState as LoginActivityViewModel.UiState.ResetPasswordSuccess).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                showResetPasswordDialog = false  // Only close on success
                // Clear the fields
                resetToken = ""
                newPassword.value = ""
                confirmPassword.value = ""
            }
            else -> {}
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo
        Image(
            painter = painterResource(R.drawable.logo), // Replace with your logo
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp))

        Spacer(modifier = Modifier.height(40.dp))

        // Email Field
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.green),
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        PasswordOutlinedTextField(password = password)

        // Forgot Password
        // Forgot Password
        Text(
            text = buildAnnotatedString {
                append("Forgot password?")
                addStyle(
                    style = SpanStyle(
                        color = colorResource(R.color.green),
                        fontSize = 14.sp
                    ),
                    start = 0,
                    end = "Forgot password?".length
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable {
                    if (email.value.isBlank()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    } else if (!isValidEmail(email.value)) {
                        Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.forgotPassword(email.value)
                    }
                },
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = {
                if (email.value.isBlank() || password.value.isBlank()) {
                    Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.login(email.value, password.value)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.green),
                contentColor = Color.White
            )
        ) {
            Text("Login", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Registration Prompt
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                color = Color.Gray
            )
            Text(
                text = buildAnnotatedString {
                    append("Register here")
                    addStyle(
                        style = SpanStyle(
                            color = colorResource(R.color.green),
                            fontSize = 14.sp
                        ),
                        start = 0,
                        end = "Register here".length
                    )
                },
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return email.matches(emailRegex.toRegex())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordOutlinedTextField(
    password: MutableState<String>,
    label: String = "Password",
    onPasswordChange: (String) -> Unit = { password.value = it }
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password.value,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (showPassword)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(imageVector = image, contentDescription = if (showPassword) "Hide password" else "Show password")
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(R.color.green),
            unfocusedBorderColor = Color.LightGray
        ),
        singleLine = true
    )
}


@Preview(showBackground = true)
@Composable
fun LoginActivityPreview() {
    //LoginActivity()
}