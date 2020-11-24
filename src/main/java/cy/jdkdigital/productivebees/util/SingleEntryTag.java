package cy.jdkdigital.productivebees.util;

import net.minecraft.tags.Tag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleEntryTag<T> extends Tag<T>
{
    private List<T> collection = new ArrayList<>();

    public SingleEntryTag(T entry) {
        super(Collections.emptySet(), entry.getClass());

        collection.add(entry);
    }

    @Override
    public boolean contains(@Nonnull T entryIn) {
        return this.contentsClassType.isInstance(entryIn) && collection.contains(entryIn);
    }

    @Nonnull
    @Override
    public List<T> getAllElements() {
        return collection;
    }
}
