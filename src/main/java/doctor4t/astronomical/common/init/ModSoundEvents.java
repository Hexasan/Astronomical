package doctor4t.astronomical.common.init;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModSoundEvents {

	Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();
//	SoundEvent BLOCK_LOCKER_INTERACT = createSoundEvent("block.locker.interact");

	static void initialize() {
		SOUND_EVENTS.keySet().forEach(soundEvent -> Registry.register(Registry.SOUND_EVENT, SOUND_EVENTS.get(soundEvent), soundEvent));
	}

	private static SoundEvent createSoundEvent(String path) {
		SoundEvent soundEvent = new SoundEvent(Astronomical.id(path));
		SOUND_EVENTS.put(soundEvent, Astronomical.id(path));
		return soundEvent;
	}

}
