package com.mr3y.podcaster.ui.screens

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
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

    protected val context = ApplicationProvider.getApplicationContext<Application>()

    @Before
    fun setup() {
        Coil.setImageLoader(provideFakeImageLoader())
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun provideFakeImageLoader(): ImageLoader {
        val engine = FakeImageLoaderEngine.Builder()
            .intercept({ it is String }, loadTestBitmap("adb_test_image.png".toPath()))
            .build()
        return ImageLoader.Builder(context)
            .components { add(engine) }
            .build()
    }

    private fun loadTestBitmap(path: Path): BitmapDrawable = FileSystem.RESOURCES.read(path) {
        BitmapDrawable(context.resources, BitmapFactory.decodeStream(this.inputStream()))
    }
}
