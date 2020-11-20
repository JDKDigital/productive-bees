package cy.jdkdigital.productivebees.util;

import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class SingleEntryTag<T> extends Tag<T>
{
    private Collection<T> collection = new HashSet<>();

    public SingleEntryTag(ResourceLocation resourceLocationIn, T entry, boolean preserveOrder) {
        super(resourceLocationIn, Collections.emptyList(), preserveOrder);

        collection.add(entry);
    }

    @Override
    public boolean contains(T entryIn) {
        return collection.contains(entryIn);
    }

    @Override
    public Collection<T> getAllElements() {
        return collection;
    }
}
