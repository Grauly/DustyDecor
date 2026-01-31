package grauly.dustydecor.screens

import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.input.InputWithModifiers
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class ImageCyclingButtonWidget<T>(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val baseExplanation: Component,
    private val options: List<CycleEntry<T>>,
    private val action: (ImageCyclingButtonWidget<T>, T) -> Unit
) : AbstractButton(x, y, width, height, Component.empty()) {
    private var selectedIndex = 0

    init {
        cycle(0)
    }

    override fun onPress(input: InputWithModifiers) {
        if (input.hasShiftDown()) {
            this.cycle(-1)
        } else {
            this.cycle(1)
        }
        action.invoke(this, getActiveElement().value)
    }

    override fun updateWidgetNarration(builder: NarrationElementOutput) {
        val selection = getActiveElement()
        builder.add(NarratedElementType.TITLE, baseExplanation, selection.name, selection.activeNarrationMessage)
        if (!active) return
        val nextSelection = getOffsetElement(1)
        if (this.isFocused) {
            builder.add(
                NarratedElementType.USAGE,
                Component.translatable("narration.cycle_button.usage.focused", nextSelection.name)
            )
        } else {
            builder.add(
                NarratedElementType.USAGE,
                Component.translatable("narration.cycle_button.usage.hovered", nextSelection.name)
            )
        }
    }

    override fun renderWidget(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        deltaTicks: Float
    ) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks)
        getActiveElement().render(context, mouseX, mouseY, deltaTicks, x, y, width, height)
    }

    fun getValue(): T = getActiveElement().value

    private fun getOffsetElement(offset: Int): CycleEntry<T> =
        options[(Mth.positiveModulo(selectedIndex + offset, options.size))]

    fun setValue(value: T) {
        val foundOption = options.find { it.value == value }
            ?: throw IllegalArgumentException("Given value $value is not part of the predefined options")
        val index = options.indexOf(foundOption)
        updateIndexRaw(index)
    }

    private fun getActiveElement(): CycleEntry<T> = options[selectedIndex]

    private fun cycle(offset: Int) {
        updateIndexRaw(Mth.positiveModulo(selectedIndex + offset, options.size))
    }

    private fun updateIndexRaw(index: Int) {
        selectedIndex = index
        setTooltip(generateTooltip(getActiveElement()))
    }

    private fun generateTooltip(entry: CycleEntry<T>): Tooltip {
        return Tooltip.create(
            MutableComponent.create(baseExplanation.contents).append(entry.name),
            entry.activeNarrationMessage
        )
    }

    abstract class CycleEntry<A>(
        val value: A,
        val name: Component,
        val activeNarrationMessage: Component,
    ) {
        abstract fun render(
            context: GuiGraphics,
            mouseX: Int,
            mouseY: Int,
            deltaTicks: Float,
            x: Int,
            y: Int,
            width: Int,
            height: Int
        )
    }

    class IdentifierCycleEntry<A>(
        value: A,
        private val texture: ResourceLocation,
        name: Component,
        activeNarrationMessage: Component
    ) : CycleEntry<A>(value, name, activeNarrationMessage) {
        override fun render(
            context: GuiGraphics,
            mouseX: Int,
            mouseY: Int,
            deltaTicks: Float,
            x: Int,
            y: Int,
            width: Int,
            height: Int
        ) {
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x + 2, y + 2,
                0f, 0f,
                width - 4, height - 4,
                width - 4, height - 4,
            )
        }

    }

    class ItemCycleEntry<A>(
        value: A,
        private val item: ItemStack,
        name: Component,
        activeNarrationMessage: Component
    ) : CycleEntry<A>(value, name, activeNarrationMessage) {
        override fun render(
            context: GuiGraphics,
            mouseX: Int,
            mouseY: Int,
            deltaTicks: Float,
            x: Int,
            y: Int,
            width: Int,
            height: Int
        ) {
            context.renderItem(item, x + 1, y + 1)
        }

    }
}