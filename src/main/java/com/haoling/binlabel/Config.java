package com.haoling.binlabel;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /** ビンの中身表示のオン/オフ。 */
    public static final ModConfigSpec.BooleanValue SHOW_LABEL = BUILDER
            .translation("binlabel.configuration.showLabel")
            .comment("Show the item name and count when looking at a Mekanism bin")
            .define("showLabel", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
