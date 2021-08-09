package cy.jdkdigital.productivebees.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NectarParticleType extends ParticleType<NectarParticleType> implements ParticleOptions
{
    private float[] color = null;

    private static final Deserializer<NectarParticleType> DESERIALIZER = new Deserializer<NectarParticleType>()
    {
        @Nonnull
        @Override
        public NectarParticleType fromCommand(@Nonnull ParticleType<NectarParticleType> particleType, @Nonnull StringReader stringReader) throws CommandSyntaxException {
            return (NectarParticleType) particleType;
        }

        @Nonnull
        @Override
        public NectarParticleType fromNetwork(@Nonnull ParticleType<NectarParticleType> particleType, @Nonnull FriendlyByteBuf buffer) {
            return (NectarParticleType) particleType;
        }
    };

    private final Codec<NectarParticleType> codec = Codec.unit(this::getType);

    @Override
    public Codec<NectarParticleType> codec() {
        return codec;
    }

    public NectarParticleType() {
        super(false, DESERIALIZER);
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    @Nullable
    public float[] getColor() {
        return this.color;
    }

    @Nonnull
    @Override
    public NectarParticleType getType() {
        return this;
    }

    @Override
    public void writeToNetwork(@Nonnull FriendlyByteBuf packetBuffer) {
    }

    @Nonnull
    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this).toString();
    }
}