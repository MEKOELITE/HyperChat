package com.hyperchat.app.ui.screens

import androidx.lifecycle.ViewModel
import com.hyperchat.app.domain.model.EQExample
import com.hyperchat.app.domain.model.EQScenario
import com.hyperchat.app.domain.repository.EQLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileUiState(
    val scenarios: List<EQScenario> = emptyList(),
    val selectedScenarioIndex: Int = 0,
    val examples: List<EQExample> = emptyList()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val eqLibraryRepository: EQLibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadScenarios()
    }

    private fun loadScenarios() {
        val scenarios = eqLibraryRepository.getAllScenarios()
        _uiState.value = _uiState.value.copy(
            scenarios = scenarios,
            selectedScenarioIndex = 0
        )
        loadExamplesForScenario(0)
    }

    fun selectScenario(index: Int) {
        _uiState.value = _uiState.value.copy(selectedScenarioIndex = index)
        loadExamplesForScenario(index)
    }

    private fun loadExamplesForScenario(index: Int) {
        val scenarios = _uiState.value.scenarios
        if (index in scenarios.indices) {
            val examples = eqLibraryRepository.getExamplesByScenario(scenarios[index])
            _uiState.value = _uiState.value.copy(examples = examples)
        }
    }
}
