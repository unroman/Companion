package snownee.companion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.storage.LevelData;
import snownee.companion.CompanionCommonConfig;
import snownee.companion.Hooks;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	// We teleport all pets before level info being synced
	@SuppressWarnings("rawtypes")
	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V"
			), method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void companion_changeDimension(ServerLevel to, CallbackInfoReturnable<Entity> cir, ServerLevel from, ResourceKey resourceKey, LevelData levelData, PlayerList playerList, PortalInfo portalInfo) {
		if (CompanionCommonConfig.portalTeleportingPets)
			Hooks.changeDimension((ServerPlayer) (Object) this, to, from, false);
	}

	@Inject(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;unRide()V"
			), method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"
	)
	private void companion_returnFromEnd(ServerLevel to, CallbackInfoReturnable<Entity> cir) {
		if (CompanionCommonConfig.portalTeleportingPets) {
			ServerPlayer player = (ServerPlayer) (Object) this;
			Hooks.changeDimension(player, to, player.getLevel(), true);
		}
	}

}
