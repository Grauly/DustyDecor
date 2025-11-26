package grauly.dustydecor.screens

import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.client.input.AbstractInput
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

class ImageCyclingButtonWidget<T>(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val baseExplanation: Text,
    private val options: List<CycleEntry<T>>,
    private val action: (ImageCyclingButtonWidget<T>, T) -> Unit
) : PressableWidget(x, y, width, height, Text.empty()) {
    private var selectedIndex = 0

    init {
        cycle(0)
    }

    override fun onPress(input: AbstractInput) {
        if (input.hasShift()) {
            this.cycle(-1)
        } else {
            this.cycle(1)
        }
        action.invoke(this, getActiveElement().value)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        val selection = getActiveElement()
        builder.put(NarrationPart.TITLE, baseExplanation, selection.name, selection.activeNarrationMessage)
        if (!active) return
        val nextSelection = getOffsetElement(1)
        if (this.isFocused) {
            builder.put(
                NarrationPart.USAGE,
                Text.translatable("narration.cycle_button.usage.focused", nextSelection.name)
            )
        } else {
            builder.put(
                NarrationPart.USAGE,
                Text.translatable("narration.cycle_button.usage.hovered", nextSelection.name)
            )
        }
    }

    override fun renderWidget(
        context: DrawContext,
        mouseX: Int,
        mouseY: Int,
        deltaTicks: Float
    ) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks)
        getActiveElement().render(context, mouseX, mouseY, deltaTicks, x, y, width, height)
    }

    fun getValue(): T = getActiveElement().value

    private fun getOffsetElement(offset: Int): CycleEntry<T> =
        options[(MathHelper.floorMod(selectedIndex + offset, options.size))]

    fun setValue(value: T) {
        val foundOption = options.find { it.value == value }
            ?: throw IllegalArgumentException("Given value $value is not part of the predefined options")
        val index = options.indexOf(foundOption)
        updateIndexRaw(index)
    }

    private fun getActiveElement(): CycleEntry<T> = options[selectedIndex]

    private fun cycle(offset: Int) {
        updateIndexRaw(MathHelper.floorMod(selectedIndex + offset, options.size))
    }

    private fun updateIndexRaw(index: Int) {
        selectedIndex = index
        setTooltip(generateTooltip(getActiveElement()))
    }

    private fun generateTooltip(entry: CycleEntry<T>): Tooltip {
        return Tooltip.of(
            MutableText.of(baseExplanation.content).append(entry.name),
            entry.activeNarrationMessage
        )
    }

    abstract class CycleEntry<A>(
        val value: A,
        val name: Text,
        val activeNarrationMessage: Text,
    ) {
        abstract fun render(
            context: DrawContext,
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
        private val texture: Identifier,
        name: Text,
        activeNarrationMessage: Text
    ) : CycleEntry<A>(value, name, activeNarrationMessage) {
        override fun render(
            context: DrawContext,
            mouseX: Int,
            mouseY: Int,
            deltaTicks: Float,
            x: Int,
            y: Int,
            width: Int,
            height: Int
        ) {
            context.drawTexture(
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
        name: Text,
        activeNarrationMessage: Text
    ) : CycleEntry<A>(value, name, activeNarrationMessage) {
        override fun render(
            context: DrawContext,
            mouseX: Int,
            mouseY: Int,
            deltaTicks: Float,
            x: Int,
            y: Int,
            width: Int,
            height: Int
        ) {
            context.drawItem(item, x + 1, y + 1)
        }

    }
}