package jp.co.kotakestudio.googleplaygamesservices

import android.util.Log
import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.Player
import com.google.android.gms.tasks.Task
import org.godotengine.godot.BuildConfig
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo


class PlayGameServicesGodot(godot: Godot?) : GodotPlugin(godot) {

    companion object {
        var TAG = "PlayGameServicesGodot"
        var PLUGIN_NAME = "GooglePlayGamesServices"
        val SIGNAL_SIGN_IN_SUCCESSFUL = SignalInfo("_on_sign_in_success", String::class.java)
        val SIGNAL_SIGN_IN_FAILED = SignalInfo("_on_sign_in_failed")
    }

    init {
        activity?.let {
            Log.i(TAG, "initialize")
            PlayGamesSdk.initialize(it)
        }
    }

    override fun getPluginName(): String {
        return PLUGIN_NAME
    }

    override fun getPluginMethods(): MutableList<String> {
        return mutableListOf(
            "signIn",
        )
    }

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SIGNAL_SIGN_IN_SUCCESSFUL,
            SIGNAL_SIGN_IN_FAILED,
        )
    }

    fun signIn() {
        runOnUiThread {
            val gamesSignInClient = PlayGames.getGamesSignInClient(activity!!)
            gamesSignInClient.isAuthenticated.addOnCompleteListener { isAuthenticatedTask: Task<AuthenticationResult> ->
                val isAuthenticated =
                    isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated
                if (isAuthenticated) {
                    Log.i(TAG, "isAuthenticated successful")
                    // Continue with Play Games Services
                    PlayGames.getPlayersClient(activity!!).getCurrentPlayer()
                        .addOnCompleteListener {
                            Log.i(
                                TAG,
                                "getCurrentPlayer successful playerId: ${it.result.playerId}"
                            )
                            this.onSignInSuccess(it.result.playerId)
                        }
                } else {
                    // Disable your integration with Play Games Services or show a
                    // login button to ask  players to sign-in. Clicking it should
                    // call GamesSignInClient.signIn().
                    Log.i(TAG, "isAuthenticated failed")
                    gamesSignInClient.signIn().addOnCompleteListener {
                        val isReAuthenticated = it.isSuccessful && it.result.isAuthenticated
                        if (isReAuthenticated) {
                            // Continue with Play Games Services
                            PlayGames.getPlayersClient(activity!!).getCurrentPlayer()
                                .addOnCompleteListener { task: Task<Player> ->
                                    Log.i(
                                        TAG,
                                        "getCurrentPlayer successful playerId: ${task.result.playerId}"
                                    )
                                    this.onSignInSuccess(task.result.playerId)
                                }
                        } else {
                            Log.i(TAG, "isReAuthenticated failed")
                            emitSignal(SIGNAL_SIGN_IN_FAILED.name)
                        }
                    }
                }
            }
        }
    }

    private fun onSignInSuccess(playerId: String) {
        runOnUiThread {
            emitSignal(SIGNAL_SIGN_IN_SUCCESSFUL.name, playerId)
        }
    }

}