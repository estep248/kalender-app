package com.pocket_plan.j7_003.data.settings.sub_categories

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.pocket_plan.j7_003.MainActivity
import com.pocket_plan.j7_003.R
import com.pocket_plan.j7_003.data.settings.SettingId
import com.pocket_plan.j7_003.data.settings.SettingsManager
import kotlinx.android.synthetic.main.fragment_settings_appearance.view.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsAppearanceFr(mainActivity: MainActivity) : Fragment() {
    private val myActivity = mainActivity
    private lateinit var spTheme: Spinner
    private lateinit var spShapes: Spinner
    private lateinit var spLanguages: Spinner

    private lateinit var swShakeTaskInHome: SwitchCompat
    private lateinit var swSystemTheme: SwitchCompat

    private lateinit var clResetToDefault: ConstraintLayout

    private var initialDisplay: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_settings_appearance, container, false)

        initializeComponents(myView)
        initializeAdapters()
        initializeDisplayValues()
        initializeListeners()

        return myView
    }

    private fun initializeComponents(myView: View) {

        //initialize references to view
        //spinners
        spTheme = myView.spTheme
        spShapes = myView.spShapes
        spLanguages = myView.spLanguages

        //switches
        swShakeTaskInHome = myView.swShakeTaskInHome
        swSystemTheme = myView.swSystemTheme

        //ConstraintLayouts
        clResetToDefault = myView.clResetToDefault
    }

    private fun initializeAdapters() {
        //Spinner for color theme
        val spAdapterTheme = ArrayAdapter(
            myActivity,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.themes)
        )
        spAdapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTheme.adapter = spAdapterTheme

        //Spinner for shapes
        val spAdapterShapes = ArrayAdapter(
            myActivity,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.shapes)
        )
        spAdapterShapes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spShapes.adapter = spAdapterShapes

        //Spinner for languages
        val spAdapterLanguages = ArrayAdapter(
            myActivity,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.languages)
        )
        spAdapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLanguages.adapter = spAdapterLanguages
    }

    private fun initializeDisplayValues() {
        val spThemePosition = when(SettingsManager.getSetting(SettingId.THEME_DARK)){
            //show "dark" setting
            true -> 0
            //show "light" setting
            else -> 1
        }
        spTheme.setSelection(spThemePosition)

        val shapePosition = when(SettingsManager.getSetting(SettingId.SHAPES_ROUND)){
            //show "round" setting
            true -> 1
            //show "normal" setting
            else -> 0
        }
        spShapes.setSelection(shapePosition)

        spLanguages.setSelection(
            when (SettingsManager.getSetting(SettingId.LANGUAGE)) {
                //0 = english
                0.0 -> 0
                //1 = german
                else -> 1
            }
        )
        swShakeTaskInHome.isChecked = SettingsManager.getSetting(SettingId.SHAKE_TASK_HOME) as Boolean
        swSystemTheme.isChecked = SettingsManager.getSetting(SettingId.USE_SYSTEM_THEME) as Boolean
    }

    private fun initializeListeners() {
        spLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val setTo = when(spLanguages.selectedItemPosition){
                    0 -> 0.0
                    else -> 1.0
                }
                if(setTo!=SettingsManager.getSetting(SettingId.LANGUAGE)){
                    SettingsManager.addSetting(SettingId.LANGUAGE, setTo)
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("NotificationEntry", "appearance")
                    startActivity(intent)
                    myActivity.finish()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //Listener for theme spinner
        spTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(initialDisplay){
                    initialDisplay = false
                    return
                }

                //check if selected theme is dark theme (dark is position 0, light is 1)
                val selectedDarkTheme = spTheme.selectedItemPosition==0

                //check if use system theme is set and if current change does not conform to system theme
                //if yes, disable "use system theme"
                if(SettingsManager.getSetting(SettingId.USE_SYSTEM_THEME) as Boolean){
                    val systemDark = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                    //check if systemDarkState not equal to selected dark state
                    if(systemDark != selectedDarkTheme)
                        SettingsManager.addSetting(SettingId.USE_SYSTEM_THEME, false)
                        swSystemTheme.isChecked = false
                    }

                //check if selected dark state is equal to current dark state
                if(selectedDarkTheme != SettingsManager.getSetting(SettingId.THEME_DARK)){
                    SettingsManager.addSetting(SettingId.THEME_DARK, selectedDarkTheme)
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("NotificationEntry", "appearance")
                    startActivity(intent)
                    myActivity.finish()
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        //Listener for shape spinner
        spShapes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                SettingsManager.addSetting(SettingId.SHAPES_ROUND, spShapes.selectedItemPosition==1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        swShakeTaskInHome.setOnClickListener {
            SettingsManager.addSetting(SettingId.SHAKE_TASK_HOME, swShakeTaskInHome.isChecked)
        }

        swSystemTheme.setOnClickListener {
            SettingsManager.addSetting(SettingId.USE_SYSTEM_THEME, swSystemTheme.isChecked)

            //use system theme got disabled, current theme will stay activated
            if(!swSystemTheme.isChecked){
                return@setOnClickListener
            }

            val previousSettingDark = SettingsManager.getSetting(SettingId.THEME_DARK) as Boolean

           //use system theme got enabled, check if system uses night mode
            when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                //system uses night mode, add required setting
                true -> SettingsManager.addSetting(SettingId.THEME_DARK, true)

                //system does not use night mode, add required setting
                else -> SettingsManager.addSetting(SettingId.THEME_DARK, false)
            }

            //if theme got changed, trigger activity reload to load new theme
            if(previousSettingDark != SettingsManager.getSetting(SettingId.THEME_DARK) as Boolean){
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("NotificationEntry", "appearance")
                startActivity(intent)
                myActivity.finish()
            }

        }

        clResetToDefault.setOnClickListener {
            val action: () -> Unit = {
                SettingsManager.settings.clear()
                myActivity.loadDefaultSettings()
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("NotificationEntry", "appearance")
                startActivity(intent)
                myActivity.finish()
            }
            myActivity.dialogConfirmDelete(R.string.titleRestoreSettings, action)
        }

    }
}
