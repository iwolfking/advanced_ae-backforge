package net.pedroksl.advanced_ae.xmod.iris;
import net.pedroksl.advanced_ae.common.Addons;

import net.irisshaders.iris.api.v0.IrisApi;

public class IrisPlugin {

    public static boolean isShaderPackInUse() {
        try {
            return Addons.IRIS.isLoaded() && IrisApi.getInstance().isShaderPackInUse();
        } catch (Exception e) {
            return false;
        }
    }
}
