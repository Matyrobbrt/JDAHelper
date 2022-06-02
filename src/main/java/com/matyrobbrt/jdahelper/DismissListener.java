package com.matyrobbrt.jdahelper;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A listener for dismissing. <br>
 * Register a {@link DismissListener} instance to a {@link net.dv8tion.jda.api.JDA bot} using {@link net.dv8tion.jda.api.JDA#addEventListener(Object...)}.
 */
@SuppressWarnings("unused")
public final class DismissListener implements EventListener {

    public static final String BUTTON_PREFIX = "dismiss";

    @NotNull
    private static DefaultStyle defaultStyle = new DefaultStyle(ButtonStyle.SECONDARY, "\uD83D\uDEAE️ Dismiss", null);

    /**
     * Sets the default style of buttons created by listeners
     *
     * @param defaultStyle the default style
     */
    public static void setDefaultStyle(@NotNull DefaultStyle defaultStyle) {
        DismissListener.defaultStyle = defaultStyle;
    }

    @Override
    @SubscribeEvent
    public void onEvent(@NotNull GenericEvent gEvent) {
        if (!(gEvent instanceof ButtonInteractionEvent event))
            return;

        final Button button = event.getButton();
        if (button.getId() == null || button.getId().isBlank()) {
            return;
        }
        final var idParts = button.getId().split("-");
        if (!idParts[0].equals(BUTTON_PREFIX)) {
            return;
        }
        switch (idParts.length) {
            // dismiss
            case 1 -> {
                if (event.getMessage().getInteraction() != null) {
                    final var owner = event.getMessage().getInteraction().getUser();
                    deleteIf(owner.getId(), event).queue();
                }
            }
            // dismiss-userId
            case 2 -> deleteIf(idParts[1], event).queue();
            // dismiss-userId-commandMessageId
            case 3 -> deleteIf(idParts[1], event)
                    .and(event.getChannel().retrieveMessageById(idParts[2])
                            .flatMap(m -> m.delete().reason("User dismissed the command"))
                            .addCheck(() -> canDelete(idParts[1], event))
                    )
                    .queue();
        }
    }

    private static RestAction<?> deleteIf(final String targetId, final ButtonInteractionEvent event) {
        if (canDelete(targetId, event)) {
            return event.getMessage().delete().reason("User dismissed the message");
        } else {
            return event.deferEdit();
        }
    }

    private static boolean canDelete(final String targetId, final ButtonInteractionEvent event) {
        return targetId.equals(event.getUser().getId()) && !event.isAcknowledged() && !event.getMessage().getType().canDelete();
    }

    /**
     * Creates a dismission {@link Button} which <strong>only</strong> works
     * for interactions, and whose owner is the user who triggered the interaction.
     *
     * @return the button
     */
    public static Button createDismissButton() {
        return Button.of(defaultStyle.style(), BUTTON_PREFIX, defaultStyle.label(), defaultStyle.emoji());
    }

    /**
     * Creates a dismission {@link Button}.
     *
     * @param buttonOwner the ID of the user that can use the button
     * @return the button
     */
    public static Button createDismissButton(final long buttonOwner) {
        return Button.of(defaultStyle.style(), BUTTON_PREFIX + "-" + buttonOwner, defaultStyle.label(), defaultStyle.emoji());
    }

    /**
     * Creates a dismission {@link Button}.
     *
     * @param buttonOwner the ID of the user that can use the button
     * @return the button
     */
    public static Button createDismissButton(final String buttonOwner) {
        return Button.of(defaultStyle.style(), BUTTON_PREFIX + "-" + buttonOwner, defaultStyle.label(), defaultStyle.emoji());
    }

    /**
     * Creates a dismiss button which will also delete the message that invoked the command.
     *
     * @param buttonOwner      the owner of the button
     * @param commandMessageId the message that invoked the command
     * @return the button
     */
    public static Button createDismissButton(final long buttonOwner, final long commandMessageId) {
        return Button.of(defaultStyle.style(), BUTTON_PREFIX + "-" + buttonOwner + "-" + commandMessageId, defaultStyle.label(), defaultStyle.emoji());
    }

    /**
     * Creates a dismission {@link Button}.
     *
     * @param buttonOwner    the user that can use the button
     * @param commandMessage a message that will be deleted as well (a.k.a. the message that invoked a command)
     * @return the button
     */
    public static Button createDismissButton(final User buttonOwner, final Message commandMessage) {
        return createDismissButton(buttonOwner.getIdLong(), commandMessage.getIdLong());
    }

    /**
     * Creates a dismission {@link Button}.
     *
     * @param buttonOwner the user that can use the button
     * @return the button
     */
    public static Button createDismissButton(final Member buttonOwner) {
        return createDismissButton(buttonOwner.getIdLong());
    }

    /**
     * Creates a dismission {@link Button}.
     *
     * @param buttonOwner the user that can use the button
     * @return the button
     */
    public static Button createDismissButton(final User buttonOwner) {
        return createDismissButton(buttonOwner.getIdLong());
    }

    /**
     * Creates a dismission {@link Button}.
     *
     * @param interaction the interaction that caused the message to be sent
     * @return the button
     */
    public static Button createDismissButton(final Interaction interaction) {
        return createDismissButton(interaction.getUser());
    }

    record DefaultStyle(ButtonStyle style, @Nullable String label, @Nullable Emoji emoji) {
    }
}
