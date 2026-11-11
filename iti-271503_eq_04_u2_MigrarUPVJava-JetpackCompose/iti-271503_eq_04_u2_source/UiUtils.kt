package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

internal object UiUtils {

    private const val CARDBOARD_WEBSITE =
        "http://google.com/cardboard/cfg?vrtoolkit_version=0.5.1"

    private const val CARDBOARD_CONFIGURE_ACTION =
        "com.google.vrtoolkit.cardboard.CONFIGURE"

    private const val INTENT_EXTRAS_VERSION_KEY = "VERSION"
    private const val CARDBOARD_VERSION = "0.5.1"
    private const val NO_BROWSER_TEXT = "No browser to open website."

    fun launchOrInstallCardboard(context: Context) {
        val packageManager = context.packageManager

        val settingsIntent = Intent().apply {
            action = CARDBOARD_CONFIGURE_ACTION
            putExtra(INTENT_EXTRAS_VERSION_KEY, CARDBOARD_VERSION)
        }

        val resolveInfos: List<ResolveInfo> =
            packageManager.queryIntentActivities(settingsIntent, 0)

        val intentsToGoogleCardboard: MutableList<Intent> = ArrayList()

        for (info in resolveInfos) {
            val packageName = info.activityInfo.packageName

            if (packageName.startsWith("com.google.")) {
                val intent = Intent(settingsIntent)
                intent.setClassName(packageName, info.activityInfo.name)
                intentsToGoogleCardboard.add(intent)
            }
        }

        when {
            intentsToGoogleCardboard.isEmpty() -> {
                showInstallDialog(context)
            }

            intentsToGoogleCardboard.size == 1 -> {
                showConfigureDialog(context, intentsToGoogleCardboard[0])
            }

            else -> {
                showConfigureDialog(context, settingsIntent)
            }
        }
    }

    private fun showInstallDialog(context: Context) {
        val activity = context as? Activity ?: return

        pendingDialogStrings = InstallDialogStrings()
        pendingPositiveButtonListener = DialogInterface.OnClickListener { _, _ ->
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(CARDBOARD_WEBSITE)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context.applicationContext,
                    NO_BROWSER_TEXT,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val dialog = SettingsDialogFragment()
        dialog.show(activity.fragmentManager, "InstallCardboardDialog")
    }

    private fun showConfigureDialog(context: Context, intent: Intent?) {
        val activity = context as? Activity ?: return

        pendingDialogStrings = ConfigureDialogStrings()
        pendingPositiveButtonListener = DialogInterface.OnClickListener { _, _ ->
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showInstallDialog(context)
            }
        }

        val dialog = SettingsDialogFragment()
        dialog.show(activity.fragmentManager, "ConfigureCardboardDialog")
    }

    internal open class DialogStrings {
        var mTitle: String? = null
        var mMessage: String? = null
        var mPositiveButtonText: String? = null
        var mNegativeButtonText: String? = null
    }

    private class InstallDialogStrings : DialogStrings() {
        init {
            mTitle = "Configure"
            mMessage = "Get the Cardboard app in order to configure your viewer."
            mPositiveButtonText = "Go to Play Store"
            mNegativeButtonText = "Cancel"
        }
    }

    private class ConfigureDialogStrings : DialogStrings() {
        init {
            mTitle = "Configure"
            mMessage = "Set up your viewer for the best experience."
            mPositiveButtonText = "Setup"
            mNegativeButtonText = "Cancel"
        }
    }

    private var pendingDialogStrings: DialogStrings? = null
    private var pendingPositiveButtonListener: DialogInterface.OnClickListener? = null

    class SettingsDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialogStrings = pendingDialogStrings ?: DialogStrings().apply {
                mTitle = "Configure"
                mMessage = "Configure Cardboard."
                mPositiveButtonText = "OK"
                mNegativeButtonText = "Cancel"
            }

            val positiveListener = pendingPositiveButtonListener

            return AlertDialog.Builder(activity)
                .setTitle(dialogStrings.mTitle)
                .setMessage(dialogStrings.mMessage)
                .setPositiveButton(
                    dialogStrings.mPositiveButtonText,
                    positiveListener
                )
                .setNegativeButton(
                    dialogStrings.mNegativeButtonText,
                    null
                )
                .create()
        }

        override fun onDestroy() {
            super.onDestroy()

            pendingDialogStrings = null
            pendingPositiveButtonListener = null
        }
    }
}