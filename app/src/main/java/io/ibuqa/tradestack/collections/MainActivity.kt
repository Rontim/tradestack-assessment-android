package io.ibuqa.tradestack.collections

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.ibuqa.tradestack.collections.ui.CollectionListScreen
import io.ibuqa.tradestack.collections.ui.RecordCollectionScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val nav = rememberNavController()
                    NavHost(nav, startDestination = "list") {
                        composable("list") {
                            CollectionListScreen(onRecord = { nav.navigate("record") })
                        }
                        composable("record") {
                            RecordCollectionScreen(onDone = { nav.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
