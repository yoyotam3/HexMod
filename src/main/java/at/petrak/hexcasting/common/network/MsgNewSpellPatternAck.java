package at.petrak.hexcasting.common.network;

import at.petrak.hexcasting.api.spell.casting.ControllerInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Sent server->client when the player finishes casting a spell.
 */
public record MsgNewSpellPatternAck(ControllerInfo info) {

    public static MsgNewSpellPatternAck deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);

        var wasSpellCast = buf.readBoolean();
        var hasCastingSound = buf.readBoolean();
        var isStackEmpty = buf.readBoolean();
        var wasPrevPatternInvalid = buf.readBoolean();
        var descsLen = buf.readInt();
        var desc = new ArrayList<Component>(descsLen);
        for (int i = 0; i < descsLen; i++) {
            desc.add(buf.readComponent());
        }

        return new MsgNewSpellPatternAck(
            new ControllerInfo(wasSpellCast, hasCastingSound, isStackEmpty, wasPrevPatternInvalid, desc)
        );
    }

    public void serialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);

        buf.writeBoolean(this.info.getWasSpellCast());
        buf.writeBoolean(this.info.getHasCastingSound());
        buf.writeBoolean(this.info.isStackClear());
        buf.writeBoolean(this.info.getWasPrevPatternInvalid());
        buf.writeInt(this.info.getStackDesc().size());
        for (var desc : this.info.getStackDesc()) {
            buf.writeComponent(desc);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler handler = new ClientPacketHandler(this);
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> handler::newPattern);
        });
        ctx.get().setPacketHandled(true);
    }

}
