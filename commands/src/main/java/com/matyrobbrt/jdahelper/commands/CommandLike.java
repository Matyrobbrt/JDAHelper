package com.matyrobbrt.jdahelper.commands;

/**
 * Represents an object that can be represented as a command.
 * @see Command
 * @see com.matyrobbrt.jdahelper.commands.builder.CommandBuilder
 * @param <T> the type of the command
 */
public interface CommandLike<T extends Command<?>> {
    /**
     * {@return this object, as a command}
     */
    T asCommand();
}
