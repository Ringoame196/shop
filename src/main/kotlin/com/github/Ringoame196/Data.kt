package com.github.Ringoame196

import org.bukkit.inventory.Inventory

class Data {
    fun getBarrel(inventory: Inventory): Int {
        var c = 0
        for (i in 0 until inventory.size) {
            val item = inventory.getItem(i) ?: continue
            c += item.amount
        }
        return c
    }
}
