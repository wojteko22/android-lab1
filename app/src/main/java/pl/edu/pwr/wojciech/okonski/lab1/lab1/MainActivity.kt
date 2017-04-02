package pl.edu.pwr.wojciech.okonski.lab1.lab1

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ShareActionProvider
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.edu.pwr.wojciech.okonski.lab1.lab1.R.array.units
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private var bmiCounter: BmiCounter by Delegates.notNull()
    private var shareActionProvide: ShareActionProvider by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setSpinner()
        btnCount.setOnClickListener { displayBmiStuff() }
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, units, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun displayBmiStuff() {
        hideSoftKeyboard(this)
        val massString = etMass.text.toString()
        val heightString = etHeight.text.toString()
        tryToCalculateBMI(massString, heightString)
        invalidateOptionsMenu()
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }

    private fun tryToCalculateBMI(massString: String, heightString: String) {
        try {
            tryToCalculateBMI(massString.toFloat(), heightString.toFloat())
        } catch (e: NumberFormatException) {
            handleInvalidInput()
        }
    }

    private fun tryToCalculateBMI(mass: Float, height: Float) {
        setBmiCounter()
        try {
            calculateBmi(mass, height)
        } catch(e: IllegalArgumentException) {
            handleInvalidInput()
        }
    }

    private fun setBmiCounter() {
        bmiCounter =
                if (isKgMSelected())
                    KgMBmiCounter()
                else
                    LbInBmiCounter()
    }

    private fun isKgMSelected() = spinner.selectedItemPosition == 0

    private fun calculateBmi(mass: Float, height: Float) {
        val bmi = bmiCounter.calculateBMI(mass, height).toString()
        tvBmiResult.text = bmi
        setFireWorks(bmi.toFloat())
    }

    private fun setFireWorks(bmi: Float) {
        when (bmi) {
            in 30f..Float.POSITIVE_INFINITY -> {
                setDescription(R.string.obese)
                setColor(Color.RED)
            }
            in 25..30 -> {
                setDescription(R.string.overweight)
                setColor(Color.MAGENTA)
            }
            in 18.5f..25f -> {
                setDescription(R.string.normalBmi)
                setColor(Color.GREEN)
            }
            in 16.5f..18.5f -> {
                setDescription(R.string.underweight)
                setColor(Color.MAGENTA)
            }
            else -> {
                setDescription(R.string.seriouslyUnderweight)
                setColor(Color.RED)
            }
        }
    }

    private fun setDescription(stringId: Int) {
        tvDescription.text = resources.getString(stringId)
    }

    private fun setColor(color: Int) {
        tvBmiResult.setTextColor(color)
    }

    private fun handleInvalidInput() {
        cleanTextIn(tvBmiResult)
        setDescription(R.string.invalidInput)
    }

    private fun cleanTextIn(textView: TextView) {
        textView.text = ""
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val textColor = savedInstanceState.getInt(COLOR_KEY)
        tvBmiResult.setTextColor(textColor)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val textColor = tvBmiResult.textColors.defaultColor
        outState.putInt(COLOR_KEY, textColor)
    }

    companion object {
        val COLOR_KEY = "COLOR"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        findShareActionProvider(menu)
        prepareSharingIntent()
        return true
    }

    private fun findShareActionProvider(menu: Menu) {
        val shareItem = menu.findItem(R.id.share)
        shareActionProvide = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
    }

    private fun prepareSharingIntent() {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        sendIntent.type = "text/plain"
        shareActionProvide.setShareIntent(sendIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.author -> {
                startAuthorActivity()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startAuthorActivity() {
        val intent = Intent(this, AuthorActivity::class.java)
        startActivity(intent)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        showShareItemIfBmiIsCounted(menu)
        return true
    }

    private fun showShareItemIfBmiIsCounted(menu: Menu) {
        menu.findItem(R.id.share).isVisible = tvBmiResult.text.isNotEmpty()
    }
}