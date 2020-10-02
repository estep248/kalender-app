package com.pocket_plan.j7_003.data.shoppinglist

import com.pocket_plan.j7_003.MainActivity
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class ItemTemplateList : ArrayList<ItemTemplate>() {
    init {
        loadFromAssets()
    }

    /**
     * Returns an ItemTemplate if one was defined in the assets itemList.json file.
     * @param name The name the ItemTemplate is supposed to have.
     * @return Returns the template if found, null otherwise.
     */
    fun getTemplateByName(name: String): ItemTemplate? {

        this.forEach { e ->
            if (e.n.toLowerCase(Locale.ROOT) == name.toLowerCase(Locale.ROOT)) {
                return e
            }
        }
        return null

    }

    private fun loadFromAssets() {
        val jsonString =
            MainActivity.act.assets.open("itemList.json").bufferedReader().readText()

        val list: ArrayList<TMPTemplate> = GsonBuilder().create()
                .fromJson(jsonString, object : TypeToken<ArrayList<TMPTemplate>>() {}.type
        )

        list.forEach { e ->
            this.add(ItemTemplate(e.n, e.c, e.s))
        }
    }

    private class TMPTemplate(val n: String, val c: String, val s: String)
}

data class ItemTemplate(var n: String, var c: String, var s: String)
