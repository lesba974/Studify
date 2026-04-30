package ca.uqac.studify

import ca.uqac.studify.ui.screens.home.HomeViewModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun archived_isFalse_byDefault() {

        val viewModel = HomeViewModel()

        assertFalse(viewModel.showArchived.value)
    }

    @Test
    fun setShowArchived_changesValue() {

        val viewModel = HomeViewModel()

        viewModel.setShowArchived(true)

        assertTrue(viewModel.showArchived.value)
    }
}