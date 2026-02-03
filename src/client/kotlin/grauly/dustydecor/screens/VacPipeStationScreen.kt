package grauly.dustydecor.screens

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.blockentity.vac_station.CopperGolemMode
import grauly.dustydecor.blockentity.vac_station.RedstoneEmissionMode
import grauly.dustydecor.blockentity.vac_station.SendMode
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.GOLEM_MODE
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.REDSTONE_MODE
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.SEND_MODE
import grauly.dustydecor.screen.VacPipeStationScreenHandler
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerListener
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

abstract class VacPipeStationScreen<T : VacPipeStationScreenHandler<*>>(
    handler: T,
    inventory: Inventory,
    title: Component,
    private val texture: Identifier,
) : AbstractContainerScreen<T>(handler, inventory, title) {
    private lateinit var golemModeButton: ImageCyclingButtonWidget<CopperGolemMode>
    private lateinit var redstoneModeButton: ImageCyclingButtonWidget<RedstoneEmissionMode>
    private lateinit var sendingModeButton: ImageCyclingButtonWidget<SendMode>
    private val updateListener: ContainerListener = object : ContainerListener {
        override fun slotChanged(
            handler: AbstractContainerMenu,
            slotId: Int,
            stack: ItemStack
        ) {
            // [Space intentionally left blank]
        }

        override fun dataChanged(
            handler: AbstractContainerMenu,
            property: Int,
            value: Int
        ) {
            when (property) {
                GOLEM_MODE -> {
                    golemModeButton.setValue(CopperGolemMode.entries[value])
                }
                REDSTONE_MODE -> {
                    redstoneModeButton.setValue(RedstoneEmissionMode.entries[value])
                }
                SEND_MODE -> {
                    sendingModeButton.setValue(SendMode.entries[value])
                }
            }
        }

    }

    init {
        imageWidth = 176
        imageHeight = 189
        inventoryLabelY = this.imageHeight - 94
        handler.addSlotListener(updateListener)
    }

    override fun init() {
        super.init()
        golemModeButton = addRenderableWidget(
            ImageCyclingButtonWidget(
                leftPos + 96, topPos + 74,
                18, 18,
                Component.translatable(COPPER_GOLEM_MODE_TRANSLATION_KEY),
                listOf(
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        CopperGolemMode.INTERACT,
                        Items.COPPER_GOLEM_STATUE.defaultInstance,
                        Component.translatable(COPPER_GOLEM_MODE_TRANSLATION_KEY_INTERACT),
                        Component.translatable(COPPER_GOLEM_MODE_TRANSLATION_KEY_INTERACT_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        CopperGolemMode.IGNORE,
                        Items.BARRIER.defaultInstance,
                        Component.translatable(COPPER_GOLEM_MODE_TRANSLATION_KEY_IGNORE),
                        Component.translatable(COPPER_GOLEM_MODE_TRANSLATION_KEY_IGNORE_NARRATION)
                    )
                )
            ) { button, mode ->
                sendPropertyUpdate(GOLEM_MODE, mode.ordinal)
            }
        )

        redstoneModeButton = addRenderableWidget(
            ImageCyclingButtonWidget(
                leftPos + 122, topPos + 74,
                18, 18,
                Component.translatable(REDSTONE_MODE_TRANSLATION_KEY),
                listOf(
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        RedstoneEmissionMode.ON_SEND,
                        Items.OAK_BUTTON.defaultInstance,
                        Component.translatable(REDSTONE_MODE_TRANSLATION_KEY_ON_SEND),
                        Component.translatable(REDSTONE_MODE_TRANSLATION_KEY_ON_SEND_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        RedstoneEmissionMode.ON_RECEIVE,
                        Items.TARGET.defaultInstance,
                        Component.translatable(REDSTONE_MODE_TRANSLATION_KEY_ON_RECEIVE),
                        Component.translatable(REDSTONE_MODE_TRANSLATION_KEY_ON_RECEIVE_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        RedstoneEmissionMode.WHILE_EMPTY,
                        Items.GLASS_BOTTLE.defaultInstance,
                        Component.translatable(REDSTONE_MODE_TRANSLATION_KEY_WHILE_EMPTY),
                        Component.translatable(REDSTONE_MODE_TRANSLATION_KEY_WHILE_EMPTY_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        RedstoneEmissionMode.NONE,
                        Items.BARRIER.defaultInstance,
                        Component.translatable(REDSTONE_MODE_TRANSLATION_KEY_NONE),
                        Component.translatable(REDSTONE_MODE_TRANSLATION_KEY_NONE_NARRATION)
                    )
                )
            ) { button, mode ->
                sendPropertyUpdate(REDSTONE_MODE, mode.ordinal)
            }
        )

        sendingModeButton = addRenderableWidget(
            ImageCyclingButtonWidget(
                leftPos + 144, topPos + 74,
                18, 18,
                Component.translatable(SENDING_MODE_TRANSLATION_KEY),
                listOf(
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        SendMode.MANUAL,
                        Items.OAK_BUTTON.defaultInstance,
                        Component.translatable(SENDING_MODE_TRANSLATION_KEY_MANUAL),
                        Component.translatable(SENDING_MODE_TRANSLATION_KEY_MANUAL_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        SendMode.ON_REDSTONE,
                        Items.REDSTONE.defaultInstance,
                        Component.translatable(SENDING_MODE_TRANSLATION_KEY_REDSTONE),
                        Component.translatable(SENDING_MODE_TRANSLATION_KEY_REDSTONE_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        SendMode.AUTOMATIC,
                        Items.PISTON.defaultInstance,
                        Component.translatable(SENDING_MODE_TRANSLATION_KEY_AUTOMATIC),
                        Component.translatable(SENDING_MODE_TRANSLATION_KEY_AUTOMATIC_NARRATION)
                    ),
                )
            ) { button, mode ->
                sendPropertyUpdate(SEND_MODE, mode.ordinal)
            }
        )

        golemModeButton.setValue(menu.getGolemMode())
        redstoneModeButton.setValue(menu.getRedstoneMode())
        sendingModeButton.setValue(menu.getSendingMode())
    }

    private fun sendPropertyUpdate(property: Int, value: Int) {
        Minecraft.getInstance().gameMode?.handleInventoryButtonClick(menu.containerId, property * 10 + value)
    }

    override fun containerTick() {
        super.containerTick()
        golemModeButton.setValue(menu.getGolemMode())
        redstoneModeButton.setValue(menu.getRedstoneMode())
        sendingModeButton.setValue(menu.getSendingMode())
    }

    override fun renderBg(context: GuiGraphics, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
            leftPos,
            topPos,
            0f,
            0f,
            imageWidth,
            imageHeight,
            256,
            256
        )
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        super.render(context, mouseX, mouseY, deltaTicks)
        renderTooltip(context, mouseX, mouseY)
    }

    companion object {
        val SEND_TEXTURE: Identifier =
            Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "textures/gui/container/vac_pipe_station_send.png")
        val RECEIVE_TEXTURE: Identifier =
            Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "textures/gui/container/vac_pipe_station_receive.png")

        const val COPPER_GOLEM_MODE_TRANSLATION_KEY: String = "screen.${DustyDecorMod.MODID}.vac_pipe_station.mode.copper_golem"
        const val COPPER_GOLEM_MODE_TRANSLATION_KEY_INTERACT: String = "$COPPER_GOLEM_MODE_TRANSLATION_KEY.interact"
        const val COPPER_GOLEM_MODE_TRANSLATION_KEY_INTERACT_NARRATION: String = "$COPPER_GOLEM_MODE_TRANSLATION_KEY_INTERACT.narration"
        const val COPPER_GOLEM_MODE_TRANSLATION_KEY_IGNORE: String = "$COPPER_GOLEM_MODE_TRANSLATION_KEY.ignore"
        const val COPPER_GOLEM_MODE_TRANSLATION_KEY_IGNORE_NARRATION: String = "$COPPER_GOLEM_MODE_TRANSLATION_KEY_IGNORE.narration"

        const val REDSTONE_MODE_TRANSLATION_KEY: String = "screen.${DustyDecorMod.MODID}.vac_pipe_station.mode.redstone"
        const val REDSTONE_MODE_TRANSLATION_KEY_ON_SEND: String = "$REDSTONE_MODE_TRANSLATION_KEY.on_send"
        const val REDSTONE_MODE_TRANSLATION_KEY_ON_SEND_NARRATION: String = "$REDSTONE_MODE_TRANSLATION_KEY_ON_SEND.narration"
        const val REDSTONE_MODE_TRANSLATION_KEY_ON_RECEIVE: String = "$REDSTONE_MODE_TRANSLATION_KEY.on_arrival"
        const val REDSTONE_MODE_TRANSLATION_KEY_ON_RECEIVE_NARRATION: String = "$REDSTONE_MODE_TRANSLATION_KEY_ON_RECEIVE.narration"
        const val REDSTONE_MODE_TRANSLATION_KEY_WHILE_EMPTY: String = "$REDSTONE_MODE_TRANSLATION_KEY.while_empty"
        const val REDSTONE_MODE_TRANSLATION_KEY_WHILE_EMPTY_NARRATION: String = "$REDSTONE_MODE_TRANSLATION_KEY_WHILE_EMPTY.narration"
        const val REDSTONE_MODE_TRANSLATION_KEY_NONE: String = "$REDSTONE_MODE_TRANSLATION_KEY.none"
        const val REDSTONE_MODE_TRANSLATION_KEY_NONE_NARRATION: String = "$REDSTONE_MODE_TRANSLATION_KEY_NONE.narration"

        const val SENDING_MODE_TRANSLATION_KEY: String = "screen.${DustyDecorMod.MODID}.vac_pipe_station.mode.sending"
        const val SENDING_MODE_TRANSLATION_KEY_MANUAL: String = "$SENDING_MODE_TRANSLATION_KEY.manual"
        const val SENDING_MODE_TRANSLATION_KEY_MANUAL_NARRATION: String = "$SENDING_MODE_TRANSLATION_KEY_MANUAL.narration"
        const val SENDING_MODE_TRANSLATION_KEY_REDSTONE: String = "$SENDING_MODE_TRANSLATION_KEY.redston"
        const val SENDING_MODE_TRANSLATION_KEY_REDSTONE_NARRATION: String = "$SENDING_MODE_TRANSLATION_KEY_REDSTONE.narration"
        const val SENDING_MODE_TRANSLATION_KEY_AUTOMATIC: String = "$SENDING_MODE_TRANSLATION_KEY.automatic"
        const val SENDING_MODE_TRANSLATION_KEY_AUTOMATIC_NARRATION: String = "$SENDING_MODE_TRANSLATION_KEY_AUTOMATIC.narration"
    }
}