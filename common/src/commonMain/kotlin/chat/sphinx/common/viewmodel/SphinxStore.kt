package chat.sphinx.common.viewmodel

import androidx.compose.runtime.remember
import chat.sphinx.authentication.model.OnBoardStep
import chat.sphinx.authentication.model.OnBoardStepHandler
import chat.sphinx.common.state.*
import chat.sphinx.di.container.SphinxContainer
import kotlinx.coroutines.launch

class SphinxStore {
    private val scope = SphinxContainer.appModule.applicationScope
    private val authenticationManager = SphinxContainer.authenticationModule.authenticationCoreManager
    private val authenticationStorage = SphinxContainer.authenticationModule.authenticationStorage
    private val encryptionKeyHandler = SphinxContainer.authenticationModule.encryptionKeyHandler
    private val onBoardStepHandler = OnBoardStepHandler()

    fun removeAccount() {
        // TODO: logout Confirmation...
        scope.launch(SphinxContainer.appModule.dispatchers.main) {

            SphinxContainer.appModule.sphinxCoreDBImpl.deleteDatabase()

            authenticationStorage.clearAuthenticationStorage()
            authenticationManager.logOut()
            encryptionKeyHandler.clearKeysToRestore()
            // TODO: Restart DB...

            AppState.screenState(ScreenType.LandingScreen)
        }
    }

    suspend fun restoreSignupStep() {
        onBoardStepHandler.retrieveOnBoardStep()?.let { onBoardStep ->
            when (onBoardStep) {
                is OnBoardStep.Step1_WelcomeMessage -> {
                    LandingScreenState.screenState(LandingScreenType.OnBoardMessage)
                }
                is OnBoardStep.Step2_Name,
                is OnBoardStep.Step3_Picture,
                is OnBoardStep.Step4_Ready-> {
                    LandingScreenState.screenState(LandingScreenType.SignupLocked)
                }
            }
        }
    }
}