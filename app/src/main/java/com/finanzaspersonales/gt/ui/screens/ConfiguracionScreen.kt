package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.ui.components.ActionRow
import com.finanzaspersonales.gt.utils.PreferencesHelper

private val SettingsMaxWidth = 390.dp
private val SettingsGroupShape = RoundedCornerShape(14.dp)

/**
 * Ajustes con la misma línea estética que la pantalla de Seguridad:
 * cabecera con flecha atrás, etiquetas de sección y grupos redondeados.
 */
@Composable
fun ConfiguracionScreen(navController: NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    var selectedLanguage by remember { mutableStateOf(PreferencesHelper.getLanguage(context)) }
    var selectedCurrency by remember { mutableStateOf(PreferencesHelper.getCurrency(context)) }
    val languageOptions = listOf(
        "es" to stringResource(R.string.language_spanish),
        "cak" to stringResource(R.string.language_kaqchikel),
        "quc" to stringResource(R.string.language_quiche)
    )
    val currencyOptions = listOf(
        "gtq" to stringResource(R.string.currency_gtq),
        "usd" to stringResource(R.string.currency_usd),
        "eur" to stringResource(R.string.currency_eur)
    )

    val goBack: () -> Unit = {
        if (!navController.popBackStack()) {
            navController.navigate("inicio") { launchSingleTop = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = SettingsMaxWidth)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = goBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back_arrow), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.settings),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = stringResource(R.string.config_subtitle),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SectionLabel(stringResource(R.string.current_preferences))
            SettingsGroup {
                PreferenceInfo(Icons.Default.Language, stringResource(R.string.language), languageOptions.find { it.first == selectedLanguage }?.second ?: stringResource(R.string.language_spanish))
                PreferenceInfo(Icons.Default.Storage, stringResource(R.string.currency), currencyOptions.find { it.first == selectedCurrency }?.second ?: stringResource(R.string.currency_gtq))
                PreferenceInfo(Icons.Default.Lock, stringResource(R.string.security), stringResource(R.string.security_managed_in_profile))
            }

            SectionLabel(stringResource(R.string.language))
            SettingsGroup {
                Text(stringResource(R.string.select_app_language), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                languageOptions.forEachIndexed { index, (code, name) ->
                    SelectableRow(name, selectedLanguage == code) {
                        selectedLanguage = code
                        PreferencesHelper.saveLanguage(context, code)
                        activity?.let { PreferencesHelper.applyLanguageAndRecreate(it, code) }
                    }
                    if (index != languageOptions.lastIndex) GroupDivider()
                }
            }

            SectionLabel(stringResource(R.string.currency))
            SettingsGroup {
                currencyOptions.forEachIndexed { index, (code, name) ->
                    SelectableRow(name, selectedCurrency == code) {
                        selectedCurrency = code
                        PreferencesHelper.saveCurrency(context, code)
                        PreferencesHelper.applyCurrency(context, code)
                        activity?.let { PreferencesHelper.applyLanguageAndRecreate(it, PreferencesHelper.getLanguage(context)) }
                    }
                    if (index != currencyOptions.lastIndex) GroupDivider()
                }
            }

            ActionRow(Icons.Filled.Lock, stringResource(R.string.open_profile_security), { navController.navigate("seguridad") })

            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.local_data_notice),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = SettingsGroupShape, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun SelectableRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun GroupDivider() {
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun PreferenceInfo(icon: ImageVector, title: String, description: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
