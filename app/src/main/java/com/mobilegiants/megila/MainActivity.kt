package com.mobilegiants.megila

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mobilegiants.megila.custom_views.InteractiveScrollView
import com.mobilegiants.megila.managers.AdManager
import com.mobilegiants.megila.managers.PreferencesManager
import com.mobilegiants.megila.managers.SoundEffectManager
import com.mobilegiants.megila.viewmodels.MainViewModel
import com.pushwoosh.Pushwoosh

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var interactiveScrollView: InteractiveScrollView
    private lateinit var megillaTextTv: TextView
    private lateinit var showLawsBtn: ImageButton
    private lateinit var rahashanBtn: ImageButton
    private lateinit var gunFireBtn: ImageButton
    private lateinit var granadeBtn: ImageButton
    private lateinit var purimSongsBtn: TextView
    private lateinit var blessingsTv: TextView
    private lateinit var autoScrollBtn: ImageButton

    private var soundEffects: SoundEffectManager? = null
    private val scrollHandler = Handler(Looper.getMainLooper())
    private var scrollYButton = 0
    private var onCreateTimestamp = 0L
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Pushwoosh.getInstance().registerForPushNotifications()
        onCreateTimestamp = System.currentTimeMillis()
        setContentView(R.layout.splash)
        initAd()
        setFontScale()
        initTopBar()
        setupBackHandler()
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun initTopBar() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = getColor(R.color.beige)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setFontScale()
    }

    private fun initAd() {
        AdManager.initialize(this)
        val adRequest = AdRequest.Builder().build()
        val adUnitId = AdManager.getInterstitialAdUnitId()

        InterstitialAd.load(this, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.i(TAG, "onAdLoaded")
                startInterstitialAdLogic()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d(TAG, loadAdError.toString())
                interstitialAd = null
                startInterstitialAdLogic()
            }
        })
    }

    private fun startInterstitialAdLogic() {
        if (interstitialAd != null) {
            showInterstitialAd()
        } else {
            showMainScreen()
        }
    }

    private fun showInterstitialAd() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed fullscreen content.")
                interstitialAd = null
                startInterstitialAdLogic()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Ad failed to show fullscreen content.")
                interstitialAd = null
                startInterstitialAdLogic()
            }

            override fun onAdImpression() {
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
        interstitialAd?.show(this) ?: Log.d(TAG, "The interstitial ad wasn't ready yet.")
    }

    private fun showMainScreen() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - onCreateTimestamp >= SPLASH_TIME_IN_MILLIS) {
            runOnUiThread { initMainActivity() }
        } else {
            val splashLayout: RelativeLayout = findViewById(R.id.splash_layout)
            splashLayout.postDelayed({ initMainActivity() }, currentTime - onCreateTimestamp)
        }
    }

    private fun initMainActivity() {
        setContentView(R.layout.activity_main)
        initViews()
        initSounds()
        initListeners()
        initCustomTypeface()
        initMegillaText()
    }

    private fun initViews() {
        purimSongsBtn = findViewById(R.id.purimSongsButton)
        blessingsTv = findViewById(R.id.blessingsTv)
        megillaTextTv = findViewById(R.id.myTextViewTv)
        interactiveScrollView = findViewById(R.id.myscrollview)
        showLawsBtn = findViewById(R.id.lawsBtn)
        rahashanBtn = findViewById(R.id.rahashanBtn)
        gunFireBtn = findViewById(R.id.gunfireBtn)
        granadeBtn = findViewById(R.id.grenadeBtn)
        autoScrollBtn = findViewById(R.id.autoScrollBtn)
    }

    private fun initSounds() {
        soundEffects = SoundEffectManager(this)
    }

    private fun initListeners() {
        purimSongsBtn.setOnClickListener {
            startActivity(Intent(this, SongsActivity::class.java))
        }
        blessingsTv.setOnClickListener { showBlessingsDialog() }

        interactiveScrollView.onBottomReached = {
            blessingsTv.visibility = View.VISIBLE
            scrollYButton = interactiveScrollView.scrollY
        }

        interactiveScrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = interactiveScrollView.scrollY
            if (scrollY != 0) {
                blessingsTv.visibility =
                    if (scrollYButton == 0 || scrollY < scrollYButton) View.GONE
                    else View.VISIBLE
            } else {
                blessingsTv.visibility = View.VISIBLE
            }
        }

        showLawsBtn.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        rahashanBtn.setOnClickListener { soundEffects?.playNoise() }
        gunFireBtn.setOnClickListener { soundEffects?.playGunfire() }
        granadeBtn.setOnClickListener { soundEffects?.playExplosion() }

        autoScrollBtn.setOnClickListener {
            if (viewModel.uiState.value.isAutoScrollEnabled) {
                autoScrollBtn.setImageResource(android.R.drawable.ic_media_play)
                viewModel.setAutoScroll(false)
                scrollHandler.removeCallbacksAndMessages(null)
            } else {
                initScrollSpeedDialog()
            }
        }
    }

    private fun initScrollSpeedDialog() {
        val items = resources.getStringArray(R.array.scroll_items)
        val lastCheckItem = PreferencesManager.scrollSpeed
        var checkedItem = lastCheckItem
        if (lastCheckItem != -1) checkedItem--

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.auto_scroll_dialog_title))
            .setSingleChoiceItems(items, checkedItem) { _, which ->
                val scrollRate = Character.getNumericValue(items[which][0])
                PreferencesManager.scrollSpeed = scrollRate
            }
            .setPositiveButton(getString(R.string.auto_scroll_dialog_confirm)) { _, _ ->
                val speed = PreferencesManager.scrollSpeed
                if (speed != -1) {
                    autoScrollBtn.setImageResource(android.R.drawable.ic_media_pause)
                    viewModel.setAutoScroll(true, speed)
                    initAutoScroll(speed)
                }
            }
            .setNegativeButton(getString(R.string.auto_scroll_dialog_cancel), null)
            .show()
    }

    private fun initAutoScroll(scrollRate: Int) {
        val runnable = object : Runnable {
            override fun run() {
                interactiveScrollView.smoothScrollBy(0, (SCROLL_AMOUNT * scrollRate).toInt())
                scrollHandler.postDelayed(this, 30)
            }
        }
        scrollHandler.post(runnable)
    }

    private fun showBlessingsDialog() {
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
            .setTitle(getString(R.string.blessings_dialog_title))
            .setMessage("${getString(R.string.blessings_before_the_reading)}\n\n\n${getString(R.string.blessings_after_the_reading)}")
            .setCancelable(true)
            .setPositiveButton(getString(R.string.blessings_dialog_close)) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun initCustomTypeface() {
        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.stam)
        megillaTextTv.typeface = typeface
    }

    private fun initMegillaText() {
        val megillaTextBuilder = SpannableStringBuilder(getString(R.string.first_chapter)).apply {
            append(getString(R.string.second_chapter))
            append(getString(R.string.third_chapter))
            append(getString(R.string.fourth_chapter))
            append(getString(R.string.fifth_chapter))
            append(getString(R.string.sixth_chapter))
            append(getString(R.string.seventh_chapter))
            append(getString(R.string.eighth_chapter))
            append(getString(R.string.ninth_chapter))
            append(getString(R.string.tenth_chapter))
        }
        val megillaTextOriginal = megillaTextBuilder.toString()

        // Hebrew verse numbers
        val hebrewVerseNumbers = listOf(
            "{א}", "{ב}", "{ג}", "{ד}", "{ה}", "{ו}", "{ז}", "{ח}", "{ט}", "{י}",
            "{יא}", "{יב}", "{יג}", "{יד}", "{טו}", "{טז}", "{יז}", "{יח}", "{יט}",
            "{כ}", "{כא}", "{כב}", "{כג}", "{כד}", "{כה}", "{כו}", "{כז}", "{כח}",
            "{כט}", "{ל}", "{לא}", "{לב}", "{לג}"
        )
        for (verse in hebrewVerseNumbers) {
            colorSpecificText(megillaTextOriginal, megillaTextBuilder, verse, Color.BLACK, VERSE_TEXT_SIZE)
        }

        // Chapter headers
        val chapters = listOf("פרק א׳ ", "פרק ב׳ ", "פרק ג׳ ", "פרק ד׳ ", "פרק ה׳ ",
            "פרק ו׳ ", "פרק ז׳ ", "פרק ח׳ ", "פרק ט׳ ", "פרק י׳ ")
        for (chapter in chapters) {
            colorSpecificText(megillaTextOriginal, megillaTextBuilder, chapter, Color.BLUE, CHAPTER_TEXT_SIZE)
        }

        // Haman mentions in red
        val hamanArray = resources.getStringArray(R.array.haman_array)
        for (haman in hamanArray) {
            colorSpecificText(megillaTextOriginal, megillaTextBuilder, haman, getColor(R.color.red), 0f)
        }

        // Samech marker
        colorSpecificText(megillaTextOriginal, megillaTextBuilder, "(ס)", Color.BLACK, SAMECH_TEXT_SIZE)

        // Verses said out loud
        val sayingOutLoudVerses = resources.getStringArray(R.array.saying_out_loud_verses)
        for (verse in sayingOutLoudVerses) {
            colorSpecificText(megillaTextOriginal, megillaTextBuilder, verse, Color.BLUE, 0f)
        }

        megillaTextTv.text = megillaTextBuilder
    }

    private fun colorSpecificText(
        originalText: String,
        builder: SpannableStringBuilder,
        textToColor: String,
        color: Int,
        textSize: Float
    ) {
        var startIndex = originalText.indexOf(textToColor)
        while (startIndex >= 0) {
            val endIndex = startIndex + textToColor.length
            builder.setSpan(
                ForegroundColorSpan(color), startIndex, endIndex,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (textSize != 0f) {
                builder.setSpan(RelativeSizeSpan(textSize), startIndex, endIndex, 0)
            }
            startIndex = originalText.indexOf(textToColor, endIndex)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiState.value.isAutoScrollEnabled) {
            initAutoScroll(PreferencesManager.scrollSpeed)
        }
    }

    override fun onStop() {
        super.onStop()
        if (viewModel.uiState.value.isAutoScrollEnabled) {
            scrollHandler.removeCallbacksAndMessages(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundEffects?.release()
    }

    private fun setFontScale() {
        val config = resources.configuration
        val fontScale = config.fontScale
        var newFontScale = fontScale
        if (fontScale < MIN_FONT_SCALE_SIZE) {
            newFontScale = MIN_FONT_SCALE_SIZE
        } else if (fontScale > MAX_FONT_SCALE_SIZE) {
            newFontScale = MAX_FONT_SCALE_SIZE
        }
        if (fontScale != newFontScale) {
            config.fontScale = newFontScale
            val metrics = resources.displayMetrics
            metrics.scaledDensity = config.fontScale * metrics.density
            @Suppress("DEPRECATION")
            baseContext.resources.updateConfiguration(config, metrics)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val SPLASH_TIME_IN_MILLIS = 3000L
        private const val SAMECH_TEXT_SIZE = 0.6f
        private const val VERSE_TEXT_SIZE = 0.7f
        private const val CHAPTER_TEXT_SIZE = 1.15f
        private const val SCROLL_AMOUNT = 0.5f
        private const val MIN_FONT_SCALE_SIZE = 0.5f
        private const val MAX_FONT_SCALE_SIZE = 1.1f
    }
}
