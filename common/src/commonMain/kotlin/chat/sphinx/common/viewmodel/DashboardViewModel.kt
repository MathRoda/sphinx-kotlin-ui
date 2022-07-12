package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.DashboardScreenType
import chat.sphinx.common.state.DashboardState
import chat.sphinx.concepts.socket_io.SocketIOManager
import chat.sphinx.concepts.socket_io.SocketIOState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.dashboard.RestoreProgress
import chat.sphinx.wrapper.lightning.NodeBalance
import chat.sphinx.wrapper.lightning.Sat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel {
    val dispatchers = SphinxContainer.appModule.dispatchers
    val viewModelScope = SphinxContainer.appModule.applicationScope
    val sphinxNotificationManager = createSphinxNotificationManager()
    val repositoryDashboard = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryDashboard
    val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    val socketIOManager: SocketIOManager = SphinxContainer.networkModule.socketIOManager

    private val _balanceStateFlow: MutableStateFlow<NodeBalance?> by lazy {
        MutableStateFlow(null)
    }

    val balanceStateFlow: StateFlow<NodeBalance?>
        get() = _balanceStateFlow.asStateFlow()

    private val _addContactWindowStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val addContactWindowStateFlow: StateFlow<Boolean>
        get() = _addContactWindowStateFlow.asStateFlow()

    fun toggleAddContactWindow() {
        _addContactWindowStateFlow.value = !_addContactWindowStateFlow.value
    }

    init {
        if (SphinxContainer.authenticationModule.authenticationCoreManager.getEncryptionKey() != null) {
            DashboardState.screenState(DashboardScreenType.Unlocked)
            networkRefresh()
        }

        viewModelScope.launch(dispatchers.mainImmediate) {
            socketIOManager.socketIOStateFlow.collect { state ->
                if (state is SocketIOState.Uninitialized) {
                    socketIOManager.connect()
                }
            }
        }

        viewModelScope.launch(dispatchers.mainImmediate) {
            repositoryDashboard.getAccountBalanceStateFlow().collect {
                _balanceStateFlow.value = it
            }
        }
    }

    private val _networkStateFlow: MutableStateFlow<LoadResponse<Boolean, ResponseError>> by lazy {
        MutableStateFlow(LoadResponse.Loading)
    }

    private val _restoreStateFlow: MutableStateFlow<RestoreProgress?> by lazy {
        MutableStateFlow(null)
    }

    val networkStateFlow: StateFlow<LoadResponse<Boolean, ResponseError>>
        get() = _networkStateFlow.asStateFlow()

    val restoreStateFlow: StateFlow<RestoreProgress?>
        get() = _restoreStateFlow.asStateFlow()

    private var jobNetworkRefresh: Job? = null
    private var jobPushNotificationRegistration: Job? = null


    fun networkRefresh() {
        if (jobNetworkRefresh?.isActive == true) {
            return
        }

        jobNetworkRefresh = viewModelScope.launch(dispatchers.mainImmediate) {
            contactRepository.networkRefreshContacts.collect { response ->
                Exhaustive@
                when (response) {
                    is LoadResponse.Loading -> {
                    }
                    is Response.Error -> {
                    }
                    is Response.Success -> {
                    }
                }
            }

            repositoryDashboard.networkRefreshBalance.collect { response ->
                Exhaustive@
                when (response) {
                    is LoadResponse.Loading,
                    is Response.Error -> {
                        _networkStateFlow.value = response
                    }
                    is Response.Success -> {}
                }
            }

            if (_networkStateFlow.value is Response.Error) {
                jobNetworkRefresh?.cancel()
            }

            repositoryDashboard.networkRefreshLatestContacts.collect { response ->
                Exhaustive@
                when (response) {
                    is LoadResponse.Loading -> {}
                    is Response.Error -> {
                        _networkStateFlow.value = response
                    }
                    is Response.Success -> {
                        val restoreProgress = response.value

                        if (restoreProgress.restoring) {
                            _restoreStateFlow.value = restoreProgress
                        }
                    }
                }
            }

            if (_networkStateFlow.value is Response.Error) {
                jobNetworkRefresh?.cancel()
            }

            // must occur after contacts have been retrieved such that
            // an account owner is available, otherwise it just suspends
            // until it is.
            if (jobPushNotificationRegistration == null) {
                jobPushNotificationRegistration = launch(dispatchers.mainImmediate) {
                    // TODO: Return push notifications...
//                    pushNotificationRegistrar.register().let { response ->
//                        Exhaustive@
//                        when (response) {
//                            is Response.Error -> {
//                                // TODO: Handle on the UI
//                            }
//                            is Response.Success -> {}
//                        }
//                    }
                }
            }

            repositoryDashboard.networkRefreshMessages.collect { response ->
                Exhaustive@
                when (response) {
                    is Response.Success -> {
                        val restoreProgress = response.value

                        if (restoreProgress.restoring && restoreProgress.progress < 100) {
                            _restoreStateFlow.value = restoreProgress
                        } else {
                            _restoreStateFlow.value = null

                            _networkStateFlow.value = Response.Success(true)
                        }
                    }
                    is Response.Error -> {
                        _networkStateFlow.value = response
                    }
                    is LoadResponse.Loading -> {
                        _networkStateFlow.value = response
                    }
                }
            }
        }
    }



    fun cancelRestore() {
        jobNetworkRefresh?.cancel()

        viewModelScope.launch(dispatchers.mainImmediate) {

            _networkStateFlow.value = Response.Success(true)
            _restoreStateFlow.value = null

            repositoryDashboard.didCancelRestore()
        }
    }
}