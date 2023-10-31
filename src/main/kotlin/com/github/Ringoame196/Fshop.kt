package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

class Fshop {
    fun buyGUI(player: Player, item: ItemStack, price: String, uuid: String) {
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}Fショップ:$uuid")
        gui.setItem(3, item)
        gui.setItem(4, Item().make(Material.EMERALD_BLOCK, "${ChatColor.GREEN}購入", "${price}円", null, null))
        player.openInventory(gui)
    }
    fun buy(player: Player, item: ItemStack, price: Int, title: String) {
        val uuid = title.replace("${ChatColor.BLUE}Fショップ:", "")
        val itemFrame = Bukkit.getEntity(UUID.fromString(uuid)) ?: return
        if (itemFrame !is ItemFrame) { return }
        if (itemFrame.item != item) { return }
        if ((Economy().get(player.name) ?: return) < price) {
            Player().errorMessage(player, "お金が足りません")
            return
        }
        val name = itemFrame.customName
        val owner = name?.substring(name.indexOf("userID:") + 7)
        player.inventory.addItem(item)
        Economy().remove(player.name, price)
        player.sendMessage(owner?.substring(0, owner.indexOf(",")))
        Economy().add(owner?.substring(0, owner.indexOf(",")) ?: return, price)
        itemFrame.remove()
        player.closeInventory()
        player.sendMessage("${ChatColor.GREEN}購入しました")
    }
}
