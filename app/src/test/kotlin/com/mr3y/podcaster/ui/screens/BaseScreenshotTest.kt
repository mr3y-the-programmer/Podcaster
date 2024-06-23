package com.mr3y.podcaster.ui.screens

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.test.FakeImageLoaderEngine
import coil3.test.intercept
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.ThresholdValidator
import com.github.takahirom.roborazzi.captureRoboImage
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.junit.Before
import org.junit.Rule
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], manifest = Config.NONE, qualifiers = RobolectricDeviceQualifiers.Pixel7)
open class BaseScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    var tolerance = 0.01f

    protected val context = ApplicationProvider.getApplicationContext<Application>()

    @Before
    fun setup() {
        SingletonImageLoader.setSafe(provideFakeImageLoader())
    }

    @OptIn(ExperimentalRoborazziApi::class)
    protected fun takeScreenshot() {
        composeRule.onRoot().captureRoboImage(
            roborazziOptions = RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(
                    resultValidator = ThresholdValidator(tolerance),
                ),
            ),
        )
    }

    private fun provideFakeImageLoader(): SingletonImageLoader.Factory {
        return SingletonImageLoader.Factory {
            val engine = FakeImageLoaderEngine.Builder()
                .intercept({ it is String }, loadTestBitmap("adb_test_image.png".toPath()))
                .build()
            ImageLoader.Builder(context)
                .components { add(engine) }
                .build()
        }
    }

    private fun loadTestBitmap(path: Path): BitmapDrawable = FileSystem.RESOURCES.read(path) {
        BitmapDrawable(context.resources, BitmapFactory.decodeStream(this.inputStream()))
    }
}

/**
 * Used to filter ScreenshotTests using -Pscreenshot parameter
 */
interface ScreenshotTests
