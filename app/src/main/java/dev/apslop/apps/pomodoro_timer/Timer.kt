package dev.apslop.apps.pomodoro_timer

/**
 * ポモドーロの1フェーズ。
 *
 * WORK が 25 分、BREAK が 5 分。終わったら [next] のフェーズへ進む。
 */
enum class Phase(val durationMillis: Long) {
    WORK(25L * 60L * 1000L),
    BREAK(5L * 60L * 1000L);

    val next: Phase
        get() = if (this == WORK) BREAK else WORK
}

/** 残り時間を "MM:SS" 形式に整形する（1 秒未満の端数は切り上げ）。 */
fun formatMillis(millis: Long): String {
    val totalSeconds = (millis.coerceAtLeast(0L) + 999L) / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%02d:%02d".format(minutes, seconds)
}
