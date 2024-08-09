package com.example.loginapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.loginapp.ui.theme.LoginAppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var currentScreen by remember { mutableStateOf("LoginScreen") }
                    var loggedInUser by remember { mutableStateOf("") }
                    val registeredUsers = remember { mutableStateListOf<Pair<String, String>>() } // Pair of (Email, Password)
                    var registeredUsername by remember { mutableStateOf("") }
                    var showSnackbar by remember { mutableStateOf(false) }

                    when (currentScreen) {
                        "LoginScreen" -> LoginScreen(
                            onLoginSuccess = { username ->
                                loggedInUser = username
                                currentScreen = "HomeScreen"
                            },
                            onGuestLogin = {
                                loggedInUser = "Invitado"
                                currentScreen = "HomeScreen"
                            },
                            onNavigateToRegister = {
                                currentScreen = "RegisterScreen"
                            },
                            registeredUsers = registeredUsers
                        )
                        "RegisterScreen" -> RegisterScreen(
                            onRegisterSuccess = { username ->
                                registeredUsername = username
                                showSnackbar = true
                                currentScreen = "LoginScreen" // Ahora dirige a la pantalla de login después de registrarse
                            },
                            onNavigateToLogin = {
                                currentScreen = "LoginScreen"
                            },
                            registeredUsers = registeredUsers
                        )
                        "HomeScreen" -> HomeScreen(
                            username = loggedInUser,
                            onLogout = {
                                loggedInUser = ""
                                currentScreen = "LoginScreen"
                            },
                            onNavigateToRegister = {
                                currentScreen = "RegisterScreen"
                            },
                            onNavigateToLogin = {
                                currentScreen = "LoginScreen"
                            }
                        )
                    }

                    if (showSnackbar) {
                        Snackbar(
                            action = {
                                Button(onClick = { showSnackbar = false }) {
                                    Text("OK")
                                }
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Registro exitoso")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onGuestLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    registeredUsers: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            isError = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val user = registeredUsers.find { it.first == email && it.second == password }
                if (user != null) {
                    loginMessage = "Inicio de sesión exitoso"
                    onLoginSuccess(user.first) // Usamos el correo como nombre de usuario temporalmente
                } else {
                    loginMessage = "Credenciales inválidas"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onGuestLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar como invitado")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = loginMessage, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    registeredUsers: MutableList<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var registerMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Registrarse", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            isError = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                ) {
                    if (registeredUsers.any { it.first == email }) {
                        registerMessage = "El correo ya está registrado"
                    } else {
                        registeredUsers.add(email to password)
                        registerMessage = "Registro exitoso"
                        onRegisterSuccess(username)
                    }
                } else {
                    registerMessage = "Por favor, complete todos los campos correctamente"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a iniciar sesión")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = registerMessage, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    username: String,
    onLogout: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Tienda en Línea") },
            actions = {
                Box {
                    Text(
                        text = "Bienvenido, $username",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                expanded = false
                                onLogout()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ir a registro") },
                            onClick = {
                                expanded = false
                                onNavigateToRegister()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ir a inicio de sesión") },
                            onClick = {
                                expanded = false
                                onNavigateToLogin()
                            }
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Barra de búsqueda
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Buscar productos...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de productos (simulada)
        LazyColumn {
            items(10) { index ->
                ProductItem(
                    name = "Producto $index",
                    price = "$${(index + 1) * 10}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun ProductItem(name: String, price: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Imagen de ejemplo
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = name, style = MaterialTheme.typography.bodyLarge)
                Text(text = price, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LoginAppTheme {
        HomeScreen(
            username = "Usuario",
            onLogout = {},
            onNavigateToRegister = {},
            onNavigateToLogin = {}
        )
    }
}
