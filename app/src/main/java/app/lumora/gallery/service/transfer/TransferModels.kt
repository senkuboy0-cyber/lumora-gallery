package app.lumora.gallery.service.transfer

data class TransferPeer(val id: String, val name: String, val initials: String)
data class TransferProgress(val fileName: String, val fileIndex: Int, val fileCount: Int, val speedMbps: Double, val etaSeconds: Long, val progress: Float)

enum class TransferStatus { Idle, Scanning, Connecting, Transferring, Complete, Failed, Cancelled }
