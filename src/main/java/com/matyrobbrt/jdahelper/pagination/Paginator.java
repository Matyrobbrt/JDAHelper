package com.matyrobbrt.jdahelper.pagination;

import com.matyrobbrt.jdahelper.DismissListener;
import com.matyrobbrt.jdahelper.components.Component;
import com.matyrobbrt.jdahelper.components.ComponentListener;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;

/**
 * Utility class for paginating messages, using {@link com.matyrobbrt.jdahelper.components.Component components}. <br>
 * There should be only one {@link Paginator} instance per feature, which should be reused.
 */
@SuppressWarnings("unused")
public interface Paginator {
    ButtonFactory DEFAULT_BUTTON_FACTORY = ButtonFactory.emoji(ButtonStyle.PRIMARY, fromUnicode("◀️"), fromUnicode("▶️"), fromUnicode("⏮️"), fromUnicode("⏭️"));
    List<ButtonType> DEFAULT_BUTTON_ORDER = List.of(ButtonType.PREVIOUS, ButtonType.DISMISS, ButtonType.NEXT);
    List<ButtonType> NATURAL_BUTTON_ORDER = List.of(ButtonType.FIRST, ButtonType.PREVIOUS, ButtonType.DISMISS, ButtonType.NEXT, ButtonType.LAST);

    /**
     * Gets the component listener for handling the pagination. <br>
     * <strong>The listener has to be registered to a {@link com.matyrobbrt.jdahelper.components.ComponentManager manager}
     * before the paginator can be used.</strong>
     *
     * @return the component listener
     */
    ComponentListener getListener();

    /**
     * The lifespan of the buttons created by the paginator.
     *
     * @return the lifespan of the buttons created by the paginator
     */
    default Component.Lifespan getLifespan() {
        return Component.Lifespan.TEMPORARY;
    }

    /**
     * Gets how many items should be sent per individual page.
     *
     * @return how many items should be sent per individual page
     */
    default int getItemsPerPage() {
        return 25;
    }

    /**
     * If messages sent by the paginator should be dismissible. <br>
     * The message is only dismissible by the user that triggered the event which made the message to be sent.
     *
     * @return if message sent by the paginator should be dismissible
     */
    default boolean isDismissible() {
        return true;
    }

    /**
     * If the "scroll" buttons can only be used by the owner of the sent message. <br>
     * This is mainly used for commands that display user-specific information.
     *
     * @return if the scroll buttons are owner only
     */
    default boolean areButtonsOwnerOnly() {
        return false;
    }

    /**
     * Gets the {@link ButtonFactory factory} used for creating the buttons.
     *
     * @return the factory
     */
    @Nonnull
    default ButtonFactory getButtonFactory() {
        return DEFAULT_BUTTON_FACTORY;
    }

    @Nonnull
    default List<ButtonType> getButtonOrder() {
        return DEFAULT_BUTTON_ORDER;
    }

    /**
     * Given the index of the start of the message, get the next {@link #getItemsPerPage() items}. <br>
     * Before being sent, the message will have the pagination buttons added.
     *
     * @param startingIndex the index of the first item in the list.
     * @param maximum       the maximum amount of items to be displayed
     * @param arguments     the arguments of the button
     * @return a built message that can be sent
     */
    @Nonnull
    MessageCreateBuilder getMessage(int startingIndex, int maximum, final List<String> arguments);

    /**
     * Create the row of Component interaction buttons.
     * <p>
     * This creates the buttons based on the {@link #getButtonOrder() button order}.
     *
     * @param id          the component ID of the buttons
     * @param start       the index of the item at the start of the current page.
     * @param maximum     the maximum amount of items
     * @param buttonOwner the ID of the owner of the button. Can be {@code null} unless {@link #areButtonsOwnerOnly() buttons are owner-only}.
     * @return A row of buttons to go back and forth by one page
     */
    ActionRow createScrollButtons(String id, int start, int maximum, @Nullable Long buttonOwner);

