package cy.jdkdigital.productivebees.util;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class BeeCreator
{
    public static CompoundNBT create(ResourceLocation id, JsonObject json) {
        Color primary = Color.decode(json.get("primaryColor").getAsString());
        Color secondary = Color.decode(json.get("secondaryColor").getAsString());

        CompoundNBT data = new CompoundNBT();
        data.putInt("primaryColor", primary.getRGB());
        data.putInt("secondaryColor", secondary.getRGB());
        data.putString("id", id.toString());
        data.putString("name", idToName(id.getPath()));

        return data;
    }

    public static String idToName(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
