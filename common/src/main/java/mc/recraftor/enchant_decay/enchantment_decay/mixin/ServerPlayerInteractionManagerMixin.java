package mc.recraftor.enchant_decay.enchantment_decay.mixin;

import mc.recraftor.enchant_decay.enchantment_decay.DecaySource;
import mc.recraftor.enchant_decay.enchantment_decay.EnchantmentDecay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @Inject(
            method = "tryBreakBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;canHarvest(Lnet/minecraft/block/BlockState;)Z",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void tryBreakBlockCanHarvestInjector(BlockPos pos, CallbackInfoReturnable<Boolean> cir,
                                                 BlockState blockState, BlockEntity blockEntity, Block block,
                                                 boolean bl, ItemStack itemStack, ItemStack itemStack2) {
        if (bl) {
            EnchantmentDecay.decay(itemStack, player.getRandom(), DecaySource.MINING);
        }
    }
}
