package tests

import com.kaspersky.components.alluresupport.interceptors.step.ScreenshotStepInterceptor
import com.kaspersky.components.alluresupport.withForcedAllureSupport
import com.kaspersky.components.composesupport.config.addComposeSupport
import com.kaspersky.kaspresso.interceptors.watcher.testcase.impl.screenshot.ScreenshotFailStepWatcherInterceptor
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

open class BaseTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder
        .withFailureOnlyArtifacts()
        .addComposeSupport()
)

private fun Kaspresso.Builder.Companion.withFailureOnlyArtifacts(): Kaspresso.Builder =
    withForcedAllureSupport(
        shouldRecordVideo = false
    ).apply {
        stepWatcherInterceptors.removeAll {
            it is ScreenshotStepInterceptor
        }

        stepWatcherInterceptors.add(
            ScreenshotFailStepWatcherInterceptor(screenshots)
        )
    }