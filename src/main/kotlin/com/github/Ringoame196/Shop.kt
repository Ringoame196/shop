package com.github.Ringoame196

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Barrel
import org.bukkit.block.Sign
import org.bukkit.inventory.ItemStack

class Shop {
    fun make(barrel: Barrel, player: org.bukkit.entity.Player, sign: Sign) {
        if (player.world.name != "shop" && player.world.name != "Home" && !player.isOp) { return }
        val item = player.inventory.itemInMainHand
        for (i in 0 until barrel.inventory.size) {
            val barrelItem = barrel.inventory.getItem(i)
            if (barrelItem == null || barrelItem.type == Material.AIR) { continue }
            Player().errorMessage(player, "${ChatColor.RED}樽の中身を空にしてください")
            return
        }
        sign.setLine(0, "${ChatColor.GREEN}shop")
        sign.setLine(1, if (sign.getLine(1) == "s") { "${ChatColor.RED}買取" } else { "${ChatColor.AQUA}販売" })
        sign.setLine(2, "${ChatColor.YELLOW}${sign.getLine(2)}円")
        val merchandise = if (item.itemMeta?.displayName != "") { item.itemMeta?.displayName } else { item.type }
        sign.setLine(3, "${ChatColor.GOLD}$merchandise")
        barrel.customName = "${player.name}@shop"
        barrel.update()
        sign.update()
        val playerItem = item.clone()
        playerItem.amount = 1
        barrel.inventory.addItem(playerItem.clone())
        player.inventory.removeItem(playerItem)
        player.sendMessage("${ChatColor.AQUA}ショップを設置しました")
    }
    fun sell(player: org.bukkit.entity.Player, barrel: Barrel, price: Int, count: Int) {
        val playerName = barrel.customName?.replace("@shop", "")?.replace("@adminshop", "") ?: return
        if ((Economy().get(playerName) ?: return) <= price) {
            Player().errorMessage(player, "${ChatColor.RED}買取不可")
            return
        }
        if (Data().getBarrel(barrel.inventory) > (1728 - count)) {
            Player().errorMessage(player, "買取不可")
            return
        }
        val item = player.inventory.itemInMainHand
        val playerItem = item.clone()
        val merchandise = barrel.inventory.getItem(0)?.clone()
        playerItem.amount = 1
        merchandise?.amount = 1
        if (merchandise != playerItem) {
            Player().errorMessage(player, "アイテムが一致しませんでした")
            return
        }
        playerItem.amount = count
        if (!barrel.customName!!.contains("@adminshop")) {
            barrel.inventory.addItem(playerItem)
        }
        player.inventory.removeItem(playerItem)
        Economy().remove(playerName, price)
        Economy().add(player.name, price)
    }
    fun purchase(player: org.bukkit.entity.Player, barrel: Barrel, price: Int) {
        if ((Economy().get(player.name) ?: return) < price) {
            Player().errorMessage(player, "お金が足りません")
            return
        }
        if (Data().getBarrel(barrel.inventory) == 1) {
            Player().errorMessage(player, "買取不可")
            return
        }
        val gui = Bukkit.createInventory(null, 9, "${ChatColor.RED}ショップ購入:${barrel.location.x},${barrel.location.y},${barrel.location.z}")
        for (i in 0 until gui.size) {
            gui.setItem(i, Item().make(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " ", null, null, null))
        }
        val item = barrel.inventory.getItem(0)?.clone() ?: return
        item.amount = 1
        gui.setItem(1, item)
        gui.setItem(4, Item().make(Material.EMERALD, "${ChatColor.GREEN}購入", "${price}円", null, 1))
        gui.setItem(5, Item().make(Material.EMERALD, "${ChatColor.GREEN}購入", "${price}円", null, 32))
        gui.setItem(6, Item().make(Material.EMERALD, "${ChatColor.GREEN}購入", "${price}円", null, 64))
        player.openInventory(gui)
    }
    fun buy(player: org.bukkit.entity.Player, price: Int, barrel: Barrel, count: Int) {
        if (Data().getBarrel(barrel.inventory) <= count) {
            Player().errorMessage(player, "買取不可")
            player.closeInventory()
            return
        }
        val playerName = barrel.customName?.replace("@shop", "")?.replace("@adminshop", "") ?: return
        Economy().remove(player.name, price)
        Economy().add(playerName, price)
        val merchandise = barrel.inventory.getItem(0)?.clone()
        merchandise?.amount = count
        barrelItemRemove(barrel, count)
        player.inventory.addItem(merchandise)
    }
    private fun barrelItemRemove(barrel: Barrel, count: Int) {
        val barrelInventory = barrel.inventory

        for (c in 0 until count) {
            for (i in barrelInventory.size - 1 downTo 0) {
                val item = barrelInventory.getItem(i)
                if (item != null && item.type != Material.AIR) {
                    // 最後のアイテムを1つ減らす
                    val amount = item.amount
                    if (amount > 1) {
                        item.amount = amount - 1
                        break
                    } else {
                        // アイテムが1つだけ残っている場合、空にする
                        barrelInventory.setItem(i, ItemStack(Material.AIR))
                        break
                    }
                }
            }
        }
    }
}
