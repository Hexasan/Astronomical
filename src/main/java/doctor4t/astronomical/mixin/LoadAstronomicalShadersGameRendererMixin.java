package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.shader.ShaderStage;
import com.mojang.datafixers.util.Pair;
import doctor4t.astronomical.client.AstronomicalShaders;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
final class LoadAstronomicalShadersGameRendererMixin {
	@Inject(method = "loadShaders", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void lodestone$registerShaders(ResourceFactory manager, CallbackInfo ci, List<ShaderStage> list, List<Pair<ShaderProgram, Consumer<ShaderProgram>>> list2) throws IOException {
		AstronomicalShaders.init(manager);
		list2.addAll(AstronomicalShaders.shaderList);
	}
}
