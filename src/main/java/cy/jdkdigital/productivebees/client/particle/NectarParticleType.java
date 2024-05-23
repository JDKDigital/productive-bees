package cy.jdkdigital.productivebees.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NectarParticleType extends ParticleType<NectarParticleType> implements ParticleOptions
{
    private final MapCodec<NectarParticleType> codec = MapCodec.unit(this::getType);
    private final StreamCodec<RegistryFriendlyByteBuf, NectarParticleType> streamCodec = StreamCodec.unit(this);

    private float[] color = null;

    @Override
    public MapCodec<NectarParticleType> codec() {
        return codec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, NectarParticleType> streamCodec() {
        return streamCodec;
    }

    public NectarParticleType() {
        super(false);
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
}