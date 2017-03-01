package ramBot;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class ReadyEventListener implements IListener<ReadyEvent> {

	@Override
	public void handle(ReadyEvent event) {
		IUser sunkink29 = RamBot.INSTANCE.client.getUserByID("194936758696148992");
		IUser botManager = RamBot.INSTANCE.client.getUserByID("285607196245622785");
		IChannel sunkink29Dm = null;
		try {
			sunkink29Dm = sunkink29.getOrCreatePMChannel();
		} catch (RateLimitException | DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RamBot.INSTANCE.sunkink29 = sunkink29;
		RamBot.INSTANCE.botManager = botManager;
		RamBot.INSTANCE.sunkink29Dm = sunkink29Dm;
	}

}