    /**
     * Create a {@link MessageCreateData} which, if the number of items requires, also contains buttons for scrolling.
     *
     * @param startingIndex the index of the first item to display
     * @param maximum       the maximum of items
     * @param messageOwner  the ID of the "owner" of the message (mainly used for commands). Can be {@code null} unless {@link #areButtonsOwnerOnly() buttons are owner-only}.
     * @param args          arguments the arguments that will be saved in the database, bound to the button's component ID
     * @return the message
     */
    MessageCreateData createPaginatedMessage(final int startingIndex, final int maximum, @Nullable final Long messageOwner, final List<String> args);

    /**
     * Create a {@link MessageCreateData} which, if the number of items requires, also contains buttons for scrolling.
     *
     * @param startingIndex the index of the first item to display
     * @param maximum       the maximum of items
     * @param messageOwner  the ID of the "owner" of the message (mainly used for commands). Can be {@code null} unless {@link #areButtonsOwnerOnly() buttons are owner-only}.
     * @param args          arguments the arguments that will be saved in the database, bound to the button's component ID
     * @return the message
     */
    default MessageCreateData createPaginatedMessage(final int startingIndex, final int maximum, @Nullable final Long messageOwner, final String... args) {
        return createPaginatedMessage(startingIndex, maximum, messageOwner, Arrays.asList(args));
    }

    /**
     * Creates a {@link Paginator} builder.
     *
     * @param componentListener the builder of the component listener that the paginator will use
     * @return the builder
     */
    static PaginatorBuilder builder(final ComponentListener.Builder componentListener) {
        return new PaginatorBuilder(componentListener);
    }

    @FunctionalInterface
    interface MessageGetter {
        /**
         * Given the index of the start of the message, get the next {@link #getItemsPerPage() items}. <br>
         * Before being sent, the message will have the pagination buttons added.
         *
         * @param startingIndex the index of the first item in the list.
         * @param maximum       the maximum amount of items to be displayed
         * @param arguments     the arguments of the button
         * @return a built message that can be sent
         */
        @Nonnull
        MessageCreateBuilder getMessage(int startingIndex, int maximum, final List<String> arguments);
    }

    @FunctionalInterface
    interface ButtonFactory {

        /**
         * Creates a button.
         *
         * @param type     the type of the button to create
         * @param buttonId the ID of the button to create
         * @return the created button
         */
        Button build(ButtonType type, String buttonId);

        /**
         * A {@link ButtonFactory} implementation which builds buttons with emojis as labels.
         *
         * @param style    the style of the buttons
         * @param previous the emoji to use for the "previous" button
         * @param next     the emoji to use for the "next" button
         * @param first    the emoji to use for the "first" button
         * @param last     the emoji to use for the "last" button
         * @return the factory
         */
        static ButtonFactory emoji(ButtonStyle style, Emoji previous, Emoji next, Emoji first, Emoji last) {
            return (type, buttonId) -> switch (type) {
                case NEXT -> Button.of(style, buttonId, null, next);
                case PREVIOUS -> Button.of(style, buttonId, null, previous);
                case FIRST -> Button.of(style, buttonId, null, first);
                case LAST -> Button.of(style, buttonId, null, last);
                case DISMISS -> DismissListener.createDismissButton();
            };
        }

        /**
         * Extends the factory.
         * @return the extended factory
         */
        default ButtonFactory with(ButtonType type, Function<String, Button> other) {
            return (type1, buttonId) -> {
                if (type1 == type) {
                    return other.apply(buttonId);
                } else {
                    return this.build(type1, buttonId);
                }
            };
        }
    }

    /**
     * The logical order of the types is: <br>
     * {@code first, previous, next, last} <br>
     * {@code <<|, <, >, |>>}
     */
    enum ButtonType {
        FIRST("first"),
        PREVIOUS("prev"),

        NEXT("next"),
        LAST("last"),

        DISMISS("dismiss");

        private final String id;

        ButtonType(final String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }

        @Nullable
        public static ButtonType byId(String id) {
            return switch (id) {
                case "prev" -> PREVIOUS;
                case "next" -> NEXT;
                case "last" -> LAST;
                case "first" -> FIRST;
                default -> null;
            };
        }
    }
}
