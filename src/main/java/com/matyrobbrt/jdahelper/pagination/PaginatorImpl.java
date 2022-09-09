package com.matyrobbrt.jdahelper.pagination;

import com.matyrobbrt.jdahelper.DismissListener;
import com.matyrobbrt.jdahelper.components.Component;
import com.matyrobbrt.jdahelper.components.ComponentListener;
import com.matyrobbrt.jdahelper.components.context.ButtonInteractionContext;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class PaginatorImpl implements Paginator {
    private final ComponentListener listener;
    private final Component.Lifespan lifespan;
    private final int itemsPerPage;
    private final boolean dismissible;
    private final boolean buttonsOwnerOnly;
    private final MessageGetter embedGetter;
    private final ButtonFactory buttonFactory;
    private final List<ButtonType> buttonOrder;

    PaginatorImpl(final ComponentListener.Builder listener, @Nullable final Consumer<? super ButtonInteractionContext> buttonInteractionHandler, final Component.Lifespan lifespan, final int itemsPerPage, final boolean dismissible, final boolean buttonsOwnerOnly, final MessageGetter embedGetter, final ButtonFactory buttonFactory, final List<ButtonType> buttonOrder) {
        this.embedGetter = embedGetter;
        this.listener = listener
                .onButtonInteraction(buttonInteractionHandler == null ? this::onButtonInteraction : buttonInteractionHandler)
                .build();
        this.lifespan = lifespan;
        this.itemsPerPage = itemsPerPage;
        this.dismissible = dismissible;
        this.buttonsOwnerOnly = buttonsOwnerOnly;
        this.buttonFactory = buttonFactory;
        this.buttonOrder = buttonOrder;
    }

    @NotNull
    @Override
    public List<ButtonType> getButtonOrder() {
        return buttonOrder;
    }

    /**
     * The default button interaction handler.
     */
    public void onButtonInteraction(final ButtonInteractionContext context) {
        final var owner = context.getItemComponentArguments().size() > 1 ? Long.parseLong(context.getItemComponentArguments().get(1)) : null;
        final var event = context.getEvent();
        final var interaction = event.getMessage().getInteraction();
        if (areButtonsOwnerOnly()) {
            if (owner != null) {
                if (owner != event.getUser().getIdLong()) {
                    event.deferEdit().queue();
                    return;
                }
            } else if (interaction != null && interaction.getUser().getIdLong() != event.getUser().getIdLong()) {
                event.deferEdit().queue();
                return;
            }
        }
        final int current = context.getArgument(0, () -> 0, Integer::parseInt);
        final int maximum = context.getArgument(1, () -> 0, Integer::parseInt);
        final List<String> newArgs = context.getArguments().size() == 2 ? List.of() : context.getArguments().subList(2, context.getArguments().size());

        // If it has action rows already, don't delete them
        final var oldActionRowsSize = event.getMessage().getActionRows().size();
        final var oldActionRows = oldActionRowsSize < 2 ? new ArrayList<ActionRow>() :
                new ArrayList<>(event.getMessage().getActionRows().subList(1, oldActionRowsSize));

        final var buttonId = context.getComponentId().toString();

        final var buttonType = ButtonType.byId(context.getItemComponentArguments().get(0));
        if (buttonType == null) return;

        switch (buttonType) {
            case NEXT -> {
                final var start = current + itemsPerPage;

                oldActionRows.add(0, createScrollButtons(buttonId, start, maximum, owner));
                event.editMessage(MessageEditData.fromCreateData(getMessage(start, maximum, newArgs)
                                .setComponents(oldActionRows)
                                .build()))
                        .queue();

                // Argument 0 == current index
                context.updateArgument(0, String.valueOf(start));
            }
            case LAST -> {
                final var start = maximum - Math.min(maximum, itemsPerPage);
                oldActionRows.add(0, createScrollButtons(buttonId, start, maximum, owner));
                event.editMessage(MessageEditData.fromCreateData(getMessage(start, maximum, newArgs)
                                .setComponents(oldActionRows)
                                .build()))
                        .queue();

                // Argument 0 == current index
                context.updateArgument(0, String.valueOf(start));
            }

            case PREVIOUS -> {
                final var start = current - itemsPerPage;

                oldActionRows.add(0, createScrollButtons(buttonId, start, maximum, owner));
                event.editMessage(MessageEditData.fromCreateData(getMessage(start, maximum, newArgs)
                                .setComponents(oldActionRows)
                                .build()))
                        .queue();

                // Argument 0 == current index
                context.updateArgument(0, String.valueOf(start));
            }
            case FIRST -> {
                final var start = 0;

                oldActionRows.add(0, createScrollButtons(buttonId, start, maximum, owner));
                event.editMessage(MessageEditData.fromCreateData(getMessage(start, maximum, newArgs)
                                .setComponents(oldActionRows)
                                .build()))
                        .queue();

                // Argument 0 == current index
                context.updateArgument(0, String.valueOf(start));
            }
        }
    }

    @NotNull
    @Override
    public MessageCreateBuilder getMessage(final int startingIndex, final int maximum, final List<String> arguments) {
        return embedGetter.getMessage(startingIndex, maximum, arguments);
    }

    @Override
    public ComponentListener getListener() {
        return listener;
    }

    @Override
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    @Override
    public Component.Lifespan getLifespan() {
        return lifespan;
    }

    @Override
    public boolean areButtonsOwnerOnly() {
        return buttonsOwnerOnly;
    }

    @Override
    public boolean isDismissible() {
        return dismissible;
    }

    @Override
    public ActionRow createScrollButtons(final String id, final int start, final int maximum, @Nullable final Long buttonOwner) {
        final List<Button> buttons = new ArrayList<>();
        buttonOrder.forEach(type -> {
            final var btn = createButton(type, start, maximum, id, buttonOwner);
            if (btn != null) {
                buttons.add(btn);
            }
        });
        return ActionRow.of(buttons);
    }

    @Nullable
    private Button createButton(final ButtonType type, final int start, final int maximum, final String id, @Nullable final Long buttonOwner) {
        return switch (type) {
            case DISMISS -> {
                if (isDismissible()) {
                    yield buttonOwner == null ? buttonFactory.build(ButtonType.DISMISS, DismissListener.BUTTON_PREFIX) : buttonFactory.build(ButtonType.DISMISS, DismissListener.BUTTON_PREFIX + "-" + buttonOwner);
                } else {
                    yield null;
                }
            }

            case NEXT -> {
                final var btn = buttonFactory.build(ButtonType.NEXT, resolveButtonId(id, ButtonType.NEXT, buttonOwner)).asDisabled();
                if (start + getItemsPerPage() < maximum) {
                    yield btn.asEnabled();
                }
                yield btn;
            }
            case LAST -> {
                final var btn = buttonFactory.build(ButtonType.LAST, resolveButtonId(id, ButtonType.LAST, buttonOwner)).asDisabled();
                if (start + getItemsPerPage() < maximum) {
                    yield btn.asEnabled();
                }
                yield btn;
            }

            case PREVIOUS -> {
                final var btn = buttonFactory.build(ButtonType.PREVIOUS, resolveButtonId(id, ButtonType.PREVIOUS, buttonOwner)).asDisabled();
                if (start != 0) {
                    yield btn.asEnabled();
                }
                yield btn;
            }
            case FIRST -> {
                final var btn = buttonFactory.build(ButtonType.FIRST, resolveButtonId(id, ButtonType.FIRST, buttonOwner)).asDisabled();
                if (start != 0) {
                    yield btn.asEnabled();
                }
                yield btn;
            }
        };
    }

    private String resolveButtonId(final String baseId, final ButtonType type, final @Nullable Long buttonOwner) {
        return Component.createIdWithArguments(baseId, areButtonsOwnerOnly() ? new Object[] {type, buttonOwner} : new Object[] {type});
    }

    @NotNull
    @Override
    public Paginator.ButtonFactory getButtonFactory() {
        return buttonFactory;
    }

    @Override
    public MessageCreateData createPaginatedMessage(final int startingIndex, final int maximum, final @Nullable Long messageOwner, final List<String> args) {
        final var id = UUID.randomUUID();
        final var argsList = new ArrayList<>(args);
        final var message = getMessage(startingIndex, maximum, argsList);
        final var startStr = String.valueOf(startingIndex);
        final var maxStr = String.valueOf(maximum);
        if (argsList.size() == 0) {
            argsList.add(startStr);
            argsList.add(maxStr);
        } else {
            argsList.add(0, startStr);
            argsList.add(1, maxStr);
        }
        final var component = new Component(getListener().getName(), id, argsList, Component.Lifespan.TEMPORARY);
        getListener().insertComponent(component);
        message.addComponents(createScrollButtons(id.toString(), startingIndex, maximum, messageOwner));
        return message.build();
    }
}
