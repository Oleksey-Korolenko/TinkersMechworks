package tmechworks.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import tmechworks.blocks.logic.DrawbridgeLogic;
import tmechworks.lib.TMechworksRegistry;

public class DrawbridgeContainer extends Container {

    public DrawbridgeLogic logic;
    public int progress = 0;
    public int fuel = 0;
    public int fuelGague = 0;

    public DrawbridgeContainer(InventoryPlayer inventoryplayer, DrawbridgeLogic logic) {
        this.logic = logic;

        this.addSlotToContainer(new DrawbridgeSlot(logic, 0, 80, 36, logic));
        this.addSlotToContainer(new SlotOpaqueBlocksOnly(logic, 1, 35, 36));

        /* Player inventory */
        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlotToContainer(
                        new Slot(inventoryplayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 8 + column * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return logic.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack stack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotID);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (slotID < logic.getSizeInventory()) {
                if (!this.mergeItemStack(slotStack, logic.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(slotStack, 0, logic.getSizeInventory(), false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startSlotId, int endSlotId, boolean reverseMerge) {
        if (stack == null || !(stack.getItem() instanceof ItemBlock) || logic.hasExtended()) {
            return false;
        }

        if (TMechworksRegistry.isItemDBBlacklisted((ItemBlock) stack.getItem())) {
            return false;
        }

        return super.mergeItemStack(stack, startSlotId, endSlotId, reverseMerge);
    }
}
