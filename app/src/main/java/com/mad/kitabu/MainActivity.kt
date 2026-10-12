
package com.mad.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.mad.kitabu.data.local.LibraryDatabase
import com.mad.kitabu.data.repository.LibraryRepository
import com.mad.kitabu.ui.navigation.KitabuNavigation
import com.mad.kitabu.ui.theme.Kitabu_MADB372_SF1Theme
import com.mad.kitabu.ui.viewmodel.LibraryViewModel
import com.mad.kitabu.ui.viewmodel.LibraryViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy {
        LibraryDatabase.getDatabase(applicationContext)
    }

    private val repository by lazy {
        LibraryRepository(
            database = database,
            context = applicationContext
        )
    }

    private val libraryViewModel: LibraryViewModel by viewModels {
        LibraryViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Kitabu_MADB372_SF1Theme {
                KitabuNavigation(
                    viewModel = libraryViewModel
                )
            }
        }
    }
}
