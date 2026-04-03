package com.example.breakfree.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.breakfree.session.SessionManager

class BreakFreeAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val session = SessionManager.activeSession.value ?: return

        if (session.isExpired) {
            SessionManager.endSession()
            return
        }

        val packageName = event.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (session.blockedApps.contains(packageName)) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    return
                }
                if (packageName == INSTAGRAM_PACKAGE) {
                    checkAndRedirectReels()
                }
            }
            // Intercept the actual tap on the Reels tab before the screen loads
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                if (packageName == INSTAGRAM_PACKAGE) {
                    val source = event.source
                    val desc = source?.contentDescription?.toString() ?: ""
                    source?.recycle()
                    if (desc.contains("reel", ignoreCase = true)) {
                        checkAndRedirectReels()
                    }
                }
            }
        }
    }

    /**
     * Recursively searches the node tree for a Reels tab that is selected,
     * then clicks the Home tab. Uses full tree traversal because Instagram's
     * nav items use content descriptions (not visible text), so
     * findAccessibilityNodeInfosByText() misses them.
     */
    private fun checkAndRedirectReels() {
        val root = rootInActiveWindow ?: return
        try {
            val reelsNodes = findNodesByContentDesc(root, "reel")
            var reelsActive = false
            for (node in reelsNodes) {
                if (node.isSelected || node.isChecked) reelsActive = true
                node.recycle()
            }

            if (reelsActive) {
                val homeNodes = findNodesByContentDesc(root, "home")
                val clicked = homeNodes.firstOrNull()
                    ?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
                homeNodes.forEach { it.recycle() }
                // Fallback: if we couldn't find Home tab, press Back
                if (!clicked) performGlobalAction(GLOBAL_ACTION_BACK)
            }
        } finally {
            root.recycle()
        }
    }

    /**
     * Walks the entire accessibility tree and collects nodes whose
     * contentDescription or text contains [query] (case-insensitive).
     * Callers are responsible for recycling returned nodes.
     */
    private fun findNodesByContentDesc(
        root: AccessibilityNodeInfo,
        query: String
    ): List<AccessibilityNodeInfo> {
        val results = mutableListOf<AccessibilityNodeInfo>()
        fun traverse(node: AccessibilityNodeInfo) {
            val desc = node.contentDescription?.toString() ?: ""
            val text = node.text?.toString() ?: ""
            if (desc.contains(query, ignoreCase = true) || text.contains(query, ignoreCase = true)) {
                results.add(AccessibilityNodeInfo.obtain(node))
            }
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                traverse(child)
                child.recycle()
            }
        }
        traverse(root)
        return results
    }

    override fun onInterrupt() {}

    companion object {
        const val INSTAGRAM_PACKAGE = "com.instagram.android"

        fun isEnabled(context: Context): Boolean {
            val service =
                "${context.packageName}/${BreakFreeAccessibilityService::class.java.canonicalName}"
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            val splitter = TextUtils.SimpleStringSplitter(':')
            splitter.setString(enabledServices)
            while (splitter.hasNext()) {
                if (splitter.next().equals(service, ignoreCase = true)) return true
            }
            return false
        }
    }
}
