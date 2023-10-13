package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Barrel
import org.bukkit.block.Sign
import java.util.UUID

class Shop {
    fun make(barrel: Barrel, player: org.bukkit.entity.Player, sign: Sign) {
        val item = player.inventory.itemInMainHand
        for (i in 0 until barrel.inventory.size) {
            val barrelItem = barrel.inventory.getItem(i)
            if (barrelItem == null || barrelItem.type == Material.AIR) { continue }
            Player().errorMessage(player, "${ChatColor.RED}樽の中身を空にしてください")
            return
        }
        sign.setLine(0, "${ChatColor.GREEN}shop")
        sign.setLine(1, if (sign.getLine(1) == "s") { "${ChatColor.RED}買い取り" } else { "${ChatColor.AQUA}販売" })
        sign.setLine(2, "${ChatColor.YELLOW}${sign.getLine(2)}円")
        val merchandise = if (item.itemMeta?.displayName != "") { item.itemMeta?.displayName } else { item.type }
        sign.setLine(3, "${ChatColor.GOLD}$merchandise")
        barrel.customName = "${player.uniqueId}"
        barrel.update()
        sign.update()
        val playerItem = item.clone()
        playerItem.amount = 1
        barrel.inventory.addItem(playerItem.clone())
        player.inventory.removeItem(playerItem)
        player.sendMessage("${ChatColor.AQUA}ショップを設置しました")
    }
    fun purchase(player: org.bukkit.entity.Player, barrel: Barrel, price: Int) {
        val uuid = barrel.customName ?: return
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).player
        if ((Economy().get(owner ?: return) ?: return) < price) {
            Player().errorMessage(player, "${ChatColor.RED}買取不可")
            return
        }
        if (Data().getBarrel(barrel.inventory) == 1728) {
            Player().errorMessage(player, "買取不可")
            return
        }
        Economy().remove(owner, price)
        val item = player.inventory.itemInMainHand
        val playerItem = item.clone()
        val merchandise = barrel.inventory.getItem(0)?.clone()
        playerItem.amount = 1
        merchandise?.amount = 1
        if (merchandise != playerItem) {
            Player().errorMessage(player, "アイテムが一致しませんでした")
            return
        }
        barrel.inventory.addItem(playerItem)
        player.inventory.removeItem(playerItem)
        Economy().add(player, price)
    }
}
