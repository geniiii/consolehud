package site.geni.renderevents.callbacks.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface InGameHudDrawCallback {
	interface Pre {
		Event<InGameHudDrawCallback.Pre> EVENT = EventFactory.createArrayBacked(
			InGameHudDrawCallback.Pre.class,
			listeners -> partialTicks -> {
				for (InGameHudDrawCallback.Pre listener : listeners) {
					listener.draw(partialTicks);
				}
			}
		);

		void draw(float partialTicks);
	}


	interface Post {
		Event<InGameHudDrawCallback.Post> EVENT = EventFactory.createArrayBacked(
			InGameHudDrawCallback.Post.class,
			listeners -> partialTicks -> {
				for (InGameHudDrawCallback.Post listener : listeners) {
					listener.draw(partialTicks);
				}
			}
		);

		void draw(float partialTicks);
	}
}
