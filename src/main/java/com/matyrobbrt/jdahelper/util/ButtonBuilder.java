package com.matyrobbrt.jdahelper.util;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.function.Supplier;

/**
 * A builder for buttons.
 */
@SuppressWarnings("unused")
public class ButtonBuilder implements Supplier<Button> {

    private ButtonStyle style;
    private String idOrUrl;
    private String label;
    private Emoji emoji;

    /**
     * Creates a new button builder.
     *
     * @param style the style of the button
     * @return the builder
     */
    public static ButtonBuilder builder(ButtonStyle style) {
        return new ButtonBuilder()
                .style(style);
    }

    /**
     * Sets the button's style.
     *
     * @param style the new style
     * @return the builder instance
     */
    public ButtonBuilder style(ButtonStyle style) {
        this.style = style;
        return this;
    }

    /**
     * Sets the button's ID / URL (depending on its {@link ButtonBuilder#style style}).
     *
     * @param idOrUrl the new id
     * @return the builder instance
     */
    public ButtonBuilder idOrUrl(String idOrUrl) {
        this.idOrUrl = idOrUrl;
        return this;
    }

    /**
     * Sets the button's label. <br>
     * Mutually exclusive with {@link ButtonBuilder#emoji(Emoji)}.
     *
     * @param label the new label
     * @return the builder instance
     */
    public ButtonBuilder label(String label) {
        this.label = label;
        return this;
    }

    /**
     * Sets the button's emoji. <br>
     * Mutually exclusive with {@link ButtonBuilder#label(String)}.
     *
     * @param emoji the new emoji
     * @return the builder instance
     */
    public ButtonBuilder emoji(Emoji emoji) {
        this.emoji = emoji;
        return this;
    }

    /**
     * Builds the button.
     *
     * @return the built button
     */
    @Override
    public Button get() {
        return build();
    }

    /**
     * Builds the button.
     *
     * @return the built button
     */
    public Button build() {
        return Button.of(style, idOrUrl, label, emoji);
    }
}
