package com.finanzaspersonales.gt.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.ui.components.CircleIconButton

/**
 * Barra superior mínima compartida: botón de cerrar sesión (puerta) alineado a la derecha,
 * y opcionalmente el botón de ajustes (tuerca). Reutiliza los colores del tema.
 */
@Composable
fun AppTopBar(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onSettings: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onSettings != null) {
            CircleIconButton(Icons.Filled.Settings, stringResource(R.string.action_settings), onSettings)
            Spacer(Modifier.width(8.dp))
        }
        CircleIconButton(Icons.AutoMirrored.Filled.Logout, stringResource(R.string.logout_button), onLogout)
    }
}
