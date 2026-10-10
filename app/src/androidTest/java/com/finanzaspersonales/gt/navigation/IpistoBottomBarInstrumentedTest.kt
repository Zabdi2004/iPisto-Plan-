package com.finanzaspersonales.gt.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.finanzaspersonales.gt.ui.components.QuetzalMascot
import com.finanzaspersonales.gt.ui.screens.EducacionFinancieraScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IpistoBottomBarInstrumentedTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun mainDestinationsAndCentralAddRemainNavigable() {
        val navController = TestNavHostController(InstrumentationRegistry.getInstrumentation().targetContext)
        composeRule.runOnUiThread {
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            navController.graph = navController.createGraph(startDestination = Screen.Inicio) {
                composable(Screen.Inicio) { }
                composable(Screen.Graficas) { }
                composable(Screen.Metas) { }
                composable(Screen.Perfil) { }
                composable(Screen.NuevoRegistro) { }
            }
        }
        composeRule.setContent {
            val entry by navController.currentBackStackEntryAsState()
            IpistoBottomBar(entry?.destination?.route, navController)
        }

        composeRule.onNodeWithText("Informes").performClick()
        composeRule.waitForIdle()
        assertEquals(Screen.Graficas, navController.currentDestination?.route)
        composeRule.onNodeWithText("Metas").performClick()
        composeRule.waitForIdle()
        assertEquals(Screen.Metas, navController.currentDestination?.route)
        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.waitForIdle()
        assertEquals(Screen.Perfil, navController.currentDestination?.route)
        composeRule.onNodeWithText("Inicio").performClick()
        composeRule.waitForIdle()
        assertEquals(Screen.Inicio, navController.currentDestination?.route)
        composeRule.onNodeWithContentDescription("Nuevo registro").performClick()
        composeRule.waitForIdle()
        assertEquals(Screen.NuevoRegistro, navController.currentDestination?.route)
        // The bar stays available on the form route, so the user can recover without restarting.
        composeRule.onNodeWithText("Informes").performClick()
        composeRule.waitForIdle()
        assertEquals(Screen.Graficas, navController.currentDestination?.route)
    }

    @Test fun quetzalSemanticsRenderAtCompactAndLargeSizes() {
        composeRule.setContent {
            Row {
                QuetzalMascot(Modifier.testTag("quetzal-small"), size = 32.dp)
                QuetzalMascot(Modifier.testTag("quetzal-card"), size = 56.dp)
                QuetzalMascot(Modifier.testTag("quetzal-large"), size = 96.dp)
            }
        }
        composeRule.onAllNodesWithContentDescription("Quetzal verde de iPisto").assertCountEquals(3)
    }

    @Test fun financialEducationCanScrollToEndAndBackToStart() {
        composeRule.setContent {
            EducacionFinancieraScreen()
        }

        composeRule.onNodeWithText("Protección Patrimonial").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("EDUCACIÓN FINANCIERA").performScrollTo().assertIsDisplayed()
    }
}
