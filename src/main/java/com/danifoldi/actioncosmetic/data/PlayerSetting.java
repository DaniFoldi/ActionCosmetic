package com.danifoldi.actioncosmetic.data;

public class PlayerSetting {

    String sneakSelection = "";
    String jumpSelection = "";

    public String getSneakSelection() {

        return sneakSelection;
    }

    public String getJumpSelection() {

        return jumpSelection;
    }

    public PlayerSetting withSneakSelection(String sneakSelection) {

        PlayerSetting newSetting = new PlayerSetting();
        newSetting.sneakSelection = sneakSelection;
        newSetting.jumpSelection = jumpSelection;
        return newSetting;
    }

    public PlayerSetting withJumpSelection(String jumpSelection) {

        PlayerSetting newSetting = new PlayerSetting();
        newSetting.sneakSelection = sneakSelection;
        newSetting.jumpSelection = jumpSelection;
        return newSetting;
    }
}
