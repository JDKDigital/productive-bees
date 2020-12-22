package cy.jdkdigital.productivebees.util;

import net.minecraft.tags.ITag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SingleEntryTag<T> implements ITag<T>
{
    private List<T> collection;

    public SingleEntryTag(T entry) {
        super();

        collection = new ArrayList<>();
        collection.add(entry);
    }

    @Override
    public boolean contains(@Nonnull T entryIn) {
        return collection.contains(entryIn);
    }

    @Nonnull
    @Override
    public List<T> getAllElements() {
        return collection;
    }
}
