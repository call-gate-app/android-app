package app.callgate.android.modules.connection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val connectionSvc: ConnectionService
) : ViewModel() {
    private val _localIP = MutableLiveData<String>()
    val localIP: LiveData<String> = _localIP

    private val _publicIP = MutableLiveData<String>()
    val publicIP: LiveData<String> = _publicIP

    fun getLocalIP() {
        viewModelScope.launch(Dispatchers.IO) {
            _localIP.postValue(connectionSvc.getLocalIP())
        }
    }

    fun getPublicIP() {
        viewModelScope.launch(Dispatchers.IO) {
            _publicIP.postValue(connectionSvc.getPublicIP())
        }
    }
}