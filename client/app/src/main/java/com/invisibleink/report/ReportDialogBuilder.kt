package com.invisibleink.report

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.invisibleink.R

/**
 * Helper class that builds a dialog for reporting notes and forwards
 * submit/cancellation callbacks to attached listeners.
 */
class ReportDialogBuilder {

    private companion object {
        private const val DEFAULT_ITEM = 0
        private val reportTypes = ReportType.values()
    }

    fun buildReportDialog(
        context: Context,
        onSubmitListener: (ReportType?, comment: String?) -> Unit,
        onCancelListener: () -> Unit
    ): AlertDialog {
        val reportItems = reportTypes.map { it.asString(context) as CharSequence }.toTypedArray()
        val reportClickWrapper = ReportClickWrapper(onSubmitListener, onCancelListener)
        val commentView = buildCommentView(context)
        val commentEditText: EditText = commentView.findViewById(R.id.reportEditText)

        return AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.report_dialog_title))
            .setPositiveButton(context.getString(R.string.report_dialog_submit)) { dialog, _ ->
                reportClickWrapper.forwardSubmit(dialog, commentEditText.text.toString())
            }
            .setNegativeButton(context.getString(R.string.report_dialog_cancel)) { _, _ ->
                reportClickWrapper.forwardCancel()
            }
            .setSingleChoiceItems(reportItems, DEFAULT_ITEM) { _, _ -> }
            .setView(commentView)
            .create()
    }

    private fun buildCommentView(context: Context): View = LayoutInflater.from(context)
        .inflate(R.layout.report_dialog_comment_view, null, false)


    /**
     * Wraps click events from the ReportDialog. Handles extracting the selected [ReportType] submitted.
     * Forwards any cancellation or submissions to the [onCancelListener] and [onSubmitListener].
     */
    private class ReportClickWrapper(
        val onSubmitListener: (ReportType?, String?) -> Unit,
        val onCancelListener: () -> Unit
    ) {

        fun forwardCancel() = onCancelListener.invoke()

        fun forwardSubmit(dialog: DialogInterface?, comment: String?) {
            (dialog as? AlertDialog)?.listView?.let {
                val checkedPosition = it.checkedItemPosition
                val reportType: ReportType? =
                    if (checkedPosition > -1 && checkedPosition < reportTypes.size) {
                        ReportType.values()[checkedPosition]
                    } else {
                        null
                    }

                onSubmitListener.invoke(reportType, comment)
            }
        }
    }
}