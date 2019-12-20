class Helpers {
    companion object {

        private var source: AccessibilityNodeInfo? = null

        private fun selectSearchBar(context: Context, onSuccess: (() -> Unit)?) {
            if (source == null) {
                afterPauseSelectSearchBar(
                    context,
                    onSuccess
                )
                return
            }
            val searchBarList =
                (source as AccessibilityNodeInfo).findAccessibilityNodeInfosByViewId(Constants.OLA_VIEW_ID_PICKUP)
            if (searchBarList == null || searchBarList.isEmpty()) {
                afterPauseSelectSearchBar(
                    context,
                    onSuccess
                )
                return
            }
            val searchBar = searchBarList[0]
            val isSearchBarClicked =
                searchBar.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
            if (isSearchBarClicked.not()) {
                afterPauseSelectSearchBar(
                    context,
                    onSuccess
                )
                return
            }
            onSuccess?.invoke()
        }

        private fun afterPauseSelectSearchBar(context: Context, onSuccess: (() -> Unit)?) {
            AirBgTask.executeWithContext(context, {
                Thread.sleep(500)
                true
            }, {
                selectSearchBar(
                    context,
                    onSuccess
                )
            })
        }

        private fun selectHomeOpposite(context: Context, onSuccess: (() -> Unit)?) {
            selectInRv(
                context,
                Constants.HOME_OPPOSITE_STRING
            ) {
                onSuccess?.invoke()
            }
        }

        private fun selectPlatform1(context: Context, onSuccess: (() -> Unit)?) {
            selectInRv(
                context,
                Constants.PLATFORM_1_STRING
            ) {
                onSuccess?.invoke()
            }
        }

        private fun selectPlatform6(context: Context, onSuccess: (() -> Unit)?) {
            selectInRv(
                context,
                Constants.PLATFORM_6_STRING
            ) {
                onSuccess?.invoke()
            }
        }

        private fun selectHomeGate(context: Context, onSuccess: (() -> Unit)?) {
            selectInRv(
                context,
                Constants.HOME_GATE_STRING
            ) {
                onSuccess?.invoke()
            }
        }

        private fun selectInRv(context: Context, favoriteLocation: String, onSuccess: (() -> Unit)?) {
            val homeOppositeChildLayoutList =
                (source as AccessibilityNodeInfo).findAccessibilityNodeInfosByText(favoriteLocation)
            if (homeOppositeChildLayoutList == null || homeOppositeChildLayoutList.isEmpty()) {
                afterPauseSelectInRv(
                    context,
                    onSuccess
                )
                return
            }
            val homeOppositeLayout = homeOppositeChildLayoutList[0].parent
            val isHomeOppositeChildLayoutClicked = homeOppositeLayout.performAction(
                AccessibilityNodeInfoCompat.ACTION_CLICK
            )
            if (isHomeOppositeChildLayoutClicked.not()) {
                afterPauseSelectInRv(
                    context,
                    onSuccess
                )
                return
            }
            onSuccess?.invoke()
        }

        private fun afterPauseSelectInRv(context: Context, onSuccess: (() -> Unit)?) {
            AirBgTask.executeWithContext(context, {
                Thread.sleep(500)
                true
            }, {
                selectInRv(
                    context,
                    Constants.HOME_OPPOSITE_STRING,
                    onSuccess
                )
            })
        }

        private fun selectDropBar(context: Context, onSuccess: (() -> Unit)?) {
            val dropBarList = (source as AccessibilityNodeInfo).findAccessibilityNodeInfosByViewId(
                Constants.OLA_VIEW_ID_DROP
            )
            if (dropBarList == null || dropBarList.isEmpty()) {
                afterPauseSelectDropBar(
                    context,
                    onSuccess
                )
                return
            }
            val dropBar = dropBarList[0]
            val isDropBarClicked = dropBar.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
            if (isDropBarClicked.not()) {
                afterPauseSelectDropBar(
                    context,
                    onSuccess
                )
                return
            }
            onSuccess?.invoke()
        }

        private fun afterPauseSelectDropBar(context: Context, onSuccess: (() -> Unit)?) {
            AirBgTask.executeWithContext(context, {
                Thread.sleep(500)
                true
            }, {
                selectDropBar(
                    context,
                    onSuccess
                )
            })
        }

        private fun hideKeyboard(
            context: Context,
            accessibilityService: AccessibilityService,
            onSuccess: (() -> Unit)?
        ) {
            AirBgTask.executeWithContext(context, {
                Thread.sleep(2000)
                true
            }, {
                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                onSuccess?.invoke()
            })
        }

        fun openAccessibilitySettings(activity: AppCompatActivity?) {
            if (activity == null) {
                return
            }
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            activity.startActivity(intent)
        }

        fun openOlaApp(context: Context?) {
            if (context == null) {
                return
            }
            val launchIntent =
                context.packageManager.getLaunchIntentForPackage(Constants.OLA_APP_PACKAGE_NAME)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            launchIntent?.let { context.startActivity(it) }
        }

        fun setFromHomeInOla(context: Context?, accessibilityService: AccessibilityService?) {
            if (context == null || accessibilityService == null) {
                return
            }
            selectSearchBar(context) {
                hideKeyboard(context, accessibilityService) {
                    selectHomeOpposite(context) {
                        selectDropBar(context) {
                            hideKeyboard(context, accessibilityService) {
                                selectPlatform1(context) {
                                    accessibilityService.disableSelf()
                                }
                            }
                        }
                    }
                }
            }
        }

        fun setSourceFromEventIfApplicable(event: AccessibilityEvent?) {
            try {
                if (event == null) {
                    return
                }
                val source = event.source ?: return
                Helpers.source = source
            } catch (e: Exception) {
                // do nothing
            }
        }

    }
}
