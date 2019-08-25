package zion.common

enum class Source {
    SENTINELS,
    SNIFFERS,
    LOOPHOLES;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}
