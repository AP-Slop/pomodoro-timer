# ポモドーロタイマー

25 分の「作業」と 5 分の「休憩」を自動でくり返す、シンプルなポモドーロ・テクニック用タイマーです。
Kotlin + Jetpack Compose (Material3) の単一 Activity アプリで、ネットワーク通信は行いません。

このアプリは [APSlop](https://apslop.dev) によって生成されました。

## 機能

- 25 分作業 → 5 分休憩を自動でくり返す
- 残り時間を `MM:SS` で大きく表示、円形プログレスで進捗を表示
  （作業中は赤系、休憩中は緑系に変化）
- 開始 / 一時停止 / リセット / スキップ
- 作業フェーズをやり切るたびに完了ラウンド数をカウント。
  カウントは DataStore Preferences に保存され、アプリを終了しても残ります
- 画面を回転してもタイマーは止まりません（ViewModel で状態を保持）

## 使い方

1. アプリを開くと「作業 25:00」の状態で待機します。
2. 「開始」でカウントダウン開始。
3. 25 分経つと自動で「休憩 05:00」に切り替わり、完了ラウンドが 1 増えます。
4. 休憩が終わると再び「作業 25:00」に戻ります。くり返し。
5. 「リセット」で作業フェーズの最初に戻り、「スキップ」で次のフェーズへ進みます
   （スキップは完了ラウンドに数えません）。

## 構成

```
settings.gradle.kts / build.gradle.kts / gradle.properties   Gradle 設定 (通常触らない)
gradle/libs.versions.toml                                   依存バージョン一覧
app/build.gradle.kts                                         applicationId / namespace / 依存
app/src/main/AndroidManifest.xml                             権限・Activity (権限なし)
app/src/main/java/dev/apslop/apps/pomodoro_timer/
  MainActivity.kt                                            画面 (Compose)
  Timer.kt                                                   Phase と時間フォーマット
  PomodoroViewModel.kt                                       タイマー本体 (coroutine)
  data/RoundStore.kt                                         完了ラウンドの保存 (DataStore)
  ui/Theme.kt                                                配色
app/src/main/res/values/strings.xml                          表示文字列 (app_name など)
app/src/test/java/dev/apslop/apps/pomodoro_timer/            単体テスト
.github/workflows/ci.yml                                     PR / main の自動ビルド
.github/workflows/release.yml                                タグ push → APK を Release に添付
```

## ローカルで動かす

```sh
./gradlew assembleDebug            # app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug             # 接続した端末にインストール
./gradlew testDebugUnitTest        # 時間表示フォーマットの単体テスト
```

Android Studio で開く場合はルートディレクトリを開くだけで動きます。

## リリースする

```sh
git tag v0.2.0
git push origin v0.2.0
```

`v<major>.<minor>.<patch>` 形式のタグを push すると `release.yml` が動き、
`versionName = 0.2.0`, `versionCode = 200` で **未署名** の release APK を Release に添付します。
署名は APSlop ストアが取り込み時に行うため、このリポジトリに鍵は不要です。

バージョンはローカルでは `gradle.properties` の `VERSION_NAME` / `VERSION_CODE`、
CI では環境変数 `APSLOP_VERSION_NAME` / `APSLOP_VERSION_CODE` で決まります。
