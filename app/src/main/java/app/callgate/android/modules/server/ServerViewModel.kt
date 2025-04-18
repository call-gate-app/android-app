package app.callgate.android.modules.server

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ServerViewModel(
    private val serverSvc: ServerService
) : ViewModel() {
    private val _credentials = MutableLiveData<Credentials>()
    val credentials: LiveData<Credentials> = _credentials

    fun refresh() {
        _credentials.postValue(
            Credentials(
                serverSvc.settings.username,
                serverSvc.settings.password,
            )
        )
    }

    init {
        refresh()
    }

    data class Credentials(
        val username: String,
        val password: String,
    )
}