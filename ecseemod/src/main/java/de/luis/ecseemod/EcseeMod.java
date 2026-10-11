package de.luis.ecseemod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class EcseeMod implements ClientModInitializer {

    private static KeyBinding ecseeKey;

    @Override
    public void onInitializeClient() {
        ecseeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ecseemod.ecsee",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KeyBinding.Category.create(Identifier.of("ecseemod", "general"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ecseeKey.wasPressed()) {
                runInvsee(client);
            }
        });
    }

    private static void runInvsee(MinecraftClient client) {
        if (client.player == null || client.world == null || client.player.networkHandler == null) {
            return;
        }

        AbstractClientPlayerEntity nearest = null;
        double best = Double.MAX_VALUE;

        for (AbstractClientPlayerEntity other : client.world.getPlayers()) {
            if (other == client.player) continue;
            double d = client.player.squaredDistanceTo(other);
            if (d < best) {
                best = d;
                nearest = other;
            }
        }

        if (nearest == null) {
            client.player.sendMessage(Text.literal("Kein Spieler in der Nähe."), true);
            return;
        }

        client.player.networkHandler.sendChatCommand("ecsee " + nearest.getGameProfile().name());
    }
}
