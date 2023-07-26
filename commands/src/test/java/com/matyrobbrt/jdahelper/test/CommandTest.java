package com.matyrobbrt.jdahelper.test;

import com.matyrobbrt.jdahelper.commands.Localizable;
import com.matyrobbrt.jdahelper.commands.Options;
import com.matyrobbrt.jdahelper.commands.SlashCommand;
import com.matyrobbrt.jdahelper.commands.manager.CommandManager;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public class CommandTest {
    public static void main(String[] args) throws Exception {
        final CommandManager manager = CommandManager.builder()
                .addCommand(SlashCommand.builder()
                        .setName(Localizable.of("ping"))
                        .setDescription(Localizable.of("Reply with pong"))
                        .action(event -> event.reply("Pong!").queue())
                        .build())
                .addCommand(SlashCommand.builder()
                        .setName(Localizable.of("testing"))
                        .addChildren(SlashCommand.builder()
                                .setName(Localizable.of("nr1"))
                                .setDescription(Localizable.of("The nr1 sub command!"))
                                .action(event -> event.reply("Hi from nr1!").queue()))
                        .addChildren(SlashCommand.builder()
                                .setName(Localizable.of("group"))
                                .setDescription(Localizable.of("Some nice group"))
                                .addChildren(SlashCommand.builder()
                                        .setName(Localizable.of("gr1"))
                                        .setDescription(Localizable.of("The group 1 command"))
                                        .action(event -> event.reply("Hi from gr1!").queue()))
                                .addChildren(SlashCommand.builder()
                                        .setName(Localizable.of("gr2"))
                                        .setDescription(Localizable.of("Execute the gr2 stuff."))
                                        .addOptions(Options.channel(Localizable.of("channel")
                                                .addLocalization(DiscordLocale.ENGLISH_UK, "chanal"), Localizable.of("The channel to refer to")).setRequired(true))
                                        .action(event -> event.reply("Hi from gr2! " + event.getOption("channel", OptionMapping::getAsChannel).getAsMention()).queue())))
                        .build())
                .build();
        JDABuilder.createLight(System.getenv("BOT_TOKEN"))
                .addEventListeners(manager.getListener())
                .build()
                .awaitReady();
    }
}
