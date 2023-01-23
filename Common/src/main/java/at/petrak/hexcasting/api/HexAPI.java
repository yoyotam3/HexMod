package at.petrak.hexcasting.api;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import com.google.common.base.Suppliers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public interface HexAPI {
    String MOD_ID = "hexcasting";
    Logger LOGGER = LogManager.getLogger(MOD_ID);

    Supplier<HexAPI> INSTANCE = Suppliers.memoize(() -> {
        try {
            return (HexAPI) Class.forName("at.petrak.hexcasting.common.impl.HexAPIImpl")
                .getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            LogManager.getLogger().warn("Unable to find HexAPIImpl, using a dummy");
            return new HexAPI() {
            };
        }
    });

    /**
     * Return the localization key for the given action.
     * <p>
     * Note we're allowed to have action <em>resource keys</em> on the client, just no actual actions.
     * <p>
     * Special handlers should be calling {@link SpecialHandler#getName()}
     */
    default String getActionI18nKey(ResourceKey<ActionRegistryEntry> action) {
        return "hexcasting.action.%s".formatted(action.location().toString());
    }

    default String getSpecialHandlerI18nKey(ResourceKey<SpecialHandler.Factory<?>> action) {
        return "hexcasting.special.%s".formatted(action.location().toString());
    }

    /**
     * Currently introspection/retrospection/consideration are hardcoded, but at least their names won't be
     */
    default String getRawHookI18nKey(ResourceLocation name) {
        return "hexcasting.rawhook.%s".formatted(name);
    }

    default Component getActionI18n(ResourceKey<ActionRegistryEntry> key, boolean isGreat) {
        return Component.translatable(getActionI18nKey(key))
            .withStyle(isGreat ? ChatFormatting.GOLD : ChatFormatting.LIGHT_PURPLE);
    }

    default Component getSpecialHandlerI18n(ResourceKey<SpecialHandler.Factory<?>> key) {
        return Component.translatable(getSpecialHandlerI18nKey(key))
            .withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    default Component getRawHookI18n(ResourceLocation name) {
        return Component.translatable(getRawHookI18nKey(name)).withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    static HexAPI instance() {
        return INSTANCE.get();
    }

    static ResourceLocation modLoc(String s) {
        return new ResourceLocation(MOD_ID, s);
    }
}