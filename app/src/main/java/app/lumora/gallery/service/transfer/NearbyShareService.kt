package app.lumora.gallery.service.transfer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class NearbyShareService {
    private val peers = MutableStateFlow<List<TransferPeer>>(emptyList())
    private val status = MutableStateFlow(TransferStatus.Idle)

    fun observePeers(): Flow<List<TransferPeer>> = peers
    fun observeStatus(): Flow<TransferStatus> = status

    fun startScan() {
        status.value = TransferStatus.Scanning
        peers.value = listOf(TransferPeer("nearby-1", "Lumora Phone", "LP"), TransferPeer("nearby-2", "Gallery Tablet", "GT"))
    }

    fun connect(peer: TransferPeer) {
        status.value = if (peer.id.isNotBlank()) TransferStatus.Connecting else TransferStatus.Failed
    }
}
