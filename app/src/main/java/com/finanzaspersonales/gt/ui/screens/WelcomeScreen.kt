package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.R

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterVertically,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "iPisto",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Administra tus finanzas de forma sencilla.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("INICIAR SESIÓN", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("CREAR CUENTA", style = MaterialTheme.typography.labelLarge)
        }
    }
}
