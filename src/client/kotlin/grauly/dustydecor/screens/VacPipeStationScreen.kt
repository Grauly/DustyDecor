package grauly.dustydecor.screens

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.blockentity.vac_station.CopperGolemMode
import grauly.dustydecor.blockentity.vac_station.RedstoneEmissionMode
import grauly.dustydecor.blockentity.vac_station.SendMode
import grauly.dustydecor.packet.UpdateVacPipeStationScreenHandlerPropertiesC2SPacket
import grauly.dustydecor.screen.VacPipeStationScreenHandler
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Identifier

abstract class VacPipeStationScreen<T : VacPipeStationScreenHandler<*>>(
    handler: T,
    inventory: PlayerInventory?,
    title: Text?,
    private val texture: Identifier,
) : HandledScreen<T>(handler, inventory, title) {
    private lateinit var golemModeButton: ImageCyclingButtonWidget<CopperGolemMode>
    private lateinit var redstoneModeButton: ImageCyclingButtonWidget<RedstoneEmissionMode>
    private lateinit var sendingModeButton: ImageCyclingButtonWidget<SendMode>

    init {
        backgroundWidth = 176
        backgroundHeight = 189
        playerInventoryTitleY = this.backgroundHeight - 94
    }

    override fun init() {
        super.init()
        golemModeButton = addDrawableChild(
            ImageCyclingButtonWidget(
                x + 96, y + 74,
                18, 18,
                Text.translatable(COPPER_GOLEM_MODE),
                listOf(
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        CopperGolemMode.INTERACT,
                        Items.COPPER_GOLEM_STATUE.defaultStack,
                        Text.translatable(COPPER_GOLEM_MODE_INTERACT),
                        Text.translatable(COPPER_GOLEM_MODE_INTERACT_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        CopperGolemMode.IGNORE,
                        Items.BARRIER.defaultStack,
                        Text.translatable(COPPER_GOLEM_MODE_IGNORE),
                        Text.translatable(COPPER_GOLEM_MODE_IGNORE_NARRATION)
                    )
                )
            ) { button, mode -> sendPropertyUpdate(0, mode.ordinal) }
        )

        redstoneModeButton = addDrawableChild(
            ImageCyclingButtonWidget(
                x + 122, y + 74,
                18, 18,
                Text.translatable(REDSTONE_MODE),
                listOf(
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        RedstoneEmissionMode.ON_SEND,
                        Items.OAK_BUTTON.defaultStack,
                        Text.translatable(REDSTONE_MODE_ON_SEND),
                        Text.translatable(REDSTONE_MODE_ON_SEND_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        RedstoneEmissionMode.ON_RECEIVE,
                        Items.TARGET.defaultStack,
                        Text.translatable(REDSTONE_MODE_ON_RECEIVE),
                        Text.translatable(REDSTONE_MODE_ON_RECEIVE_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        RedstoneEmissionMode.WHILE_EMPTY,
                        Items.GLASS_BOTTLE.defaultStack,
                        Text.translatable(REDSTONE_MODE_WHILE_EMPTY),
                        Text.translatable(REDSTONE_MODE_WHILE_EMPTY_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        RedstoneEmissionMode.NONE,
                        Items.BARRIER.defaultStack,
                        Text.translatable(REDSTONE_MODE_NONE),
                        Text.translatable(REDSTONE_MODE_NONE_NARRATION)
                    )
                )
            ) { button, mode -> sendPropertyUpdate(1, mode.ordinal) }
        )

        sendingModeButton = addDrawableChild(
            ImageCyclingButtonWidget(
                x + 144, y + 74,
                18, 18,
                Text.translatable(SENDING_MODE),
                listOf(
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        SendMode.MANUAL,
                        Items.OAK_BUTTON.defaultStack,
                        Text.translatable(SENDING_MODE_MANUAL),
                        Text.translatable(SENDING_MODE_MANUAL_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        SendMode.ON_REDSTONE,
                        Items.REDSTONE.defaultStack,
                        Text.translatable(SENDING_MODE_REDSTONE),
                        Text.translatable(SENDING_MODE_REDSTONE_NARRATION)
                    ),
                    ImageCyclingButtonWidget.ItemCycleEntry(
                        SendMode.AUTOMATIC,
                        Items.PISTON.defaultStack,
                        Text.translatable(SENDING_MODE_AUTOMATIC),
                        Text.translatable(SENDING_MODE_AUTOMATIC_NARRATION)
                    ),
                )
            ) { button, mode -> sendPropertyUpdate(2, mode.ordinal) }
        )
    }

    private fun sendPropertyUpdate(property: Int, value: Int) {
        ClientPlayNetworking.send(
            UpdateVacPipeStationScreenHandlerPropertiesC2SPacket(handler.syncId, property, value)
        )
    }

    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            texture,
            x,
            y,
            0f,
            0f,
            backgroundWidth,
            backgroundHeight,
            256,
            256
        )
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        super.render(context, mouseX, mouseY, deltaTicks)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    companion object {
        val SEND_TEXTURE: Identifier =
            Identifier.of(DustyDecorMod.MODID, "textures/gui/container/vac_pipe_station_send.png")
        val RECEIVE_TEXTURE: Identifier =
            Identifier.of(DustyDecorMod.MODID, "textures/gui/container/vac_pipe_station_receive.png")

        const val COPPER_GOLEM_MODE: String = "screen.${DustyDecorMod.MODID}.vac_pipe_station.mode.copper_golem"
        const val COPPER_GOLEM_MODE_INTERACT: String = "$COPPER_GOLEM_MODE.interact"
        const val COPPER_GOLEM_MODE_INTERACT_NARRATION: String = "$COPPER_GOLEM_MODE_INTERACT.narration"
        const val COPPER_GOLEM_MODE_IGNORE: String = "$COPPER_GOLEM_MODE.ignore"
        const val COPPER_GOLEM_MODE_IGNORE_NARRATION: String = "$COPPER_GOLEM_MODE_IGNORE.narration"

        const val REDSTONE_MODE: String = "screen.${DustyDecorMod.MODID}.vac_pipe_station.mode.redstone"
        const val REDSTONE_MODE_ON_SEND: String = "$REDSTONE_MODE.on_send"
        const val REDSTONE_MODE_ON_SEND_NARRATION: String = "$REDSTONE_MODE_ON_SEND.narration"
        const val REDSTONE_MODE_ON_RECEIVE: String = "$REDSTONE_MODE.on_arrival"
        const val REDSTONE_MODE_ON_RECEIVE_NARRATION: String = "$REDSTONE_MODE_ON_RECEIVE.narration"
        const val REDSTONE_MODE_WHILE_EMPTY: String = "$REDSTONE_MODE.while_empty"
        const val REDSTONE_MODE_WHILE_EMPTY_NARRATION: String = "$REDSTONE_MODE_WHILE_EMPTY.narration"
        const val REDSTONE_MODE_NONE: String = "$REDSTONE_MODE.none"
        const val REDSTONE_MODE_NONE_NARRATION: String = "$REDSTONE_MODE_NONE.narration"

        const val SENDING_MODE: String = "screen.${DustyDecorMod.MODID}.vac_pipe_station.mode.sending"
        const val SENDING_MODE_MANUAL: String = "$SENDING_MODE.manual"
        const val SENDING_MODE_MANUAL_NARRATION: String = "$SENDING_MODE_MANUAL.narration"
        const val SENDING_MODE_REDSTONE: String = "$SENDING_MODE.redston"
        const val SENDING_MODE_REDSTONE_NARRATION: String = "$SENDING_MODE_REDSTONE.narration"
        const val SENDING_MODE_AUTOMATIC: String = "$SENDING_MODE.automatic"
        const val SENDING_MODE_AUTOMATIC_NARRATION: String = "$SENDING_MODE_AUTOMATIC.narration"
    }
}