package org.battleplugins.arena.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.battleplugins.arena.util.PaginationCalculator;
import org.bukkit.command.CommandSender;

public class Message {
    private final String translationKey;
    private Component text;

    boolean context;

    Message(String translationKey, Component defaultText) {
        this.translationKey = translationKey;
        this.text = defaultText;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public Component getText() {
        return this.text;
    }

    void setText(Component text) {
        this.text = text;
    }

    public void send(CommandSender sender) {
        sender.sendMessage(this.toComponent());
    }

    public void send(CommandSender sender, String... replacements) {
        sender.sendMessage(this.toComponent(replacements));
    }

    public void sendCentered(CommandSender sender, String... replacements) {
        sender.sendMessage(PaginationCalculator.center(this.toComponent(replacements), Component.space()));
    }

    public void sendCentered(CommandSender sender, Message... replacements) {
        sender.sendMessage(PaginationCalculator.center(this.toComponent(replacements), Component.space()));
    }

    public void send(CommandSender sender, Component... replacements) {
        String[] strReplacements = new String[replacements.length];
        for (int i = 0; i < strReplacements.length; i++) {
            Component replacement = replacements[i];
            strReplacements[i] = PlainTextComponentSerializer.plainText().serialize(replacement);
        }

        sender.sendMessage(this.toComponent(strReplacements));
    }

    public void send(CommandSender sender, Message... replacements) {
        String[] strReplacements = new String[replacements.length];
        for (int i = 0; i < strReplacements.length; i++) {
            Message replacement = replacements[i];
            strReplacements[i] = replacement.asPlainText();
        }

        sender.sendMessage(this.toComponent(strReplacements));
    }

    public Message withContext(Message... replacements) {
        String[] strReplacements = new String[replacements.length];
        for (int i = 0; i < strReplacements.length; i++) {
            Message replacement = replacements[i];
            strReplacements[i] = replacement.asPlainText();
        }

        return this.withContext(this.toComponent(strReplacements));
    }

    public Message withContext(Component... replacements) {
        String[] strReplacements = new String[replacements.length];
        for (int i = 0; i < strReplacements.length; i++) {
            Component replacement = replacements[i];
            strReplacements[i] = PlainTextComponentSerializer.plainText().serialize(replacement);
        }

        return this.withContext(strReplacements);
    }

    public Message withContext(String... replacements) {
        return Message.of(this.translationKey, this.toComponent(replacements)).attachContext();
    }

    public String asPlainText() {
        return PlainTextComponentSerializer.plainText().serialize(this.toComponent());
    }

    public String asPlainText(String... replacements) {
        return PlainTextComponentSerializer.plainText().serialize(this.toComponent(replacements));
    }

    public Component toComponent() {
        return this.text;
    }

    public Component toComponent(String... replacements) {
        Component text = this.text;
        for (String replacement : replacements) {
            text = text.replaceText(builder -> builder.matchLiteral("{}").once().replacement(replacement));
        }

        return text;
    }

    public Component toComponent(Message... replacements) {
        String[] strReplacements = new String[replacements.length];
        for (int i = 0; i < strReplacements.length; i++) {
            Message replacement = replacements[i];
            strReplacements[i] = replacement.asPlainText();
        }

        return this.toComponent(strReplacements);
    }

    private Message attachContext() {
        this.context = true;
        return this;
    }

    static Message of(String translationKey, Component text) {
        return new Message(translationKey, text);
    }
}
