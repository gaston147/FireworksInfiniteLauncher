package fr.vincentteam.fireworksinfinitelauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class FireworksInfiniteLauncher extends JavaPlugin implements Listener {
	private List<Group> groups;
	
	private class Group {
		private int x, y, z;
		private boolean powered;
		
		public Group(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getZ() {
			return z;
		}
		
		public boolean isPowered() {
			return powered;
		}
		
		public void setPowered(boolean powered) {
			this.powered = powered;
		}
	}
	
	public void onEnable() {
		groups = new ArrayList<Group>();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
			Sign sign = (Sign) block.getState();
			if (sign.getLine(0).equalsIgnoreCase("[fireworks]")) {
				Group group = getGroup(block.getX(), block.getY(), block.getZ());
				if (block.getBlockPower() > 0) {
					if (group == null || !group.isPowered()) {
						launchFireWorks(convertFromBlock(block.getLocation()), sign.getLine(1).matches("h=\\d+") ? Integer.valueOf(sign.getLine(1).substring(2, sign.getLine(1).length())) : (int) (Math.random() * 2) + 1, sign.getLine(2).matches("c1=\\d+") ? Integer.valueOf(sign.getLine(2).substring(3, sign.getLine(2).length())) : (int) (Math.random() * 17), sign.getLine(3).matches("c2=\\d+") ? Integer.valueOf(sign.getLine(3).substring(3, sign.getLine(3).length())) : (int) (Math.random() * 17));
						setPowered(block.getX(), block.getY(), block.getZ(), true);
					}
				} else {
					setPowered(block.getX(), block.getY(), block.getZ(), false);
				}
			}
		}
	}
	
	private void setPowered(int x, int y, int z, boolean powered) {
		Group group = getGroup(x, y, z);
		if (group == null) {
			group = new Group(x, y, z);
			group.setPowered(powered);
			groups.add(group);
		} else {
			group.setPowered(powered);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		groups.remove(getGroup(block.getX(), block.getY(), block.getZ()));
	}
	
	private Type[] types = new Type[] {
		Type.BALL,
		Type.BALL_LARGE,
		Type.BURST,
		Type.CREEPER,
		Type.STAR
    };
	
	private void launchFireWorks(Location location, int height, int color1, int color2) {
		Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		Random r = new Random();
		Type type = types[r.nextInt(5)];
		Color c1 = getColor(color1);
		Color c2 = getColor(color2);
		FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

		fwm.addEffect(effect);
		fwm.setPower(height);
		fw.setFireworkMeta(fwm);
	}

	private Location convertFromBlock(Location location) {
		return new Location(location.getWorld(), location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5);
	}
	
	private static final Color[] colors = new Color[] {
		Color.AQUA,
		Color.BLACK,
		Color.BLUE,
		Color.FUCHSIA,
		Color.GRAY,
		Color.GREEN,
		Color.LIME,
		Color.MAROON,
		Color.NAVY,
		Color.OLIVE,
		Color.ORANGE,
		Color.PURPLE,
		Color.RED,
		Color.SILVER,
		Color.TEAL,
		Color.WHITE,
		Color.YELLOW
	};
	
	private Color getColor(int i) {
		return colors[i];
	}
	
	public Group getGroup(int x, int y, int z) {
		for (Group group : groups)
			if (group.getX() == x && group.getY() == y && group.getZ() == z)
				return group;
		return null;
	}
}
