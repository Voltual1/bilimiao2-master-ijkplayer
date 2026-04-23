import cn.a10miaomiao.bilimiao.build.*
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
    id("bilimiao-build")
}

android {
    namespace = "com.a10miaomiao.bilimiao"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.a10miaomiao.bilimiao"
        minSdk = 21
        targetSdk = 34
        versionCode = 106
        versionName = "2.3.11"

        flavorDimensions.add("default")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "armeabi", "x86", "x86_64"))
        }
    }

    // 签名配置：优先使用环境变量，否则从 signing.properties 读取
    val keystorePathEnv = System.getenv("KEYSTORE_PATH")
    val keystorePasswordEnv = System.getenv("KEYSTORE_PASSWORD")
    val keyAliasEnv = System.getenv("KEY_ALIAS")
    val keyPasswordEnv = System.getenv("KEY_PASSWORD")

    val signingPropsFile = file("signing.properties")
    val signingProps = Properties().apply {
        if (signingPropsFile.exists()) {
            load(FileInputStream(signingPropsFile))
        }
    }

    signingConfigs {
        create("miao") {
            storeFile = when {
                keystorePathEnv != null -> file(keystorePathEnv)
                signingProps.containsKey("KEYSTORE_FILE") -> file(signingProps.getProperty("KEYSTORE_FILE"))
                else -> null
            }
            storePassword = keystorePasswordEnv ?: signingProps.getProperty("KEYSTORE_PASSWORD")
            keyAlias = keyAliasEnv ?: signingProps.getProperty("KEY_ALIAS")
            keyPassword = keyPasswordEnv ?: signingProps.getProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "bilimiao dev")
            manifestPlaceholders["channel"] = "Development"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getOrNull("miao")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
            isDebuggable = false
        }
    }

    productFlavors {
        create("full") {
            dimension = flavorDimensions[0]
            val channelName = project.properties["channel"] ?: "Unknown"
            manifestPlaceholders["channel"] = channelName.toString()
        }
        create("foss") {
            dimension = flavorDimensions[0]
            manifestPlaceholders["channel"] = "FOSS"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {
    implementation(Libraries.core)
    implementation(Libraries.appcompat)
    implementation(Libraries.material)
    implementation(Libraries.lifecycle)
    implementation(Libraries.lifecycleViewModel)
    implementation(Libraries.navigationFragment)
    implementation(Libraries.navigationUi)
    implementation(Libraries.datastore)
    implementation(Libraries.media)
    implementation(Libraries.browser)
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")

    implementation(Libraries.kotlinxCoroutinesAndroid)
    implementation(Libraries.kodeinDi)

    implementation(Libraries.recyclerview)
    implementation(Libraries.baseRecyclerViewAdapterHelper)
    implementation(Libraries.swiperefreshlayout)
    implementation(Libraries.flexbox)
    implementation(Libraries.foregroundCompat)
    implementation(Libraries.drawer)
    implementation(Libraries.dialogX) {
        exclude("com.github.kongzue.DialogX", "DialogXInterface")
    }
    implementation(Libraries.zxingLite)

    implementationSplitties()
    implementationMojito()

    implementation(Libraries.media3)
    implementation(Libraries.media3Session)
    implementation(Libraries.media3Decoder)
    implementation(Libraries.media3Ui)
    implementation(Libraries.media3ExoPlayer)
    implementation(Libraries.media3ExoPlayerDash)
    implementation(Libraries.gsyVideoPlayer)

    implementation(Libraries.gson)
    implementation(Libraries.okhttp3)
    implementation(Libraries.pbandkRuntime)
    implementation(Libraries.glide)
    annotationProcessor(Libraries.glideCompiler)

    implementation(project(":bilimiao-comm"))
    implementation(project(":bilimiao-download"))
    implementation(project(":bilimiao-cover"))
    implementation(project(":bilimiao-compose"))
    implementation(project(":miao-binding"))
    implementation(project(":miao-binding-android"))
    implementation(project(":DanmakuFlameMaster"))

    "fullImplementation"(Libraries.baiduMobstat)
    "fullImplementation"(Libraries.sensebot)
    "fullImplementation"(files("libs/lib-decoder-av1-release.aar"))

    testImplementation(Libraries.junit)
    androidTestImplementation(Libraries.androidxJunit)
    androidTestImplementation(Libraries.espresso)
}