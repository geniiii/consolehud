package site.geni.renderevents.callbacks.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface InGameHudDrawCallback {
	interface Pre {
		void draw(float partialTicks);

		Event<InGameHudDrawCallback.Pre> EVENT = EventFactory.createArrayBacked(
			InGameHudDrawCallback.Pre.class,
			listeners -> partialTicks -> {
				for (InGameHudDrawCallback.Pre listener : listeners) {
					listener.draw(partialTicks);
				}
			}
		);
	}


	interface Post {
		void draw(float partialTicks);

		Event<InGameHudDrawCallback.Post> EVENT = EventFactory.createArrayBacked(
			InGameHudDrawCallback.Post.class,
			listeners -> partialTicks -> {
				for (InGameHudDrawCallback.Post listener : listeners) {
					listener.draw(partialTicks);
				}
			}
		);
	}
}
